package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import android.app.Activity;
import android.content.Intent;


public abstract class OAuthBaseClass {
    protected static ResponseOAuthCovenantInterface mResponseOAuthCovenantInterface;
    protected static Activity mContext;
    protected static int OAUTH_GOOGLE_CLIENT_FOR_RESULT = 9901; // Google Activity ForResult

    public void setResponseOAuthCovenantInterface(ResponseOAuthCovenantInterface responseOAuthCovenantInterface){
        mResponseOAuthCovenantInterface = responseOAuthCovenantInterface;
    }

    public void setResponseOAuthCovenantContext(Activity context){
        mContext = context;
    }

    // OAuth SDK Setting
    public abstract void requestStartAppOAuth();
    public abstract void requestDidAppOAuth();
    public abstract void initOAuthSDK();

    // OAuth Request
    public abstract Boolean requestIsLogin();
    public abstract void requestOAuthLogin();
    public abstract void requestOAuthLogout();
    public abstract void requestOAuthremove();
    public abstract void requestUserInfo();
    public abstract Boolean requestActivityResult(int requestCode, int resultCode, Intent data);


    // OAuth Result Response InterFace
    public interface ResponseOAuthCovenantInterface {
        void responseOAuthCovenantLoginResult(OAuthType oAuthType, Boolean result, String token, String error);
        void responseOAuthLogoutResult(OAuthType oAuthType, Boolean result);
        void responseOAuthRemoveResult(OAuthType oAuthType, Boolean result, String error);
        void responseOAuthUserFrofileInfoResult(OAuthType oAuthType, Boolean result, String userinfo, String error);
    }

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



