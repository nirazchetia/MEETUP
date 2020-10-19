package com.example.meetup.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.example.meetup.R;
import com.example.meetup.models.messageWrapper;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {
    private String groupId;
    private static String GROUP_ID="supergroup";
    private MessagesListAdapter<IMessage> adapter;

    public static void start(Context context,String groupId) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(GROUP_ID,groupId);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        if(intent != null){
            groupId=intent.getStringExtra(ChatActivity.GROUP_ID);
            System.out.println(groupId);
        }
        initViews();
        addlistener();
        fetchPreviousMessages();
    }

    private void fetchPreviousMessages() {
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setUnread(true).setLimit(20).build();
        // fetches previous unread/missed messages
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List <BaseMessage> baseMessages) {
                addMessages(baseMessages);
            }
            @Override
            public void onError(CometChatException e) {
                System.out.println("error");
            }
        });
    }

    private void addMessages(List<BaseMessage> baseMessages) {
        List<IMessage> list = new ArrayList<>();
        for(BaseMessage message : baseMessages){
            if( message instanceof TextMessage){
                list.add(new messageWrapper((TextMessage) message));
            }
        }
        adapter.addToEnd(list,true);
    }

    private void addlistener() {
        String listenerID = "UNIQUE_LISTENER_ID";
        CometChat.addMessageListener(listenerID, new CometChat.MessageListener() {    //listens for incoming messages
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                addMessage(textMessage);// adds messages from the bottom
            }
            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
                // media messages are not allowed yet
            }
            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
               //custom messages are not allowed yet
            }
        });
    }

    private void initViews() {
        MessageInput inputView =findViewById(R.id.input);
        MessagesList messagesList = findViewById(R.id.messagesList);
        String userName = CometChat.getLoggedInUser().getName();

        inputView.setInputListener(input -> {
            sendMessage(userName+":"+input.toString());         //format username:message
            return true;
        });
        String senderId = CometChat.getLoggedInUser().getUid();
        ImageLoader imageLoader = (imageView, url, payload) -> Picasso.get().load(url).into(imageView);
        adapter = new MessagesListAdapter<>(senderId, imageLoader);
        messagesList.setAdapter(adapter);
    }

    private void sendMessage(String message) {       // sending message sequence

        String receiverType = CometChatConstants.RECEIVER_TYPE_GROUP;
        TextMessage textMessage = new TextMessage(groupId, message,receiverType);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) { addMessage(textMessage); }

            @Override
            public void onError(CometChatException e) {   }
        });
    }

    private void addMessage(TextMessage textMessage) {
        adapter.addToStart(new messageWrapper(textMessage),true);
    }
}
