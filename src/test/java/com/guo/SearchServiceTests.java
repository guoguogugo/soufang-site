package com.guo;

import com.guo.form.RentSearch;
import com.guo.services.ISearchService;
import com.guo.services.ServiceMultiResult;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @desception:
 * @author: mi
 * @date: 2019-09-02 16:42
 */
public class SearchServiceTests extends SoufangSiteApplicationTests {

  @Autowired
  private ISearchService searchService;

  @Test
  public void testIndex() {
    Long targetHouseId = 15L;
    /*boolean success =  searchService.index(targetHouseId);
    Assert.assertTrue(success);*/
    searchService.index(targetHouseId);
  }

  @Test
  public void testRemove() {
    Long targetHouseId = 15L;

    searchService.remove(targetHouseId);
  }

  @Test
  public void testQuery() {
    RentSearch rentSearch = new RentSearch();
    rentSearch.setCityEnName("bj");
    rentSearch.setStart(0);
    rentSearch.setSize(10);
    // rentSearch.setKeywords("国贸");
    ServiceMultiResult<Long> serviceResult = searchService.query(rentSearch);
    Assert.assertTrue(serviceResult.getTotal() > 0);
  }
}
