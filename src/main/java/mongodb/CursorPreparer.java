package mongodb;

import com.mongodb.DBCursor;

public interface CursorPreparer {
    DBCursor prepare(DBCursor cursor);
}
