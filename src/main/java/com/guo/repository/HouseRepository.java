package com.guo.repository;

import com.guo.entity.House;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sun.awt.SunHints;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-16 9:10
 */
//public interface HouseRepository extends CrudRepository<House,Long> {
//}

/*public interface HouseRepository extends PagingAndSortingRepository<House,Long>{
}*/
public interface HouseRepository extends PagingAndSortingRepository<House, Long>, JpaSpecificationExecutor<House> {

  @Modifying
  @Query("update House as house set house.cover = :cover where house.id = :id")
  public void updateCover(@Param(value = "id") Long id, @Param(value = "cover") String cover);

  @Modifying
  @Query("update House as house set house.status = :status where house.id = :id")
  public void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);

  @Modifying
  @Query("update House as house set house.watchTimes = house.watchTimes + 1 where house.id = :id")
  void updateWatchTimes(@Param(value = "id") Long houseId);
}
