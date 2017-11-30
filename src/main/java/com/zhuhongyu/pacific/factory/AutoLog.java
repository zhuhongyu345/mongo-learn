package com.zhuhongyu.pacific.factory;

import com.zhuhongyu.pacific.po.BachPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class AutoLog implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoLog.class);
    @Resource
    private ConcurrentLinkedDeque<String> idQueue;
    @Resource
    private ConcurrentLinkedDeque<BachPo> poQueue;

    private void run() {
        while(true){
            try {
                Thread.sleep(10000);
                LOGGER.info("************************** id size : {} **************************",idQueue.size());
                LOGGER.info("************************** po size : {} **************************",poQueue.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        new Thread(this::run).start();
    }
}
