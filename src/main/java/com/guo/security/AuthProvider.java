package com.guo.security;

import com.guo.entity.User;
import com.guo.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @desception: 自定义认证实现
 * @author: mi
 * @date: 2019-08-08 10:13
 */
public class AuthProvider implements AuthenticationProvider {

  @Autowired
  private IUserService userServices;

  private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();


  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String userName = authentication.getName();
    String inputPassWord = (String) authentication.getCredentials();

    User user = userServices.findUserByName(userName);

    if (user == null) {
      throw new AuthenticationCredentialsNotFoundException("authError");
    }

    if (this.passwordEncoder.isPasswordValid(user.getPassword(), inputPassWord, user.getId())) {

      return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    throw new BadCredentialsException("authError");
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return true;
  }
}
