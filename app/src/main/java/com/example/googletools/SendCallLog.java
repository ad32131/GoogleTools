package com.example.googletools;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendCallLog extends AsyncTask<String, Void, String>  {

    private static final String TAG = "SendMessage";
    //ProgressDialog progressDialog;

    public Context mContext;

    public SendCallLog(Context mContext) {
        super();

        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //progressDialog = ProgressDialog.show(mContext,"Please Wait", null, true, true);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //progressDialog.dismiss();
        //mTextViewResult.setText(result);
        Log.d(TAG, "POST response  - " + result);
    }


    @Override
    protected String doInBackground(String... params) {

        // POST 방식으로 데이터 전달시에는 데이터가 주소에 직접 입력되지 않습니다.
        String serverURL = (String) params[0];

        // 1. PHP 파일을 실행시킬 수 있는 주소와 전송할 데이터를 준비합니다.
        String key = (String) params[1];

        // 2. PHP 파일을 실행시킬 수 있는 주소와 전송할 데이터를 준비합니다.
        String value = (String) params[2];

        String number = (String) params[3];
        String value2 = (String) params[4];

        String sendPhoneNumber = (String) params[5];
        String value3 = (String) params[6];

        // ex : String postParameters = "name=" + name + "&country=" + country;
        String postParameters = key + "=" + value + "&"+number + "=" + value2 + "&"+  sendPhoneNumber + "="+ value3;

        try {
            // 2. HttpURLConnection 클래스를 사용하여 POST 방식으로 데이터를 전송합니다.
            URL url = new URL(serverURL); // 주소가 저장된 변수를 이곳에 입력합니다.

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(5000); //5초안에 응답이 오지 않으면 예외가 발생합니다.

            httpURLConnection.setConnectTimeout(5000); //5초안에 연결이 안되면 예외가 발생합니다.

            httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 합니다.
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8")); //전송할 데이터가 저장된 변수를 이곳에 입력합니다.

            outputStream.flush();
            outputStream.close();


            // 응답을 읽습니다.

            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "POST response code - " + responseStatusCode);

            InputStream inputStream;
            if (responseStatusCode == HttpURLConnection.HTTP_OK) {

                // 정상적인 응답 데이터
                inputStream = httpURLConnection.getInputStream();
            } else {

                // 에러 발생

                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }


            bufferedReader.close();


            return sb.toString();


        } catch (Exception e) {

            Log.d(TAG, "InsertData: Error ", e);

            return new String("Error: " + e.getMessage());
        }

    }


}
