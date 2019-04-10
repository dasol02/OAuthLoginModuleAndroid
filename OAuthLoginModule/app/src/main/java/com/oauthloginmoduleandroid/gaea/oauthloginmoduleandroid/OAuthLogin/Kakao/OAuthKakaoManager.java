package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao;

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
import java.util.ArrayList;
import java.util.List;

public class OAuthKakaoManager extends OAuthBaseClass {

    SessionCallback callback;


    @Override
    public void requestStartAppOAuth() {
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    @Override
    public void requestDidAppOAuth() {
        Session.getCurrentSession().removeCallback(callback); // kakao Session 제거
    }


    @Override
    public void initOAuthSDK() {

    }

    /*
     ** kakao 로그인 유무 확인
     */
    @Override
    public Boolean requestIsLogin() {
        String accToken = Session.getCurrentSession().getTokenInfo().getAccessToken();
        String refreshToken = Session.getCurrentSession().getTokenInfo().getRefreshToken();

//        Log.d("OAuth KAKAO", "onAccessTokenReceived =\naccToken = "+accToken+"\nrefreshToken = "+refreshToken);

        if(!TextUtils.isEmpty(accToken) && !TextUtils.isEmpty(refreshToken)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * kakao Login
     */
    @Override
    public void requestOAuthLogin() {
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        // 우선권 부여 : AuthType.KAKAO_TALK
        // kakao talk 미 설치시 KAKAO_ACCOUNT로 연결
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL,mContext);
    }

    /**
     * kakao Logout
     */
    @Override
    public void requestOAuthLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                // 로그아웃을 성공한 경우 불립니다. 서버에 로그아웃 도달과 무관하게 항상 성공
                mResponseOAuthCovenantInterface.responseOAuthLogoutResult(OAuthType.OAuth_KAKAO,true);
                Log.d("OAuth KAKAO","requestLogout onCompleteLogout");
            }
        });
    }


    /*
     ** kakao 연동 해제
     */
    @Override
    public void requestOAuthremove() {
        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
            String error;

            @Override
            public void onFailure(ErrorResult errorResult) {
                if(TextUtils.isEmpty(errorResult.toString())){
                    error = "NULL";
                }else{
                    error = errorResult.toString();
                }
                Log.d("OAuth KAKAO",error);
                mResponseOAuthCovenantInterface.responseOAuthRemoveResult(OAuthType.OAuth_KAKAO,false,error);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                if(TextUtils.isEmpty(errorResult.toString())){
                    error = "NULL";
                }else{
                    error = errorResult.toString();
                }
                Log.d("OAuth KAKAO",error);
                mResponseOAuthCovenantInterface.responseOAuthRemoveResult(OAuthType.OAuth_KAKAO,false,error);
            }

            @Override
            public void onNotSignedUp() {
                Log.d("OAuth KAKAO","onNotSignedUp");
                mResponseOAuthCovenantInterface.responseOAuthRemoveResult(OAuthType.OAuth_KAKAO,true,null);
            }

            @Override
            public void onSuccess(Long userId) {
                Log.d("OAuth KAKAO","onSuccess");
                mResponseOAuthCovenantInterface.responseOAuthRemoveResult(OAuthType.OAuth_KAKAO,true,null);
            }
        });
    }

    /**
     * 사용자 정보 호출
     */
    @Override
    public void requestUserInfo() {
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
                Log.e("OAuth KAKAO", "onFailure error message=" + error);
                mResponseOAuthCovenantInterface.responseOAuthUserFrofileInfoResult(OAuthType.OAuth_KAKAO,false,"사용자 정보 호출 실패",error);
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
                Log.e("OAuth KAKAO", "onFailure error message=" + error);
                mResponseOAuthCovenantInterface.responseOAuthUserFrofileInfoResult(OAuthType.OAuth_KAKAO,false,"재로그인이 필요 합니다.(토큰 만료)",error);

            }

            @Override
            public void onSuccess(MeV2Response result) {
                String userdata = "";
                userdata = userdata+"\n "+"id = "+String.valueOf(result.getId());
                userdata = userdata+"\n "+"email = "+result.getKakaoAccount().getEmail();
                userdata = userdata+"\n\n"+requestAccessTokenInfo();

                mResponseOAuthCovenantInterface.responseOAuthUserFrofileInfoResult(OAuthType.OAuth_KAKAO,true,userdata,null);

                Log.d("OAuth KAKAO", result.toString());
                Log.d("OAuth KAKAO id = ", result.getId() + "");
                Log.d("OAuth KAKAO email = ", result.getKakaoAccount().getEmail());
            }

        });

    }

    /**
     * kakao 연동 화면 생성 여부
     * true : kakao 호출, fasle : 미호출
     */
    @Override
    public Boolean requestActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return true;
        }else {
            return false;
        }
    }

    /*
     ** kakao 토근 정보 얻기
     */
    public String requestAccessTokenInfo() {
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
            Log.e("OAuth KAKAO", "Login onSessionOpened");
            String token = requestAccessTokenInfo();
            mResponseOAuthCovenantInterface.responseOAuthCovenantLoginResult(OAuthType.OAuth_KAKAO,true,token,null);
        }


        // 로그인 실패
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("OAuth KAKAO", "Login onSessionOpenFailed");
            mResponseOAuthCovenantInterface.responseOAuthCovenantLoginResult(OAuthType.OAuth_KAKAO,false,null,exception.toString());
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




