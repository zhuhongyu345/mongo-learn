package com.zhuhongyu.pacific.api;

import com.zhuhongyu.pacific.service.BachService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/bach")
public class WarriorController {
    @Resource
    private BachService bachService;

    @RequestMapping("/warrior")
    public String bach() {
        bachService.collect();
        return "SUCCESS";
    }
}
