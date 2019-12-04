package com.guo;

import com.guo.entity.BaiduMapLocation;
import com.guo.services.AddressService;
import com.guo.services.ServiceResult;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @desception:
 * @author: mi
 * @date: 2019-09-11 14:15
 */
public class AddressServerTest extends SoufangSiteApplicationTests {

  @Autowired
  private AddressService addressService;

  @Test
  public void testGetMapLocation() {
    String city = "北京";
    String address = "北京市昌平区北店嘉园南区4号楼4单元";
    ServiceResult<BaiduMapLocation> serviceResult = addressService.getBaiduMapLocation(city, address);

    Assert.assertTrue(serviceResult.isSuccess());

    Assert.assertTrue(serviceResult.getResult().getLongitude() > 0);
    Assert.assertTrue(serviceResult.getResult().getLatitude() > 0);

  }
}
