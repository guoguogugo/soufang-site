package com.guo.services;

import java.util.List;

/**
 * @desception:通用多结果Service返回结构
 * @author: mi
 * @date: 2019-08-14 16:47
 */
public class ServiceMultiResult<T> {
  private long total;
  private List<T> result;

  public ServiceMultiResult(long total, List<T> result) {
    this.total = total;
    this.result = result;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public List<T> getResult() {
    return result;
  }

  public void setResult(List<T> result) {
    this.result = result;
  }

  public int getResultSize() {
    if (this.result == null) {
      return 0;
    }
    return this.result.size();
  }
}

