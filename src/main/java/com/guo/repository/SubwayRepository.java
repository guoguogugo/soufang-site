package com.guo.repository;

import com.guo.entity.Subway;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-15 9:45
 */
public interface SubwayRepository extends CrudRepository<Subway, Long> {

  List<Subway> findALLByCityEnName(String cityEnName);
}
