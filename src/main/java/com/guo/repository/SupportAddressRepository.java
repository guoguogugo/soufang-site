package com.guo.repository;

import com.guo.entity.SupportAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-14 15:24
 */
public interface SupportAddressRepository extends CrudRepository<SupportAddress, Long> {

  /**
   * 获取所有对应行政级别信息
   */
  List<SupportAddress> findAllByLevel(String level);

  SupportAddress findByEnNameAndLevel(String enName, String level);

  SupportAddress findByEnNameAndBelongTo(String enName, String belongTo);

  List<SupportAddress> findAllByLevelAndBelongTo(String level, String belongTo);

}
