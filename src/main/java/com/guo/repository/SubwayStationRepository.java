package com.guo.repository;

import com.guo.entity.SubwayStation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-15 9:49
 */
public interface SubwayStationRepository extends CrudRepository<SubwayStation, Long> {
  List<SubwayStation> findAllBySubwayId(Long subwayId);
}
