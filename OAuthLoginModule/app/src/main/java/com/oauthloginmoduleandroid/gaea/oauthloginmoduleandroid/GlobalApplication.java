package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid;

import android.app.Application;

import com.kakao.auth.KakaoSDK;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao.OAuthKakaoManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;

public class GlobalApplication extends Application {

    private static volatile GlobalApplication obj = null;

    public static GlobalApplication getGlobalApplicationContext() {
        return obj;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        OAuthManager.getsInstance().requestStartApp();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        OAuthManager.getsInstance().requestDidApp();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        OAuthManager.getsInstance().requestDidApp();
    }



}
