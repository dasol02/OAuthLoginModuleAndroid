package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.GlobalApplication;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthBaseClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;

import java.util.ArrayList;
import java.util.List;

public class OAuthKakaoManager extends OAuthBaseClass {

    private SessionCallback callback;
    private OAuthManager.OAuthLoginInterface mOAuthLoginInterface;

    @Override
    public void requestStartAppOAuth() {
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    @Override
    public void requestDidAppOAuth() {
        Session.getCurrentSession().removeCallback(callback); // kakao Session 제거
    }


    @Override
    public void initOAuthSDK(Activity callBackActivity) {
    }

    /*
     ** kakao 로그인 유무 확인
     */
    @Override
    public void requestIsLogin(Activity callBackActivity, OAuthManager.OAuthIsLoginInterface oAuthIsLoginInterface) {
        String accToken = Session.getCurrentSession().getTokenInfo().getAccessToken();
        String refreshToken = Session.getCurrentSession().getTokenInfo().getRefreshToken();
        if(!TextUtils.isEmpty(accToken) && !TextUtils.isEmpty(refreshToken)){
            oAuthIsLoginInterface.responseIsLoginResult(true,null);
        }else{
            oAuthIsLoginInterface.responseIsLoginResult(false,"Kakao is not Login");
        }
    }

    /**
     * kakao Login
     */
    @Override
    public void requestOAuthLogin(Activity callBackActivity, OAuthManager.OAuthLoginInterface oAuthLoginInterface) {
        mOAuthLoginInterface = oAuthLoginInterface;
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        // 우선권 부여 : AuthType.KAKAO_TALK
        // kakao talk 미 설치시 KAKAO_ACCOUNT로 연결
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, callBackActivity);
    }

    /**
     * kakao Logout
     */
    @Override
    public void requestOAuthLogout(Activity callBackActivity, final OAuthManager.OAuthLogoutInterface oAuthLogoutInterface) {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                // 로그아웃을 성공한 경우 불립니다. 서버에 로그아웃 도달과 무관하게 항상 성공
                oAuthLogoutInterface.responseLogoutResult(true);
            }
        });
    }


    /*
     ** kakao 연동 해제
     */
    @Override
    public void requestOAuthRemove(Activity callBackActivity, final OAuthManager.OAuthRemoveInterface oAuthRemoveInterface) {
        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
            String error;

            @Override
            public void onFailure(ErrorResult errorResult) {
                if(TextUtils.isEmpty(errorResult.toString())){
                    error = "NULL";
                }else{
                    error = errorResult.toString();
                }
                oAuthRemoveInterface.responseRemoveResult(false,error);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                if(TextUtils.isEmpty(errorResult.toString())){
                    error = "NULL";
                }else{
                    error = errorResult.toString();
                }
                oAuthRemoveInterface.responseRemoveResult(false,error);
            }

            @Override
            public void onNotSignedUp() {
                oAuthRemoveInterface.responseRemoveResult(true,null);
            }

            @Override
            public void onSuccess(Long userId) {
                oAuthRemoveInterface.responseRemoveResult(true,null);
            }
        });
    }

    /**
     * 사용자 정보 호출
     */
    @Override
    public void requestUserInfo(Activity callBackActivity, final OAuthManager.OAuthUserFrofileInterface oAuthUserFrofileInterface) {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys,new MeV2ResponseCallback() {
            String error;

            @Override
            public void onFailure(ErrorResult errorResult) {
                if(TextUtils.isEmpty(errorResult.toString())){
                    error = "NULL";
                }else{
                    error = errorResult.toString();
                }
                oAuthUserFrofileInterface.responseUserFrofileInfoResult(false,"사용자 정보 호출 실패",error);
                super.onFailure(errorResult);
            }


            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //세션이 닫혀 실패한 경우로 에러 결과를 받습니다. 재로그인 / 토큰발급이 필요합니다.
                if(TextUtils.isEmpty(errorResult.toString())){
                    error = "NULL";
                }else{
                    error = errorResult.toString();
                }
                oAuthUserFrofileInterface.responseUserFrofileInfoResult(false,"재로그인이 필요 합니다.(토큰 만료)",error);

            }

            @Override
            public void onSuccess(MeV2Response result) {
                String userdata = "";
                userdata = userdata+"\n "+"id = "+String.valueOf(result.getId());
                userdata = userdata+"\n "+"email = "+result.getKakaoAccount().getEmail();
                userdata = userdata+"\n\n"+requestAccessTokenInfo();

                oAuthUserFrofileInterface.responseUserFrofileInfoResult(true,userdata,null);
            }

        });

    }

    /**
     * kakao 연동 화면 생성 여부
     * true : kakao 호출, fasle : 미호출
     */
    @Override
    public void requestActivityResult(int requestCode, int resultCode, Intent data) {
        Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data);
    }

    /*
     ** kakao 토근 정보 얻기
     */
    private String requestAccessTokenInfo() {
        String accToken = Session.getCurrentSession().getTokenInfo().getAccessToken();
        String refreshToken = Session.getCurrentSession().getTokenInfo().getRefreshToken();

        if(TextUtils.isEmpty(accToken)){
            accToken = "NUll";
        }

        if(TextUtils.isEmpty(refreshToken)){
            refreshToken = "NUll";
        }

        return "onAccessTokenReceived =\n\naccToken = \n"+accToken+"\n\nrefreshToken = \n"+refreshToken;
    }



    /*
     ** 카카오 로그인 콜백
     */
    public class SessionCallback implements ISessionCallback {

        // 로그인 성공
        @Override
        public void onSessionOpened() {
            String token = requestAccessTokenInfo();
            mOAuthLoginInterface.responseLoginResult(true,token,null);
        }


        // 로그인 실패
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            mOAuthLoginInterface.responseLoginResult(false,null,exception.toString());
        }

    }

    /**
     * KakaoSDK를 사용하기 KakaoAdapter 구현
     * SDK에 필요한 정보 제공을 위해 IApplicationConfig와 ISessionConfig 구현
     */
    public static class KakaoSDKAdapter extends KakaoAdapter {

        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        }
    }

}




