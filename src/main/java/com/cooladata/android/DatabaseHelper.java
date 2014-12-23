package com.cooladata.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String EVENT_TABLE_NAME = "events";
    private static final String ID_FIELD = "id";
    private static final String EVENT_FIELD = "event";
    private static final String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + EVENT_TABLE_NAME + " (" + ID_FIELD
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EVENT_FIELD + " TEXT);";
    private static DatabaseHelper instance;
    private File file;

    private DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        file = context.getDatabasePath(Constants.DATABASE_NAME);
    }

    static DatabaseHelper getDatabaseHelper(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // INTEGER PRIMARY KEY AUTOINCREMENT guarantees that all generated
        // values
        // for the field will be monotonically increasing and unique over the
        // lifetime of the table, even if rows get removed
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    synchronized long addEvent(String event) {
        long result = -1;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(EVENT_FIELD, event);
            result = db.insert(EVENT_TABLE_NAME, null, contentValues);
            if (result == -1) {
                Log.w(CoolaDataTracker.TAG, "Insert failed");
            }
        } catch (SQLiteException e) {
            Log.e(CoolaDataTracker.TAG, "addEvent failed", e);
            // Not much we can do, just start fresh
            delete();
        } finally {
            close();
        }
        return result;
    }

    synchronized Pair<Long, List<String>> getEvents(int limit) throws JSONException {
        long maxId = -1;
        List<String> events = new LinkedList<String>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(EVENT_TABLE_NAME,
                    new String[]{ID_FIELD, EVENT_FIELD},
                    null, null,
                    null, null, ID_FIELD + " ASC", limit >= 0 ? "" + limit : null);

            long calcMax = 0;
            
            while (cursor.moveToNext()) {
                long eventId = cursor.getLong(0);
                String event = cursor.getString(1);
                events.add(event);
                maxId = eventId;
                if (maxId > calcMax) { calcMax = maxId; }
            }
            if (calcMax != maxId && maxId >= 0) {
            	Log.e(CoolaDataTracker.TAG, "ERROR!! getEvents maxId: " + maxId + "; calcMax " + calcMax);
           }
             
        } catch (SQLiteException e) {
            Log.e(CoolaDataTracker.TAG, "getEvents failed", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return new Pair<Long, List<String>>(maxId, events);
    }

    synchronized long getEventCount() {
        long numberRows = 0;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + EVENT_TABLE_NAME;
            SQLiteStatement statement = db.compileStatement(query);
            numberRows = statement.simpleQueryForLong();
        } catch (SQLiteException e) {
            Log.e(CoolaDataTracker.TAG, "getNumberRows failed", e);
        } finally {
            close();
        }
        return numberRows;
    }

    synchronized long getNthEventId(long n) {
        long nthEventId = -1;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT " + ID_FIELD + " FROM " + EVENT_TABLE_NAME + " LIMIT 1 OFFSET " + (n - 1);
            SQLiteStatement statement = db.compileStatement(query);
            nthEventId = -1;
            try {
                nthEventId = statement.simpleQueryForLong();
            } catch (SQLiteDoneException e) {
                Log.w(CoolaDataTracker.TAG, e);
            }
        } catch (SQLiteException e) {
            Log.e(CoolaDataTracker.TAG, "getNthEventId failed", e);
        } finally {
            close();
        }
        return nthEventId;
    }

    synchronized int removeEvents(long maxId) {
    	int delCount = 0;
    	try {
    		SQLiteDatabase db = getWritableDatabase();
    		delCount = db.delete(EVENT_TABLE_NAME, ID_FIELD + " <= " + maxId, null);
    	} catch (SQLiteException e) {
    		Log.e(CoolaDataTracker.TAG, "removeEvents failed", e);
    	} finally {
    		close();
    	}
    	return delCount;
    }

    synchronized void removeEvent(long id) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(EVENT_TABLE_NAME, ID_FIELD + " = " + id, null);
        } catch (SQLiteException e) {
            Log.e(CoolaDataTracker.TAG, "removeEvent failed", e);
        } finally {
            close();
        }
    }

    private void delete() {
        try {
            close();
            file.delete();
        } catch (SecurityException e) {
            Log.e(CoolaDataTracker.TAG, "delete failed", e);
        }
    }
}
