package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Naver;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthLoginState;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthCovenantClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthCovenantInterface;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.SNSAuthType;


public class OAuthNaverManager extends OAuthCovenantClass {

    /**
     * client 정보
     */
    private static String OAUTH_CLIENT_ID = "DMM9F1vOnLoTdfm8Qx6w";
    private static String OAUTH_CLIENT_SECRET = "5_uvXsmbPj";
    private static String OAUTH_CLIENT_NAME = "loginmoduleapp";

    private static final String TAG = "OAuth Naver";

    private static OAuthNaverManager sInstance;
    private static OAuthLogin mOAuthLoginInstance;

    public static OAuthNaverManager getInstance() {
        if (sInstance == null) {
            sInstance = new OAuthNaverManager();
        }
        return sInstance;
    }


    /**
     * 네이버 OAuth 셋팅
     */
    public void setNaverOAuthSetting(){
        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.showDevelopersLog(true);
        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);
    }


    /**
     * 네이버 로그인 상태 호출
     */
    public Boolean requestLoginInfo(){
        if(OAuthLogin.getInstance().getState(mContext) == OAuthLoginState.OK){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 네이버 로그인
     */
    public void naverLogin(){
        mOAuthLoginInstance.startOauthLoginActivity(mContext, new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    String result = getrequestToken();
                    Log.d(TAG,result);
                    mOAuthCovenantInterface.responseCovenantLoginResult(SNSAuthType.SNS_NAVER,true,result,null);

                } else {
                    String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                    String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                    mOAuthCovenantInterface.responseCovenantLoginResult(SNSAuthType.SNS_NAVER,false,null,"\nError code : "+errorCode+"\nErrorDesc : "+errorDesc);
                }
            }
        });
    }


    /**
     * 네이버 로그아웃
     */
    public void naverLogout(){
        mOAuthLoginInstance.logout(mContext);
        if(mOAuthLoginInstance.getState(mContext) == OAuthLoginState.NEED_LOGIN){
            Log.d(TAG,"Logout Success");
            mOAuthCovenantInterface.responseLogoutResult(SNSAuthType.SNS_NAVER,true);
        }else{
            Log.d(TAG,"Logout Fail");
            mOAuthCovenantInterface.responseLogoutResult(SNSAuthType.SNS_NAVER,false);
        }
    }


    /**
     * 네이버 연동 해제
     */
    public void naverDelete(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(mContext);

                if (!isSuccessDeleteToken) {
                    // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                    Log.d(TAG, "naverDelete FAIL" );
                    Log.d(TAG, "errorCode:" + mOAuthLoginInstance.getLastErrorCode(mContext));
                    Log.d(TAG, "errorDesc:" + mOAuthLoginInstance.getLastErrorDesc(mContext));
                    mOAuthCovenantInterface.responseDeleteResult(SNSAuthType.SNS_NAVER,true,null);
                }else{
                    Log.d(TAG, "naverDelete SUCCESS");
                    mOAuthCovenantInterface.responseDeleteResult(SNSAuthType.SNS_NAVER,false,null);
                }

            }
        });
    }


    /**
     * 네이버 사용자 정보 조회
     */
    public void requestUserInfo(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String url = "https://openapi.naver.com/v1/nid/me";
                String at = mOAuthLoginInstance.getAccessToken(mContext);
                String userInfo = mOAuthLoginInstance.requestApi(mContext, at, url);
                if(TextUtils.isEmpty(userInfo)){
                    Log.d(TAG,"requestUserInfo FAIL");
                    mOAuthCovenantInterface.responseUserFrofileInfoResult(SNSAuthType.SNS_NAVER,false,"사용자 정보 호출 실패",null);
                }else{
                    Log.d(TAG,"requestUserInfo SUCCESS");
                    String result = getrequestToken();
                    result = result+"\n\n\n"+userInfo.toString();
                    Log.d(TAG,"requestUserInfo :"+result);
                    mOAuthCovenantInterface.responseUserFrofileInfoResult(SNSAuthType.SNS_NAVER,true,result,null);
                }
            }
        });
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


    public String getrequestToken(){
        String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
        String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
        long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
        String tokenType = mOAuthLoginInstance.getTokenType(mContext);
        String result = "\n\naccessToken = "+accessToken+"\n\nrefreshToken = "+refreshToken+"\n\nexpiresAt = "+String.valueOf(expiresAt)+"\n\ntokenType = "+tokenType;

        return result;
    }


}
