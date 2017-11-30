package com.zhuhongyu.pacific.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class MrDao {
    @Resource
    private MongoTemplate mongoTemplate;

    public void groupMr() {

        DBObject dbo = new BasicDBObject();
        dbo.put("phone","18962571881");
        Query query = new Query(Criteria.where("phone").is("18962571881"));

        String map = "";
        String reduce = "";
        String target = "";

        mongoTemplate.getCollection("main").mapReduce(map,reduce,target,dbo);
    }

}
