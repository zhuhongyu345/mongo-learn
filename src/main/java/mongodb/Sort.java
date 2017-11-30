package mongodb;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Sort {

    private Map<String, Order> field = new LinkedHashMap<String, Order>();

    public Sort() {
    }

    public Sort(String key, Order order) {
        field.put(key, order);
    }

    public Sort on(String key, Order order) {
        field.put(key, order);
        return this;
    }

    public DBObject getSortObject() {
        DBObject dbo = new BasicDBObject();
        for (String k : field.keySet()) {
            dbo.put(k, (field.get(k).equals(Order.ASC) ? 1 : -1));
        }
        return dbo;
    }


}
