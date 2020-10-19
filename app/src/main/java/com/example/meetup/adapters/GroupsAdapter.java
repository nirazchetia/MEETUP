package com.example.meetup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.example.meetup.utils.ExampleDialog;
import com.example.meetup.R;
import com.example.meetup.utils.ChatActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {
    private List<Group> groups;
    private Context context;

    public GroupsAdapter(List<Group> groups,Context context){
        this.groups= groups;
        this.context=context;
    }
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupViewHolder(LayoutInflater.from(context).inflate(R.layout.group_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsAdapter.GroupViewHolder holder, int position) {
        holder.bind(groups.get(position));
    }



    @Override
    public int getItemCount() {
        return groups.size();
    }
    class GroupViewHolder extends RecyclerView.ViewHolder{
        TextView groupNameTextView;
        LinearLayout containerLayout;
        ToggleButton toggleButton;
        int g;


        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            containerLayout = itemView.findViewById(R.id.containerLayout);
            toggleButton = itemView.findViewById(R.id.toggleButton);

        }

        public void bind(Group group){
            GroupMembersRequest groupMembersRequest = null;
            String userUId=CometChat.getLoggedInUser().getUid();
            groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(group.getGuid()).setSearchKeyword(userUId).build();
            groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>(){ //checks the status of the user
                @Override
                public void onSuccess(List<GroupMember> list) { // if user is a member of the group
                    g+=1;
                    toggleButton.setChecked(true);
                }
                @Override
                public void onError(CometChatException e) {    // if user is not a member of the group
                    g=0;
                }

            });

            toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {  // toggle button position
                if(g<=0) {
                    if (isChecked) {                // if in 'JOINED' position
                        joinGroup(group.getGuid());// joining function
                        containerLayout.setEnabled(true);
                    } else {                        // if in 'LEFT' position
                        leaveGroup(group.getGuid());// leaving function
                        containerLayout.setEnabled(false);
                    }
                }
                --g;
            });
            groupNameTextView.setText(group.getName());
            containerLayout.setOnClickListener(v -> ChatActivity.start(context,group.getGuid()));// If user clicks on group's name
        }                                                                                        // he goes to chat room of that group

        private void joinGroup(String guid) {
            String groupType = CometChatConstants.GROUP_TYPE_PUBLIC;
            String password = "";
            CometChat.joinGroup(guid, groupType, password, new CometChat.CallbackListener<Group>() {// joining group sequence
                @Override
                public void onSuccess(Group joinedGroup) {
                    openDialog("Successfully joined the group");// prompt message
                }
                @Override
                public void onError(CometChatException e) {
                    openDialog("Un-Successful in joining");     // prompt message
                }
            });

        }
        private void leaveGroup(String guid) {

            CometChat.leaveGroup(guid, new CometChat.CallbackListener<String>() {//leaving group sequence
                @Override
                public void onSuccess(String successMessage) {
                    openDialog("Successfully left the group");// prompt message
                }

                @Override
                public void onError(CometChatException e) {
                    openDialog("Un-Successful in leaving");// prompt message
                }
            });
        }
        private void openDialog(String s) {                             // dialogbox implementation
            ExampleDialog exampleDialog = new ExampleDialog();
            exampleDialog.str = s;
            exampleDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "example dialog");
        }
    }
}

