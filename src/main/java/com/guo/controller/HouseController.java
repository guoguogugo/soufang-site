package com.guo.controller;

import com.guo.base.ApiResponse;
import com.guo.base.RentValueBlock;
import com.guo.dto.*;
import com.guo.entity.SupportAddress;
import com.guo.form.MapSearch;
import com.guo.form.RentSearch;
import com.guo.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-14 14:42
 */
@Controller
public class HouseController {

  @Autowired
  private AddressService addressService;

  @Autowired
  private HouseService houseService;

  @Autowired
  private IUserService userServices;

  @Autowired
  private ISearchService searchService;

  //自动补全接口
  @GetMapping("rent/house/autocomplete")
  @ResponseBody
  public ApiResponse autocomplete(@RequestParam(value = "prefix") String prefix) {
    if (prefix.isEmpty()) {
      return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
    }
    ServiceResult<List<String>> result = this.searchService.suggest(prefix);

    /*List<String> result = new ArrayList<>();
    result.add("我要疯了");
    result.add("我真的要疯了");*/

    return ApiResponse.ofSuccess(result.getResult());
  }

  //获取支持城市列表
  @GetMapping("address/support/cities")
  @ResponseBody
  public ApiResponse getSupportCities() {
    ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
    if (result.getResultSize() == 0) {
      return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUND);
    }
    return ApiResponse.ofSuccess(result.getResult());
  }

  //获取对应城市区域列表
  @GetMapping("address/support/regions")
  @ResponseBody
  private ApiResponse getSupportRegions(@RequestParam(name = "city_name") String cityEnName) {

    ServiceMultiResult<SupportAddressDTO> addressResult = addressService.findAllRegionsByCityName(cityEnName);
    if (addressResult.getResult() == null || addressResult.getTotal() < 1) {

      return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
    }
    return ApiResponse.ofSuccess(addressResult.getResult());
  }

  //获取各个城市的地铁线路
  @GetMapping("address/support/subway/line")
  @ResponseBody
  private ApiResponse getCitySubway(@RequestParam(name = "city_name") String cityEnName) {

    List<SubwayDTO> subways = addressService.findAllSubwayByCity(cityEnName);
    if (subways.isEmpty()) {
      return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
    }
    return ApiResponse.ofSuccess(subways);
  }

  /**
   * 获取对应地铁线路所支持的地铁站点
   */
  @GetMapping("/address/support/subway/station")
  @ResponseBody
  public ApiResponse getSupportSubwayStation(@RequestParam(name = "subway_id") Long subwayId) {
    List<SubwayStationDTO> stationDTOS = addressService.findAllStationBySubway(subwayId);
    if (stationDTOS.isEmpty()) {
      return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
    }
    return ApiResponse.ofSuccess(stationDTOS);
  }

  /** 注解@ModelAttribute是一个非常常用的注解，其功能主要在两方面：
   1.运用在参数上，会将客户端传递过来的参数按名称注入到指定对象中，并且会将这个对象自动加入ModelMap中，便于View层使用；
   2.运用在方法上，会在每一个@RequestMapping标注的方法前执行，如果有返回值，则自动将该返回值加入到ModelMap中；
   @RedirectAttributes是Spring mvc 3.1版本之后出来的一个功能，专门用于重定向之后还能带参数跳转的     */

  /**
   * 租房信息查询
   *
   * @return
   */
  @GetMapping("rent/house")
  public String rentHousePage(@ModelAttribute RentSearch rentSearch, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

    if (rentSearch.getCityEnName() == null) {
      String cityEnNameSession = (String) session.getAttribute("cityEnName");
      if (cityEnNameSession == null) {
        redirectAttributes.addAttribute("msg", "must_chose_city");
        return "redirect:/index";
      } else {
        rentSearch.setCityEnName(cityEnNameSession);
      }
    } else {
      session.setAttribute("cityEnName", rentSearch.getCityEnName());
    }

    //选择城市
    ServiceResult<SupportAddressDTO> city = addressService.findCity(rentSearch.getCityEnName());
    if (!city.isSuccess()) {
      redirectAttributes.addAttribute("msg", "must_chose_city");
      return "redirect:/index";
    }

    model.addAttribute("currentCity", city.getResult());

    // 选择城市区域
    ServiceMultiResult<SupportAddressDTO> addressResult = addressService.findAllRegionsByCityName(rentSearch.getCityEnName());
    if (addressResult.getResult() == null || addressResult.getTotal() < 1) {
      redirectAttributes.addAttribute("msg", "must_chose_city");
      return "redirect:/index";
    }

    ServiceMultiResult<HouseDTO> serviceMultiResult = houseService.query(rentSearch);

    model.addAttribute("total", serviceMultiResult.getTotal());
    model.addAttribute("houses", serviceMultiResult.getResult());
   /* model.addAttribute("total", 0);
    model.addAttribute("houses", new ArrayList<>());*/

    if (rentSearch.getRegionEnName() == null) {
      rentSearch.setRegionEnName("*");
    }

    model.addAttribute("searchBody", rentSearch);
    model.addAttribute("regions", addressResult.getResult());

    model.addAttribute("priceBlocks", RentValueBlock.PRICE_BLOCK);
    model.addAttribute("areaBlocks", RentValueBlock.AREA_BLOCK);

    model.addAttribute("currentPriceBlock", RentValueBlock.matchPrice(rentSearch.getPriceBlock()));
    model.addAttribute("currentAreaBlock", RentValueBlock.matchArea(rentSearch.getAreaBlock()));

    return "rent-list";

  }

  /**
   * 租房信息单个房源信息详情
   *
   * @return
   */
  @GetMapping("rent/house/show/{id}")
  public String showHouseDetail(@PathVariable(value = "id") Long houseId, Model model) {

    if (houseId <= 0) {
      return "404";
    }

    ServiceResult<HouseDTO> serviceResult = houseService.findCompleteOne(houseId);
    if (!serviceResult.isSuccess()) {
      return "404";
    }

    HouseDTO houseDTO = serviceResult.getResult();

    Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService.findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());

    SupportAddressDTO city = addressMap.get(SupportAddress.Level.CITY);
    SupportAddressDTO region = addressMap.get(SupportAddress.Level.REGION);

    model.addAttribute("city", city);
    model.addAttribute("region", region);

    ServiceResult<UserDTO> userResult = userServices.findById(houseDTO.getAdminId());

    model.addAttribute("agent", userResult.getResult());
    model.addAttribute("house", houseDTO);

    ServiceResult<Long> aggResult = searchService.aggregateDistrictHouse(city.getEnName(), region.getEnName(), houseDTO.getDistrict());
    model.addAttribute("houseCountInDistrict", aggResult.getResult());
    // model.addAttribute("houseCountInDistrict",0);    // 该初默认设置为0  写死

    return "house-detail";
  }

  @GetMapping("rent/house/map")
  public String rentMapPage(@RequestParam(value = "cityEnName") String cityEnName, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

    ServiceResult<SupportAddressDTO> city = addressService.findCity(cityEnName);
    if (!city.isSuccess()) {
      redirectAttributes.addAttribute("msg", "must_chose_city");
      return "redirect/index";
    } else {
      session.setAttribute("cityName", cityEnName);
      model.addAttribute("city", city.getResult());
    }

    ServiceMultiResult<SupportAddressDTO> regions = addressService.findAllRegionsByCityName(cityEnName);

    ServiceMultiResult<HouseBucketDTO> serviceResult = searchService.mapAggregate(cityEnName);

    model.addAttribute("aggData", serviceResult.getResult());
    model.addAttribute("total", serviceResult.getTotal());
    model.addAttribute("regions", regions.getResult());

    return "rent-map";
  }

  @GetMapping("rent/house/map/houses")
  @ResponseBody
  @CrossOrigin
  public ApiResponse rentMapHouse(@ModelAttribute MapSearch mapSearch) {
    if (mapSearch.getCityEnName() == null) {
      return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须选择城市");
    }

    ServiceMultiResult<HouseDTO> serviceMultiResult;
    if (mapSearch.getLevel() < 13) {
      serviceMultiResult = houseService.wholeMapQuery(mapSearch);
    } else {
      // 小地图查询必须要传递地图边界参数
      serviceMultiResult = houseService.boundMapQuery(mapSearch);
    }

    ApiResponse response = ApiResponse.ofSuccess(serviceMultiResult.getResult());
    response.setMore(serviceMultiResult.getTotal() > (mapSearch.getStart() + mapSearch.getSize()));
    return response;
  }
}
