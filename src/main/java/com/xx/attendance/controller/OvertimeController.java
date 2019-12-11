package com.xx.attendance.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xx.attendance.dto.requset.OvertimeRequest;
import com.xx.attendance.dto.response.OvertimeDetail;
import com.xx.attendance.dto.view.ListBaseView;
import com.xx.attendance.server.OvertimeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("overtime")
@RestController
public class OvertimeController {

    @Reference
    private OvertimeService overtimeService;

    @ResponseBody
    @RequestMapping(value = "queryOvertimeInfoByParam", method = RequestMethod.POST)
    public ListBaseView<OvertimeDetail> queryOvertimeInfoByParam( OvertimeRequest request) {
        ListBaseView<OvertimeDetail> view = new ListBaseView<>();
        List<OvertimeDetail> dataList = overtimeService.queryOvertimeInfoByParam(request);
        view.success(dataList);
        return view;
    }
}
