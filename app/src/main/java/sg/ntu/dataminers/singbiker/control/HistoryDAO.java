package sg.ntu.dataminers.singbiker.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import sg.ntu.dataminers.singbiker.entity.History;
import sg.ntu.dataminers.singbiker.entity.Trip;

import static sg.ntu.dataminers.singbiker.control.HistoryDatabaseManager.*;

public class HistoryDAO {

    private SQLiteDatabase db = null;
    private HistoryDatabaseManager dbmanager = null;
    private Gson gson;

    public HistoryDAO(Context context) {
        this.dbmanager = new HistoryDatabaseManager(context);
        this.gson = new Gson();
    }

    public SQLiteDatabase open() {
        db = dbmanager.getWritableDatabase();
        return db;
    }

    public void close() {
        dbmanager.close();
    }

    public SQLiteDatabase getDB() {
        return db;
    }

    public void addHistory(History h) {
        ContentValues content = new ContentValues();
        content.put(HISTORY_DATE, Long.toString(h.getDate().getTime()));
        content.put(HISTORY_TRIP, gson.toJson(h.getTrip()).getBytes());
        db.insert(HISTORY_TABLE_NAME, null, content);
    }

    public void removeHistory(long id) {
        db.delete(HISTORY_TABLE_NAME, HISTORY_KEY+" = ?", new String[]{Long.toString(id)});
    }

    public void updateHistory(History h) {
        ContentValues content = new ContentValues();
        content.put(HISTORY_DATE, Long.toString(h.getDate().getTime()));
        content.put(HISTORY_TRIP, gson.toJson(h.getTrip()).getBytes());
        db.update(HISTORY_TABLE_NAME, content, HISTORY_KEY+" = ?", new String[] {Long.toString(h.getDBId())});
    }

    public History getHistory(long id)  {
        Cursor c = db.rawQuery("SELECT * FROM "+HISTORY_TABLE_NAME+" WHERE id = ?", new String[] {Long.toString(id)});
        c.moveToNext();

        Long idH = c.getLong(c.getColumnIndex(HISTORY_KEY));
        Date dH = Date.valueOf(c.getString(c.getColumnIndex(HISTORY_DATE)));
        byte[] blob = c.getBlob(c.getColumnIndex(HISTORY_TRIP));
        Trip tH = gson.fromJson(new String(blob), new TypeToken<Trip>(){}.getType());

        c.close();
        return new History(tH, dH, idH);
    }

    public List<History> getAllHistory() {
        Cursor c = db.rawQuery("SELECT * FROM "+HISTORY_TABLE_NAME, new String[] {});
        List<History> list = new ArrayList<History>();

        History h;
        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()) {
            Long idH = c.getLong(c.getColumnIndex(HISTORY_KEY));
            Date dH = new Date(Long.parseLong(c.getString(c.getColumnIndex(HISTORY_DATE))));
            byte[] blob = c.getBlob(c.getColumnIndex(HISTORY_TRIP));
            Trip tH = gson.fromJson(new String(blob), new TypeToken<Trip>() {}.getType());

            h = new History(tH, dH, idH);
            list.add(h);
        }

        c.close();
        return list;
    }

    public void wipeHistoryTable() {
        db.execSQL(HISTORY_TABLE_DROP);
        dbmanager.onCreate(db);
    }

}
