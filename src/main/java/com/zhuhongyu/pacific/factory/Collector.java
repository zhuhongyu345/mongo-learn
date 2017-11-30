package com.zhuhongyu.pacific.factory;

import com.zhuhongyu.pacific.dao.BachDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class Collector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Collector.class);

    @Resource
    private ConcurrentLinkedDeque<String> idQueue;
    @Resource
    private BachDao bachDao;

    public void collect() {
        List<String> dIds = (List<String>) bachDao.getIdsGroup("detail", "uid").getMappedResults().get(0).get("keys");
        List<String> iIds = (List<String>) bachDao.getIdsGroup("ide", "uid").getMappedResults().get(0).get("keys");
        List<String> mIds = (List<String>) bachDao.getIdsGroup("main", "uid").getMappedResults().get(0).get("keys");

        Set<String> set = new HashSet<>();
        set.addAll(dIds);
        set.addAll(mIds);
        set.addAll(iIds);

        LOGGER.info("collect {} :{} ", set.size(), set.toString());
        idQueue.addAll(set);
    }
}
