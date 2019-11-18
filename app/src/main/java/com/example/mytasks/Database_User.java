package com.example.mytasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database_User extends SQLiteOpenHelper {
    public static final String DATABASE ="signup.db";
    public static final String TABLE ="signup_user";
    public static final String COL_ID ="ID";
    public static final String COL_USERNAME ="username";
    public static final String COL_PASS ="password";

    public Database_User(Context context) {
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS signup_user (ID INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,username TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE);
        onCreate(sqLiteDatabase);
    }

    public long addUser(String name,String user, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("username",user);
        contentValues.put("password",password);
        long res = db.insert("signup_user",null,contentValues);
        db.close();
        return res;
    }

    public boolean checkUser(String username, String password){
        String[] columns = { COL_ID };
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_USERNAME + "=?" + " and " + COL_PASS + "=?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE,columns,selection,selectionArgs,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if(count>0)
            return  true;
        return false;
    }

    public boolean checkAccount (String username){
        String[] columns = { COL_ID };
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_USERNAME + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE,columns,selection,selectionArgs,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if(count>0)
            return  true;
        return false;
    }
}
