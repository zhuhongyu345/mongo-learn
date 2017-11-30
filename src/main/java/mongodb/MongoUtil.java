package mongodb;

import com.mongodb.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * MongoUtil
 */
public class MongoUtil {
    private final static ThreadLocal<Mongo> mongos = new ThreadLocal<Mongo>();
    private static Properties p = new Properties();

    public static Mongo getMongos() {
        Mongo mongo = mongos.get();
        if (mongo == null) {
            try {
                p = ConfigHelper.getConfigProperties("mongodb.properties");
                mongo = new Mongo(p.getProperty("ip"), Integer.parseInt(p.getProperty("port")));
                mongos.set(mongo);
            } catch (GenaUtilException e) {
                e.printStackTrace();
            }
        }
        return mongo;

    }

    public static void close() {
        Mongo mongo = mongos.get();
        if (mongo != null) {
            mongo.close();
            mongos.remove();
        }
    }

    public static DB getdb() {
        return getMongos().getDB(p.getProperty("db"));
    }

    /**
     * 获取集合（表）
     *
     * @param collectionName
     */
    public static DBCollection getCollection(String collectionName) {
        return getdb().getCollection(collectionName);
    }

    public static void displayCollections() {
        Set<String> colls = getdb().getCollectionNames();
        for (String s : colls) {
            System.out.println(s);
        }
    }

    /**
     * 插入
     *
     * @param o 插入
     */
    public static WriteResult insert(String collectionName, DBObject o) {
        return getCollection(collectionName).insert(o);
    }

    /**
     * 批量插入
     *
     * @param list 插入的列表
     */
    public static WriteResult insertBatch(String collectionName, List<DBObject> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return getCollection(collectionName).insert(list);
    }

    /**
     * 添加操作
     * <br>------------------------------<br>
     *
     * @param map
     * @param collectionName
     */
    public static WriteResult add(Map<String, Object> map, String collectionName) {
        DBObject dbObject = new BasicDBObject(map);
        return getCollection(collectionName).insert(dbObject);
    }

    /**
     * 添加操作
     * <br>------------------------------<br>
     *
     * @param list
     * @param collectionName
     */
    public static void add(List<Map<String, Object>> list, String collectionName) {
        for (Map<String, Object> map : list) {
            add(map, collectionName);
        }
    }

    /**
     * 删除操作
     * <br>------------------------------<br>
     *
     * @param map
     * @param collectionName
     */
    public static WriteResult delete(Map<String, Object> map, String collectionName) {
        DBObject dbObject = new BasicDBObject(map);
        return getCollection(collectionName).remove(dbObject);
    }

    /**
     * 删除操作,根据主键
     * <br>------------------------------<br>
     *
     * @param id
     * @param collectionName
     */
    public static WriteResult delete(String id, String collectionName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("_id", new ObjectId(id));
        return delete(map, collectionName);
    }

    /**
     * 删除全部
     * <br>------------------------------<br>
     *
     * @param collectionName
     */
    public static void deleteAll(String collectionName) {
        getCollection(collectionName).drop();
    }

    public static long count(String collectionName, DBObject query) {
        if (query == null)
            return getCollection(collectionName).count();

        return getCollection(collectionName).count(query);
    }


    /**
     * count
     * <br>------------------------------<br>
     *
     * @param map
     * @param collectionName
     * @return
     */
    public static long count(Map<String, Object> map, String collectionName) {
        DBObject dbObject = new BasicDBObject(map);
        return getCollection(collectionName).count(dbObject);

    }


    //groupField  分组字段
    //sumField  自定义显示的统计字段
    //underSumField  被统计字段

    public static long sum(String groupField, String sumField, String underSumField, String collectionName, DBObject condition) {
        BasicDBObject match = new BasicDBObject(
                "$match", condition
        );

        BasicDBObject group = new BasicDBObject(
                "$group", new BasicDBObject("_id", groupField).append(
                sumField, new BasicDBObject("$sum", "$" + underSumField)
        )
        );

        AggregationOutput output = getCollection(collectionName).aggregate(match, group);
        JSONArray arr = JSONArray.fromObject(output.results());
//    		System.out.println("result-------->"+output.getCommandResult().get("result"));
        if (!arr.isEmpty()) {
            return JSONObject.fromObject(arr.get(0)).getLong(sumField);
        } else {
            return 0;
        }
    }


