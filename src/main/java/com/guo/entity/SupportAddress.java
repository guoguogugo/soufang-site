package com.guo.entity;

import javax.persistence.*;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-14 14:47
 */
@Entity
@Table(name = "support_address")
public class SupportAddress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "belong_to")
  private String belongTo;//上级行政单位

  @Column(name = "en_name")
  private String enName;//行政单位英文名缩写

  @Column(name = "cn_name")
  private String cnName;//中文名称

  private String level;//行政级别 市-city 地区-region

  /**
   * 行政级别定义
   */
  public enum Level {
    CITY("city"),
    REGION("region");
    private String value;

    Level(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static Level of(String value) {

      for (Level level : Level.values()) {
        if (level.getValue().equals(value)) {
          return level;
        }
      }

      throw new IllegalArgumentException();
    }

  }

  @Column(name = "baidu_map_lng")
  private double baiduMapLongitude;

  @Column(name = "baidu_map_lat")
  private double baiduMapLatitude;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBelongTo() {
    return belongTo;
  }

  public void setBelongTo(String belongTo) {
    this.belongTo = belongTo;
  }

  public String getEnName() {
    return enName;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }

  public String getCnName() {
    return cnName;
  }

  public void setCnName(String cnName) {
    this.cnName = cnName;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public double getBaiduMapLongitude() {
    return baiduMapLongitude;
  }

  public void setBaiduMapLongitude(double baiduMapLongitude) {
    this.baiduMapLongitude = baiduMapLongitude;
  }

  public double getBaiduMapLatitude() {
    return baiduMapLatitude;
  }

  public void setBaiduMapLatitude(double baiduMapLatitude) {
    this.baiduMapLatitude = baiduMapLatitude;
  }
}
