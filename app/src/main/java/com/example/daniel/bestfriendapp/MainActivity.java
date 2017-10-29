package com.example.daniel.bestfriendapp;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String MY_PREFS_NAME = "MyPrefs";
    private static final String FRIEND_PREF = "FriendPref";
    private TextView tvName, tvName2;
    private Button btnCall, btnCall2, btnSMS, btnEmail, btnRemove;
    private FriendsInfo friendsInfo;
    private EditText textMessage;


    private Button btnAddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        friendsInfo = new FriendsInfo();
        initializeComponents();
        registerButtonListener();
        setFriends();
    }

    private void setFriends(){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String friensJsonArr = prefs.getString(FRIEND_PREF, null);
        if (friensJsonArr != null) {
            FriendsInfo friends = new FriendsInfo();
            friends.setFriends(friensJsonArr);

            ArrayList<Friend> list = friends.getFriends();
            // TODO support several friends later
            //for(int i = 0; i < list.size(); i++){
            //}

            if(list.size() > 0){
                tvName.setText(list.get(0).getName());
                tvName.setVisibility(View.VISIBLE);
                btnCall.setTag(list.get(0).getPhoneNumber());
                btnCall.setTag(R.id.btnTag, list.get(0).getName());
                btnCall.setVisibility(View.VISIBLE);
                btnSMS.setTag(list.get(0).getPhoneNumber());
                btnSMS.setVisibility(View.VISIBLE);
                btnEmail.setTag(list.get(0).getEmail());
                btnEmail.setVisibility(View.VISIBLE);
                btnRemove.setTag(list.get(0).getName());
                btnRemove.setVisibility(View.VISIBLE);
            }else{
                tvName.setText("");
                tvName.setVisibility(View.VISIBLE);
            }

            if (list.size() > 1) {
                tvName2.setText(list.get(1).getName());
                tvName2.setVisibility(View.VISIBLE);
                btnCall2.setTag(list.get(1).getPhoneNumber());
                btnCall2.setVisibility(View.VISIBLE);

            }
        }
    }

    private void initializeComponents() {
        btnAddContact = (Button) findViewById(R.id.btnAddContact);
        btnCall = (Button) findViewById(R.id.btnCall);
        btnCall2 = (Button) findViewById(R.id.btnCall2);
        btnSMS = (Button) findViewById(R.id.btnSms);
        btnEmail = (Button) findViewById(R.id.btnEmail);
        tvName = (TextView) findViewById(R.id.tvName);
        btnRemove = (Button) findViewById(R.id.btnRemove);
        tvName2 = (TextView) findViewById(R.id.tvName2);
    }

    private void registerButtonListener() {
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameToBeRemoved = btnRemove.getTag().toString();
                friendsInfo.removeFriend(nameToBeRemoved);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(FRIEND_PREF, friendsInfo.toString());
                editor.apply();
                setFriends();
                Toast.makeText(MainActivity.this,"So you don't like your friend?",Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,"No surprise, shallow friendships are exchangeable",Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Are you sure?");
                builder.setMessage("You are about to call " + btnCall.getTag(R.id.btnTag).toString());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callNumber(btnCall.getTag().toString());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this,"Then why on earth did you tap the call-button!?",Toast.LENGTH_LONG).show();
                        Toast.makeText(MainActivity.this,"What, did your finger slip on it?",Toast.LENGTH_LONG).show();
                        Toast.makeText(MainActivity.this,"Or maybe deep down inside you don't really want to talk to this so-called 'friend'?",Toast.LENGTH_LONG).show();
                        Toast.makeText(MainActivity.this,"Maybe your friendship is just fake ...",Toast.LENGTH_LONG).show();
                        Toast.makeText(MainActivity.this,"Anyway, you'r pathetic.",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.custom_alertdialog, null);

                builder.setView(dialoglayout);
                builder.show();
            }
        });

        btnCall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callNumber(btnCall2.getTag().toString());
            }
        });

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialog alert = new ViewDialog();
                alert.showDialog(MainActivity.this, btnSMS.getTag().toString());
            }
        });
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Email", btnEmail.getTag().toString());
                if(btnEmail.getTag().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Your contact has no registered email",Toast.LENGTH_SHORT).show();
                }else{
                    EmailDialog emailDialog = new EmailDialog();
                    emailDialog.showDialog(MainActivity.this, getApplicationContext(), btnEmail.getTag().toString());
                }
            }
        });

    }

    private void callNumber(String phoneNumber){
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CALL_PHONE},   //request specific permission from user
                    10);
            return;
        }else {     //have got permission
            try{
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                startActivity(intent);  //call activity and make phone call
            }
            catch (Exception ex){
                Toast.makeText(getApplicationContext(),"Error! Have you added a friend?",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Email.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(index);
                Log.i("Number", number);

                index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(index);
                Log.i("Name", name);


                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
                String email = getEmail(id);
                Log.i("Email", email);

                friendsInfo.addFriend(new Friend(name, number, email));
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(FRIEND_PREF, friendsInfo.toString());
                editor.apply();
                Toast.makeText(this, "Friend saved!", Toast.LENGTH_SHORT).show();
                setFriends();
            }
        }
    }


    private String getEmail(String id) {
        String email = "";
        String[] projections = {id};
        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        Cursor cursorEmail = getContentResolver().query(uri,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                projections,
                null);
        if(cursorEmail.moveToFirst()){
            email = cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            cursorEmail.close();
        }else{
            Toast.makeText(MainActivity.this,"Your contact has no registered email",Toast.LENGTH_SHORT).show();
        }
        return email;
    }

}
