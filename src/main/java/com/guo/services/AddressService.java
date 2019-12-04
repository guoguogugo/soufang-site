package com.guo.services;

import com.guo.dto.SubwayDTO;
import com.guo.dto.SubwayStationDTO;
import com.guo.dto.SupportAddressDTO;
import com.guo.entity.BaiduMapLocation;
import com.guo.entity.SupportAddress;


import java.util.List;
import java.util.Map;

/**
 * @desception:地址服务接口
 * @author: mi
 * @date: 2019-08-14 15:43
 */
public interface AddressService {

  /**
   * 获取城市列表
   */
  ServiceMultiResult<SupportAddressDTO> findAllCities();

  /**
   * 根据英文简写获取区域信息
   */
  Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);


  /**
   * 根据城市英文简写获取该城市所有支持的区域信息
   */
  ServiceMultiResult findAllRegionsByCityName(String cityName);

  /**
   * 获取地铁线信息
   */
  ServiceResult<SubwayDTO> findSubway(Long subwayId);


  /**
   * 获取地铁站点信息
   */
  ServiceResult<SubwayStationDTO> findSubwayStation(Long stationId);

  /**
   * 获取该城市所有的地铁线路
   */
  List<SubwayDTO> findAllSubwayByCity(String cityEnName);

  /**
   * 获取各个地铁线路的所有站点
   */
  List<SubwayStationDTO> findAllStationBySubway(Long subwayId);

  /**
   * 根据城市英文简写获取城市详细信息
   */
  ServiceResult<SupportAddressDTO> findCity(String cityEnName);

  /**
   * 根据城市以及具体地位获取百度地图的经纬度
   */
  ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city, String address);

  /**
   * 上传百度LBS数据
   */
  ServiceResult lbsUpload(BaiduMapLocation location, String title, String address,
                          long houseId, int price, int area);

  /**
   * 移除百度LBS数据
   *
   * @param houseId
   * @return
   */
  ServiceResult removeLbs(Long houseId);


}
