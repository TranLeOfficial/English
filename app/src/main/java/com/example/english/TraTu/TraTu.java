package com.example.english.TraTu;

/*
Tran Thanh Nhan 20/7/2020
* */

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.english.Database;
import com.example.english.R;
import com.example.english.Note.edtNote;

import java.util.Locale;

public class TraTu  extends AppCompatActivity implements SearchView.OnQueryTextListener {
    TextView textView;
    TextToSpeech textToSpeech;
    String worl;
    String mean;
    Database DB = new Database(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tra_tu);
        textView = (TextView) findViewById(R.id.tvhientratu);
        Bundle b = getIntent().getExtras();
        worl = b.getString("key_Word");
        mean = b.getString("key_Mean");
        Lichsu(worl);
        textView.setText(mean);
        //hiện tiêu đề;
        ActionBar ab = getSupportActionBar();
        //set mầu cho actionBar
        ab.setTitle(worl);
        //Hiện nút back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_tratu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        edtNote edtnote = new edtNote();
        switch (item.getItemId()) {
            case R.id.ThemNote:
                Bundle b = getIntent().getExtras();
                worl = b.getString("key_Word");
                mean = b.getString("key_Mean");
                mean=android.database.DatabaseUtils.sqlEscapeString(mean);
                int sbg = DB.GetCount("select * from GhiChu where TenGhiChu=\"" + worl.trim() + "\"");
                int count = DB.GetCount("Select * from GhiChu");
                count = count + 1;
                if (sbg == 1) {
                    AlertDialog.Builder al = new AlertDialog.Builder(this);
                    al.setTitle("Thông báo");
                    al.setMessage("Tên ghi chú đã tồn tại. ");
                    al.create().show();
                } else if (sbg == 0) {
                    /*Thêm 2 dấu nháy kép và 1 2 dấu xổ chéo để đọc được các kí tự đặc biệt, kí hiệu trong txt*/
                    DB.ExecuteSQL("Insert into GhiChu values("+count+",\""+worl.trim()+"\",\""+mean+"\")");
                    Toast.makeText(this, "Đã Thêm", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.item_search:
                Intent in = new Intent(getApplicationContext(), Search_tran.class);
                finish();
                startActivity(in);
                break;

            case R.id.itemdoc:
                textToSpeech.speak(worl, TextToSpeech.QUEUE_FLUSH, null);
                Toast.makeText(this, "Đang đọc", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Lichsu(String a) {
        Cursor c = DB.getCursor("select * from LichSuTraTu");
        int leng = c.getCount();
        if (leng < 50) {
            setLS(a, leng);
        } else {
            DB.ExecuteSQL("delete from LichSuTraTu where ID = 1");// xoa lich sử
            setLS(a, leng);
        }
    }

    //kiểm tra trùng lặp
    public void setLS(String a, int i) {
        Cursor c = DB.getCursor("select * from LichSuTraTu where work = '" + a + "'");
        int leng = c.getCount();
        if (leng == 0) {
            DB.ExecuteSQL("insert into LichSuTraTu values(" + i +1 + ",\"" + a + "\")");
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
