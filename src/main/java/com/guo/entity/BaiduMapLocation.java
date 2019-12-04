package com.guo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @desception:百度位置信息
 * @author: mi
 * @date: 2019-08-15 10:07
 */
public class BaiduMapLocation {

  // 经度
  @JsonProperty("lon")
  private double longitude;

  // 纬度
  @JsonProperty("lat")
  private double latitude;

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }
}
