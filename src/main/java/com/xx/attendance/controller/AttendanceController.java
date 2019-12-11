package com.xx.attendance.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xx.attendance.common.PublicData;
import com.xx.attendance.dto.requset.approve.ApprovalRequest;
import com.xx.attendance.dto.requset.approve.QueryApproveParam;
import com.xx.attendance.dto.requset.attendance.QueryAttendanceInfoParam;
import com.xx.attendance.dto.response.ConfigDetail;
import com.xx.attendance.dto.response.approve.ApproveInfoData;
import com.xx.attendance.dto.response.attendance.AttendanceDetail;
import com.xx.attendance.dto.response.attendance.AttendanceMonthInfo;
import com.xx.attendance.dto.view.ListBaseView;
import com.xx.attendance.dto.view.SimpleView;
import com.xx.attendance.dto.view.StringView;
import com.xx.attendance.server.ApproveService;
import com.xx.attendance.server.AttendanceService;
import com.xx.attendance.server.ConfigureService;
import com.xx.attendance.utils.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈考勤控制器-通用员工〉
 *
 * @author xx
 * @create 2019/11/24
 * @since 1.0.0
 */
@RestController
@RequestMapping("attendance")
public class AttendanceController {

    @Reference
    private ConfigureService configureService;

    @Reference
    private AttendanceService attendanceService;

    @Reference
    private ApproveService approveService;

    /**
     * 上班打卡
     * 
     * 员工ID
     */
    @RequestMapping(value = "punchIn", method = RequestMethod.POST)
    @ResponseBody
    public SimpleView punchIn() {
        SimpleView view = new SimpleView();
        if (PublicData.loginUser == null) {
            view.fail("请先登陆！");
        } else {
            view = attendanceService.punchIn(PublicData.loginUser.getEmployeeId());
        }
        return view;
    }

    /**
     * 下班打卡
     * 
     * 员工ID
     */
    @RequestMapping(value = "punchOut", method = RequestMethod.POST)
    @ResponseBody
    public SimpleView punchOut() {
        SimpleView view = new SimpleView();

        if (PublicData.loginUser == null) {
            view.fail("请先登陆！");
        } else {
            view = attendanceService.punchOut(PublicData.loginUser.getEmployeeId());
        }
        return view;
    }

    /**
     * 本日本人考勤情况
     *
     */
    @RequestMapping(value = "getAttendanceInfoByToday", method = RequestMethod.POST)
    @ResponseBody
    public SimpleView getAttendanceInfoByToday() {
        SimpleView view = new SimpleView();

        if (PublicData.loginUser == null) {
            view.turn();
        } else {
            QueryAttendanceInfoParam param = new QueryAttendanceInfoParam();
            param.setEmployeeId(PublicData.loginUser.getEmployeeId());
            Date nowDate = new Date();
            int year = DateUtil.getYear(nowDate);
            int month = DateUtil.getMonth(nowDate);
            int day = DateUtil.getDay(nowDate);
            param.setRecordYear(year);
            param.setRecordMonth(month);
            param.setRecordDay(day);
            List<AttendanceDetail> attendanceDetails = attendanceService.queryAttendanceDetailListByParam(param);
            if (CollectionUtils.isEmpty(attendanceDetails)) {
                view.turn();
            } else {
                view.success(attendanceDetails.get(0));
            }
        }
        return view;
    }

    /**
     * 查看员工考勤信息列表（日）
     * 
     * @param request
     */
    @RequestMapping(value = "queryAttendanceInfoByParam", method = RequestMethod.POST)
    @ResponseBody
    public ListBaseView<AttendanceDetail> queryAttendanceInfoByParam(QueryAttendanceInfoParam request) {
        Assert.notNull(request.getEmployeeId(), "员工id不能为空");
        ListBaseView<AttendanceDetail> view = new ListBaseView();
        List<AttendanceDetail> attendanceDetails = attendanceService.queryAttendanceDetailListByParam(request);
        view.success(attendanceDetails);
        return view;
    }

    /**
     * 查看员工考勤信息详情（月）
     * 
     * @param request
     */
    @RequestMapping(value = "queryAttendanceMonthInfoByParam", method = RequestMethod.POST)
    @ResponseBody
    public SimpleView queryAttendanceMonthInfoByParam(QueryAttendanceInfoParam request) {
        SimpleView view = new SimpleView();

        if (PublicData.loginUser == null) {
            view.fail("请先登陆！");
        } else {
            if (request.getAttendanceDate() != null) {
                Integer year = DateUtil.getYear(DateUtil.stringToDate(request.getAttendanceDate(), DateUtil.DATE_BASE));
                Integer recordMonth =
                    DateUtil.getDayOfMonth(DateUtil.stringToDate(request.getAttendanceDate(), DateUtil.DATE_BASE));
                request.setRecordYear(year);
                request.setRecordMonth(recordMonth);
            }

            AttendanceMonthInfo result = new AttendanceMonthInfo();

            List<AttendanceMonthInfo> attendanceMonthInfos = attendanceService.queryAttendanceMonthListByParam(request);
            if (!CollectionUtils.isEmpty(attendanceMonthInfos)) {
                result = attendanceMonthInfos.get(0);
            }
            view.success(result);
        }
        return view;
    }

    /**
     * 发起审批流程
     * 
     * @param request:
     *            all
     */
    @RequestMapping(value = "createApproval", method = RequestMethod.POST)
    @ResponseBody
    public StringView createApproval(ApprovalRequest request) {
        StringView view = new StringView();

        approveService.addApprove(request);

        view.success("审批流程发起成功！");
        return view;
    }

    /**
     * 查看自身审批信息列表
     * 
     * @param request:
     *            员工ID，审批类型、审批日期筛选
     */
    @RequestMapping(value = "queryApprovalInfoListByEmployeeId", method = RequestMethod.POST)
    @ResponseBody
    public ListBaseView queryApprovalInfoListByEmployeeId(ApprovalRequest request) {
        Assert.notNull(request.getApprovalUserId(), "员工id不能为空");

        ListBaseView listView = new ListBaseView();

        QueryApproveParam param = new QueryApproveParam();
        BeanUtils.copyProperties(request, param);
        List<ApproveInfoData> approveInfoData = approveService.queryApprovalListByParam(param);

        listView.success(approveInfoData);
        return listView;
    }

    /**
     * 查看公司考勤规则
     *
     * @return
     */
    @RequestMapping(value = "getAttendanceRule", method = RequestMethod.POST)
    @ResponseBody
    public SimpleView getAttendanceRule() {
        SimpleView resp = new SimpleView();
        ConfigDetail config = configureService.getConfig();
        resp.success(config);
        return resp;
    }

}