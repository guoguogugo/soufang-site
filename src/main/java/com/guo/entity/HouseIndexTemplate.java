package com.guo.entity;

import java.util.Date;
import java.util.List;

/**
 * @desception:索引结构模板
 * @author: mi
 * @date: 2019-09-02 10:15
 */
public class HouseIndexTemplate {

  private Long houseId;

  private String title;

  private Integer price;

  private Integer area;

  private Date createTime;

  private Date lastUpdateTime;

  private String cityEnName;
  ;

  private String regionEnName;

  private Integer direction;

  private Integer distanceToSubway;

  private String subwayLineName;

  private String subwayStationName;

  private List<String> tags;

  private String street;

  private String district;

  private String description;

  private String layoutDesc;

  private String traffic;

  private String roundService;

  private int rentWay;

  private List<HouseSuggest> suggest;

  private BaiduMapLocation location;


  public Long getHouseId() {
    return houseId;
  }

  public void setHouseId(Long houseId) {
    this.houseId = houseId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public Integer getArea() {
    return area;
  }

  public void setArea(Integer area) {
    this.area = area;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(Date lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public String getCityEnName() {
    return cityEnName;
  }

  public void setCityEnName(String cityEnName) {
    this.cityEnName = cityEnName;
  }

  public String getRegionEnName() {
    return regionEnName;
  }

  public void setRegionEnName(String regionEnName) {
    this.regionEnName = regionEnName;
  }

  public Integer getDirection() {
    return direction;
  }

  public void setDirection(Integer direction) {
    this.direction = direction;
  }

  public Integer getDistanceToSubway() {
    return distanceToSubway;
  }

  public void setDistanceToSubway(Integer distanceToSubway) {
    this.distanceToSubway = distanceToSubway;
  }

  public String getSubwayLineName() {
    return subwayLineName;
  }

  public void setSubwayLineName(String subwayLineName) {
    this.subwayLineName = subwayLineName;
  }

  public String getSubwayStationName() {
    return subwayStationName;
  }

  public void setSubwayStationName(String subwayStationName) {
    this.subwayStationName = subwayStationName;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLayoutDesc() {
    return layoutDesc;
  }

  public void setLayoutDesc(String layoutDesc) {
    this.layoutDesc = layoutDesc;
  }

  public String getTraffic() {
    return traffic;
  }

  public void setTraffic(String traffic) {
    this.traffic = traffic;
  }

  public String getRoundService() {
    return roundService;
  }

  public void setRoundService(String roundService) {
    this.roundService = roundService;
  }

  public int getRentWay() {
    return rentWay;
  }

  public void setRentWay(int rentWay) {
    this.rentWay = rentWay;
  }

  public List<HouseSuggest> getSuggest() {
    return suggest;
  }

  public void setSuggest(List<HouseSuggest> suggest) {
    this.suggest = suggest;
  }

  public BaiduMapLocation getLocation() {
    return location;
  }

  public void setLocation(BaiduMapLocation location) {
    this.location = location;
  }
}
