package com.parse.demo.controller;

import com.parse.demo.web.config.MomentUrlConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(MomentUrlConfig.APP_URL_PREFIX + "/moment2")
public class MomentTestController2 {

    @GetMapping("/getDiscoverFeedListBackupData2")
    public Object getDiscoverFeedListBackupData(Integer pageSize) {
        return "/moment/getDiscoverFeedListBackupData";
    }
    @PostMapping("/delActivityAwardInfoCache2")
    public String delActivityAwardInfoCache(Long activityId) {
        return "/moment/delActivityAwardInfoCache";
    }
    @RequestMapping("/getByPositionCodeNew2")
    public String getByPositionCode(int positionCode, String source) {
        return "/moment/getByPositionCodeNew";
    }

}

