package com.guo.services;

/**
 * @desception:验证码服务
 * @author: mi
 * @date: 2019-09-16 15:47
 */
public interface SmsService {
  // 验证码发送到手机并缓存10分钟及请求间隔为1分钟
  ServiceResult<String> sendSms(String telephone);

  // 获取缓存中的验证码
  String getSms(String telephone);

  // 移除缓存中的验证码
  void removeSms(String telephone);
}
