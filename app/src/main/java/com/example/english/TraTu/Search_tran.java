package com.example.english.TraTu;

/*
Tran Thanh Nhan 20/7/2020
* */

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.english.Database;
import com.example.english.R;

import java.util.ArrayList;



public class Search_tran extends AppCompatActivity implements TextView.OnEditorActionListener {
    ArrayList<String> arrayList = null;
    ArrayAdapter adapter = null;
    EditText editText;
    ListView listView;
    String[] item = null;
    Integer[] icon = {R.drawable.ic_history_black_24dp, R.drawable.ic_search_black_24dp};
    Database DB = new Database(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_tran_layout);
        editText = (EditText) findViewById(R.id.edtSearch);
        final ImageView a = (ImageView) findViewById(R.id.iconSearch);
        listView = (ListView) findViewById(R.id.lv);
        //
        arrayList = new ArrayList<String>();
        //
        editText.setOnEditorActionListener(this);// sét sự kiện phím ok
        getHistory();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(editText.getText()+"" !=  "") {//lây dũ liệu tìm kiếm được
                    String a = ((TextView) view.findViewById(R.id.itemSearch)).getText().toString();// lay ra từ
                    SearchTu(a);
                }
                else
                {//lấy dữ liệu trong lịch sử
                    String a = ((TextView) view.findViewById(R.id.itemSearch1)).getText().toString();// lay ra từ
                    SearchTu(a);
                }
            }
        });
        //hiện từ gợi ý
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {
                String search = editText.getText().toString();
                if (editText.getText() + "" == "") {
                    getHistory();
                } else {
                    copy2ListView(search);
                    Search_tran.this.adapter.getFilter().filter(s);//lọc thông minh
                }
            }
        });

    }

    public void copy2ListView(String s) {
        arrayList.clear();
        if (s != "") {
            Cursor cu = DB.getCursor("select * from anhviet where tu like \"" + s + "%\" limit 0, 50");
            if (cu.moveToFirst()) {
                do {
                    arrayList.add(cu.getString(0));
                } while (cu.moveToNext());
                adapter = new ArrayAdapter<String>(this, R.layout.item_search, R.id.itemSearch, arrayList);
                listView.setAdapter(adapter);
            }
        }

    }

    //tìm kiếm từ
    public void SearchTu(String s) {
        Cursor cursor = DB.getCursor("select * from anhviet where tu like \"" + s + "\"");
        if (cursor.moveToFirst()) {
            Intent iDetail = new Intent(this, TraTu.class);
            Bundle b = new Bundle();
            b.putString("key_Word", cursor.getString(0));
            b.putString("key_Mean", cursor.getString(1));
            iDetail.putExtras(b);
            finish();
            startActivity(iDetail);
        }
    }

    // sự kiện ấn k trên phím ảo
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_GO) {
            SearchTu(editText.getText().toString());
            handled = true;
        }
        return handled;
    }

    //lấy lịch sử lên listView
    public void getHistory() {
        arrayList.clear();
        Cursor c = DB.getCursor("select * from LichSuTraTu");
        if (c.moveToLast()) {
            do {
                arrayList.add(c.getString(1));
            } while (c.moveToPrevious());
            adapter = new ArrayAdapter<String>(this, R.layout.history_layout, R.id.itemSearch1, arrayList);
            listView.setAdapter(adapter);
        }
    }
    public void DeleleHistory(String s)
    {
        DB.ExecuteSQL("delete from LichSuTraTu where work = \""+s+"\" ");
    }
}