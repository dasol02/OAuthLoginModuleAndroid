package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao.KakaoSDKAdapter;

public class GlobalApplication extends Application {

    private static volatile GlobalApplication obj = null;

    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GlobalApplication getGlobalApplicationContext() {
        return obj;
    }


}
