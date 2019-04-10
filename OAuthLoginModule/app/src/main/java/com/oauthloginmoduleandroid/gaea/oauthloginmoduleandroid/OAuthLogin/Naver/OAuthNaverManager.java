package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Naver;

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
    public void initOAuthSDK() {
        // 네이버 OAuth 셋팅
        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.showDevelopersLog(true);
        mOAuthLoginInstance.init(mContext, OAuthManager.getsInstance().getOauthClientId(), OAuthManager.getsInstance().getOauthClientSecret(), OAuthManager.getsInstance().getOauthClientName());
    }

    /**
     * 네이버 로그인 상태 호출
     */
    @Override
    public Boolean requestIsLogin() {
        if(OAuthLogin.getInstance().getState(mContext) == OAuthLoginState.OK){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 네이버 로그인
     */
    @Override
    public void requestOAuthLogin() {
        mOAuthLoginInstance.startOauthLoginActivity(mContext, new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    String result = getrequestToken();
                    Log.d(TAG,result);
                    mResponseOAuthCovenantInterface.responseOAuthCovenantLoginResult(OAuthType.OAuth_NAVER,true,result,null);

                } else {
                    String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                    String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                    mResponseOAuthCovenantInterface.responseOAuthCovenantLoginResult(OAuthType.OAuth_NAVER,false,null,"\nError code : "+errorCode+"\nErrorDesc : "+errorDesc);
                }
            }
        });
    }

    /**
     * 네이버 로그아웃
     */
    @Override
    public void requestOAuthLogout() {
        mOAuthLoginInstance.logout(mContext);
        if(mOAuthLoginInstance.getState(mContext) == OAuthLoginState.NEED_LOGIN){
            Log.d(TAG,"Logout Success");
            mResponseOAuthCovenantInterface.responseOAuthLogoutResult(OAuthType.OAuth_NAVER,true);
        }else{
            Log.d(TAG,"Logout Fail");
            mResponseOAuthCovenantInterface.responseOAuthLogoutResult(OAuthType.OAuth_NAVER,false);
        }
    }

    /**
     * 네이버 연동 해제
     */
    @Override
    public void requestOAuthremove() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(mContext);

                if (!isSuccessDeleteToken) {
                    // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                    Log.d(TAG, "naverDelete FAIL" );
                    Log.d(TAG, "errorCode:" + mOAuthLoginInstance.getLastErrorCode(mContext));
                    Log.d(TAG, "errorDesc:" + mOAuthLoginInstance.getLastErrorDesc(mContext));
                    mResponseOAuthCovenantInterface.responseOAuthRemoveResult(OAuthType.OAuth_NAVER,true,null);
                }else{
                    Log.d(TAG, "naverDelete SUCCESS");
                    mResponseOAuthCovenantInterface.responseOAuthRemoveResult(OAuthType.OAuth_NAVER,true,null);
                }

            }
        });
    }


    /**
     * 네이버 사용자 정보 조회
     */
    @Override
    public void requestUserInfo(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String url = "https://openapi.naver.com/v1/nid/me";
                String at = mOAuthLoginInstance.getAccessToken(mContext);
                String userInfo = mOAuthLoginInstance.requestApi(mContext, at, url);
                if(TextUtils.isEmpty(userInfo)){
                    Log.d(TAG,"requestUserInfo FAIL");
                    mResponseOAuthCovenantInterface.responseOAuthUserFrofileInfoResult(OAuthType.OAuth_NAVER,false,"사용자 정보 호출 실패",null);
                }else{
                    Log.d(TAG,"requestUserInfo SUCCESS");
                    String result = getrequestToken();
                    result = result+"\n\n\n"+userInfo.toString();
                    Log.d(TAG,"requestUserInfo :"+result);
                    mResponseOAuthCovenantInterface.responseOAuthUserFrofileInfoResult(OAuthType.OAuth_NAVER,true,result,null);
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
    public void naverRefreshToken(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String token = mOAuthLoginInstance.refreshAccessToken(mContext);
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
     * @return
     */
    public String getrequestToken(){
        String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
        String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
        long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
        String tokenType = mOAuthLoginInstance.getTokenType(mContext);
        String result = "\n\naccessToken = "+accessToken+"\n\nrefreshToken = "+refreshToken+"\n\nexpiresAt = "+String.valueOf(expiresAt)+"\n\ntokenType = "+tokenType;

        return result;
    }
}
