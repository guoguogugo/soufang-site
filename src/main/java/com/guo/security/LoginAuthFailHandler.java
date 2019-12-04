package com.guo.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @desception:登录验证失败处理
 * @author: mi
 * @date: 2019-08-12 14:38
 */
public class LoginAuthFailHandler extends SimpleUrlAuthenticationFailureHandler {

  private final LoginUrlEntryPoint urlEntryPoint;

  public LoginAuthFailHandler(LoginUrlEntryPoint urlEntryPoint) {
    this.urlEntryPoint = urlEntryPoint;
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
          throws IOException, ServletException {

    String targetUrl = this.urlEntryPoint.determineUrlToUseForThisRequest(request, response, exception);
    targetUrl += "?" + exception.getMessage();
    super.setDefaultFailureUrl(targetUrl);
    super.onAuthenticationFailure(request, response, exception);

  }
}
