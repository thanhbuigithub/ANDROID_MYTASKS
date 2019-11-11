package com.example.mytasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "MyTasksDB";
    private static final int DB_VER = 1;
    public static final String DB_TABLE_TASK = "Task";
    public static final String DB_TABLE_LIST = "List";
    public static final String DB_COLUMN_ID = "ID";
    public static final String DB_COLUMN_NAME = "Name";
    public static final String DB_COLUMN_ISIMPORTANT = "IsImportant";
    public static final String DB_COLUMN_ISDONE = "IsDone";
    public static final String DB_COLUMN_IDLIST = "IDList";
    public static final String DB_COLUMN_ICON = "Icon";


    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "CREATE TABLE " + DB_TABLE_TASK + " ( "
                + DB_COLUMN_ID + " integer primary key, "
                + DB_COLUMN_NAME + " text, "
                + DB_COLUMN_ISDONE + " integer, "
                + DB_COLUMN_ISIMPORTANT + " integer, "
                + DB_COLUMN_IDLIST + " integer)";
        db.execSQL(script);

        script = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_LIST + " ( "
                + DB_COLUMN_ID + " integer primary key, "
                + DB_COLUMN_NAME + " text, "
                + DB_COLUMN_ICON + " integer)";
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

    public void insertNewList(TaskList list){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_NAME,list.getmName());
        values.put(DB_COLUMN_ICON,list.getmIcon());
        db.insertWithOnConflict(DB_TABLE_LIST,null,values,SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void insertNewTaskToList(Task task, TaskList list){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_NAME,task.getmName());
        values.put(DB_COLUMN_ISDONE, task.getmIsDone());
        values.put(DB_COLUMN_ISIMPORTANT, task.getmIsImportant());
        values.put(DB_COLUMN_IDLIST, list.getmID());
        db.insertWithOnConflict(DB_TABLE_LIST,null,values,SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void deleteTask(Task task)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE_TASK, DB_COLUMN_ID + " = ?", new String[]{String.valueOf(task.getmID())});
        db.close();
    }

    public void deleteList(TaskList list)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE_LIST, DB_COLUMN_ID + " = ?", new String[]{String.valueOf(list.getmID())});
        db.delete(DB_TABLE_TASK, DB_COLUMN_IDLIST + " = ?", new String[]{String.valueOf(list.getmID())});
    }

    public ArrayList<TaskList> getAllList(){
        ArrayList<TaskList> allList = new ArrayList<>();
        TaskList list = new TaskList();
        Task task = new Task();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQueryList = "SELECT * FROM " + DB_TABLE_LIST;
        String selectQueryTask = "SELECT * FROM " + DB_TABLE_TASK;

        Cursor cursorList = db.rawQuery(selectQueryList,null);
        Cursor cursorTask = db.rawQuery(selectQueryTask,null);

        cursorList.moveToPosition(-1);
        while(cursorList.moveToNext()){
            list = new TaskList();
            list.setmID(cursorList.getInt(cursorList.getColumnIndex(DB_COLUMN_ID)));
            list.setmName(cursorList.getString(cursorList.getColumnIndex(DB_COLUMN_NAME)));
            list.setmIcon(cursorList.getInt(cursorList.getColumnIndex(DB_COLUMN_ICON)));
            cursorTask.moveToPosition(-1);
            while (cursorTask.moveToNext()){
                task = new Task();
                task.setmID(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ID)));

                task.setmName(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_NAME)));

                task.setmIsDone(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ISDONE)));

                task.setmIsImportant(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ISIMPORTANT)));

                task.setmIDList(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_IDLIST)));

                list.getmListTasks().add(task);
            }
            allList.add(list);
        }
        cursorList.close();
        cursorTask.close();

        return allList;
    }

    public void updateList(TaskList list){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_NAME, list.getmName());
        values.put(DB_COLUMN_ICON,list.getmIcon());
        db.update(DB_TABLE_LIST,values,DB_COLUMN_ID + " = ?", new String[] {String.valueOf(list.getmID())});
        db.close();
    }

    public void updateTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_NAME, task.getmName());
        values.put(DB_COLUMN_ISDONE, task.getmIsDone());
        values.put(DB_COLUMN_ISIMPORTANT, task.getmIsImportant());
        db.update(DB_TABLE_TASK,values,DB_COLUMN_ID + " = ?", new String[] {String.valueOf(task.getmID())});
        db.close();
    }

    public TaskList getImportantList(){
        TaskList list = new TaskList();
        list.setmName("Quan Trọng");
        Task task = new Task();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQueryTask = "SELECT * FROM " + DB_TABLE_TASK;
        Cursor cursorTask = db.rawQuery(selectQueryTask,null);
        cursorTask.moveToPosition(-1);
        while (cursorTask.moveToNext()){
            task.setmID(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ID)));

            task.setmName(cursorTask.getString(cursorTask.getColumnIndex(DB_COLUMN_NAME)));

            task.setmIsDone(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ISDONE)));

            task.setmIsImportant(cursorTask.getInt(cursorTask.getColumnIndex(DB_COLUMN_ISIMPORTANT)));

            list.getmListTasks().add(task);
        }

        return list;
    }
}
