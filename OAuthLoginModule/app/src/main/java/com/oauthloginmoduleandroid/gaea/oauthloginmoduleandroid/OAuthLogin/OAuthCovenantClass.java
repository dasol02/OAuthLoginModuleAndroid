package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import android.app.Activity;


public abstract class OAuthCovenantClass {

    protected static OAuthCovenantInterface mOAuthCovenantInterface;
    protected static Activity mContext;


    public void setoAuthCovenantInterface(OAuthCovenantInterface oAuthCovenantInterface){
        mOAuthCovenantInterface = oAuthCovenantInterface;
    }


    public void setoAuthCovenantContext(Activity context){
        mContext = context;
    }

}
