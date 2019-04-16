package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Facebook.OAuthFacebookManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Google.OAuthGoogleManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao.OAuthKakaoManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Naver.OAuthNaverManager;


public class OAuthManager {
    private OAuthBaseClass oAuthClass; // OAuth 클랙스

    private static OAuthManager sInstance;

    public static OAuthManager getsInstance() {
        if (sInstance == null) {
            synchronized (OAuthManager.class) {
                if (sInstance == null) {
                    sInstance = new OAuthManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * OAuth Class 생성
     */
    private OAuthBaseClass createOAuthClass(OAuthBaseClass.OAuthType oAuthType) {

        OAuthBaseClass oAuthBaseClassResult = null;

        switch (oAuthType){
            case OAuth_KAKAO:
                oAuthBaseClassResult = new OAuthKakaoManager();
                break;
            case OAuth_NAVER:
                oAuthBaseClassResult = new OAuthNaverManager();
                break;
            case OAuth_FACEBOOK:
                oAuthBaseClassResult = new OAuthFacebookManager();
                break;
            case OAuth_GOOGLE:
                oAuthBaseClassResult = new OAuthGoogleManager();
                break;
            default:
                break;
        }

        return oAuthBaseClassResult;
    }

    /**
     * 앱실행
     */
    public void requestStartApp(){
        new OAuthKakaoManager().requestStartAppOAuth();
        new OAuthNaverManager().requestStartAppOAuth();
        new OAuthFacebookManager().requestStartAppOAuth();
        new OAuthGoogleManager().requestStartAppOAuth();
    }

    /**
     * 앱 종료
     */
    public void requestDidApp(){
        new OAuthKakaoManager().requestDidAppOAuth();
        new OAuthNaverManager().requestDidAppOAuth();
        new OAuthFacebookManager().requestDidAppOAuth();
        new OAuthGoogleManager().requestDidAppOAuth();
    }

    /**
     * 로그인 여부 확인
     */
    public void requestOAuthIsLogin(@NonNull Activity callBackActivity, @NonNull final OAuthIsLoginInterface oAuthIsLoginInterface) {
        if(oAuthClass == null){ oAuthIsLoginInterface.responseIsLoginResult(false,"OAuth Class NUll"); return; } // OAuth Class Null
        oAuthClass.requestIsLogin(callBackActivity, oAuthIsLoginInterface); // OAuth Is Login
    }

    /**
     * 로그인 시도
     * @param oAuthType : 연동사 이름 변수
     */
    public void requestSNSLogin(@NonNull OAuthBaseClass.OAuthType oAuthType, @NonNull Activity callBackActivity, @NonNull final OAuthLoginInterface oAuthLoginInterface) {
        oAuthClass = createOAuthClass(oAuthType); // OAuth Class Create
        if(oAuthClass == null){ oAuthLoginInterface.responseLoginResult(false,null,"OAuth Type Fail"); return; } // OAuth Create Fail
        oAuthClass.initOAuthSDK(callBackActivity); // SDK Setting
        oAuthClass.requestOAuthLogin(callBackActivity, oAuthLoginInterface); // OAuth Login
    }

    /**
     * 로그 아웃
     */
    public void requestSNSLogOut(@NonNull Activity callBackActivity, @NonNull final OAuthLogoutInterface oAuthLogoutInterface){
        if(oAuthClass == null){ oAuthLogoutInterface.responseLogoutResult(false); return;}
         oAuthClass.requestOAuthLogout(callBackActivity, oAuthLogoutInterface); // OAuth Logout
    }

    /**
     * 연동사 해제
     */
    public void requestSNSDelete(@NonNull Activity callBackActivity, @NonNull final OAuthRemoveInterface oAuthRemoveInterface){
        if(oAuthClass == null){ oAuthRemoveInterface.responseRemoveResult(false,"OAuth Class NULL"); return;}
        oAuthClass.requestOAuthRemove(callBackActivity, oAuthRemoveInterface);
    }

    /**
     * 연동사 로그인 페이지 연결
     */
    public void responseOnActivityResult(int requestCode, int resultCode, Intent data){
        if(oAuthClass == null){return;};
        oAuthClass.requestActivityResult(requestCode, resultCode, data);
    }


    /**
     * 사용자 정보 호출
     */
    public void requestUserFrofileInfo(@NonNull Activity callBackActivity, @NonNull final OAuthUserFrofileInterface oAuthUserFrofileInterface){
        if(oAuthClass == null){ oAuthUserFrofileInterface.responseUserFrofileInfoResult(false,null,"OAuth Class NUll"); return;}
        oAuthClass.requestUserInfo(callBackActivity, oAuthUserFrofileInterface);
    }



    /**
     * OAuth Response Interface
     */
    // 인터페이스 (로그인 여부)
    public interface OAuthIsLoginInterface {
        /**
         * 로그인 여부 CallBack
         * @param result : 결과 (true : 연결 성공, false : 실패)
         * @param error : 해당 연동사 로그인 토큰
         */
        void responseIsLoginResult(Boolean result, String error);
    }

    public interface OAuthLoginInterface {
        /**
         * 로그인 결과 CallBack
         * @param result : 결과 (true : 연결 성공, false : 실패)
         * @param token : 해당 연동사 로그인 토큰
         */
        void responseLoginResult(Boolean result, String token, String error);
    }

    // 인터페이스 (로그아웃)
    public interface OAuthLogoutInterface {
        /**
         * 로그아웃 결과 CallBack
         * @param result : 결과 (True, False)
         */
        void responseLogoutResult(Boolean result);
    }

    // 인터페이스 (연동해제)
    public interface OAuthRemoveInterface {
        /**
         * 연동해제 CallBack
         * @param result : 결과 (True, False)
         * @param error : 오류 내용
         */
        void responseRemoveResult(Boolean result, String error);
    }

    // 인터페이스 (사용자 정보)
    public interface OAuthUserFrofileInterface {
        /**
         * 사용자 정보 CallBack
         * @param result : 결과 (True, False)
         * @param oAuthUserInfo : 사용자 정보
         * @param error : 오류 내용
         */
        void responseUserFrofileInfoResult(Boolean result, OAuthUserInfo oAuthUserInfo, String error);
    }

}
