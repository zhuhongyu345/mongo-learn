package mongodb;

import com.mongodb.DBObject;

public class CursorObject {
    private int skip;

    private int limit;

    private Sort sort;

    public CursorObject skip(int skip) {
        this.skip = skip;
        return this;
    }

    public CursorObject limit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }

    public Sort sort() {
        if (this.sort == null) {
            this.sort = new Sort();
        }
        return this.sort;
    }

    public DBObject getSortObject() {
        if (this.sort == null) {
            return null;
        }
        return this.sort.getSortObject();
    }

}
