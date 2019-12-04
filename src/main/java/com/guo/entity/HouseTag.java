package com.guo.entity;

import javax.persistence.*;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-15 23:34
 */
@Entity
@Table(name = "house_tag")
public class HouseTag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "house_id")
  private Long houseId;

  private String name;

  public HouseTag(Long houseId, String name) {
    this.houseId = houseId;
    this.name = name;
  }

  public HouseTag() {

  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getHouseId() {
    return houseId;
  }

  public void setHouseId(Long houseId) {
    this.houseId = houseId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
