package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import com.google.android.gms.common.annotation.KeepName;

import java.lang.annotation.Retention;

public class OAuthUserInfo {

    private String userName;
    private String userID;
    private String userGender;
    private String userEmail;
    private String userNickName;
    private String userAgeRang;
    private String userBirthday;
    private String userProfileImage;
    private String userAccessToken;
    private String userRefreshToken;
    private String userTokenRefreshDate;

    public OAuthUserInfo(String userName, String userID, String userGender, String userEmail, String userNickName, String userAgeRang, String userBirthday, String userProfileImage, String userAccessToken, String userRefreshToken, String userTokenRefreshDate) {
        this.userName = userName;
        this.userID = userID;
        this.userGender = userGender;
        this.userEmail = userEmail;
        this.userNickName = userNickName;
        this.userAgeRang = userAgeRang;
        this.userBirthday = userBirthday;
        this.userProfileImage = userProfileImage;
        this.userAccessToken = userAccessToken;
        this.userRefreshToken = userRefreshToken;
        this.userTokenRefreshDate = userTokenRefreshDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public String getUserAgeRang() {
        return userAgeRang;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public String getUserAccessToken() {
        return userAccessToken;
    }

    public String getUserRefreshToken() {
        return userRefreshToken;
    }

    public String getUserTokenRefreshDate() {
        return userTokenRefreshDate;
    }

    @Override
    public String toString() {
        return "OAuthUserInfo{" +
                "userName='" + userName + '\'' +
                ", userID='" + userID + '\'' +
                ", userGender='" + userGender + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userNickName='" + userNickName + '\'' +
                ", userAgeRang='" + userAgeRang + '\'' +
                ", userBirthday='" + userBirthday + '\'' +
                ", userProfileImage='" + userProfileImage + '\'' +
                ", userAccessToken='" + userAccessToken + '\'' +
                ", userRefreshToken='" + userRefreshToken + '\'' +
                ", userTokenRefreshDate='" + userTokenRefreshDate + '\'' +
                '}';
    }
}
