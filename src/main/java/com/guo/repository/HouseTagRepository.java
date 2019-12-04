package com.guo.repository;

import com.guo.entity.HouseTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-16 9:16
 */
public interface HouseTagRepository extends CrudRepository<HouseTag, Long> {
  HouseTag findByNameAndHouseId(String name, Long houseId);

  List<HouseTag> findAllByHouseId(Long id);

  List<HouseTag> findAllByHouseIdIn(List<Long> houseIds);
}
