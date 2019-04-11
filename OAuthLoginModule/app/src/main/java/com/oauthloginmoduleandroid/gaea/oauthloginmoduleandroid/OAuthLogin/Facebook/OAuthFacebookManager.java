package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthBaseClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;

import org.json.JSONObject;

import java.util.Arrays;

public class OAuthFacebookManager extends OAuthBaseClass {

 private static final String TAG = "OAuth Facebok";

    private static CallbackManager callbackManager;


    @Override
    public void requestStartAppOAuth() {

    }

    @Override
    public void requestDidAppOAuth() {

    }

    @Override
    public void initOAuthSDK(Activity callBackActivity) {
         //  facebook 셋팅
        callbackManager = CallbackManager.Factory.create();
    }


    // 페이스북 로그인 상태 호출
    @Override
    public void requestIsLogin(Activity callBackActivity, OAuthManager.OAuthIsLoginInterface oAuthIsLoginInterface) {
        // 로그인 확인
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        oAuthIsLoginInterface.responseIsLoginResult(isLoggedIn,null);
    }

    // 페이스북 로그인
    @Override
    public void requestOAuthLogin(Activity callBackActivity, final OAuthManager.OAuthLoginInterface oAuthLoginInterface) {
        LoginManager.getInstance().logInWithReadPermissions(callBackActivity, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                String userId = loginResult.getAccessToken().getUserId();
                String result = "\nuserId : " + userId + "\naccessToken : " + accessToken;
                oAuthLoginInterface.responseLoginResult(true, result, null);
            }

            @Override
            public void onCancel() {
                oAuthLoginInterface.responseLoginResult(false,null,"\nError code : onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                oAuthLoginInterface.responseLoginResult(false,null,"\nError code : "+error.toString());
            }
        });

    }

    // 페이스북 로그아웃
    @Override
    public void requestOAuthLogout(Activity callBackActivity, final OAuthManager.OAuthLogoutInterface oAuthLogoutInterface) {
        LoginManager.getInstance().logOut();
        requestIsLogin(callBackActivity, new OAuthManager.OAuthIsLoginInterface() {
            @Override
            public void responseIsLoginResult(Boolean result, String error) {
                if (result) {
                    oAuthLogoutInterface.responseLogoutResult(false);
                } else {
                    oAuthLogoutInterface.responseLogoutResult(true);
                }
            }
        });
    }


    // 페이스북 연동 해제
    @Override
    public void requestOAuthRemove(Activity callBackActivity, final OAuthManager.OAuthRemoveInterface oAuthRemoveInterface) {
        // 연동해제 기능 없음.
        LoginManager.getInstance().logOut();
        requestIsLogin(callBackActivity, new OAuthManager.OAuthIsLoginInterface() {
            @Override
            public void responseIsLoginResult(Boolean result, String error) {
                if (result) {
                    oAuthRemoveInterface.responseRemoveResult(false,"can Not Facebook OAuth Remove!!");
                } else {
                    oAuthRemoveInterface.responseRemoveResult(true,"");
                }
            }
        });


    }

    // 페이스북 사용자 정보 조회
    @Override
    public void requestUserInfo(Activity callBackActivity, final OAuthManager.OAuthUserFrofileInterface oAuthUserFrofileInterface){
        GraphRequest request;
        request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse response) {
                if (response.getError() != null) {
                    oAuthUserFrofileInterface.responseUserFrofileInfoResult(false,"사용자 정보 호출 실패",response.getError().toString());
                } else {
                    String accessToken = AccessToken.getCurrentAccessToken().getToken();
                    String userId = AccessToken.getCurrentAccessToken().getUserId();
                    String result = "\nuser: \n" + user.toString()+"\n\nuserId : "+userId+"\n\n\naccessToken : "+accessToken;
                    oAuthUserFrofileInterface.responseUserFrofileInfoResult(true,result,null);
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * 페이스북 연동 화면 생성 여부
     * true : kakao 호출, fasle : 미호출
     */
    @Override
    public void requestActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 페이스북 토큰 갱신
     */
    public void facebookRefreshToken(){
        AccessToken.refreshCurrentAccessTokenAsync(new AccessToken.AccessTokenRefreshCallback() {
            @Override
            public void OnTokenRefreshed(AccessToken accessToken) {
            }

            @Override
            public void OnTokenRefreshFailed(FacebookException exception) {
            }
        });
    }


    /**
     * 페이스북 저장되어 있는 토큰 정보 호출
     * @return
     */
    public String getrequestToken(){
        String accessToken = AccessToken.getCurrentAccessToken().toString();
        String userId = AccessToken.getCurrentAccessToken().getUserId();
        String result = "\n\naccessToken = "+accessToken+"\n\nuserId = "+userId;
        return result;
    }
}
