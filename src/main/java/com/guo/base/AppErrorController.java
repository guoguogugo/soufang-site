package com.guo.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @desception: Web错误全局配置
 * @author:
 * @date: 2019-07-26 10:32
 */
@Controller
public class AppErrorController implements ErrorController {

  private static final String ERROR_PATH = "/error";//不能的大写 server.error.whitelabel.enabled=false  注意此处配置
  private ErrorAttributes errrAttributes;

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }

  @Autowired
  public AppErrorController(ErrorAttributes errorAttributes) {
    this.errrAttributes = errorAttributes;
  }

  /*
   *  Web页面错误处理
   */
  @RequestMapping(value = ERROR_PATH, produces = "text/html")
  public String ErrorPageHandle(HttpServletRequest request, HttpServletResponse response) {

    int status = response.getStatus();
    switch (status) {
      case 403:
        return "403";
      case 404:
        return "404";
      case 500:
        return "500";
    }
    return "index";
  }

  /**
   * 除Web页面外的错误处理，比如Json/XML等
   */

  @RequestMapping(value = ERROR_PATH)
  @ResponseBody
  public ApiResponse ErrorApiHandle(HttpServletRequest request) {

    RequestAttributes requestAttributes = new ServletRequestAttributes(request);
    Map<String, Object> attr = this.errrAttributes.getErrorAttributes(requestAttributes, false);
    int status = getStatus(request);

    return ApiResponse.ofMessage(status, String.valueOf(attr.getOrDefault("message", "error")));
  }

  public int getStatus(HttpServletRequest request) {
    Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (status != null) {
      return status;
    }
    return 500;
  }

}
