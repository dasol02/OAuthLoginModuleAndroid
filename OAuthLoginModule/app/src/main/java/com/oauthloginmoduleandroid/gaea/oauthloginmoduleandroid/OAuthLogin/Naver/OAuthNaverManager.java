package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Naver;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthLoginState;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthBaseClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;

public class OAuthNaverManager extends OAuthBaseClass {

    private static final String TAG = "OAuth Naver";
    private static OAuthLogin mOAuthLoginInstance;


    @Override
    public void requestStartAppOAuth() {

    }

    @Override
    public void requestDidAppOAuth() {

    }

    @Override
    public void initOAuthSDK(Activity callBackActivity) {
        // 네이버 OAuth 셋팅
        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.showDevelopersLog(true);
        mOAuthLoginInstance.init(callBackActivity, OAuthManager.getsInstance().getOauthClientId(), OAuthManager.getsInstance().getOauthClientSecret(), OAuthManager.getsInstance().getOauthClientName());
    }

    /**
     * 네이버 로그인 상태 호출
     */
    @Override
    public void requestIsLogin(Activity callBackActivity, OAuthManager.OAuthIsLoginInterface oAuthIsLoginInterface) {
        if(OAuthLogin.getInstance().getState(callBackActivity) == OAuthLoginState.OK){
            oAuthIsLoginInterface.responseIsLoginResult(true,"");
        }else{
            oAuthIsLoginInterface.responseIsLoginResult(false,"");
        }
    }

    /**
     * 네이버 로그인
     */
    @Override
    public void requestOAuthLogin(final Activity callBackActivity, final OAuthManager.OAuthLoginInterface oAuthLoginInterface) {
        mOAuthLoginInstance.startOauthLoginActivity(callBackActivity, new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    String result = getrequestToken(callBackActivity);
                    Log.d(TAG,result);
                    oAuthLoginInterface.responseLoginResult(true,result,null);

                } else {
                    String errorCode = mOAuthLoginInstance.getLastErrorCode(callBackActivity).getCode();
                    String errorDesc = mOAuthLoginInstance.getLastErrorDesc(callBackActivity);
                    oAuthLoginInterface.responseLoginResult(false,null,"\nError code : "+errorCode+"\nErrorDesc : "+errorDesc);
                }
            }
        });
    }

    /**
     * 네이버 로그아웃
     */
    @Override
    public void requestOAuthLogout(Activity callBackActivity, OAuthManager.OAuthLogoutInterface oAuthLogoutInterface) {
        mOAuthLoginInstance.logout(callBackActivity);
        if(mOAuthLoginInstance.getState(callBackActivity) == OAuthLoginState.NEED_LOGIN){
            Log.d(TAG,"Logout Success");
            oAuthLogoutInterface.responseLogoutResult(true);
        }else{
            Log.d(TAG,"Logout Fail");
            oAuthLogoutInterface.responseLogoutResult(false);
        }
    }

    /**
     * 네이버 연동 해제
     */
    @Override
    public void requestOAuthRemove(final Activity callBackActivity, final OAuthManager.OAuthRemoveInterface oAuthRemoveInterface) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(callBackActivity);

                if (!isSuccessDeleteToken) {
                    // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                    Log.d(TAG, "naverDelete FAIL" );
                    Log.d(TAG, "errorCode:" + mOAuthLoginInstance.getLastErrorCode(callBackActivity));
                    Log.d(TAG, "errorDesc:" + mOAuthLoginInstance.getLastErrorDesc(callBackActivity));
                    oAuthRemoveInterface.responseRemoveResult(true,null);
                }else{
                    Log.d(TAG, "naverDelete SUCCESS");
                    oAuthRemoveInterface.responseRemoveResult(true,null);
                }

            }
        });
    }


    /**
     * 네이버 사용자 정보 조회
     */
    @Override
    public void requestUserInfo(final Activity callBackActivity, final OAuthManager.OAuthUserFrofileInterface oAuthUserFrofileInterface){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String url = "https://openapi.naver.com/v1/nid/me";
                String at = mOAuthLoginInstance.getAccessToken(callBackActivity);
                String userInfo = mOAuthLoginInstance.requestApi(callBackActivity, at, url);
                if(TextUtils.isEmpty(userInfo)){
                    Log.d(TAG,"requestUserInfo FAIL");
                    oAuthUserFrofileInterface.responseUserFrofileInfoResult(false,"사용자 정보 호출 실패",null);
                }else{
                    Log.d(TAG,"requestUserInfo SUCCESS");
                    String result = getrequestToken(callBackActivity);
                    result = result+"\n\n\n"+userInfo.toString();
                    Log.d(TAG,"requestUserInfo :"+result);
                    oAuthUserFrofileInterface.responseUserFrofileInfoResult(true,result,null);
                }
            }
        });
    }



    @Override
    public Boolean requestActivityResult(int requestCode, int resultCode, Intent data) {
        return false; // naver 콜백 없음
    }



    /**
     * 네이버 토큰 갱신
     */
    private void naverRefreshToken(final Activity callBackActivity){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String token = mOAuthLoginInstance.refreshAccessToken(callBackActivity);
                if(TextUtils.isEmpty(token)){
                    Log.d(TAG,"naverRefreshToken FAIL");
                }else{
                    Log.d(TAG,"naverRefreshToken SUCCESS");
                    Log.d(TAG,token.toString());
                }
            }
        });
    }

    /**
     * 네이버 저장되어 있는 토큰 정보 호출
     */
    private String getrequestToken(Activity callBackActivity){
        String accessToken = mOAuthLoginInstance.getAccessToken(callBackActivity);
        String refreshToken = mOAuthLoginInstance.getRefreshToken(callBackActivity);
        long expiresAt = mOAuthLoginInstance.getExpiresAt(callBackActivity);
        String tokenType = mOAuthLoginInstance.getTokenType(callBackActivity);
        return "\n\naccessToken = "+accessToken+"\n\nrefreshToken = "+refreshToken+"\n\nexpiresAt = "+String.valueOf(expiresAt)+"\n\ntokenType = "+tokenType;
    }
}
