package com.guo.services;

import com.guo.dto.HouseBucketDTO;
import com.guo.form.MapSearch;
import com.guo.form.RentSearch;

import java.util.List;

/**
 * @desception: 检索接口
 * @author: mi
 * @date: 2019-08-20 0:17
 */
public interface ISearchService {

  //索引目标房源
//  public boolean index(Long houseId);
  public void index(Long houseId);

  //移除目标房源
  public void remove(Long houseId);

  //查询房源接口
  ServiceMultiResult<Long> query(RentSearch rentSearch);

  //获取补全建议关键词
  ServiceResult<List<String>> suggest(String prefix);

  //聚合特定小区的房间数
  ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district);


  //聚合城市数
  ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName);


  //城市级别查询
  ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy,
                                    String orderDirection, int start, int size);

  //精确范围数据查询
  ServiceMultiResult<Long> mapQuery(MapSearch mapSearch);
}
