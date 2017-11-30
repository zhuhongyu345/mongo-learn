package com.zhuhongyu.pacific.dao;

import com.mongodb.DBObject;
import com.zhuhongyu.pacific.po.BachPo;
import org.bson.BasicBSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class BachDao {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private MongoTemplate seMongoTemplate;

    public AggregationResults<BasicBSONObject> getIdsGroup(String collection, String key) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("_class").addToSet(key).as("keys")
        );
        return mongoTemplate.aggregate(aggregation, collection, BasicBSONObject.class);
    }

    public List<DBObject> getOne(String collection, String key, String value){
        Query query = new Query(Criteria.where(key).is(value));
        return mongoTemplate.find(query, DBObject.class,collection);
    }

    public void insertWarrior(List<BachPo> bachPos){
        seMongoTemplate.insert(bachPos,"warrior");
    }
}
