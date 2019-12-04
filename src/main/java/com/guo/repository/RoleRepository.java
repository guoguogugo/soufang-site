package com.guo.repository;

import com.guo.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @desception:角色数据Dao
 * @author: mi
 * @date: 2019-08-08 11:30
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

  List<Role> findRolesByUserId(Long userId);
}
