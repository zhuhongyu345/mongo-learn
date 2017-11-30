package com.zhuhongyu.pacific.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MapReduceOutput;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class MrDao {
    @Resource
    private MongoTemplate mongoTemplate;

    public void groupMr() {

        DBObject dbo = new BasicDBObject();
        dbo.put("phone_city", "北京");
//        function(){
//            if(this.phone_city=='北京'){
//                var value = {id:this._id,uid:this.uid};
//                emit(this.phone_carrier,value);
//            }
//        }
        String map = "function(){" +
                "    if(this.phone_city=='北京'){" +
                "        var value = {id:this._id,uid:this.uid};" +
                "        emit(this.phone_carrier,value);" +
                "    }" +
                "}";
//        function(key,values){
//            var res = {};
//            var count = 0;
//            var uids = "";
//            for(var i in values){
//                uids+=values[i].uid;
//                count += 1;
//            }
//            res.count = count;
//            res.uids = uids;
//            return res;
//        }
        String reduce = "function(key,values){" +
                "    var res = {};" +
                "    var count = 0;" +
                "    var uids = \"\";" +
                "    for(var i in values){" +
                "        uids+=values[i].uid;" +
                "        count += 1;" +
                "    }" +
                "    res.count = count;" +
                "    res.uids = uids;" +
                "    return res;" +
                "}";
        String target = "map_reduce_res_2016_2";
        MapReduceOutput main = mongoTemplate.getCollection("main").mapReduce(map, reduce, target, dbo);
        System.out.println(main);
    }

}
