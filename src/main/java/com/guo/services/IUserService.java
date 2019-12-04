package com.guo.services;

import com.guo.dto.UserDTO;
import com.guo.entity.User;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-08 10:25
 */
public interface IUserService {

  User findUserByName(String UserName);

  ServiceResult<UserDTO> findById(Long userId);

  //通过手机号进行用户查找
  User findUserByTelephone(String telephone);

  // 通过手机号注册用户
  User addUserByPhone(String telehone);

  // 修改指定属性值
  ServiceResult modifyUserProfile(String profile, String value);

}
