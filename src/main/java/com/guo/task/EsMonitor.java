package com.guo.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @desception:定时任务检测es健康状态
 * @author: mi
 * @date: 2019-09-20 11:11
 */
@Component   // 把普通pojo实例化到spring容器中，相当于配置文件中的<bean id="" class=""/>
public class EsMonitor {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JavaMailSender mailSender;

  private static final String HEALTH_CHECK_API = "http://192.168.74.129:9200/_cluster/health";

  private static final String GREEN = "green";
  private static final String YELLOW = "yellow";
  private static final String RED = "red";

  @Scheduled(fixedDelay = 5000)
  public void healthCheck() {
    HttpClient httpClient = HttpClients.createDefault();
    HttpGet get = new HttpGet(HEALTH_CHECK_API);

    try {
      HttpResponse response = httpClient.execute(get);
      if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
        System.out.println("Can not access ES Service normally! Please check the server.");
      } else {
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        JsonNode result = objectMapper.readTree(body);
        String status = result.get("status").asText();

        String message = "";
        boolean isNormal = false;

        switch (status) {
          case GREEN:
            // System.out.println("ES server run normally.");
            message = "ES server run normally.";
            isNormal = true;
            break;
          case YELLOW:
            // System.out.println("ES server gets status yellow! Please check the ES server!");
            message = "ES server gets status yellow! Please check the ES server!";
            break;
          case RED:
            //System.out.println("ES ser get status RED!!! Must Check ES Server!!!");
            message = "ES ser get status RED!!! Must Check ES Server!!!";
            break;
          default:
            //System.out.println("Unknown ES status from server for: \" + status + \". Please check it.");
            message = "Unknown ES status from server for: " + status + ". Please check it.";
            break;
        }

        if (!isNormal) {
          sendAlertMessage(message);
        }

        // 获取集群节点
        int totalNodes = result.get("number_of_nodes").asInt();
        if (totalNodes < 1) {
          sendAlertMessage("我们的es节点丢了！");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void sendAlertMessage(String message) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setFrom("g1043841533@163.com");
    mailMessage.setTo("g1043841533@163.com");
    mailMessage.setSubject("【警告】ES服务监控");
    mailMessage.setText(message);

    mailSender.send(mailMessage);
  }

}
