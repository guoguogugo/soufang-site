package com.guo.repository;

import com.guo.entity.HouseSubscribe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-19 17:22
 */
public interface HouseSubscribeRespository extends PagingAndSortingRepository<HouseSubscribe, Long> {

  HouseSubscribe findByHouseIdAndUserId(Long houseId, Long loginUserId); // 根据房屋id和用户id进行查找

  Page<HouseSubscribe> findAllByUserIdAndStatus(Long userId, int status, Pageable pageable); // 根据用户Id和状态进行分页查找

  Page<HouseSubscribe> findAllByAdminIdAndStatus(Long adminId, int status, Pageable pageable); // 根据管理员id和状态进行查找

  HouseSubscribe findByHouseIdAndAdminId(Long houseId, Long adminId); // 根据用户id和管理员id进行查找

  @Modifying
  @Query("update HouseSubscribe as subscribe set subscribe.status = :status where subscribe.id = :id")
  void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);    // 修改状态

}
