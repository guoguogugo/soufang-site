package com.guo.services.impl;

import com.google.common.collect.Lists;
import com.guo.base.LoginUserUtil;
import com.guo.dto.UserDTO;
import com.guo.entity.Role;
import com.guo.entity.User;
import com.guo.repository.RoleRepository;
import com.guo.repository.UserRepository;
import com.guo.services.IUserService;
import com.guo.services.ServiceResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @desception:
 * @author: mi
 * @date: 2019-08-08 10:28
 */
@Service
public class UserServiceImpl implements IUserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private ModelMapper modelMapper;

  private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

  @Override
  public User findUserByName(String userName) {

    User user = userRepository.findUByname(userName);

    if (user == null) {
      return null;
    }

    List<Role> roles = roleRepository.findRolesByUserId(user.getId());

    if (roles == null || roles.isEmpty()) {

      throw new DisabledException("权限非法");
    }

    List<GrantedAuthority> authorities = new ArrayList<>();
    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
    user.setAuthorityList(authorities);

    return user;
  }

  //根据id查找用户
  @Override
  public ServiceResult<UserDTO> findById(Long userId) {

    User user = userRepository.findOne(userId);
    if (user == null) {
      return ServiceResult.notFound();
    }

    UserDTO userDTO = modelMapper.map(user, UserDTO.class);

    return ServiceResult.of(userDTO);
  }

  // 根据电话号码  查找用户
  @Override
  public User findUserByTelephone(String telephone) {
    User user = userRepository.findUserByPhoneNumber(telephone);
    if (user == null) {
      return null;
    }

    List<Role> roles = roleRepository.findRolesByUserId(user.getId());
    if (roles == null || roles.isEmpty()) {
      throw new DisabledException("非法权限");
    }

    List<GrantedAuthority> authorities = new ArrayList<>();
    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("Rloe_" + role.getName())));
    user.setAuthorityList(authorities);

    return user;
  }

  @Override
  @Transactional
  public User addUserByPhone(String telehone) {
    User user = new User();
    user.setPhoneNumber(telehone);
    user.setName(telehone.substring(0, 3) + "***" + telehone.substring(7, telehone.length())); // 15765593421

    Date now = new Date();
    user.setCreateTime(now);
    user.setLastLoginTime(now);
    user.setLastLoginTime(now);
    user = userRepository.save(user);

    Role role = new Role();
    role.setName("USER");
    role.setUserId(user.getId());
    roleRepository.save(role);

    user.setAuthorityList(Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER")));

    return user;
  }

  // 修改指定属性
  @Override
  @Transactional
  public ServiceResult modifyUserProfile(String profile, String value) {

    Long userId = LoginUserUtil.getLoginUserId();

    if (profile == null) {
      return new ServiceResult(false, "修改属性不能为空");
    }

    switch (profile) {
      case "name":
        userRepository.updateUsername(userId, value);
        break;
      case "email":
        userRepository.updateEmail(userId, value);
        break;
      case "password":
        userRepository.updatePassword(userId, this.passwordEncoder.encodePassword(value, userId));
        break;
      default:
        return new ServiceResult(false, "不支持的属性");
    }
    return ServiceResult.success();
  }
}
