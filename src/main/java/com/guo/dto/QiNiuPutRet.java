package com.guo.dto;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-13 23:30
 */
public final class QiNiuPutRet {

  public String key;
  public String hash;
  public String bucket;
  public int width;
  public int height;

  @Override
  public String toString() {
    return "QiNiuPutRet{" +
            "key='" + key + '\'' +
            ", hash='" + hash + '\'' +
            ", bucket='" + bucket + '\'' +
            ", width=" + width +
            ", height=" + height +
            '}';
  }
}
