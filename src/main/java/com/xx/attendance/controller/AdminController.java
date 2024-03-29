package com.xx.attendance.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xx.attendance.common.PublicData;
import com.xx.attendance.dto.requset.approve.ApprovalIdRequest;
import com.xx.attendance.dto.requset.approve.ApprovalRequest;
import com.xx.attendance.dto.requset.approve.QueryApproveParam;
import com.xx.attendance.dto.requset.attendance.QueryAttendanceInfoParam;
import com.xx.attendance.dto.requset.employee.EmployeeIdRequest;
import com.xx.attendance.dto.requset.employee.InsertEmployeeRequest;
import com.xx.attendance.dto.requset.employee.QueryEmployeeListParam;
import com.xx.attendance.dto.response.EmployeeDetail;
import com.xx.attendance.dto.response.LogInfoDetail;
import com.xx.attendance.dto.response.approve.ApproveInfoData;
import com.xx.attendance.dto.view.ListBaseView;
import com.xx.attendance.dto.view.SimpleView;
import com.xx.attendance.dto.view.StringView;
import com.xx.attendance.entity.AttendanceInfo;
import com.xx.attendance.entity.ConfigureInfo;
import com.xx.attendance.entity.EmployeeInfo;
import com.xx.attendance.server.*;
import com.xx.attendance.utils.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈管理员控制器〉
 *
 * @author xx
 * @create 2019/11/25
 * @since 1.0.0
 */
@RequestMapping("admin")
@RestController
public class AdminController {

    @Reference
    private EmployeeService employeeService;

    @Reference
    private ApproveService approveService;

    @Reference
    private AttendanceService attendanceService;

    @Reference
    private ConfigureService configureService;

    @Reference
    private LogInfoService LogInfoService;

    /**
     * 添加员工
     * 
     * @param request
     */
    @RequestMapping(value = "addEmployee", method = RequestMethod.POST)
    @ResponseBody
    public StringView addEmployee(InsertEmployeeRequest request) {
        StringView view = new StringView();
        employeeService.insertEmployee(request);
        view.success("添加成功");
        return view;
    }

    /**
     * 修改员工信息
     * 
     * @param request
     */
    @RequestMapping(value = "updateEmployee", method = RequestMethod.POST)
    @ResponseBody
    public StringView updateEmployee(EmployeeInfo request) {
        StringView view = new StringView();
        employeeService.updateById(request);
        view.success("修改成功");
        return view;
    }

    /**
     * 员工列表查询
     * 
     * @param request
     */
    @RequestMapping(value = "queryEmployeeListByParam", method = RequestMethod.POST)
    @ResponseBody
    public ListBaseView<EmployeeDetail> queryEmployeeListByParam(QueryEmployeeListParam request) {
        ListBaseView<EmployeeDetail> view = new ListBaseView<>();

        int count = employeeService.queryEmployeeListByParamCount(request);
        view.setTotal(count);

        if (count > 0) {
            List<EmployeeDetail> resultList = employeeService.queryEmployeeListByParam(request);
            view.setRspData(resultList);
        }

        view.success();
        return view;
    }

    /**
     * 通过审批
     * 
     * @param request
     */
    @RequestMapping(value = "passApproval", method = RequestMethod.POST)
    @ResponseBody
    public StringView passApproval(ApprovalIdRequest idRequest, HttpServletRequest request) {
        StringView view = new StringView();
        EmployeeInfo user = (EmployeeInfo)request.getSession().getAttribute("user");
        approveService.passApproval(idRequest.getApprovalId(), user.getEmployeeId());
        view.success("通过审批！");
        return view;
    }

    /**
     * 拒绝审批
     *
     * @param request
     */
    @RequestMapping(value = "refusedApproval", method = RequestMethod.POST)
    @ResponseBody
    public StringView refusedApproval(ApprovalIdRequest idRequest, HttpServletRequest request) {
        StringView view = new StringView();
        EmployeeInfo user = (EmployeeInfo)request.getSession().getAttribute("user");
        approveService.refusedApproval(idRequest.getApprovalId(), user.getEmployeeId());
        view.success("拒绝审批！");
        return view;
    }

