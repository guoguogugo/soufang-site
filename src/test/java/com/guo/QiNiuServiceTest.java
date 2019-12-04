package com.guo;

import com.guo.services.QiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-13 16:38
 */
public class QiNiuServiceTest extends SoufangSiteApplicationTests {

  @Autowired
  private QiNiuService qiNiuService;

  @Test
  public void fileUploadTest() {

    String fileName = "E:/WorkSpace/MyIdeaWorkingSpace/soufang-site/tmp/53003270_p0.jpg";
    File file = new File(fileName);

    Assert.assertTrue(file.exists());

    try {
      Response response = qiNiuService.uploadFile(file);
      Assert.assertTrue(response.isOK());
    } catch (QiniuException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void deleteFileTest() {
    String key = "FpmEQycVRj_naFSJGMgXIX73fFBw";
    try {
      Response response = qiNiuService.delete(key);
      Assert.assertTrue(response.isOK());
    } catch (QiniuException e) {
      e.printStackTrace();
    }
  }
}
