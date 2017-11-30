package com.zhuhongyu.pacific.dao;

import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UniDao {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private MongoClient mongoClient;

    public AggregateIterable<Document> getMessageAll() {

        long count = mongoClient.getDatabase("mars").getCollection("main").count();
        System.out.println(count);

        BasicDBObject query1 = new BasicDBObject();
        BasicDBObject query2 = new BasicDBObject();
        BasicDBObject query3 = new BasicDBObject();
        BasicDBObject query4 = new BasicDBObject();

        BasicDBObject match = new BasicDBObject();

        BasicDBObject like = new BasicDBObject();
        like.put("$regex", "9ca78c04fe842e4a");
        match.put("trade_password", like);
        BasicBSONObject lookup = new BasicDBObject();
        lookup.put("localField", "uid");
        lookup.put("from", "ide");
        lookup.put("foreignField", "uid");
        lookup.put("as", "ide");

        BasicBSONObject lookup2 = new BasicDBObject();
        lookup2.put("localField", "uid");
        lookup2.put("from", "detail");
        lookup2.put("foreignField", "uid");
        lookup2.put("as", "detail");


        List<BasicDBObject> list = new ArrayList<>();

        query3.put("$limit", 20000);
        query1.put("$lookup", lookup);
        query4.put("$lookup", lookup2);
        query2.put("$match", match);

        list.add(query2);
        list.add(query4);
        list.add(query1);
        list.add(query3);

        //type1
        AggregationOutput mars = mongoTemplate.getCollection("main").aggregate(list);
        mars.results().forEach(System.out::println);
        System.out.println();
        //type2
        AggregateIterable<Document> documents = mongoClient.getDatabase("mars").
                getCollection("main").aggregate(list);

        documents.forEach((Block<? super Document>) System.out::println);
        return documents;
    }
}