    /**
     * 审批列表查询
     *
     * @param request
     */
    @RequestMapping(value = "queryApprovalListByParam", method = RequestMethod.POST)
    @ResponseBody
    public ListBaseView<ApproveInfoData> queryApprovalListByParam(ApprovalRequest request) {
        ListBaseView<ApproveInfoData> view = new ListBaseView<>();
        QueryApproveParam param = new QueryApproveParam();
        BeanUtils.copyProperties(request, param);
        if (request.getStartDateStr() != null) {
            param.setStartDate(
                DateUtil.getInitStart(DateUtil.stringToDate(request.getStartDateStr(), DateUtil.DATE_BASE)));
        }
        if (request.getEndDateStr() != null) {
            param.setEndDate(DateUtil.getInitEnd(DateUtil.stringToDate(request.getEndDateStr(), DateUtil.DATE_BASE)));
        }
        List<ApproveInfoData> approveInfoData = approveService.queryApprovalListByParam(param);
        view.success(approveInfoData);
        return view;
    }

    /**
     * 重新生成某月考勤信息
     *
     * @return
     */
    @RequestMapping(value = "generateMonthAttendanceInfo", method = RequestMethod.POST)
    @ResponseBody
    public StringView generateMonthAttendanceInfo(QueryAttendanceInfoParam param) {
        StringView view = new StringView();

        EmployeeInfo employeeInfo = PublicData.loginUser;
        if (employeeInfo == null) {
            view.fail("未登录！");
        } else {
            if (param.getAttendanceDate() != null) {
                param.setRecordYear(
                    DateUtil.getYear(DateUtil.stringToDate(param.getAttendanceDate(), DateUtil.DATE_BASE)));
                param.setRecordMonth(
                    DateUtil.getMonth(DateUtil.stringToDate(param.getAttendanceDate(), DateUtil.DATE_BASE)));
            }

            Integer result = attendanceService.generateMonthAttendanceInfo(param, employeeInfo.getEmployeeId());
            if (result != 1) {
                view.fail("操作失败！");
            } else {
                view.success("生成成功！");
            }
        }
        return view;
    }

    /**
     * 修改考勤信息
     *
     * @param request
     */
    @RequestMapping(value = "updateAttendanceInfo", method = RequestMethod.POST)
    @ResponseBody
    public StringView updateAttendanceInfo(AttendanceInfo request) {
        StringView view = new StringView();
        attendanceService.updateAttendance(request);
        view.success("修改成功");
        return view;
    }

    /**
     * 修改公司考勤规则
     *
     * @param request
     */
    @RequestMapping(value = "updateConfigureInfo", method = RequestMethod.POST)
    @ResponseBody
    public StringView updateConfigureInfo(ConfigureInfo request, HttpServletRequest userRequest) {
        StringView view = new StringView();
        EmployeeInfo user = (EmployeeInfo)userRequest.getSession().getAttribute("user");
        if (user == null) {
            view.success(StringView.NOTIFY, "请先登陆！", null);
        } else {
            configureService.updateConfig(request, user.getEmployeeId());
            view.success("修改成功");
        }
        return view;
    }

    /**
     * 查询日志
     *
     * @Author: chenxin
     * @Date: 2019/12/9
     */
    @RequestMapping(value = "queryLogInfo", method = RequestMethod.POST)
    @ResponseBody
    public ListBaseView<LogInfoDetail> queryLogInfo() {
        ListBaseView<LogInfoDetail> view = new ListBaseView();
        List<LogInfoDetail> LogInfoDetails = LogInfoService.queryLogInfoList();
        view.success(LogInfoDetails);
        return view;
    }

    /**
     * 设置员工id
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "setAdminEmployeeId", method = RequestMethod.POST)
    @ResponseBody
    public StringView setAdminEmployeeId(EmployeeIdRequest request) {
        StringView view = new StringView();
        PublicData.searchAttendanceEmpId = request.getEmployeeId();
        view.success();
        return view;
    }

    /**
     * 查询员工详情
     *
     * @return
     */
    @RequestMapping(value = "getEmployeeDetailById", method = RequestMethod.POST)
    @ResponseBody
    public SimpleView getEmployeeDetailById() {
        SimpleView view = new SimpleView();
        Long employeeId = PublicData.searchAttendanceEmpId;
        if (employeeId == null) {
            view.turn();
        } else {
            EmployeeDetail employeeDetail = employeeService.getEmployeeById(employeeId);
            view.success(employeeDetail);
        }
        return view;
    }

}