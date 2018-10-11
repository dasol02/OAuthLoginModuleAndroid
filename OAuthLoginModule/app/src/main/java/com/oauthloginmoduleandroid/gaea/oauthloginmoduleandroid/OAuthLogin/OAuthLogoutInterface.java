package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

public interface OAuthLogoutInterface {
    public void responseLogoutResult(SNSAuthType snsName, Boolean result);
    public void responseDeleteResult(SNSAuthType snsName, Boolean result, String error);
}
