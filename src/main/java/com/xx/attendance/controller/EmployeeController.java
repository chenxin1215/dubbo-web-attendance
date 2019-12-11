package com.xx.attendance.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xx.attendance.common.PublicData;
import com.xx.attendance.dto.requset.UpdateSelfInfoRequest;
import com.xx.attendance.dto.requset.employee.EmployeeIdRequest;
import com.xx.attendance.dto.response.EmployeeDetail;
import com.xx.attendance.dto.view.SimpleView;
import com.xx.attendance.dto.view.StringView;
import com.xx.attendance.entity.EmployeeInfo;
import com.xx.attendance.server.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

// localhost:8080/attendance-system/employee/queryAllEmployeeList

/**
 * 〈一句话功能简述〉<br>
 * 〈员工控制器-通用〉
 *
 * @author xx
 * @create 2019/11/25
 * @since 1.0.0
 */
@RestController
@RequestMapping("employee")
public class EmployeeController {

    @Reference
    private EmployeeService employeeService;

    /**
     * 设置员工id
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "setEmployeeId", method = RequestMethod.POST)
    @ResponseBody
    public StringView setEmployeeId(EmployeeIdRequest request) {
        StringView view = new StringView();
        if (request.getEmployeeId() != null) {
            PublicData.employeesId = request.getEmployeeId();
        }
        view.success();
        return view;
    }

    /**
     * 查询员工详情
     *
     * @return
     */
    @RequestMapping(value = "queryEmployeeDetailById", method = RequestMethod.POST)
    @ResponseBody
    public SimpleView queryEmployeeDetailById() {
        SimpleView view = new SimpleView();

        Long employeeId = PublicData.employeesId;
        if (employeeId == null) {
            view.fail("员工id为空！");
        } else {
            EmployeeDetail employeeDetail = employeeService.getEmployeeById(employeeId);
            view.success(employeeDetail);
        }

        return view;
    }

    /**
     * 修改自身基本信息
     *
     */
    @RequestMapping(value = "updateSelfBaseInfo", method = RequestMethod.POST)
    @ResponseBody
    public StringView updateSelfBaseInfo(UpdateSelfInfoRequest updateUpdate) {
        StringView view = new StringView();
        Assert.notNull(updateUpdate.getEmployeeId(), "员工id不能为空！");

        if (!PublicData.loginUser.getEmployeeId().equals(updateUpdate.getEmployeeId())) {
            System.out.println("PublicData.loginUser.getEmployeeId() = " + PublicData.loginUser.getEmployeeId());
            System.out.println("updateUpdate.getEmployeeId() = " + updateUpdate.getEmployeeId());
            view.fail("只能修改自己的信息");
        } else {
            EmployeeInfo employeeInfo = new EmployeeInfo();
            BeanUtils.copyProperties(updateUpdate, employeeInfo);
            employeeService.updateById(employeeInfo);
            view.success("修改成功");
        }
        return view;
    }

}
