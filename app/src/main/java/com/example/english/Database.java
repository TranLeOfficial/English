package com.example.english;

/*
Tran Thanh Nhan 20/7/2020
* */

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.core.app.CoreComponentFactory;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Database extends SQLiteOpenHelper {

    //khai bao cac bien nhan du lieu
    private Context myContext;
    private SQLiteDatabase myDatabase;
    private static final String DP_PATH = "data/data/com.example.english/luutru.sqlite";
    private static final String DP_NAME = "luutru.sqlite";
    private static final int version = 1;
    //static final String TABLE_NAME = "English";


    //contructor
    public Database(Context context) {
        super(context, DP_NAME,null, version);
        this.myContext = context;
    }
    //Mo csdl
    public void OpenDB() {
        myDatabase = SQLiteDatabase.openDatabase(DP_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
    //Dong CSDL
    public void CloseDB() {
        myDatabase.close();
    }


    public void CopySDCard() throws IOException
    {
        boolean check = false;
        try
        {
            File file = new File(DP_PATH);
            check=file.exists();
            if(check)
            {
                this.close();
            }
            else
            {
                if (!check)
                {
                    this.getReadableDatabase();
                    InputStream myInput = myContext.getAssets().open(DP_NAME);
                    String outFilename = DP_PATH;
                    OutputStream myOutput=new FileOutputStream(outFilename);
                    byte[]buffer=new byte[1024];
                    int lenght;
                    while ((lenght=myInput.read(buffer))>0)
                    {
                        myOutput.write(buffer,0,lenght);
                    }
                    myOutput.flush();
                    myOutput.close();
                    myInput.close();
                }
            }
        }catch (Exception ex)
        {
            ex.getMessage();
        }
    }
    //getCount
    public int GetCount(String sql)
    {
        OpenDB();
        Cursor cur=myDatabase.rawQuery(sql,null);
        int count = cur.getCount();
        CloseDB();
        return count;
    }
    //thuc thi
    public void ExecuteSQL(String sql)
    {
           OpenDB();
           myDatabase.execSQL(sql);
    }
    //Cursor
    public Cursor getCursor(String sql)
    {
        OpenDB();
        return myDatabase.rawQuery(sql,null);
    }

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
