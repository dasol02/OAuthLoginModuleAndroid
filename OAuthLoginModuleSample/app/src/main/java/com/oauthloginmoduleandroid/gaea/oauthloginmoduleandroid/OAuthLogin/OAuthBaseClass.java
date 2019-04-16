package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import android.app.Activity;
import android.content.Intent;


public abstract class OAuthBaseClass {

    // OAuth SNS Type
    public enum OAuthType{
        OAuth_KAKAO, OAuth_NAVER, OAuth_FACEBOOK, OAuth_GOOGLE
    }

    // OAuth SDK Setting
    public abstract void requestStartAppOAuth();
    public abstract void requestDidAppOAuth();
    public abstract void initOAuthSDK(Activity callBackActivity);

    // OAuth Request
    public abstract void requestIsLogin(Activity callBackActivity, OAuthManager.OAuthIsLoginInterface oAuthIsLoginInterface);
    public abstract void requestOAuthLogin(Activity callBackActivity, OAuthManager.OAuthLoginInterface oAuthLoginInterface);
    public abstract void requestOAuthLogout(Activity callBackActivity, OAuthManager.OAuthLogoutInterface oAuthLogoutInterface);
    public abstract void requestOAuthRemove(Activity callBackActivity, OAuthManager.OAuthRemoveInterface oAuthRemoveInterface);
    public abstract void requestUserInfo(Activity callBackActivity, OAuthManager.OAuthUserFrofileInterface oAuthUserFrofileInterface);
    public abstract void requestActivityResult(int requestCode, int resultCode, Intent data);

}



