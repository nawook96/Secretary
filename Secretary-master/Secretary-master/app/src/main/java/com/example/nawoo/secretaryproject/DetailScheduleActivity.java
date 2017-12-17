package com.example.nawoo.secretaryproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dbsal on 2017-12-17.
 */

public class DetailScheduleActivity extends AppCompatActivity {
    TextView txt_title;
    TextView txt_date;
    TextView txt_time;
    TextView txt_memo;

    private static String scheduleTitle;
    private static String scheduleDate;
    private static String scheduleTime;
    private static String scheduleMemo;

    private static String TAG = "phptest";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_TITLE="title";
    private static final String TAG_TYPE="type";

    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;

    phpDown task;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);

        Intent intent = getIntent();
        String schedule = intent.getStringExtra("schedule");

        // 앞 뒤의 {} 자르기
        String parsing = schedule.substring(1, schedule.indexOf("}"));

        // ,를 기준으로 나누기
        String scheduleInfo[] = parsing.split(", ");

        txt_title = (TextView)findViewById(R.id.txt_title);
        scheduleTitle = scheduleInfo[1].substring(scheduleInfo[1].indexOf("=")+1);
        txt_title.setText(scheduleTitle);

        txt_date = (TextView)findViewById(R.id.txt_date);
        scheduleDate = scheduleInfo[0].substring(scheduleInfo[0].indexOf("=")+1);
        txt_date.setText(scheduleDate);

        txt_time = (TextView)findViewById(R.id.txt_time);
        scheduleTime = scheduleInfo[2].substring(scheduleInfo[2].indexOf("=")+1);
        txt_time.setText(scheduleTime);

        txt_memo = (TextView)findViewById(R.id.txt_memo);
        scheduleMemo = scheduleInfo[3].substring(scheduleInfo[3].indexOf("=")+1);
        txt_memo.setText(scheduleMemo);

        mlistView = (ListView)findViewById(R.id.listView_schedule_type);
        mArrayList = new ArrayList<>();

        task = new phpDown();
        task.execute("http://211.214.113.144:8888/Dproject/userScheduleType.php");
    }


    private class phpDown extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DetailScheduleActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);

            if (result == null){

                // mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {
                URL url = new URL(serverURL);
                // schedule name 보내는 경로


                String postData = "SCHEDULENAME=" + scheduleTitle;
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                //outputStream.close();
                //conn.disconnect();


                // 받는 경로
                //HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.connect();


                int responseStatusCode = conn.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = conn.getInputStream();
                }
                else{
                    inputStream = conn.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString(TAG_TITLE);
                String type = item.getString(TAG_TYPE);

                if(type.equals("1")){
                    type = type + ". 알람 울려주기";
                }
                else if(type.equals("2")){
                    type = type + ". 날씨 보여주기";
                }
                else if(type.equals("3")){
                    type = type + ". 교통상황 보여주기";
                }
                else if(type.equals("4")){
                    type = type + ". 무음모드 전환";
                }

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_TITLE, title);
                hashMap.put(TAG_TYPE, type);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    this, mArrayList, R.layout.type_list,
                    new String[]{TAG_TYPE},
                    new int[]{R.id.textView_list_schedule_type}
            );

            mlistView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}
