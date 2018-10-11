package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

public interface OAuthCovenantInterface {
    public void responseCovenantLoginResult(SNSAuthType snsName, Boolean result, String token, String error);
    public void responseLogoutResult(SNSAuthType snsName, Boolean result);
    public void responseDeleteResult(SNSAuthType snsName, Boolean result, String error);
    public void responseUserFrofileInfoResult(SNSAuthType snsName, Boolean result, String userinfo, String error);
}
