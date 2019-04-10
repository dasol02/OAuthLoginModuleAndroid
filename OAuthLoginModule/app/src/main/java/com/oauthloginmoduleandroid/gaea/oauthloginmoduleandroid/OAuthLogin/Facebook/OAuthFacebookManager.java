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
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthBaseClass;
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
    public void initOAuthSDK() {
         //  facebook 셋팅
        callbackManager = CallbackManager.Factory.create();
    }



    // 페이스북 로그인 상태 호출
    @Override
    public Boolean requestIsLogin() {
        // 로그인 확인
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            return true;
        } else {
            return false;
        }
    }

    // 페이스북 로그인
    @Override
    public void requestOAuthLogin() {
        LoginManager.getInstance().logInWithReadPermissions(mContext, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                String userId = loginResult.getAccessToken().getUserId();
                String result = "\nuserId : " + userId + "\naccessToken : " + accessToken;
                Log.d("OAuth FACEBOOK","LOGIN onSuccess");
                mResponseOAuthCovenantInterface.responseOAuthCovenantLoginResult(OAuthType.OAuth_FACEBOOK, true, result, null);
            }

            @Override
            public void onCancel() {
                mResponseOAuthCovenantInterface.responseOAuthCovenantLoginResult(OAuthType.OAuth_FACEBOOK,false,null,"\nError code : onCancel");
                Log.d("OAuth FACEBOOK","LOGIN onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                mResponseOAuthCovenantInterface.responseOAuthCovenantLoginResult(OAuthType.OAuth_FACEBOOK,false,null,"\nError code : "+error.toString());
                Log.d("OAuth FACEBOOK","LOGIN onError : \n"+error.toString());
            }
        });

    }

    // 페이스북 로그아웃
    @Override
    public void requestOAuthLogout() {
        LoginManager.getInstance().logOut();
        if(requestIsLogin()){
            Log.d(TAG,"Logout Fail");
            mResponseOAuthCovenantInterface.responseOAuthLogoutResult(OAuthType.OAuth_FACEBOOK,false);
        }else{
            Log.d(TAG,"Logout Success");
            mResponseOAuthCovenantInterface.responseOAuthLogoutResult(OAuthType.OAuth_FACEBOOK,true);
        };
    }


    // 페이스북 연동 해제
    @Override
    public void requestOAuthremove() {
        // 연동해제 기능 없는 것으로 파악됨.

    }

    // 페이스북 사용자 정보 조회
    @Override
    public void requestUserInfo(){
        GraphRequest request;
        request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse response) {
                if (response.getError() != null) {
                    Log.d("OAuth FACEBOOK"," REQUEST USER INFO Fail : ");
                    mResponseOAuthCovenantInterface.responseOAuthUserFrofileInfoResult(OAuthType.OAuth_FACEBOOK,false,"사용자 정보 호출 실패",response.getError().toString());
                } else {
                    Log.d("OAuth FACEBOOK"," REQUEST USER INFO Success : ");
                    String accessToken = AccessToken.getCurrentAccessToken().getToken();
                    String userId = AccessToken.getCurrentAccessToken().getUserId();
                    String result = "\nuser: \n" + user.toString()+"\n\nuserId : "+userId+"\n\n\naccessToken : "+accessToken;
                    Log.d("OAuth FACEBOOK",result);
                    mResponseOAuthCovenantInterface.responseOAuthUserFrofileInfoResult(OAuthType.OAuth_FACEBOOK,true,result,null);
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
    public Boolean requestActivityResult(int requestCode, int resultCode, Intent data) {
        return callbackManager.onActivityResult(requestCode, resultCode, data);
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
}
