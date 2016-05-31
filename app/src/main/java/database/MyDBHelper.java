package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

/**
 * Created by zy on 15-4-18.
 */
public class MyDBHelper extends SQLiteOpenHelper{

    private String createHighExamSQL = "create table if not exists highExam " +
            "id int autoincrement primary key,exam text,first_subject text,second_subject text ";
    private String createOfficialSQL = "create table if not exists official " +
            "id int autoincrement primary key,first_subject text,second_subject text";
    private String createDiplomaSQL = "create table if not exists diploma " +
            "id int autoincrement primary key,first_subject text,second_subject text";

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private void createTables(SQLiteDatabase db){
        db.execSQL(createHighExamSQL);
        db.execSQL(createOfficialSQL);
        db.execSQL(createDiplomaSQL);
    }

    /**
     * 插入单条数据
     * @param tableName
     * @param contentValues
     */
    public void insert(String tableName,ContentValues contentValues){
        SQLiteDatabase db = this.getWritableDatabase();

        createTables(db);

        db.insert(tableName,null,contentValues);
        db.close();
    }


    /**
     * 设置sqlite事务提交，用于插入大量数据
     * @param tableName
     * @param contentValueses
     */
    public void insert(String tableName,List<ContentValues> contentValueses){
        SQLiteDatabase db = this.getWritableDatabase();

        createTables(db);

        db.beginTransaction();
        try{
            for (ContentValues contentValues : contentValueses){
                db.insert(tableName,null,contentValues);
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            Log.d("cout","database insert error ");
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }


    /**
     *
     * @param tableName
     * @param columns
     * @param selection
     * @param selectionArgs
     * @return
     */
    public Cursor getData(String tableName,String [] columns,String selection,String [] selectionArgs){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cs = db.query(tableName,columns,selection,selectionArgs,null,null,null);
        if (cs != null){
            cs.moveToFirst();
        }

        return cs;
    }

}
