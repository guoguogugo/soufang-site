package com.guo.controller;

import com.guo.base.ApiResponse;
import com.guo.base.LoginUserUtil;
import com.guo.services.ServiceMultiResult;
import com.guo.services.ServiceResult;
import com.guo.services.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

  @Autowired
  private SmsService smsService;

  @GetMapping(value = {"/", "/index"})
  public String index(Model model) {
    //  model.addAttribute("name","果果果");
    return "index";
  }

  @GetMapping("/get")
  @ResponseBody
  public ApiResponse get() {

    return ApiResponse.ofMessage(200, "成功");
  }

  @GetMapping("/404")
  public String notFoundPage() {
    return "404";
  }

  @GetMapping("/403")
  public String accessError() {
    return "403";
  }

  @GetMapping("/500")
  public String internalError() {
    return "500";
  }

  @GetMapping("/logout/page")
  public String logoutPage() {
    return "logout";
  }

  @GetMapping(value = "sms/code")
  @ResponseBody
  public ApiResponse smsCode(@RequestParam("telephone") String telePhone) {
    if (!LoginUserUtil.checkTelephone(telePhone)) {
      return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "请输入正确的手机号");
    }

    ServiceResult<String> result = smsService.sendSms(telePhone);
    if (result.isSuccess()) {
      return ApiResponse.ofSuccess("哈哈哈,恭喜你");
    } else {
      return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
    }
  }
}
