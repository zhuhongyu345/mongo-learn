package com.zhuhongyu.pacific.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.bson.BasicBSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class CommonMongoDao {
    @Resource
    private MongoTemplate mongoTemplate;
    private String collection = "main";

    public void insert(List<?> data, String collection) {
        mongoTemplate.insert(data, collection);
    }

    public BasicBSONObject findFlag(String flag) {
        Query query = new Query(Criteria.where(flag).exists(true));
        return mongoTemplate.findOne(query, BasicBSONObject.class, "flag");
    }

    public void updateFlag(String flag, Object value) {
        Query query = new Query(Criteria.where(flag).exists(true));
        Update update = new Update();
        update.set(flag, value);
        mongoTemplate.upsert(query, update, "flag");
    }

    public List<BasicBSONObject> findByPage(int page, int num) {
        Criteria c = new Criteria();
        DBObject fieldsObject = new BasicDBObject();
        Query query = new BasicQuery(new BasicDBObject(), fieldsObject);
        query.addCriteria(c);
        return mongoTemplate.find(query.with(new Sort(Direction.DESC, "number"))
                .skip(page).limit(num), BasicBSONObject.class, collection);
    }

    public BasicBSONObject findIdenByUid(String uid) {
        DBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("_id", false);
        fieldsObject.put("status", false);
        fieldsObject.put("id", false);
        fieldsObject.put("ctime", false);
        fieldsObject.put("uid", false);
        fieldsObject.put("mtime", false);
        Query query = new BasicQuery(new BasicDBObject(), fieldsObject);
        query.addCriteria(Criteria.where("uid").is(uid));
        return mongoTemplate.findOne(query, BasicBSONObject.class, "identity");
    }

    public BasicBSONObject findUserByUid(String uid) {
        DBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("uid", true);
        fieldsObject.put("phone", true);
        fieldsObject.put("phone_province", true);
        fieldsObject.put("phone_city", true);
        fieldsObject.put("phone_carrier", true);
        fieldsObject.put("_id", false);
        Query query = new BasicQuery(new BasicDBObject(), fieldsObject);
        query.addCriteria(Criteria.where("uid").is(uid));
        return mongoTemplate.findOne(query, BasicBSONObject.class, "user");
    }

    public BasicBSONObject findUserInfoByUid(String uid) {
        DBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("_id", false);
        fieldsObject.put("mtime", false);
        fieldsObject.put("last_signout_time", false);
        fieldsObject.put("uid", false);
        fieldsObject.put("ctime", false);
        fieldsObject.put("id", false);
        fieldsObject.put("last_signin_time", false);
        fieldsObject.put("submit_check_time", false);
        fieldsObject.put("check_time", false);
        fieldsObject.put("status", false);
        Query query = new BasicQuery(new BasicDBObject(), fieldsObject);
        query.addCriteria(Criteria.where("uid").is(uid));
        return mongoTemplate.findOne(query, BasicBSONObject.class, "userinfo");
    }

    public List<BasicBSONObject> findUidInUser(int skip, int limit) {
        DBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("uid", true);
        fieldsObject.put("_id", false);
        return mongoTemplate.find(new BasicQuery(new BasicDBObject(), fieldsObject)
                .skip(skip).limit(limit), BasicBSONObject.class, "user");
    }

    public List<BasicBSONObject> findUidInUserinfo(int skip, int limit) {
        DBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("uid", true);
        fieldsObject.put("_id", false);
        return mongoTemplate.find(new BasicQuery(new BasicDBObject(), fieldsObject)
                .skip(skip).limit(limit), BasicBSONObject.class, "userinfo");
    }

    public List<BasicBSONObject> findUidInIdentity(int skip, int limit) {
        DBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("uid", true);
        fieldsObject.put("_id", false);
        return mongoTemplate.find(new BasicQuery(new BasicDBObject(), fieldsObject)
                .skip(skip).limit(limit), BasicBSONObject.class, "identity");
    }

    public long countInUser() {
        return mongoTemplate.count(new BasicQuery(new BasicDBObject()), "user");
    }

    public long countInUserinfo() {
        return mongoTemplate.count(new BasicQuery(new BasicDBObject()), "userinfo");
    }

    public long countInIdentity() {
        return mongoTemplate.count(new BasicQuery(new BasicDBObject()), "identity");
    }

    public List<BasicBSONObject> findUidsInUserall() {
        Criteria c = new Criteria();
        c.orOperator(Criteria.where("uid").exists(true));
        DBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("uid", true);
        fieldsObject.put("_id", false);
        Query query = new BasicQuery(new BasicDBObject(), fieldsObject);
        query.addCriteria(c);
        return mongoTemplate.find(query, BasicBSONObject.class, "userall");
    }

    public List<BasicBSONObject> findAll() {
        Criteria c = new Criteria();
        c.orOperator(Criteria.where("name").is("array").and("gender").is(1), Criteria.where("name").is("json"));
//		Query query = Query.query(c);
        DBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("_id", false);//滤除字段，这个跟第一次有关，正反由第一次决定。
        Query query = new BasicQuery(new BasicDBObject(), fieldsObject);
        query.addCriteria(c);
        return mongoTemplate.find(query.with(new Sort(Direction.DESC, "number"))
                .limit(5), BasicBSONObject.class, collection);
    }

    public List<BasicBSONObject> findLike(String field, String like, int skip, int limit) {
        Query query = new Query();
        query.limit(10);
        query.addCriteria(Criteria.where(field).regex(like));
        return mongoTemplate.find(query.skip(skip).limit(limit), BasicBSONObject.class, collection);
    }

    public AggregationResults<BasicBSONObject> groupby() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("gender").is(1)),
                Aggregation.skip(20000L),//and limit   both:  有顺序，放聚合的后边表示的意义不一样。
                Aggregation.limit(500),
                Aggregation.group("ethnicity")
                        .count().as("count").avg("status").as("avgstatus")//string sum is 0
                        .first("name").as("name").addToSet("name").as("nameset")
                        .push("name").as("pushname"),//push  addToset  <-->
                Aggregation.project("count", "avgstatus", "name", "nameset", "pushname")
                        .and("ethnicity").previousOperation()
        );
        return mongoTemplate.aggregate(aggregation, collection, BasicBSONObject.class);
    }

    public WriteResult updateFirst() {
        Update update = new Update();
        update.set("gender", 1).unset("bbb");
        return mongoTemplate.updateFirst(new Query(Criteria.where("name").is("json")), update, collection);
    }

    public MapReduceResults<BasicDBObject> testMR() {
        String map = "function(){emit(this.gender,this.uid); } ";
        String reduce = "function(key,values){return values.join(',');} ";
        return mongoTemplate.mapReduce("user", map, reduce, BasicDBObject.class);
    }

    //速度没啥区别
    public void testSudu() {
        Criteria c = new Criteria();
        c.orOperator(Criteria.where("name").is("json").and("gender").is(1), Criteria.where("name").is("array"));
        Query query = Query.query(c);

        mongoTemplate.find(query.with(new Sort(Direction.DESC, "number")).limit(5),
                BasicBSONObject.class, collection);
        mongoTemplate.find(query.with(new Sort(Direction.DESC, "number")).limit(5),
                BasicDBObject.class, collection);
        long b1 = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            mongoTemplate.find(query.with(new Sort(Direction.DESC, "number")).limit(5),
                    BasicBSONObject.class, collection);
        }
        long b2 = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            mongoTemplate.find(query.with(new Sort(Direction.DESC, "number")).limit(5),
                    BasicDBObject.class, collection);
        }
        long b3 = System.currentTimeMillis();
        System.out.println(b2 - b1);
        System.out.println(b3 - b2);
    }

    //basicdbobject多一个对象类型
    public void testDetail() {
        Criteria c = new Criteria();
        c.orOperator(Criteria.where("name").is("array").and("gender").is(1));
        Query query = Query.query(c);
        List<BasicBSONObject> find = mongoTemplate.find(query.with(new Sort(Direction.DESC, "number"))
                .limit(5), BasicBSONObject.class, collection);
        List<BasicDBObject> find2 = mongoTemplate.find(query.with(new Sort(Direction.DESC, "number"))
                .limit(5), BasicDBObject.class, collection);
        System.out.println(find);
        System.out.println(find2);
    }

}