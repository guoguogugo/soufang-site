package com.guo.services;

import com.guo.base.HouseSubscribeStatus;
import com.guo.dto.HouseDTO;
import com.guo.dto.HouseSubscribeDTO;
import com.guo.form.DatatableSearch;
import com.guo.form.HouseForm;
import com.guo.form.MapSearch;
import com.guo.form.RentSearch;
import org.springframework.data.util.Pair;

import java.util.Date;

/**
 * @desception:房屋管理服务接口
 * @author: mi
 * @date: 2019-08-16 9:31
 */
public interface HouseService {
  //房源添加
  ServiceResult<HouseDTO> save(HouseForm houseForm);

  //房源信息查询
  ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody);

  //查询单个房源完整详细信息
  ServiceResult<HouseDTO> findCompleteOne(Long id);

  //房源信息修改
  ServiceResult update(HouseForm houseForm);

  //移除图片
  ServiceResult removePhoto(Long id);

  //更新封面
  ServiceResult updateCover(Long coverId, Long targetId);

  //新增标签
  ServiceResult addTag(Long houseId, String tag);

  //移除标签
  ServiceResult removeHouseTag(Long houseId, String tag);

  //更新房源状态
  ServiceResult updateStatus(Long id, int status);

  //查询房源信息集
  ServiceMultiResult<HouseDTO> query(RentSearch rentSearch);

  //全地图查询
  ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch);

  // 精确范围数据查询
  ServiceMultiResult<HouseDTO> boundMapQuery(MapSearch mapSearch);

  // 加入预约清单
  ServiceResult addSubscribeOrder(Long houseId);

  // 获取对应状态的预约列表
  ServiceMultiResult<org.springframework.data.util.Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(HouseSubscribeStatus status, int start, int size);

  // 预约看房时间
  ServiceResult subscribe(Long houseId, Date orderTime, String telephone, String desc);

  //取消预约
  ServiceResult cancelSubscribe(Long houseId);

  //管理员查询预约信息接口
  ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> findSubscribeList(int start, int size);

  //完成预约
  ServiceResult finishSubscribe(Long houseId);
}
