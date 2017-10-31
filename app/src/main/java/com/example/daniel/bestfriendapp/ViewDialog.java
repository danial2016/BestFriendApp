package com.example.daniel.bestfriendapp;

import android.app.Activity;
import android.app.Dialog;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Namra on 2017-10-28.
 */

public class ViewDialog {

    public void showDialog(final Activity activity, final String phoneNumber){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.custom_smsdialog);

        final EditText text = dialog.findViewById(R.id.edtSmsMsg);

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text.getText().toString();
                if(msg.length() > 0){
                    sendSMS(phoneNumber, msg);
                    Toast.makeText(activity, "SMS has been sent", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else{
                    Toast.makeText(activity, "Failed to send SMS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
