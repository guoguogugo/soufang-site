package com.guo.dto;

/**
 * @desception:
 * @author: mi
 * @date: 2019-09-10 14:00
 */
public class HouseBucketDTO {

  // 聚合bucket的key
  private String key;

  // 聚合结果值
  private Long count;

  public HouseBucketDTO(String key, Long count) {
    this.key = key;
    this.count = count;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }
}
