package com.guo.config;


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @desception: ElasticSearch配置
 * @author: mi
 * @date: 2019-09-02 11:15
 */
@Configuration
public class ElasticSearchConfig {

  @Value("${elasticsearch.host}")   //elasticsearch IP
  private String esHost;

  @Value("${elasticsearch.port}")            // 端口
  private int esPort;

  @Value("${elasticsearch.cluster.name}")   // 名称
  private String esName;

  @Bean
  public TransportClient esClient() throws UnknownHostException {

    Settings settings = Settings.builder()
            .put("cluster.name", "elasticsearch")
            .put("client.transport.sniff", true)
            .build();

    TransportAddress master = new TransportAddress(
            InetAddress.getByName("192.168.74.129"), 9300
    );

    TransportClient client = new PreBuiltTransportClient(settings)
            .addTransportAddress(master);

    return client;
  }
}
