package com.guo;

import com.guo.entity.User;
import com.guo.repository.UserRepository;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

public class UserTest {

//  @Autowired
//  private UserRepository userRepository;
//  private Md5PasswordEncoder passwordEncoder;
//
//  @Test
//  public void UserFindOne(){
//
//    User user = userRepository.findOne(1L);
//    Assert.assertEquals("wali",user.getName());
//  }

  public static void main(String[] args) {
    Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
    String encodePassword = passwordEncoder.encodePassword("123456", 9);
    System.out.println(encodePassword);
  }
}
