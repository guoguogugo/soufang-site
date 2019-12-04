package com.guo.repository;

import com.guo.entity.HousePicture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-16 9:15
 */
public interface HousePictureRepository extends CrudRepository<HousePicture, Long> {

  List<HousePicture> findAllByHouseId(Long id);
}
