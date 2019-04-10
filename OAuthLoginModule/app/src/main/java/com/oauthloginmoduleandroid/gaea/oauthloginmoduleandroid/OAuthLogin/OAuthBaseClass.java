package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import android.app.Activity;
import android.content.Intent;


public abstract class OAuthBaseClass {
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
    public abstract Boolean requestActivityResult(int requestCode, int resultCode, Intent data);

    // OAuth SNS Type
    public enum OAuthType{
        OAuth_KAKAO(0),
        OAuth_NAVER(1),
        OAuth_FACEBOOK(2),
        OAuth_GOOGLE(4);

        private final int number;

        OAuthType(int i) {
            this.number = i;
        }
        public int getNumber() {
            return number;
        }

        public static OAuthType valueOf(int number){
            if(number == OAuth_KAKAO.getNumber()) {
                return OAuth_KAKAO;
            } else if (number == OAuth_NAVER.getNumber()) {
                return OAuth_NAVER;
            } else if (number == OAuth_FACEBOOK.getNumber()) {
                return OAuth_FACEBOOK;
            } else if (number == OAuth_GOOGLE.getNumber()) {
                return OAuth_GOOGLE;
            } else {
                return null;
            }
        }
    }
}



