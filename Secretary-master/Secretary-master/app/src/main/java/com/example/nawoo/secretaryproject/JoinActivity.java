package com.example.nawoo.secretaryproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/* 이것은 회원 가입 */

public class JoinActivity extends AppCompatActivity {

    final Context context = this;
    EditText et_id, et_pw, et_pw_chk, et_name;
    String sId, sPw, sPw_chk, sName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        et_id = (EditText) findViewById(R.id.et_id);
        et_pw = (EditText) findViewById(R.id.et_Password);
        et_pw_chk = (EditText) findViewById(R.id.et_Password_chk);
        et_name = (EditText) findViewById(R.id.et_Name);


    }
    public void bt_Join(View view)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        sId = et_id.getText().toString();
        sPw = et_pw.getText().toString();
        sPw_chk = et_pw_chk.getText().toString();
        sName = et_name.getText().toString();

        if(sPw.equals(sPw_chk))
        {
            registDB rdb = new registDB();
            rdb.execute();
        }
        else
        {
            Log.e("RESULT","비밀번호 확인 실패");
            alertBuilder
                    .setTitle("알림")
                    .setMessage("비밀번호와 비밀번호 확인이 다릅니다.")
                    .setCancelable(true)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        }
    }
    public class registDB extends AsyncTask<Void, Integer, Void> {
        String data = "";
        @Override
        protected Void doInBackground(Void... unused) {

/* 인풋 파라메터값 생성 */
            String param = "u_id=" + sId + "&u_pw=" + sPw + "&u_name=" + sName + "";
            try {
/* 서버연결 */
                URL url = new URL(
                        "http://211.214.113.144:8888/Dproject/Join.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

/* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

/* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.e("RECV DATA", data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        /* 서버에서 응답 */
            Log.e("RECV DATA",data);

            if(data.equals("succeed"))
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                alertBuilder
                        .setTitle("알림")
                        .setMessage("성공적으로 등록되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
            else
            {
                Log.e("RESULT","에러 발생! ERRCODE = " + data);
                alertBuilder
                        .setTitle("알림")
                        .setMessage(data)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
        }
    }
}

