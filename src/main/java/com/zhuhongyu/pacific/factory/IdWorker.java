package com.zhuhongyu.pacific.factory;

import com.mongodb.DBObject;
import com.zhuhongyu.pacific.dao.BachDao;
import com.zhuhongyu.pacific.po.BachPo;
import com.zhuhongyu.pacific.util.DbObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class IdWorker implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdWorker.class);

    private ThreadPoolExecutor exec;

    @Resource
    private ConcurrentLinkedDeque<String> idQueue;
    @Resource
    private ConcurrentLinkedDeque<BachPo> poQueue;
    @Resource
    private BachDao bachDao;

    @PostConstruct
    private void init() {
        exec = new ThreadPoolExecutor(8, 8, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
         int i = 8;
        do {
            exec.execute(this::run);
        } while (--i > 0);
    }

    private void run() {
        while (true) {
            if (!idQueue.isEmpty()) {
                String poll = idQueue.poll();
                bachOne(poll);
            }
        }
    }

    private void bachOne(String key) {
        try {
            List<DBObject> main = bachDao.getOne("main", "uid", key);
            List<DBObject> ide = bachDao.getOne("ide", "uid", key);
            List<DBObject> detail = bachDao.getOne("detail", "uid", key);

            BachPo po = new BachPo();
            boolean f1 = main.isEmpty();
            boolean f2 = ide.isEmpty();
            boolean f3 = detail.isEmpty();
            if (!f1) DbObjectUtil.dbObject2Bean(main.get(0), po);
            if (!f2) DbObjectUtil.dbObject2Bean(ide.get(0), po);
            if (!f3) DbObjectUtil.dbObject2Bean(detail.get(0), po);
            poQueue.add(po);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("miss one key is {}", key);
        }
    }
}
