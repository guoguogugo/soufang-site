package com.guo.controller;

import com.guo.base.ApiResponse;
import com.guo.base.HouseSubscribeStatus;
import com.guo.base.LoginUserUtil;
import com.guo.dto.HouseDTO;
import com.guo.dto.HouseSubscribeDTO;
import com.guo.services.HouseService;
import com.guo.services.IUserService;
import com.guo.services.ServiceMultiResult;
import com.guo.services.ServiceResult;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-08 23:50
 */
@Controller
public class UserController {

  @Autowired
  private IUserService userService;

  @Autowired
  private HouseService houseService;

  @GetMapping("user/login")
  public String userLoginPage() {

    return "user/login";
  }

  @GetMapping("user/center")
  public String userCenterPage() {

    return "user/center";
  }

  @PostMapping(value = "api/user/info")
  @ResponseBody
  public ApiResponse updateUserInfo(@RequestParam(value = "profile") String profile, @RequestParam(value = "value") String value) {

    if (value.isEmpty()) {
      return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
    }

    if ("email".equals(profile) && !LoginUserUtil.checkEmail(value)) {
      return ApiResponse.ofMessage(HttpStatus.SC_BAD_REQUEST, "邮箱格式不支持");
    }

    ServiceResult result = userService.modifyUserProfile(profile, value);
    if (result.isSuccess()) {
      return ApiResponse.ofSuccess("修改成功 ");
    } else {
      return ApiResponse.ofMessage(HttpStatus.SC_BAD_REQUEST, result.getMessage());
    }
  }

  @PostMapping(value = "api/user/house/subscribe")
  @ResponseBody
  public ApiResponse subscribeHouse(@RequestParam(value = "house_id") Long houseId) {
    ServiceResult result = houseService.addSubscribeOrder(houseId);
    if (result.isSuccess()) {
      return ApiResponse.ofSuccess("");
    } else {
      return ApiResponse.ofMessage(HttpStatus.SC_BAD_REQUEST, result.getMessage());
    }
  }

  @GetMapping(value = "api/user/house/subscribe/list")
  @ResponseBody
  public ApiResponse subscribeList(
          @RequestParam(value = "start", defaultValue = "0") int start,
          @RequestParam(value = "size", defaultValue = "3") int size,
          @RequestParam(value = "status") int status) {

    ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> result = houseService.querySubscribeList(HouseSubscribeStatus.of(status), start, size);

    if (result.getResultSize() == 0) {
      return ApiResponse.ofSuccess(result.getResult());
    }

    ApiResponse response = ApiResponse.ofSuccess(result.getResult());
    response.setMore(result.getTotal() > (start + size));
    return response;
  }

  @PostMapping(value = "api/user/house/subscribe/date")
  @ResponseBody
  public ApiResponse subscribeDate(
          @RequestParam(value = "houseId") Long houseId,
          @RequestParam(value = "orderTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderTime,
          @RequestParam(value = "desc", required = false) String desc,
          @RequestParam(value = "telephone") String telephone
  ) {
    if (orderTime == null) {
      return ApiResponse.ofMessage(HttpStatus.SC_BAD_REQUEST, "请选择预约时间");
    }

    if (!LoginUserUtil.checkTelephone(telephone)) {
      return ApiResponse.ofMessage(HttpStatus.SC_BAD_REQUEST, "手机格式不正确");
    }

    ServiceResult serviceResult = houseService.subscribe(houseId, orderTime, telephone, desc);
    if (serviceResult.isSuccess()) {
      return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    } else {
      return ApiResponse.ofMessage(HttpStatus.SC_BAD_REQUEST, serviceResult.getMessage());
    }
  }

  @DeleteMapping(value = "api/user/house/subscribe")
  @ResponseBody
  public ApiResponse cancelSubscribe(@RequestParam(value = "houseId") Long houseId) {
    ServiceResult serviceResult = houseService.cancelSubscribe(houseId);
    if (serviceResult.isSuccess()) {
      return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    } else {
      return ApiResponse.ofMessage(HttpStatus.SC_BAD_REQUEST, serviceResult.getMessage());
    }
  }
}
