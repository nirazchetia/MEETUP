package com.example.meetup.models;

import com.cometchat.pro.models.User;
import com.stfalcon.chatkit.commons.models.IUser;

public class userWrapper implements IUser {
    private User user;
    public userWrapper(User user){
        this.user=user;
    }
    @Override
    public String getId() {
        return user.getUid();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getAvatar() {
        return user.getAvatar();
    }
}

