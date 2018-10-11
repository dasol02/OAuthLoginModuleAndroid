package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

public interface OAuthLoginInterface {
    public void responseLoginResult(SNSAuthType snsName, Boolean result, String token, String error);
//    public void responseLogoutResult(SNSAuthType snsName, Boolean result);

}
