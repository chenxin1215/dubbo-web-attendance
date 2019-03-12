package com.cx.login.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cx.login.entity.UserBase;
import com.cx.login.entity.common.LoginStatusEnum;
import com.cx.login.server.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author cx
 * @date 2019/03/11
 */

@RestController
@RequestMapping("user")
@Api(value = "用户控制器")
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("/getData")
    public String getData(String data) {
        return "hello world!" + data + "\n" + userService;
    }

    @ResponseBody
    @RequestMapping(value = "userLogin", method = RequestMethod.POST)
    @ApiOperation(value = "用户登陆", httpMethod = "POST", notes = "根据用户名、密码登陆")
    public Integer userLogin(HttpServletRequest request, UserBase userBase) {

        LoginStatusEnum result = userService.userLogin(userBase);
        System.out.println("登陆结果：" + result);
        if (result == LoginStatusEnum.LOGIN_SUCCESS) {
            UserBase user = userService.queryUserByUserName(userBase.getUserName());
            request.getSession().setAttribute("user", user);
        }
        return result.value();
    }

    @ResponseBody
    @RequestMapping(value = "saveUser", method = RequestMethod.POST)
    @ApiOperation(value = "添加用户", httpMethod = "POST", notes = "添加用户")
    public Long saveUser(HttpServletRequest request, UserBase userBase) {
        Long userId = userService.saveUser(userBase);
        return userId;
    }

    @ResponseBody
    @RequestMapping(value = "getAllUserListPaging", method = RequestMethod.POST)
    @ApiOperation(value = "获取所有用户", httpMethod = "POST", notes = "分页获取所有用户")
    public Object getAllUserListPaging(HttpServletRequest request, HttpServletResponse response,
        @RequestParam(value = "nowPage", defaultValue = "1") int nowPage,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Object result = userService.getAllUserList();
        return result;
    }

}
