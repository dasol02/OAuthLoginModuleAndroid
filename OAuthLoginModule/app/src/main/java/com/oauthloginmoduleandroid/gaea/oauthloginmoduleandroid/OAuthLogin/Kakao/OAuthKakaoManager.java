package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthCovenantInterface;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.SNSAuthType;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.R;

import java.util.ArrayList;
import java.util.List;

public class OAuthKakaoManager{

    SessionCallback callback;
    private static OAuthKakaoManager sInstance;
    private static OAuthCovenantInterface oAuthCovenantInterface;

    public static OAuthKakaoManager getInstance() {
        if (sInstance == null) {
            synchronized (OAuthKakaoManager.class) {
                if (sInstance == null) {
                    sInstance = new OAuthKakaoManager();
                }
            }
        }
        return sInstance;
    }


    public void setoAuthCovenantInterface(OAuthCovenantInterface oAuthCovenantInterface) {
        OAuthKakaoManager.oAuthCovenantInterface = oAuthCovenantInterface;
    }


    /**
     * kakao Login
     */
    public void kakaoLogin(){
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        // 우선권 부여 : AuthType.KAKAO_TALK
        // kakao talk 미 설치시 KAKAO_ACCOUNT로 연경
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL,OAuthManager.getmActivity());
    }


    /**
     * kakao Logout
     */
    public void kakaoLogOut(){

        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                // 로그아웃을 성공한 경우 불립니다. 서버에 로그아웃 도달과 무관하게 항상 성공
                oAuthCovenantInterface.responseLogoutResult(SNSAuthType.SNS_KAKAO,true);
                Log.d("OAuth KAKAO","requestLogout onCompleteLogout");
            }
        });
    }


    /*
     ** kakao 로그인 유무 확인
     */
    public Boolean requestLoginInfo() {
        String accToken = Session.getCurrentSession().getTokenInfo().getAccessToken();
        String refreshToken = Session.getCurrentSession().getTokenInfo().getRefreshToken();

        Log.d("OAuth KAKAO", "onAccessTokenReceived =\naccToken = "+accToken+"\nrefreshToken = "+refreshToken);


        if(!TextUtils.isEmpty(accToken) && !TextUtils.isEmpty(refreshToken)){
            return true;
        }else{
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


    /**
     * kakao 연동 화면 생성 여부
     * true : kakao 호출, fasle : 미호출
     */
    public Boolean checkActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return true;
        }else {
            return false;
        }
    }


    /**
     * kakao Session 제거
     * 앱 종료시점 호출
     */
    public void removeKakaoSession(){
        Session.getCurrentSession().removeCallback(callback);
    }


    /*
     ** kakao 연동 해제
     */
    public void kakaoDelete(){
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
                oAuthCovenantInterface.responseDeleteResult(SNSAuthType.SNS_KAKAO,false,error);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                if(TextUtils.isEmpty(errorResult.toString())){
                    error = "NULL";
                }else{
                    error = errorResult.toString();
                }
                Log.d("OAuth KAKAO",error);
                oAuthCovenantInterface.responseDeleteResult(SNSAuthType.SNS_KAKAO,false,error);
            }

            @Override
            public void onNotSignedUp() {
                Log.d("OAuth KAKAO","onNotSignedUp");
                oAuthCovenantInterface.responseDeleteResult(SNSAuthType.SNS_KAKAO,true,null);
            }

            @Override
            public void onSuccess(Long userId) {
                Log.d("OAuth KAKAO","onSuccess");
                oAuthCovenantInterface.responseDeleteResult(SNSAuthType.SNS_KAKAO,true,null);
            }
        });

    }



    /**
     * 사용자 정보 호출
     */
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
                oAuthCovenantInterface.responseUserFrofileInfoResult(SNSAuthType.SNS_KAKAO,false,"사용자 정보 호출 실패",error);
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
                oAuthCovenantInterface.responseUserFrofileInfoResult(SNSAuthType.SNS_KAKAO,false,"재로그인이 필요 합니다.(토큰 만료)",error);

            }

            @Override
            public void onSuccess(MeV2Response result) {
                String userdata = "";
                userdata = userdata+"\n "+"id = "+String.valueOf(result.getId());
                userdata = userdata+"\n "+"email = "+result.getKakaoAccount().getEmail();
                userdata = userdata+"\n\n"+requestAccessTokenInfo();

                oAuthCovenantInterface.responseUserFrofileInfoResult(SNSAuthType.SNS_KAKAO,true,userdata,null);

                Log.d("OAuth KAKAO", result.toString());
                Log.d("OAuth KAKAO id = ", result.getId() + "");
                Log.d("OAuth KAKAO email = ", result.getKakaoAccount().getEmail());
            }

        });

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
            oAuthCovenantInterface.responseCovenantLoginResult(SNSAuthType.SNS_KAKAO,true,token,null);
        }


        // 로그인 실패
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("OAuth KAKAO", "Login onSessionOpenFailed");
            oAuthCovenantInterface.responseCovenantLoginResult(SNSAuthType.SNS_KAKAO,false,null,exception.toString());
        }

    }

}
