package com.guo.repository;

import com.guo.entity.HouseDetail;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-16 9:12
 */
public interface HouseDetailRepository extends CrudRepository<HouseDetail, Long> {
  HouseDetail findByHouseId(Long houseId);

  List<HouseDetail> findAllByHouseIdIn(List<Long> houseIds);

}
