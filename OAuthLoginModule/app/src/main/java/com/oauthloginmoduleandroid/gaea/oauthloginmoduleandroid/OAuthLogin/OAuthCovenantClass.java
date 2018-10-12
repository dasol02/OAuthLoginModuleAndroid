package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import android.app.Activity;


public abstract class OAuthCovenantClass {

    protected static OAuthCovenantInterface mOAuthCovenantInterface;
    protected static Activity mContext;

    /**
     * Naver Client 정보
     */
    protected static String OAUTH_CLIENT_ID = "DMM9F1vOnLoTdfm8Qx6w";
    protected static String OAUTH_CLIENT_SECRET = "5_uvXsmbPj";
    protected static String OAUTH_CLIENT_NAME = "loginmoduleapp";




    public void setoAuthCovenantInterface(OAuthCovenantInterface oAuthCovenantInterface){
        mOAuthCovenantInterface = oAuthCovenantInterface;
    }


    public void setoAuthCovenantContext(Activity context){
        mContext = context;
    }

}
