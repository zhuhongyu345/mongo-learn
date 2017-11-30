package com.zhuhongyu.pacific.factory;

import com.zhuhongyu.pacific.dao.BachDao;
import com.zhuhongyu.pacific.po.BachPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class PoWorker implements  ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoWorker.class);

    @Resource
    private ConcurrentLinkedDeque<BachPo> poQueue;
    @Resource
    private BachDao bachDao;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        new Thread(this::run).start();
    }

    private void run() {
        while (true) {
            int size = poQueue.size();
            if (size < 1000 && size > 0) {
                List<BachPo> list = new LinkedList<>();
                for (int i = 0; i < size; i++) {
                    list.add(poQueue.poll());
                }
                bachMany(list);
            } else if (size > 1000) {
                List<BachPo> list = new LinkedList<>();
                for (int i = 0; i < 1000; i++) {
                    list.add(poQueue.poll());
                }
                bachMany(list);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void bachMany(List<BachPo> list) {
        bachDao.insertWarrior(list);
        LOGGER.info("add {} warrior",list.size());
    }
}
