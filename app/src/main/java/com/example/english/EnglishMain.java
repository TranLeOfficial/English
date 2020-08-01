package com.example.english;

/*
Tran Thanh Nhan 20/7/2020
* */

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.english.NguPhap.Main_NguPhap;
import com.example.english.Question.Main_Question;
import com.example.english.TraTu.Search_tran;

import java.io.IOException;
import java.util.ArrayList;


public class EnglishMain extends AppCompatActivity {

    ListView listView;
    Database DB = new Database(this);
    ArrayList<String> arrayList = null;
    String[] item = {"Tra Từ Điển", "Luyện Tập", "Ghi Chú", "Ngữ Pháp", "Lịch sử Luyện Tập", "Cài Đặt"};
    Integer[] icon = {R.drawable.timkiem, R.drawable.tets, R.drawable.ghi_chu, R.drawable.book2, R.drawable.book, R.drawable.settinghaha};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        try {
            DB.CopySDCard();
        } catch (IOException e) {
            e.printStackTrace();
        }

        arrayList = new ArrayList<>();
        //duyet mang
        for (String menu: item)
        {
            arrayList.add(menu);
        }
        ActionBar actionBar =getSupportActionBar();
        //set mau cho ActionBar

        actionBar.setTitle("EngList Support App");
        CustomListView adapter = new CustomListView(this, arrayList, icon);
        listView = findViewById(R.id.listItem);
        listView.setAdapter(adapter);
        //chuyen man hinh nhan CSDL
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    Intent intent = new Intent(getApplication(), Search_tran.class);
                    startActivity(intent);
                }

                if(position == 1)
                {
                    Intent intent = new Intent(getApplication(), Main_Question.class);
                    startActivity(intent);
                }

                if(position == 3)
                {
                    Intent intent = new Intent(getApplication(), Main_NguPhap.class);
                    startActivity(intent);
                }
            }
        });
    }
}