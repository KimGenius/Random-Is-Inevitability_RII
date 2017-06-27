package simsim.geniusk.randommachine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends AppCompatActivity {
    private Button result_btn;
    private TextView result_text, edit_clean;
    private EditText rand_text;
    private ImageView result_delete_all;
    private int rand_num;
    private ArrayList<String> result_arr;
    private String result;
    private int i = -1;
    private final String dbName = "random_is";
    private final String tableName = "absolute";
    private ArrayList<HashMap<String, String>> data;
    private RecyclerView recyclerView;
    private GridLayoutManager recyclerViewLayoutManager;
    private DataListAdapter adapter;
    private SQLiteDatabase sampleDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result_arr = new ArrayList<String>();
        data = new ArrayList<HashMap<String, String>>();
        result = "";
        rand_num = 0;

        result_btn = (Button) findViewById(R.id.result_btn);
        result_text = (TextView) findViewById(R.id.result_text);
        recyclerView = (RecyclerView) findViewById(R.id.data_list);
        rand_text = (EditText) findViewById(R.id.rand_text);
        edit_clean = (TextView) findViewById(R.id.edit_clean);
        result_delete_all = (ImageView) findViewById(R.id.result_delete_all);

        refresh();

        //랜덤!
        result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = rand_text.getText().toString().trim();
                result_arr = new ArrayList<String>();
                for (String arr_str : result.split(",")) {
                    if (!arr_str.trim().equals(""))
                        result_arr.add(arr_str);
                }
                Log.i("infor_size", String.valueOf(result_arr.size()));
                rand_num = (int) (Math.random() * result_arr.size());
                if (result.equals("")) {
                    Toast.makeText(getApplicationContext(), "리스트를 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else if (result_arr.size() == 0) {
                    Toast.makeText(getApplicationContext(), "리스트를 정확히 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                            .setTitleText("Loading");
                    pDialog.show();
                    pDialog.setCancelable(false);
                    new CountDownTimer(200 * 7, 200) {
                        public void onTick(long millisUntilFinished) {
                            i++;
                            switch (i) {
                                case 0:
                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
                                    break;
                                case 1:
                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_50));
                                    break;
                                case 2:
                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                                    break;
                                case 3:
                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_20));
                                    break;
                                case 4:
                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_blue_grey_80));
                                    break;
                                case 5:
                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.warning_stroke_color));
                                    break;
                                case 6:
                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                                    break;
                            }
                        }

                        public void onFinish() {
                            i = -1;
                            pDialog.setTitleText("결과 : " + result_arr.get(rand_num))
                                    .setConfirmText("Go!")
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            sampleDB = MainActivity.this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
                            sampleDB.execSQL("INSERT INTO " + tableName
                                    + " (list, result)  Values ('" + result + "', '" + result_arr.get(rand_num) + "');");
                            sampleDB.close();
                            refresh();
//                        recreate(); 반짝임 현상 진짜 개같네
//                        overridePendingTransition(1000, 2000);
                        }
                    }.start();
                }
            }
        });

        //기록 전체 삭제 액션 리스너
        result_delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("정말로 모든 데이터를 삭제할까요?")
                        .setContentText("이 작업은 되돌릴 수 없습니다!")
                        .setCancelText("안지울래요")
                        .setConfirmText("지울래요")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sendQuery("DELETE FROM " + tableName);
                                refresh();
                                sDialog.setTitleText("지웠습니다!")
                                        .setContentText("모든 데이터가 삭제되었습니다!")
                                        .setConfirmText("넹")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();
            }
        });

        //SQLite DB 셋팅
        try {
            //테이블 없으면 새로 생성 사실 이건 shardpreferns인가 그거로 대체 가능할 듯
            sendQuery("CREATE TABLE IF NOT EXISTS " + tableName
                    + " (idx INTEGER PRIMARY KEY AUTOINCREMENT,list VARCHAR(500), result VARCHAR(50) );");
        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(), "으악! 에러다! 개발자에게 문의해주세요!", Toast.LENGTH_LONG).show();
        }

        //검색입력창 초기화
        edit_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rand_text.setText("");
            }
        });
    }

    private void sendQuery(String query) {
        sampleDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        sampleDB.execSQL(query);
        sampleDB.close();
    }

    private void refresh() {
        recyclerViewLayoutManager = new GridLayoutManager(getApplication(), 1);
        showList();
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        adapter = new DataListAdapter(getApplication(), data, rand_text);
        recyclerView.setAdapter(adapter);
        data = new ArrayList<HashMap<String, String>>();
    }

    protected void showList() {
        try {
            SQLiteDatabase ReadDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
            Cursor c = ReadDB.rawQuery("SELECT * FROM " + tableName + " ORDER BY idx DESC", null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        //테이블에서 두개의 컬럼값을 가져와서
                        String idx = c.getString(c.getColumnIndex("idx"));
                        String list = c.getString(c.getColumnIndex("list"));
                        String result = c.getString(c.getColumnIndex("result"));
                        HashMap<String, String> data_in_data = new HashMap<String, String>();
                        data_in_data.put("idx", idx);
                        data_in_data.put("list", list);
                        data_in_data.put("result", result);
                        data.add(data_in_data);
                    } while (c.moveToNext());
                }
            }

            ReadDB.close();
        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(), "반가워요!", Toast.LENGTH_LONG).show();
        }

    }
}
