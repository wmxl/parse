package com.parse.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/moment")
public class MomentTestController {

    @GetMapping("/discoverFeedListBackupData")
    public Object getDiscoverFeedListBackupData(Integer pageSize) {
        return "/moment/getDiscoverFeedListBackupData";
    }
    @PostMapping("/delActivity")
    public String delActivityAwardInfoCache(Long activityId) {
        return "/moment/delActivityAwardInfoCache";
    }
    @RequestMapping("/positionCodeNew")
    public String getByPositionCode(int positionCode, String source) {
        return "/moment/getByPositionCodeNew";
    }

}

