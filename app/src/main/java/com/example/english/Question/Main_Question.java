package com.example.english.Question;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.english.Database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import com.example.english.R;

public class Main_Question extends AppCompatActivity {
    ListView listView;
    RadioButton rA, rB, rC, rD;
    Adapter_Question adapter_question;
    ArrayList<Question> arrTest = null;
    ArrayList<Integer> ID;
    String IDtich;
    Database DB = new Database(this);
    Random random = new Random();
    Chronometer time;
    FloatingActionButton floatingActionButton;
    int d = 0, a = 0;//khởi tạo biến đếm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_layout);
        //
        floatingActionButton = (FloatingActionButton) findViewById(R.id.FabTest);
        listView = (ListView) findViewById(R.id.lvquiz);
        time = (Chronometer) findViewById(R.id.chronometer2);
        //
        time.start();//bắt đầu chạy time
        //
        ID = new ArrayList<>();
        arrTest = new ArrayList<Question>();
        adapter_question = new Adapter_Question(Main_Question.this, R.layout.item_layout_quiz, arrTest, a);
        listView.setAdapter(adapter_question);

        duyet();
        //sư kiện click tính điểm
        DoneTest();
    }

    public void DoneTest() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a == 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Main_Question.this);
                    dialog.setMessage("Bạn muốn kết thúc bài thi!");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            a++;
                            setMau();//cập nhật lại đáp án và hiện trên listView
                            TinhDiem();//gọi đến phương thức tính điểm\
                            adapter_question = new Adapter_Question(Main_Question.this, R.layout.item_layout_quiz, arrTest, a);
                            listView.setAdapter(adapter_question);
                            Hien(d);
                        }
                    });
                    dialog.setNegativeButton("Canel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.create().show();
                } else {
                    Hien(d);
                }
            }
        });
    }

    //Tính điểm
    public void TinhDiem() {
        IDtich = "";//láy ra vi tri da tích
        ArrayList<Question> ArrayQuestion = adapter_question.myArray;// gợi myArray
        for (int i = 0; i < ArrayQuestion.size(); i++) {
            Question questions = ArrayQuestion.get(i);
            if (questions.isSelectedA()) {
                if (kiemtra(questions.getA().substring(0, 1), questions.getDung()))
                    d++;
                IDtich = IDtich + 1 + ",";
            }
            if (questions.isSelectedB()) {
                if (kiemtra(questions.getB().substring(0, 1), questions.getDung()))
                    d++;
                IDtich = IDtich + 2 + ",";
            }
            if (questions.isSelectedC()) {
                if (kiemtra(questions.getC().substring(0, 1), questions.getDung()))
                    d++;
                IDtich = IDtich + 3 + ",";
            }
            if (questions.isSelectedD()) {
                if (kiemtra(questions.getD().substring(0, 1), questions.getDung()))
                    d++;
                IDtich = IDtich + 4 + ",";
            }
            if (!questions.isSelectedA() && !questions.isSelectedB() && !questions.isSelectedC() && !questions.isSelectedD()) {
                IDtich = IDtich + 0 + ",";
            }
        }
    }

    //hiển thị điểm đã làm được
    public void Hien(int d) {
        time.stop();// dừng tính time
        String idCauhoi = "";
        for (int i = 0; i < ID.size(); i++) {
            idCauhoi = idCauhoi + ID.get(i) + ",";
        }
        Cursor cu = DB.getCursor("Select * from LichSuTest where IDCAUHOI = '" + idCauhoi + "'");
        if (cu.getCount() == 0) {
            WriteHistory(getIDHistory(),idCauhoi, IDtich, d);
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(Main_Question.this);
        dialog.setTitle("Kết quả Test");
        dialog.setMessage("Điểm đạt : " + d + "/10" + "\n" + "Time: " + time.getText());
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.create().show();
    }

    //kiểm tra đấp án đúng  thì trả ra true ngược lại
    public boolean kiemtra(String a, String b) {
        if (a.contains(b.replaceAll("\\s+", ""))) {
            return true;
        } else {
            return false;
        }
    }

    public void duyet() {
        RanRom();
        for (int i = 0; i < ID.size(); i++) {
            load(ID.get(i));// dưa dữ liệu nên listView theo id
        }
        adapter_question.notifyDataSetChanged();
    }

    //đọc đọc hỏi ở CSDL đưa vào ArrayLisst theo ID
    public void load(int ID) {
        Question test = new Question();
        Cursor cursor = DB.getCursor("select * from Question where ID = '" + ID + "'");
        if (cursor.moveToFirst()) {
            do {
                test.setId(cursor.getInt(0));
                test.setCauhoi(cursor.getString(1));
                test.setA(cursor.getString(2));
                test.setB(cursor.getString(3));
                test.setC(cursor.getString(4));
                test.setD(cursor.getString(5));
                test.setDung(cursor.getString(6));
                arrTest.add(test);
            } while (cursor.moveToNext());
        }

    }

    //RamRom 10 câu hỏi
    public ArrayList RanRom() {
        int iNew = 0;
        int dem = DB.GetCount("SELECT * FROM Question");
        for (int i = 0; i < 10; ) {
            iNew = random.nextInt(dem);
            if (!ID.contains(iNew) || iNew == 0) {
                i++;
                ID.add(iNew);
            }
        }
        return ID;
    }

    //ghi lịch sử vào cơ sở dữ liệu
    public void WriteHistory(int id, String idCauhoi, String idtich, int diem) {
        DB.ExecuteSQL("INSERT INTO LichSuTest VALUES('" + (id+1) + "','" + getDateTime() + "'," +
                "'" + idCauhoi + "', " +
                "'" + idtich + "', " +
                "" + diem + ",'" +
                "" + time.getText() + "')");
    }
    //lấy ra id của phần tử cuối trong bảng lichsu
    public int getIDHistory()
    {
        int id = 0;
        Cursor cu = DB.getCursor("select id from LichSuTest");
        if (cu.moveToLast())
        {
            id = cu.getInt(0);
        }
        return id;
    }

    //lấy ra thời gian và ngày tháng
    public String getDateTime() {
        String time;
        Date date = new Date();
        String strDateFormat = "dd/MM/yyyy";
        String strDateFormat24 = "HH:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);//láy ra ngày
        SimpleDateFormat sdft = new SimpleDateFormat(strDateFormat24);//lấy ra giờ
        time = sdf.format(date) + "   " + sdft.format(date);
        return time;
    }

    //để sét mầy cho đáp án đúng vè ddaps án sai
    public void setMau() {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                for (int i = 0; i < visibleItemCount; i++) {
                    View v = listView.getChildAt(i);
                    TextView IDCauHoi = (TextView) v.findViewById(R.id.txtID);
                    rA = (RadioButton) v.findViewById(R.id.Radiobt_A);
                    rB = (RadioButton) v.findViewById(R.id.Radiobt_B);
                    rC = (RadioButton) v.findViewById(R.id.Radiobt_C);
                    rD = (RadioButton) v.findViewById(R.id.Radiobt_D);
                    String DapAn = "";
                    DapAn = getDung(Integer.parseInt(IDCauHoi.getText().toString()));//lấy ra đáp án đúng từ id
                    RadioGroup rg = (RadioGroup) v.findViewById(R.id.radioGroup);
                    int Check = rg.getCheckedRadioButtonId();
                    switch (Check) {
                        //sét mầu đáp án đã tích
                        case R.id.Radiobt_A:
                            rA.setTextColor(getResources().getColor(R.color.colorSelect));
                            break;
                        case R.id.Radiobt_B:
                            rB.setTextColor(getResources().getColor(R.color.colorSelect));
                            break;
                        case R.id.Radiobt_C:
                            rC.setTextColor(getResources().getColor(R.color.colorSelect));
                            break;
                        case R.id.Radiobt_D:
                            rD.setTextColor(getResources().getColor(R.color.colorSelect));
                            break;
                    }
                    if (kiemtra(rA.getText().toString().substring(0, 1), DapAn)) {
                        setDung(1);
                    }

                    if (kiemtra(rB.getText().toString().substring(0, 1), DapAn)) {
                        setDung(2);
                    }
                    if (kiemtra(rC.getText().toString().substring(0, 1), DapAn)) {
                        setDung(3);
                    }
                    if (kiemtra(rD.getText().toString().substring(0, 1), DapAn)) {
                        setDung(4);
                    }

                }
            }
        });
    }

    //set mầy đáp án đúng
    public void setDung(int i) {
        switch (i) {
            case 1:
                rA.setTextColor(getResources().getColor(R.color.colordung));
                break;
            case 2:
                rB.setTextColor(getResources().getColor(R.color.colordung));
                break;
            case 3:
                rC.setTextColor(getResources().getColor(R.color.colordung));
                break;
            case 4:
                rD.setTextColor(getResources().getColor(R.color.colordung));
                break;
        }
    }

    //trả ra đáp án đúng theo ID
    public String getDung(int id) {
        String dung = "";
        ArrayList<Question> ArrayQuestion = adapter_question.myArray;// gợi myArray
        for (int i = 0; i < ArrayQuestion.size(); i++) {
            Question question = ArrayQuestion.get(i);
            if (question.getId() == id) {
                dung = question.getDung();
            }
        }
        return dung;
    }
}
