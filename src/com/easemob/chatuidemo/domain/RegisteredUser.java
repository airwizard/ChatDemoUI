package com.easemob.chatuidemo.domain;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daz on 15/12/6.
 */
public class RegisteredUser implements Parcelable {

    private String registeredUserName;

    public RegisteredUser(){

    }
    public RegisteredUser(String registeredUserName){

        this.registeredUserName = registeredUserName;
    }


    public String getRegisteredUserName() {
        return registeredUserName;
    }

    public void setRegisteredUserName(String registeredUserName) {
        this.registeredUserName = registeredUserName;
    }




    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.registeredUserName);

    }

    public static final Parcelable.Creator<RegisteredUser> CREATOR = new Parcelable.Creator<RegisteredUser>() {

        @Override
        public RegisteredUser createFromParcel(Parcel source) {

            RegisteredUser registeredUser = new RegisteredUser();
            registeredUser.registeredUserName = source.readString();
            return registeredUser;
        }

        @Override
        public RegisteredUser[] newArray(int size) {
            return new RegisteredUser[size];
        }
    };
}
