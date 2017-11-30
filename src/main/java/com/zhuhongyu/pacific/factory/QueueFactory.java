package com.zhuhongyu.pacific.factory;

import com.zhuhongyu.pacific.po.BachPo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentLinkedDeque;

@Configuration
public class QueueFactory {

    @Bean
    public ConcurrentLinkedDeque<String> idQueue() {
        return new ConcurrentLinkedDeque<>();
    }

    @Bean
    public ConcurrentLinkedDeque<BachPo> poQueue() {
        return new ConcurrentLinkedDeque<>();
    }

}
