package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Facebook;

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
import com.kakao.auth.Session;
import com.nhn.android.naverlogin.OAuthLogin;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthCovenantClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.SNSAuthType;

import org.json.JSONObject;

import java.util.Arrays;

public class OAuthFacebookManager extends OAuthCovenantClass{

 private static final String TAG = "OAuth Facebok";

    private static OAuthFacebookManager sInstance;
    private static CallbackManager callbackManager;

    public static OAuthFacebookManager getInstance() {
        if (sInstance == null) {
            sInstance = new OAuthFacebookManager();
        }
        return sInstance;
    }


    /**
     *  facebook 셋팅
     */
    public void setFacebookOAuthSetting(){
        callbackManager = CallbackManager.Factory.create();
    }


    /**
     * 페이스북 로그인 상태 호출
     */
    public Boolean requestLoginInfo(){
        // 로그인 확인
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 페이스북 로그인
     */
    public void facebookLogin(){

        LoginManager.getInstance().logInWithReadPermissions(mContext, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                String userId = loginResult.getAccessToken().getUserId();
                String result = "\nuserId : " + userId + "\naccessToken : " + accessToken;
                Log.d("OAuth FACEBOOK","LOGIN onSuccess");
                mOAuthCovenantInterface.responseCovenantLoginResult(SNSAuthType.SNS_FACEBOOK, true, result, null);
            }

            @Override
            public void onCancel() {
                mOAuthCovenantInterface.responseCovenantLoginResult(SNSAuthType.SNS_FACEBOOK,false,null,"\nError code : onCancel");
                Log.d("OAuth FACEBOOK","LOGIN onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                mOAuthCovenantInterface.responseCovenantLoginResult(SNSAuthType.SNS_FACEBOOK,false,null,"\nError code : "+error.toString());
                Log.d("OAuth FACEBOOK","LOGIN onError : \n"+error.toString());
            }
        });
    }


    /**
     * 페이스북 로그아웃
     */
    public void facebookLogout(){
        LoginManager.getInstance().logOut();
        if(requestLoginInfo()){
            Log.d(TAG,"Logout Fail");
             mOAuthCovenantInterface.responseLogoutResult(SNSAuthType.SNS_FACEBOOK,false);
        }else{
            Log.d(TAG,"Logout Success");
            mOAuthCovenantInterface.responseLogoutResult(SNSAuthType.SNS_FACEBOOK,true);
        };
    }



    /**
     * 페이스북 연동 해제
     */
    public void facebookDelete(){
         // 연동해제 기능 없는 것으로 파악됨.
    }


    /**
     * 페이스북 사용자 정보 조회
     */
    public void requestUserInfo(){

        GraphRequest request;

        request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse response) {
                if (response.getError() != null) {
                    Log.d("OAuth FACEBOOK"," REQUEST USER INFO Fail : ");
                    mOAuthCovenantInterface.responseUserFrofileInfoResult(SNSAuthType.SNS_FACEBOOK,false,"사용자 정보 호출 실패",response.getError().toString());
                } else {
                    Log.d("OAuth FACEBOOK"," REQUEST USER INFO Success : ");
                    String accessToken = AccessToken.getCurrentAccessToken().getToken();
                    String userId = AccessToken.getCurrentAccessToken().getUserId();
                    String result = "\nuser: \n" + user.toString()+"\n\nuserId : "+userId+"\n\n\naccessToken : "+accessToken;
                    Log.d("OAuth FACEBOOK",result);
                    mOAuthCovenantInterface.responseUserFrofileInfoResult(SNSAuthType.SNS_FACEBOOK,true,result,null);
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }



    /**
     * 페이스북 토큰 갱신
     */
    public void facebookRefreshToken(){
        AccessToken.refreshCurrentAccessTokenAsync(new AccessToken.AccessTokenRefreshCallback() {
            @Override
            public void OnTokenRefreshed(AccessToken accessToken) {
                Log.d(TAG,"facebookRefreshToken SUCCESS");
            }

            @Override
            public void OnTokenRefreshFailed(FacebookException exception) {
                Log.d(TAG,"facebookRefreshToken FAIL");
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


    /**
     * 페이스북 연동 화면 생성 여부
     * true : kakao 호출, fasle : 미호출
     */
    public Boolean checkActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return true;
        }else {
            return false;
        }
    }

}
