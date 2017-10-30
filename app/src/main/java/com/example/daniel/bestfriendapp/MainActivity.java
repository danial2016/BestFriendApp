package com.example.daniel.bestfriendapp;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String MY_PREFS_NAME = "MyPrefs";
    private static final String FRIEND_PREF = "FriendPref";
    //private TextView tvName, tvName2;
    //private Button btnCall, btnCall2, btnSMS, btnEmail, btnRemove;
    private FriendsInfo friendsInfo;
    //private EditText textMessage;
    private Button btnAddContact, btnRemove;
    private ListView listView;

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
            String[] phoneNumbers= new String[list.size()], emails = new String[list.size()], names = new String[list.size()];
            for(int i = 0; i < list.size(); i++){
                names[i] = list.get(i).getName();
                phoneNumbers[i] = list.get(i).getPhoneNumber();
                emails[i] = list.get(i).getEmail();
            }
            ListViewAdapter listViewAdapter = new ListViewAdapter(list, phoneNumbers, emails, names);
            listView.setAdapter(listViewAdapter);
        }
    }

    private void initializeComponents() {
        btnAddContact = (Button) findViewById(R.id.btnAddContact);
        listView = (ListView) findViewById(R.id.list_view);
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

                Friend friend = new Friend(name, number, email);

                if (friendsInfo.check(friend.getName())) {
                    Toast.makeText(this, "You blind?", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "You've already added this friend dummy!", Toast.LENGTH_SHORT).show();
                } else {
                    friendsInfo.addFriend(friend);
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(FRIEND_PREF, friendsInfo.toString());
                    editor.apply();
                    Toast.makeText(this, "Friend saved!", Toast.LENGTH_SHORT).show();
                    setFriends();
                }

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

    private class ListViewAdapter extends BaseAdapter {
        private ArrayList<Friend> list = new ArrayList<Friend>();
        String[] phoneNumbers= {""}, emails = {""}, names = {""};

        public ListViewAdapter(ArrayList<Friend> list, String[] phoneNumbers, String[] emails, String[] names) {
            this.list = list;
            this.phoneNumbers = phoneNumbers;
            this.emails = emails;
            this.names = names;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.layout_single_item, null);
            }
            //Handle TextView and display string from your list
            final TextView listItemText = view.findViewById(R.id.tvName);
            listItemText.setText(names[position]);

            //Handle buttons and add onClickListeners
            final Button call_btn = view.findViewById(R.id.btnCall);
            final Button email_btn = view.findViewById(R.id.btnEmail);
            final Button text_btn = view.findViewById(R.id.btnSms);
            final Button remove_btn = view.findViewById(R.id.btnRemove);

            remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Are you sure?");
                    builder.setMessage("Do you want to remove " + names[position] + " ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String nameToBeRemoved = names[position];
                            friendsInfo.removeFriend(nameToBeRemoved);
                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString(FRIEND_PREF, friendsInfo.toString());
                            editor.apply();
                            setFriends();
                            Toast.makeText(MainActivity.this,"Friend has been removed",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this,"Why not?",Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this,"You probably don't even hang out with this person.",Toast.LENGTH_LONG).show();
                        }
                    });
                    LayoutInflater inflater = getLayoutInflater();
                    View dialoglayout = inflater.inflate(R.layout.custom_alertdialog, null);
                    builder.setView(dialoglayout);
                    builder.show();
                }
            });

            call_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Are you sure?");
                    builder.setMessage("Do you want to call " + names[position] + " ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNumber(phoneNumbers[position]);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this,"Then why on earth did you tap the call-button!?",Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this,"What, did your finger slip on it?",Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this,"Or maybe deep down inside you don't really want to talk to this so-called 'friend'?",Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this,"Maybe your friendship is just fake ...",Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this,"You pathetic joke.",Toast.LENGTH_SHORT).show();
                        }
                    });
                    LayoutInflater inflater = getLayoutInflater();
                    View dialoglayout = inflater.inflate(R.layout.custom_alertdialog, null);
                    builder.setView(dialoglayout);
                    builder.show();
                }
            });
            email_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Email", emails[position]);
                    if(emails[position].equals("")){
                        Toast.makeText(MainActivity.this, "Your contact has no registered email",Toast.LENGTH_SHORT).show();
                    }else{
                        EmailDialog emailDialog = new EmailDialog();
                        emailDialog.showDialog(MainActivity.this, getApplicationContext(), emails[position]);
                    }
                }
            });
            text_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewDialog viewDialog = new ViewDialog();
                    viewDialog.showDialog(MainActivity.this, phoneNumbers[position]);
                }
            });
            return view;
        }
    }

}
