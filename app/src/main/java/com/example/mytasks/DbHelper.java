package com.example.mytasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DB_VER = 1;
    public static final String DB_TABLE_TASK = "Task";
    public static final String DB_TABLE_LIST = "List";
    public static final String DB_COLUMN_ID = "ID";
    public static final String DB_COLUMN_NAME = "Name";
    public static final String DB_COLUMN_ISIMPORTANT = "IsImportant";
    public static final String DB_COLUMN_ISDONE = "IsDone";
    public static final String DB_COLUMN_IDLIST = "IDList";
    public static final String DB_COLUMN_ICON = "Icon";
    public static final String DB_COLUMN_REMIND = "Remind";
    public static final String DB_COLUMN_DEADLINE = "Deadline";
    public static final String DB_COLUMN_REPEAT = "Repeat";
    public static final String DB_COLUMN_FILE = "File";
    public static final String DB_COLUMN_NOTE = "Note";
    public static final String DB_COLUMN_CREATEDTIME = "CreatedTime";
    public static final String DB_COLUMN_THEME = "Theme";


    public DbHelper(@Nullable Context context, String DB_NAME) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_TASK + " ( "
                + DB_COLUMN_ID + " integer primary key, "
                + DB_COLUMN_NAME + " text, "
                + DB_COLUMN_ISDONE + " integer, "
                + DB_COLUMN_ISIMPORTANT + " integer, "
                + DB_COLUMN_IDLIST + " integer, "
                + DB_COLUMN_REMIND + " text, "
                + DB_COLUMN_DEADLINE + " text, "
                + DB_COLUMN_REPEAT + " integer, "
                + DB_COLUMN_FILE + " integer, "
                + DB_COLUMN_NOTE + " text, "
                + DB_COLUMN_CREATEDTIME + " text)";
        db.execSQL(script);

        script = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_LIST + " ( "
                + DB_COLUMN_ID + " integer primary key, "
                + DB_COLUMN_NAME + " text, "
                + DB_COLUMN_ICON + " integer, "
                + DB_COLUMN_THEME + " integer)";
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String script = "DROP TABLE IF EXISTS " + DB_TABLE_TASK;
        db.execSQL(script);

        script = "DROP TABLE IF EXISTS " + DB_TABLE_LIST;
        db.execSQL(script);

        onCreate(db);
    }

    public void insertNewList(TaskList list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_NAME, list.getmName());
        values.put(DB_COLUMN_ICON, list.getmIcon());
        values.put(DB_COLUMN_THEME, list.getmTheme());
        db.insertWithOnConflict(DB_TABLE_LIST, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void insertNewTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_NAME, task.getmName());
        values.put(DB_COLUMN_ISDONE, task.getmIsDone());
        values.put(DB_COLUMN_ISIMPORTANT, task.getmIsImportant());
        values.put(DB_COLUMN_IDLIST, task.getmIDList());
        values.put(DB_COLUMN_REMIND, task.getmRemind());
        values.put(DB_COLUMN_DEADLINE, task.getmDeadline());
        values.put(DB_COLUMN_REPEAT, task.getmRepeat());
        values.put(DB_COLUMN_FILE, task.getmFile());
        values.put(DB_COLUMN_NOTE, task.getmNote());
        values.put(DB_COLUMN_CREATEDTIME, task.getmCreatedTime());
        Log.d("INSERT NEW TASK: ", task.getmName() + " " + task.getmIsDone() + " " + task.getmIsImportant());
        db.insertWithOnConflict(DB_TABLE_TASK, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE_TASK, DB_COLUMN_ID + " = ?", new String[]{String.valueOf(task.getmID())});
        db.close();
    }

    public void deleteList(TaskList list) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE_LIST, DB_COLUMN_ID + " = ?", new String[]{String.valueOf(list.getmID())});
        db.delete(DB_TABLE_TASK, DB_COLUMN_IDLIST + " = ?", new String[]{String.valueOf(list.getmID())});
        db.close();
    }

    public ArrayList<TaskList> getAllList() {
        ArrayList<TaskList> allList = new ArrayList<>();
        TaskList list = new TaskList();
        Task task = new Task();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQueryList = "SELECT * FROM " + DB_TABLE_LIST;
            String selectQueryTask = "SELECT * FROM " + DB_TABLE_TASK;

            Cursor cursorList = db.rawQuery(selectQueryList, null);
            Cursor cursorTask = db.rawQuery(selectQueryTask, null);

            cursorList.moveToPosition(-1);
            while (cursorList.moveToNext()) {
                list = new TaskList();
                list.setmID(cursorList.getInt(cursorList.getColumnIndex(DB_COLUMN_ID)));
                list.setmName(cursorList.getString(cursorList.getColumnIndex(DB_COLUMN_NAME)));
                list.setmIcon(cursorList.getInt(cursorList.getColumnIndex(DB_COLUMN_ICON)));
                list.setmTheme(cursorList.getInt(cursorList.getColumnIndex(DB_COLUMN_THEME)));
                cursorTask.moveToPosition(-1);
                while (cursorTask.moveToNext()) {
                    if (cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_IDLIST)) == list.getmID()) {
                        task = new Task();
                        task.setmID(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ID)));
                        task.setmName(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_NAME)));
                        task.setmIsDone(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ISDONE)));
                        task.setmIsImportant(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ISIMPORTANT)));
                        task.setmIDList(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_IDLIST)));
                        task.setmRemind(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_REMIND)));
                        task.setmDeadline(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_DEADLINE)));
                        task.setmRepeat(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_REPEAT)));
                        task.setmFile(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_FILE)));
                        task.setmNote(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_NOTE)));
                        task.setmCreatedTime(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_CREATEDTIME)));

                        list.getmListTasks().add(task);
                    }
                }
                allList.add(list);
            }

            cursorList.close();
            cursorTask.close();
            db.close();
        } catch (Exception e)
        {
            Log.v("DB GETALLLIST ", e.toString());
        }
        return allList;
    }

    public void updateList(TaskList list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_NAME, list.getmName());
        values.put(DB_COLUMN_ICON, list.getmIcon());
        values.put(DB_COLUMN_THEME, list.getmTheme());
        db.update(DB_TABLE_LIST, values, DB_COLUMN_ID + " = ?", new String[]{String.valueOf(list.getmID())});
        db.close();
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_NAME, task.getmName());
        values.put(DB_COLUMN_ISDONE, task.getmIsDone());
        values.put(DB_COLUMN_ISIMPORTANT, task.getmIsImportant());
        values.put(DB_COLUMN_REMIND, task.getmRemind());
        values.put(DB_COLUMN_DEADLINE, task.getmDeadline());
        values.put(DB_COLUMN_REPEAT, task.getmRepeat());
        values.put(DB_COLUMN_FILE, task.getmFile());
        values.put(DB_COLUMN_NOTE, task.getmNote());
        values.put(DB_COLUMN_CREATEDTIME, task.getmCreatedTime());
        db.update(DB_TABLE_TASK, values, DB_COLUMN_ID + " = ?", new String[]{String.valueOf(task.getmID())});
        db.close();
    }

    public TaskList getImportantList() {
        TaskList list = new TaskList();
        list.setmName("Quan Trọng");
        Task task = new Task();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQueryTask = "SELECT * FROM " + DB_TABLE_TASK;
        Cursor cursorTask = db.rawQuery(selectQueryTask, null);
        cursorTask.moveToPosition(-1);
        while (cursorTask.moveToNext()) {
            task.setmID(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ID)));
            task.setmName(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_NAME)));
            task.setmIsDone(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ISDONE)));
            task.setmIsImportant(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ISIMPORTANT)));
            task.setmRemind(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_REMIND)));
            task.setmDeadline(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_DEADLINE)));
            task.setmRepeat(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_REPEAT)));
            task.setmFile(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_FILE)));
            task.setmNote(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_NOTE)));
            task.setmCreatedTime(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_CREATEDTIME)));
            list.getmListTasks().add(task);
        }
        db.close();
        return list;
    }

    public Task getTask(int ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DB_TABLE_TASK + " WHERE " + DB_COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ID)});
        Task task = new Task();
        cursor.moveToFirst();
        task.setmID(cursor.getInt(cursor.getColumnIndex(DB_COLUMN_ID)));
        task.setmName(cursor.getString(cursor.getColumnIndex(DB_COLUMN_NAME)));
        task.setmIsDone(cursor.getInt(cursor.getColumnIndex(DB_COLUMN_ISDONE)));
        task.setmIsImportant(cursor.getInt(cursor.getColumnIndex(DB_COLUMN_ISIMPORTANT)));
        task.setmIDList(cursor.getInt(cursor.getColumnIndex(DB_COLUMN_IDLIST)));
        task.setmRemind(cursor.getString(cursor.getColumnIndex(DB_COLUMN_REMIND)));
        task.setmDeadline(cursor.getString(cursor.getColumnIndex(DB_COLUMN_DEADLINE)));
        task.setmRepeat(cursor.getInt(cursor.getColumnIndex(DB_COLUMN_REPEAT)));
        task.setmFile(cursor.getInt(cursor.getColumnIndex(DB_COLUMN_FILE)));
        task.setmNote(cursor.getString(cursor.getColumnIndex(DB_COLUMN_NOTE)));
        task.setmCreatedTime(cursor.getString(cursor.getColumnIndex(DB_COLUMN_CREATEDTIME)));
        cursor.close();
        db.close();
        return task;
    }

    public TaskList getList(int ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryList = "SELECT * FROM " + DB_TABLE_LIST + " WHERE " + DB_COLUMN_ID + " = ?";
        String queryTask = "SELECT * FROM " + DB_TABLE_TASK + " WHERE " + DB_COLUMN_IDLIST + " = ?";
        Cursor cursorList = db.rawQuery(queryList, new String[]{String.valueOf(ID)});
        Cursor cursorTask = db.rawQuery(queryTask, new String[]{String.valueOf(ID)});
        TaskList list = new TaskList();
        Task task = new Task();
        Log.d("Cursor List: ",ID + " " +  cursorList.getColumnIndex(DB_COLUMN_ID));
        cursorList.moveToFirst();
        list.setmID(cursorList.getInt(cursorList.getColumnIndex(DB_COLUMN_ID)));
        list.setmName(cursorList.getString(cursorList.getColumnIndex(DB_COLUMN_NAME)));
        list.setmIcon(cursorList.getInt(cursorList.getColumnIndex(DB_COLUMN_ICON)));
        list.setmTheme(cursorList.getInt(cursorList.getColumnIndex(DB_COLUMN_THEME)));
        cursorTask.moveToPosition(-1);
        while (cursorTask.moveToNext()) {
            task = new Task();
            task.setmID(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ID)));
            task.setmName(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_NAME)));
            task.setmIsDone(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ISDONE)));
            task.setmIsImportant(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ISIMPORTANT)));
            task.setmIDList(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_IDLIST)));
            task.setmRemind(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_REMIND)));
            task.setmDeadline(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_DEADLINE)));
            task.setmRepeat(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_REPEAT)));
            task.setmFile(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_FILE)));
            task.setmNote(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_NOTE)));
            task.setmCreatedTime(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_CREATEDTIME)));

            list.getmListTasks().add(task);
        }
        cursorList.close();
        cursorTask.close();
        db.close();
        return list;
    }
}
