package com.guo.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guo.dto.SubwayDTO;
import com.guo.dto.SubwayStationDTO;
import com.guo.dto.SupportAddressDTO;
import com.guo.entity.BaiduMapLocation;
import com.guo.entity.Subway;
import com.guo.entity.SubwayStation;
import com.guo.entity.SupportAddress;
import com.guo.repository.SubwayRepository;
import com.guo.repository.SubwayStationRepository;
import com.guo.repository.SupportAddressRepository;
import com.guo.services.ServiceMultiResult;
import com.guo.services.AddressService;

import com.guo.services.ServiceResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-14 16:06
 */
@Service
public class AddressServiceImpl implements AddressService {

  @Autowired
  private SupportAddressRepository supportAddressRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private SubwayRepository subwayRepository;

  @Autowired
  private SubwayStationRepository subwayStationRepository;

  private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

  private static final String BAIDU_MAP_KEY = "r2tqNL7MBqCqCSMR1xP0kH0QQcDIQsSi";   // baidu-map-web-api 秘钥

  private static final String BAIDU_MAP_GEOCONV_API = "http://api.map.baidu.com/geocoding/v3/?";

  /**
   * POI数据管理接口
   */
  private static final String LBS_CREATE_API = "http://api.map.baidu.com/geodata/v3/poi/create";

  private static final String LBS_QUERY_API = "http://api.map.baidu.com/geodata/v3/poi/list?";

  private static final String LBS_UPDATE_API = "http://api.map.baidu.com/geodata/v3/poi/update";

  private static final String LBS_DELETE_API = "http://api.map.baidu.com/geodata/v3/poi/delete";


  //查询所有城市
  @Override
  public ServiceMultiResult<SupportAddressDTO> findAllCities() {
    List<SupportAddress> addresses = supportAddressRepository.findAllByLevel(SupportAddress.Level.CITY.getValue());

    List<SupportAddressDTO> addressDTOS = new ArrayList<>();

    for (SupportAddress supportAddress : addresses) {

      SupportAddressDTO target = modelMapper.map(supportAddress, SupportAddressDTO.class);

      addressDTOS.add(target);
    }
    return new ServiceMultiResult<>(addressDTOS.size(), addressDTOS);
  }

  //查询城市及区域
  @Override
  public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
    Map<SupportAddress.Level, SupportAddressDTO> result = new HashMap<>();

