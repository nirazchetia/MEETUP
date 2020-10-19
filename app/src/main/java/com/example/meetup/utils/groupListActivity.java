package com.example.meetup.utils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.example.meetup.R;
import com.example.meetup.adapters.GroupsAdapter;

import java.util.List;

public class groupListActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private Toast backToast;
    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(groupListActivity.this, "You Logged Out", Toast.LENGTH_SHORT).show();
        }
    };
    public static void start(MainActivity context) {
        Intent starter = new Intent(context, groupListActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        getGroupList();
    }

    private void getGroupList() {
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {     //fetches all groups
            @Override
            public void onSuccess(List <Group> list) {
                updateUI(list);
            }

            @Override
            public void onError(CometChatException e) {}
        });
    }

    private void updateUI(List<Group> list) {
        RecyclerView groupsRecyclerView = findViewById(R.id.recyclerView);          // Showing all the available groups
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));// using an adapter to convert comet Chat
        GroupsAdapter groupsAdapter = new GroupsAdapter(list,this);        // values to chat kit UI components
        groupsRecyclerView.setAdapter(groupsAdapter);
    }

    @Override
    public void onBackPressed() {
        CometChat.logout(new CometChat.CallbackListener<String>() {             //log out sequence
            @Override
            public void onSuccess(String successMessage) {}
            @Override
            public void onError(CometChatException e) { }
        });
        mHandler.postDelayed(mToastRunnable, 500);
        super.onBackPressed();
    }


}
