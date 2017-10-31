package com.example.daniel.bestfriendapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Mardokh on 2017-10-29.
 */

public class EmailDialog {

    public void showDialog(final MainActivity activity, final Context context, final String email){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.custom_emaildialog);

        final EditText subject = dialog.findViewById(R.id.edtSubject);
        final EditText body = dialog.findViewById(R.id.edtEmailBody);
        Button dialogButton = dialog.findViewById(R.id.btnStart);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailSubject = subject.getText().toString();
                String emailBody = body.getText().toString();
                sendEmail(email, emailSubject, emailBody, activity, context);
                Toast.makeText(context, "Select email app", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    //No permission needed for email
    private void sendEmail(String email, String subject, String body, MainActivity activity, Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO)
                .setData(new Uri.Builder().scheme("mailto").build())
                .putExtra(Intent.EXTRA_EMAIL, new String[]{email})
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, body)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.


        ComponentName emailApp = intent.resolveActivity(activity.getPackageManager());
        ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
        if (emailApp != null && !emailApp.equals(unsupportedAction)) {
            try {
                // Needed to customise the chooser dialog title since it might default to "Share with"
                // Note that the chooser will still be skipped if only one app is matched
                Intent chooser = Intent.createChooser(intent, "Send email with");
                activity.startActivity(chooser);
                return;
            } catch (ActivityNotFoundException ignored) {
                ignored.printStackTrace();
            }
        }else{
            Toast.makeText(context, "Couldn't find an email app and account", Toast.LENGTH_SHORT).show();
        }
    }

}
