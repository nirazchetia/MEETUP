package com.example.meetup.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.meetup.R;

public class MainActivity extends AppCompatActivity {
    private long backPressedTime;
    private Toast backToast;
    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCometChat();
        initViews();
    }

    private void initViews() {
        String authKey = "57145a869d18123e5e2b4ca9aca6732a43c8c3c7";
        EditText userIdEditText = findViewById(R.id.userIDEditText);
        Button submitButton = findViewById(R.id.submitButton);
        TextView signUpLink =findViewById(R.id.signUpLink);
        submitButton.setOnClickListener(v -> {
            CometChat.login(userIdEditText.getText().toString(),
                    authKey,
                    new CometChat.CallbackListener<User>() {        //log in request
                        @Override
                        public void onSuccess(User user) {
                            redirectToGroupChat();                  //is UID is correct redirect to grouplistActivity
                        }

                        @Override
                        public void onError(CometChatException e) {
                            openDialog("Invalid UID,Try Again");
                            userIdEditText.setText("");             // empty the UID field for re-entry
                        }
                    });

        });
        signUpLink.setOnClickListener(v -> {                        // for registration RegisterActivity will be opened
            redirectToRegisterActivity();
        });

    }
    private void openDialog(String s) {
        ExampleDialog exampleDialog = new ExampleDialog();          // for pop-up dialog messages
        exampleDialog.str = s;
        exampleDialog.show(getSupportFragmentManager(), "example dialog");

    }
    private void redirectToRegisterActivity() {
        RegisterActivity.start(this);
    } // redirect the user to Registration page

    private void redirectToGroupChat() {
        groupListActivity.start(this);
    }// redirect to grouplist page

    private void initCometChat(){                                       // Initialising/connecting to server/dbs
        String appID = " 211044f4e852813"; // Replace with your App ID
        String region = "us"; // Replace with your App Region ("eu" or "us")

        AppSettings appSettings=new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region).build();
        CometChat.init(this, appID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
            }
            @Override
            public void onError(CometChatException e) {
            }
        });
    }
    public void onBackPressed() {                                   // Exiting Sequence
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}