    //按天数统计
    public static JSONObject sumByDay(
            String groupField,        //"createtime"
            String sumField,        //"total"
            String collectionName,    //MongoDBConstant.DB_NAME_PROGRAM
            DBObject condition        //new BasicDBObject()
    ) {
        BasicDBObject match = new BasicDBObject(
                "$match", condition
        );

        DBObject group = new BasicDBObject();
        DBObject groupDate = new BasicDBObject();
        groupDate.put("year", new BasicDBObject("$year", "$" + groupField));
        groupDate.put("month", new BasicDBObject("$month", "$" + groupField));
        groupDate.put("day", new BasicDBObject("$dayOfMonth", "$" + groupField));
        group.put("$group", new BasicDBObject("_id", groupDate).append(
                sumField, new BasicDBObject("$sum", 1)
        ));

        //按照时间倒序排列
        DBObject sort = new BasicDBObject();
        DBObject sortObj = new BasicDBObject();
        sortObj.put("_id", -1);
        sort.put("$sort", sortObj);

        AggregationOutput output = getCollection(collectionName).aggregate(match, group, sort);
        JSONArray arr = JSONArray.fromObject(output.results());
        JSONObject result = new JSONObject();
        int i = 1;
        long startDate = 0;
        long endDate = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Object temp : arr) {
            JSONObject obj = JSONObject.fromObject(temp);
            JSONObject dayinfo = obj.getJSONObject("_id");
            String key = dayinfo.getInt("year") + "-" + (dayinfo.getInt("month") < 10 ? ("0" + dayinfo.getInt("month")) : dayinfo.getInt("month")) + "-" + (dayinfo.getInt("day") < 10 ? ("0" + dayinfo.getInt("day")) : dayinfo.getInt("day"));
            long num = obj.getLong(sumField);
            //第一次取到的是最新的
            try {

                if (i == 1) {
                    endDate = sdf.parse(key).getTime();
                }
                //最后一次取到的是最老的
                if (i == arr.size()) {
                    startDate = sdf.parse(key).getTime();
                }
                result.put(sdf.parse(key).getTime() + "", num);
                i++;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        return result;
    }


    /**
     * 修改操作</br>
     * 会用一个新文档替换现有文档,文档key结构会发生改变</br>
     * 比如原文档{"_id":"123","name":"zhangsan","age":12}当根据_id修改age
     * value为{"age":12}新建的文档name值会没有,结构发生了改变
     * <br>------------------------------<br>
     *
     * @param whereMap
     * @param valueMap
     * @param collectionName
     */
    public static void update(Map<String, Object> whereMap, Map<String, Object> valueMap, String collectionName) {
        executeUpdate(collectionName, whereMap, valueMap, new UpdateCallback() {
            public DBObject doCallback(DBObject valueDBObject) {
                return valueDBObject;
            }
        });
    }

    /**
     * 修改操作,使用$set修改器</br>
     * 用来指定一个键值,如果键不存在,则自动创建,会更新原来文档, 不会生成新的, 结构不会发生改变
     * <br>------------------------------<br>
     *
     * @param whereMap
     * @param valueMap
     * @param collectionName
     */
    public static void updateSet(Map<String, Object> whereMap, Map<String, Object> valueMap, String collectionName) {
        executeUpdate(collectionName, whereMap, valueMap, new UpdateCallback() {
            public DBObject doCallback(DBObject valueDBObject) {
                return new BasicDBObject("$set", valueDBObject);
            }
        });
    }

    public static WriteResult updateSet(DBObject queryCondition, DBObject allCondition, String collectionName) {
        return getCollection(collectionName).update(queryCondition, allCondition);
    }

    /**
     * 修改操作,使用$inc修改器</br>
     * 修改器键的值必须为数字</br>
     * 如果键存在增加或减少键的值, 如果不存在创建键
     * <br>------------------------------<br>
     *
     * @param whereMap
     * @param valueMap
     * @param collectionName
     */
    public static void updateInc(Map<String, Object> whereMap, Map<String, Integer> valueMap, String collectionName) {
        executeUpdate(collectionName, whereMap, valueMap, new UpdateCallback() {
            public DBObject doCallback(DBObject valueDBObject) {
                return new BasicDBObject("$inc", valueDBObject);
            }
        });
    }

    /**
     * 修改
     * <br>------------------------------<br>
     *
     * @param collectionName
     * @param whereMap
     * @param valueMap
     * @param updateCallback
     */
    private static void executeUpdate(String collectionName, Map whereMap, Map valueMap, UpdateCallback updateCallback) {
        DBObject whereDBObject = new BasicDBObject(whereMap);
        DBObject valueDBObject = new BasicDBObject(valueMap);
        valueDBObject = updateCallback.doCallback(valueDBObject);
        getCollection(collectionName).update(whereDBObject, valueDBObject, false, false);
    }

    interface UpdateCallback {
        DBObject doCallback(DBObject valueDBObject);
    }

    /**
     * 判断集合是否存在
     * <br>------------------------------<br>
     *
     * @param collectionName
     * @return
     */
    public static boolean collectionExists(String collectionName) {
        return getdb().collectionExists(collectionName);
    }

    /**
     * 查询单个,按主键查询
     * <br>------------------------------<br>
     *
     * @param id
     * @param collectionName
     */
    public static DBObject findById(String id, String collectionName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("_id", new ObjectId(id));
        return findOne(map, collectionName);
    }

    /**
     * 查询单个
     * <br>------------------------------<br>
     *
     * @param map
     * @param collectionName
     */
    public static DBObject findOne(Map<String, Object> map, String collectionName) {
        DBObject dbObject = new BasicDBObject(map);
        return getCollection(collectionName).findOne(dbObject);
    }

    /**
     * 获取所有集合名称
     * <br>------------------------------<br>
     *
     * @return
     */
    public static Set<String> getCollection() {
        return getdb().getCollectionNames();
    }

    /**
     * 创建集合
     * <br>------------------------------<br>
     *
     * @param collectionName
     * @param options
     */
    public static void createCollection(String collectionName, DBObject options) {
        getdb().createCollection(collectionName, options);
    }

    /**
     * 删除
     * <br>------------------------------<br>
     *
     * @param collectionName
     */
    public static void dropCollection(String collectionName) {
        DBCollection collection = getCollection(collectionName);
        collection.drop();
    }

    /**
     * search
     */
    public static DBCursor search(String collectionName, DBObject searchCond, int s, int max) {
        DBCollection collection = getCollection(collectionName);
        DBCursor cursor = collection.find(searchCond).limit(max).skip(s);

        return cursor;
    }

    public static DBCursor search(String collectionName, DBObject searchCond, DBObject sortObj, int s, int max) {
        DBCollection collection = getCollection(collectionName);
        DBCursor cursor = collection.find(searchCond).sort(sortObj).limit(max).skip(s);

        return cursor;
    }

    public static DBCursor search(String collectionName, DBObject searchCond) {
        DBCollection collection = getCollection(collectionName);
        DBCursor cursor = collection.find(searchCond);

        return cursor;
    }

    public static DBCursor search(String collectionName, DBObject searchCond, DBObject sortObj) {
        DBCollection collection = getCollection(collectionName);
        DBCursor cursor = collection.find(searchCond).sort(sortObj);
        return cursor;
    }

    //唯一查询
    public static List<String> searchDistinct(String collectionName, DBObject searchCond, String field) {
        DBCollection collection = getCollection(collectionName);
        List<String> list = collection.distinct(field);
        return list;
    }

    //获取某年某周时间戳
    private static long getTimeOfWeek(int year, int week) {
        try {
            Calendar cal = Calendar.getInstance(); //设置年份
            cal.set(Calendar.YEAR, year); //设置周
            cal.set(Calendar.WEEK_OF_YEAR, week); //设置该周第一天为星期一
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String firstDayOfWeek = sdf.format(cal.getTime());

            long time = sdf.parse(firstDayOfWeek).getTime();
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //按周数统计
    public static JSONObject sumByWeek(
            String groupField,        //createtime
            String sumField,        //total
            String collectionName,    //MongoDBConstant.DB_NAME_PROGRAM
            DBObject condition) {    //new BasicDBObject()
        BasicDBObject match = new BasicDBObject(
                "$match", condition
        );

        DBObject group = new BasicDBObject();
        DBObject groupDate = new BasicDBObject();
        groupDate.put("year", new BasicDBObject("$year", "$" + groupField));
        groupDate.put("week", new BasicDBObject("$week", "$" + groupField));

        group.put("$group", new BasicDBObject("_id", groupDate).append(
                sumField, new BasicDBObject("$sum", 1)
        ));

        //按照时间倒序排列
        DBObject sort = new BasicDBObject();
        DBObject sortObj = new BasicDBObject();
        sortObj.put("_id", -1);
        sort.put("$sort", sortObj);

        AggregationOutput output = getCollection(collectionName).aggregate(match, group, sort);
        JSONArray arr = JSONArray.fromObject(output.results());
        JSONObject result = new JSONObject();
        int i = 1;
        long startDate = 0;
        long endDate = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Object temp : arr) {
            JSONObject obj = JSONObject.fromObject(temp);
            JSONObject dayinfo = obj.getJSONObject("_id");

            int year = dayinfo.getInt("year");
            int week = dayinfo.getInt("week");
            long timestamp = getTimeOfWeek(year, week);//获得年份某周时间戳


            long num = obj.getLong(sumField);
            //第一次取到的是最新的
            try {

                if (i == 1) {
                    endDate = timestamp;
                }
                //最后一次取到的是最老的
                if (i == arr.size()) {
                    startDate = timestamp;
                }
                result.put(timestamp + "", num);
                i++;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        return result;
    }

    //按月统计
    public static JSONObject sumByMonth(
            String groupField,        //createtime
            String sumField,        //total
            String collectionName,    //MongoDBConstant.DB_NAME_PROGRAM
            DBObject condition) {    //new BasicDBObject()
        BasicDBObject match = new BasicDBObject(
                "$match", condition
        );

        DBObject group = new BasicDBObject();
        DBObject groupDate = new BasicDBObject();
        groupDate.put("year", new BasicDBObject("$year", "$" + groupField));
        groupDate.put("month", new BasicDBObject("$month", "$" + groupField));

        group.put("$group", new BasicDBObject("_id", groupDate).append(
                sumField, new BasicDBObject("$sum", 1)
        ));

        //按照时间倒序排列
        DBObject sort = new BasicDBObject();
        DBObject sortObj = new BasicDBObject();
        sortObj.put("_id", -1);
        sort.put("$sort", sortObj);

        AggregationOutput output = getCollection(collectionName).aggregate(match, group, sort);
        JSONArray arr = JSONArray.fromObject(output.results());
        JSONObject result = new JSONObject();
        int i = 1;
        long startDate = 0;
        long endDate = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Object temp : arr) {
            JSONObject obj = JSONObject.fromObject(temp);
            JSONObject dayinfo = obj.getJSONObject("_id");
            String key = dayinfo.getInt("year") + "-"
                    + (dayinfo.getInt("month") < 10 ? ("0" + dayinfo.getInt("month")) : dayinfo.getInt("month")) + "-"
                    + "01";
            long num = obj.getLong(sumField);
            //第一次取到的是最新的
            try {

                if (i == 1) {
                    endDate = sdf.parse(key).getTime();
                }
                //最后一次取到的是最老的
                if (i == arr.size()) {
                    startDate = sdf.parse(key).getTime();
                }
                result.put(sdf.parse(key).getTime() + "", num);
                i++;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        return result;
    }


    //按年数统计
    public static JSONObject sumByYear(String groupField, String sumField, String collectionName, DBObject condition) {
        BasicDBObject match = new BasicDBObject(
                "$match", condition
        );

        DBObject group = new BasicDBObject();
        DBObject groupDate = new BasicDBObject();
        groupDate.put("year", new BasicDBObject("$year", "$" + groupField));
        group.put("$group", new BasicDBObject("_id", groupDate).append(
                sumField, new BasicDBObject("$sum", 1)
        ));

        //按照时间倒序排列
        DBObject sort = new BasicDBObject();
        DBObject sortObj = new BasicDBObject();
        sortObj.put("_id", -1);
        sort.put("$sort", sortObj);

        AggregationOutput output = getCollection(collectionName).aggregate(match, group, sort);
        JSONArray arr = JSONArray.fromObject(output.results());
        JSONObject result = new JSONObject();
        int i = 1;
        long startDate = 0;
        long endDate = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Object temp : arr) {
            JSONObject obj = JSONObject.fromObject(temp);
            JSONObject dayinfo = obj.getJSONObject("_id");
            String key = dayinfo.getInt("year") + "-01-01";

            long num = obj.getLong(sumField);
            //第一次取到的是最新的
            try {

                if (i == 1) {
                    endDate = sdf.parse(key).getTime();
                }
                //最后一次取到的是最老的
                if (i == arr.size()) {
                    startDate = sdf.parse(key).getTime();
                }
                result.put(sdf.parse(key).getTime() + "", num);
                i++;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        return result;
    }
}
