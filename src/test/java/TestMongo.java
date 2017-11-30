import com.mongodb.MongoClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

public class TestMongo {

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient("172.17.7.25", 27017);
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(mongoClient, "mars");
        MongoTemplate mars = new MongoTemplate(simpleMongoDbFactory);
        System.out.println(mars);
    }
}
