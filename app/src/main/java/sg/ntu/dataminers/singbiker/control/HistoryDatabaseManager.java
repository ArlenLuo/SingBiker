package sg.ntu.dataminers.singbiker.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yvesl on 02.11.2016.
 */

public class HistoryDatabaseManager extends SQLiteOpenHelper{

    public static final String HISTORY_KEY = "id";
    public static final String HISTORY_DATE = "date";
    public static final String HISTORY_TRIP = "trip";

    public static final String HISTORY_TABLE_NAME = "history_table";
    public static final String HISTORY_TABLE_CREATE =
            "CREATE TABLE "+HISTORY_TABLE_NAME+" ("+
                    HISTORY_KEY+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    HISTORY_DATE+" TEXT DEFAULT '0', "+
                    HISTORY_TRIP+" BLOB NOT NULL);";

    public static final String HISTORY_TABLE_DROP =
            "DROP TABLE IF EXISTS "+HISTORY_TABLE_NAME+";";

    protected static final int HISTORY_DB_VERSION = 1;
    protected static final String HISTORY_DB_NAME = "history.db";

    public HistoryDatabaseManager(Context context) {
        this(context, HISTORY_DB_NAME, null, HISTORY_DB_VERSION);
    }

    public HistoryDatabaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HISTORY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(HISTORY_TABLE_DROP);
        onCreate(db);

    }
}