    SupportAddress city = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
    SupportAddress region = supportAddressRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());

    result.put(SupportAddress.Level.CITY, modelMapper.map(city, SupportAddressDTO.class));
    result.put(SupportAddress.Level.REGION, modelMapper.map(region, SupportAddressDTO.class));

    return result;
  }

  //根据城市查询区域信息
  @Override
  public ServiceMultiResult findAllRegionsByCityName(String cityName) {
    if (cityName == null) {
      return new ServiceMultiResult<>(0, null);
    }
    List<SupportAddressDTO> result = new ArrayList<>();
    List<SupportAddress> regions = supportAddressRepository.findAllByLevelAndBelongTo(SupportAddress.Level.REGION.getValue(), cityName);

    for (SupportAddress region : regions) {
      result.add(modelMapper.map(region, SupportAddressDTO.class));
    }

    return new ServiceMultiResult<>(regions.size(), result);
  }

  //查询地铁信息
  @Override
  public ServiceResult<SubwayDTO> findSubway(Long subwayId) {
    if (subwayId == null) {
      return ServiceResult.notFound();
    }
    Subway subway = subwayRepository.findOne(subwayId);
    if (subway == null) {
      return ServiceResult.notFound();
    }
    return ServiceResult.of(modelMapper.map(subway, SubwayDTO.class));
  }

  //查询地铁站点信息
  @Override
  public ServiceResult<SubwayStationDTO> findSubwayStation(Long stationId) {
    if (stationId == null) {
      return ServiceResult.notFound();
    }
    SubwayStation subwayStation = subwayStationRepository.findOne(stationId);
    if (subwayStation == null) {
      return ServiceResult.notFound();
    }
    return ServiceResult.of(modelMapper.map(subwayStation, SubwayStationDTO.class));
  }

  //根据城市查询该城地铁线路
  @Override
  public List<SubwayDTO> findAllSubwayByCity(String cityEnName) {
    List<SubwayDTO> result = new ArrayList<>();
    List<Subway> subways = subwayRepository.findALLByCityEnName(cityEnName);
    if (subways.isEmpty()) {
      return result;
    }

    subways.forEach(subway -> result.add(modelMapper.map(subway, SubwayDTO.class)));
    return result;
  }

  //查询各个地铁站点信息
  @Override
  public List<SubwayStationDTO> findAllStationBySubway(Long subwayId) {
    List<SubwayStationDTO> result = new ArrayList<>();
    List<SubwayStation> subwayStations = subwayStationRepository.findAllBySubwayId(subwayId);
    if (subwayStations.isEmpty()) {
      return result;
    }

    subwayStations.forEach(subwayStation -> result.add(modelMapper.map(subwayStation, SubwayStationDTO.class)));
//    for(SubwayStation s:subwayStations){
//      SubwayStationDTO dto = modelMapper.map(s, SubwayStationDTO.class);
//      result.add(dto);
//    }
    return result;
  }

  //根据城市英文简写获取城市详细信息
  @Override
  public ServiceResult<SupportAddressDTO> findCity(String cityEnName) {
    if (cityEnName == null) {
      return ServiceResult.notFound();
    }

    SupportAddress supportAddress = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
    if (supportAddress == null) {
      return ServiceResult.notFound();
    }

    SupportAddressDTO addressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);

    return ServiceResult.of(addressDTO);
  }

  //根据城市以及具体地位获取百度地图的经纬度
  @Override
  public ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city, String address) {
    String encodeAddress;
    String encodeCity;

    try {
      encodeAddress = URLEncoder.encode(address, "UTF-8");
      encodeCity = URLEncoder.encode(city, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      logger.error("Error to encode house address", e);
      return new ServiceResult<BaiduMapLocation>(false, "Error to encode hosue address");
    }

    HttpClient httpClient = HttpClients.createDefault();
    StringBuilder sb = new StringBuilder(BAIDU_MAP_GEOCONV_API);
    sb.append("address=").append(encodeAddress).append("&")
            .append("city=").append(encodeCity).append("&")
            .append("output=json&")
            .append("ak=").append(BAIDU_MAP_KEY);

    HttpGet get = new HttpGet(sb.toString());
    try {
      HttpResponse response = httpClient.execute(get);
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        return new ServiceResult<BaiduMapLocation>(false, "Can not get baidu map location");
      }

      String result = EntityUtils.toString(response.getEntity(), "UTF-8");
      JsonNode jsonNode = objectMapper.readTree(result);
      int status = jsonNode.get("status").asInt();
      if (status != 0) {
        return new ServiceResult<BaiduMapLocation>(false, "Error to get map location for status: " + status);
      }
      {
        BaiduMapLocation location = new BaiduMapLocation();
        JsonNode jsonLocation = jsonNode.get("result").get("location");
        location.setLongitude(jsonLocation.get("lng").asDouble());
        location.setLatitude(jsonLocation.get("lat").asDouble());
        return ServiceResult.of(location);
      }

    } catch (IOException e) {
      logger.error("Error to fetch baidumap api", e);
      return new ServiceResult<BaiduMapLocation>(false, "Error to fetch baidumap api");
    }
  }

  private boolean isLbsDataExists(Long houseId) {
    HttpClient httpClient = HttpClients.createDefault();
    StringBuilder sb = new StringBuilder(LBS_QUERY_API);
    sb.append("geotable_id=").append("205491").append("&")
            .append("ak=").append(BAIDU_MAP_KEY).append("&")
            .append("houseId=").append(houseId).append(",").append(houseId);
    HttpGet get = new HttpGet(sb.toString());
    try {
      HttpResponse response = httpClient.execute(get);
      String result = EntityUtils.toString(response.getEntity(), "UTF-8");
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        logger.error("Can not get lbs data for response: " + result);
        return false;
      }

      JsonNode jsonNode = objectMapper.readTree(result);
      int status = jsonNode.get("status").asInt();
      if (status != 0) {
        logger.error("Error to get lbs data for status: " + status);
        return false;
      } else {
        long size = jsonNode.get("size").asLong();
        if (size > 0) {
          return true;
        } else {
          return false;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public ServiceResult lbsUpload(BaiduMapLocation location, String title, String address, long houseId, int price, int area) {
    HttpClient httpClient = HttpClients.createDefault();
    List<NameValuePair> nvps = new ArrayList<>();
    nvps.add(new BasicNameValuePair("latitude", String.valueOf(location.getLatitude())));
    nvps.add(new BasicNameValuePair("longitude", String.valueOf(location.getLongitude())));
    nvps.add(new BasicNameValuePair("coord_type", "3")); // 百度坐标系
    nvps.add(new BasicNameValuePair("geotable_id", "205491"));
    nvps.add(new BasicNameValuePair("ak", BAIDU_MAP_KEY));
    nvps.add(new BasicNameValuePair("houseId", String.valueOf(houseId)));
    nvps.add(new BasicNameValuePair("price", String.valueOf(price)));
    nvps.add(new BasicNameValuePair("area", String.valueOf(area)));
    nvps.add(new BasicNameValuePair("title", title));
    nvps.add(new BasicNameValuePair("address", address));

    HttpPost post;
    if (isLbsDataExists(houseId)) {
      post = new HttpPost(LBS_UPDATE_API);
    } else {
      post = new HttpPost(LBS_CREATE_API);
    }

    try {
      post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
      HttpResponse response = httpClient.execute(post);
      String result = EntityUtils.toString(response.getEntity(), "UTF-8");
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        logger.error("Can not upload lbs data for response: " + result);
        return new ServiceResult(false, "Can not upload baidu lbs data");
      } else {
        JsonNode jsonNode = objectMapper.readTree(result);
        int status = jsonNode.get("status").asInt();
        if (status != 0) {
          String message = jsonNode.get("message").asText();
          logger.error("Error to upload lbs data for status: {}, and message: {}", status, message);
          return new ServiceResult(false, "Error to upload lbs data");
        } else {
          return ServiceResult.success();
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ServiceResult(false);
  }

  @Override
  public ServiceResult removeLbs(Long houseId) {
    HttpClient httpClient = HttpClients.createDefault();
    List<NameValuePair> nvps = new ArrayList<>();
    nvps.add(new BasicNameValuePair("geotable_id", "205491"));
    nvps.add(new BasicNameValuePair("ak", BAIDU_MAP_KEY));
    nvps.add(new BasicNameValuePair("houseId", String.valueOf(houseId)));

    HttpPost delete = new HttpPost(LBS_DELETE_API);
    try {
      delete.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
      HttpResponse response = httpClient.execute(delete);
      String result = EntityUtils.toString(response.getEntity(), "UTF-8");
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        logger.error("Error to delete lbs data for response: " + result);
        return new ServiceResult(false);
      }

      JsonNode jsonNode = objectMapper.readTree(result);
      int status = jsonNode.get("status").asInt();
      if (status != 0) {
        String message = jsonNode.get("message").asText();
        logger.error("Error to delete lbs data for message: " + message);
        return new ServiceResult(false, "Error to delete lbs data for: " + message);
      }
      return ServiceResult.success();
    } catch (IOException e) {
      logger.error("Error to delete lbs data.", e);
      return new ServiceResult(false);
    }
  }
}
