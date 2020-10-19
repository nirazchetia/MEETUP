package com.example.meetup.utils;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.meetup.R;

public class RegisterActivity extends AppCompatActivity {
    String authKey = "57145a869d18123e5e2b4ca9aca6732a43c8c3c7";
    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
    }

    private void initViews() {
        EditText newUserId = findViewById(R.id.newUserId);
        EditText newUserName = findViewById(R.id.newUserName);
        Button registerButton = findViewById(R.id.registerButton);
        TextView backLink =findViewById(R.id.backLink);

        registerButton.setOnClickListener(v -> {

            User user = new User();
            user.setUid(newUserId.getText().toString()); // Replace with the UID for the user to be created
            user.setName(newUserName.getText().toString()); // Replace with the name of the user

            CometChat.createUser(user, authKey, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    openDialog("Successful");
                    Log.d("createUser", user.toString());
                }

                @Override
                public void onError(CometChatException e) {
                    openDialog("Un-Successful");
                    Log.e("createUser", e.getMessage());
                }
            });
        });
        backLink.setOnClickListener(v -> {
            redirectToMainActivity();                                       //back to Log in page if back pressed
        });
    }
    private void redirectToMainActivity() {
        MainActivity.start(this);
    } // Log in page start
    private void openDialog(String s) {                                         // for pop-up dialog messages
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.str = s;
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }
}
