package com.xx.attendance.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xx.attendance.common.PublicData;
import com.xx.attendance.dto.requset.LoginRequest;
import com.xx.attendance.dto.view.SimpleView;
import com.xx.attendance.dto.view.StringView;
import com.xx.attendance.entity.EmployeeInfo;
import com.xx.attendance.server.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 〈一句话功能简述〉<br>
 * 〈登陆控制器〉
 *
 * @author xx
 * @create 2019/11/24
 * @since 1.0.0
 */
@RestController
@RequestMapping("login")
@Api(value = "登陆控制器")
public class LoginController {

    @Reference
    private EmployeeService employeeService;

    @RequestMapping("/getData")
    public String getData(String data) {
        return "hello world!" + data + "\n" + employeeService;
    }

    @ResponseBody
    @RequestMapping(value = "userLogin", method = RequestMethod.POST)
    @ApiOperation(value = "用户登陆", httpMethod = "POST", notes = "根据工号、密码登陆")
    public StringView userLogin(LoginRequest loginRequest) {
        StringView view = new StringView();

        System.out.println(loginRequest.getEmployeeSn() + "  " + loginRequest.getPassword());
        EmployeeInfo employeeInfo = employeeService.getEmployeeBySn(loginRequest.getEmployeeSn());
        if (employeeInfo == null) {
            view.success(StringView.NOTIFY, "账号不存在！", null);
        } else if (!employeeInfo.getPassword().equals(loginRequest.getPassword())) {
            view.success(StringView.NOTIFY, "密码错误！", null);
        } else {
            PublicData.loginUser = employeeInfo;
            System.out.println("登陆用户：" + PublicData.loginUser.getEmployeeName());
            view.success("登陆成功！");
        }
        return view;
    }

    @RequestMapping(value = "getUser", method = RequestMethod.POST)
    @ResponseBody
    public SimpleView getUser() {
        SimpleView view = new SimpleView();
        if (PublicData.loginUser != null) {
            view.setRspData(PublicData.loginUser);
        }
        view.success();
        return view;
    }

    @RequestMapping(value = "outLogin", method = RequestMethod.POST)
    @ResponseBody
    public SimpleView outLogin() {
        SimpleView view = new SimpleView();
        PublicData.loginUser = null;
        view.success("注销成功");
        return view;
    }

}