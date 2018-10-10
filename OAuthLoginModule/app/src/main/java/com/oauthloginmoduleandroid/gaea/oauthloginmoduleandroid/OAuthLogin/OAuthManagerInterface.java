package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

public interface OAuthManagerInterface {
    public void responseLoginResult(SNSAuthType snsName, Boolean result, String token, String error);
//    public void responseLogoutResult(SNSAuthType snsName, Boolean result);

}
