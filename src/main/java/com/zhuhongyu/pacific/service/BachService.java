package com.zhuhongyu.pacific.service;

import com.zhuhongyu.pacific.factory.Collector;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BachService {

    @Resource
    private Collector collector;

    public void collect(){
        collector.collect();
    }
}
