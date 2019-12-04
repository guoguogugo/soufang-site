package com.guo.services;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

/**
 * @desception:七牛云服务
 * @author: mi
 * @date: 2019-08-13 14:37
 */
public interface QiNiuService {

  Response uploadFile(File file) throws QiniuException;

  Response uploadFile(InputStream inputStream) throws QiniuException;

  Response delete(String key) throws QiniuException;
}
