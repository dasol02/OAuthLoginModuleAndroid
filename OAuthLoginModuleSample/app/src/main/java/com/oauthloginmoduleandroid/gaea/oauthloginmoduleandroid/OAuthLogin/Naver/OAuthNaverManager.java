package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Naver;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthLoginState;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthBaseClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthUserInfo;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.R;

import org.json.JSONException;
import org.json.JSONObject;

public class OAuthNaverManager extends OAuthBaseClass {

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
        mOAuthLoginInstance.init(callBackActivity,
                callBackActivity.getString(R.string.naver_client_id),
                callBackActivity.getString(R.string.naver_client_secret),
                callBackActivity.getString(R.string.naver_client_name));
    }

    /**
     * 네이버 로그인 상태 호출
     */
    @Override
    public void requestIsLogin(Activity callBackActivity, OAuthManager.OAuthIsLoginInterface oAuthIsLoginInterface) {
        if(OAuthLogin.getInstance().getState(callBackActivity) == OAuthLoginState.OK){
            oAuthIsLoginInterface.responseIsLoginResult(true,null);
        }else{
            oAuthIsLoginInterface.responseIsLoginResult(false,"Naver is Not Login");
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
                    String accessToken = mOAuthLoginInstance.getAccessToken(callBackActivity);
                    String refreshToken = mOAuthLoginInstance.getRefreshToken(callBackActivity);
                    oAuthLoginInterface.responseLoginResult(true, accessToken,null);

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
            oAuthLogoutInterface.responseLogoutResult(true);
        }else{
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
                    // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태
                    oAuthRemoveInterface.responseRemoveResult(true,null);
                }else{
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
                    oAuthUserFrofileInterface.responseUserFrofileInfoResult(false,null,"사용자 정보 조회 실패");
                }else{
                    OAuthUserInfo oAuthUserInfo = null;
                    Boolean pasarResult = true;
                    try {
                        JSONObject jsonObject = new JSONObject(userInfo).getJSONObject("response");
                        oAuthUserInfo = new OAuthUserInfo(
                                jsonObject.optString("name"),
                                jsonObject.optString("id"),
                                jsonObject.optString("gender"),
                                jsonObject.optString("email"),
                                null,
                                jsonObject.optString("age"),
                                jsonObject.optString("birthday12321"),
                                null,
                                mOAuthLoginInstance.getAccessToken(callBackActivity),
                                mOAuthLoginInstance.getRefreshToken(callBackActivity),
                                null
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pasarResult = false;
                    } finally {
                        oAuthUserFrofileInterface.responseUserFrofileInfoResult(pasarResult, oAuthUserInfo,null);
                    }

                }
            }
        });
    }


    @Override
    public void requestActivityResult(int requestCode, int resultCode, Intent data) {
        // naver 콜백 없음
    }


    /**
     * 네이버 토큰 갱신
     */
    private void naverRefreshToken(final Activity callBackActivity){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // TODO : RefreshToken Setting
                String token = mOAuthLoginInstance.refreshAccessToken(callBackActivity);
            }
        });
    }

}
