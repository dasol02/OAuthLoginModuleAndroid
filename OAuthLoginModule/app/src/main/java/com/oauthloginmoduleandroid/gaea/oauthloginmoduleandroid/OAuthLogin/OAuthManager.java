package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Facebook.OAuthFacebookManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Google.OAuthGoogleManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao.OAuthKakaoManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Naver.OAuthNaverManager;


public class OAuthManager {
    // Naver Client 정보
    private String OAUTH_NAVER_CLIENT_ID;
    private String OAUTH_NAVER_CLIENT_SECRET;
    private String OAUTH_NAVER_CLIENT_NAME;

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

    // 앱실행
    public void requestStartApp(@NonNull String oAuthNaverClientID, @NonNull String oAuthNaverClientSecret, @NonNull String oAuthNaverClientName){
        this.OAUTH_NAVER_CLIENT_ID = oAuthNaverClientID;
        this.OAUTH_NAVER_CLIENT_SECRET = oAuthNaverClientSecret;
        this.OAUTH_NAVER_CLIENT_NAME = oAuthNaverClientName;

        new OAuthKakaoManager().requestStartAppOAuth();
        new OAuthNaverManager().requestStartAppOAuth();
        new OAuthFacebookManager().requestStartAppOAuth();
        new OAuthGoogleManager().requestStartAppOAuth();
    }

    // 앱 종료
    public void requestDidApp(){
        new OAuthKakaoManager().requestDidAppOAuth();
        new OAuthNaverManager().requestDidAppOAuth();
        new OAuthFacebookManager().requestDidAppOAuth();
        new OAuthGoogleManager().requestDidAppOAuth();
    }

    /**
     * 로그인 여부
     */
    public void getLoginState(@NonNull Activity callBackActivity, @NonNull final OAuthIsLoginInterface oAuthIsLoginInterface) {
        if(oAuthClass == null){
            oAuthIsLoginInterface.responseIsLoginResult(false,"");
            return;
        }
        oAuthClass.requestIsLogin(callBackActivity, new OAuthIsLoginInterface() {
            @Override
            public void responseIsLoginResult(Boolean result, String error) {
                oAuthIsLoginInterface.responseIsLoginResult(result,error);
            }
        });
    }

    /**
     * 로그인 시도
     * @param oAuthType : 연동사 이름 변수
     */
    public void requestSNSLogin(@NonNull OAuthBaseClass.OAuthType oAuthType, @NonNull Activity callBackActivity, @NonNull final OAuthLoginInterface oAuthLoginInterface) {
        oAuthClass = createOAuthClass(oAuthType);
        if(oAuthClass == null){ return; }
        oAuthClass.initOAuthSDK(callBackActivity);
        oAuthClass.requestOAuthLogin(callBackActivity, new OAuthLoginInterface() {
            @Override
            public void responseLoginResult(Boolean result, String token, String error) {
                // 로그인 결과
                oAuthLoginInterface.responseLoginResult(result, token, error);
            }
        });
    }

    // OAuth Class 생성
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
     * 로그 아웃
     */
    public void requestSNSLogOut(@NonNull Activity callBackActivity, @NonNull final OAuthLogoutInterface oAuthLogoutInterface){
        if(oAuthClass == null){return;}
         oAuthClass.requestOAuthLogout(callBackActivity, new OAuthLogoutInterface() {
             @Override
             public void responseLogoutResult(Boolean result) {
                 // 로그아웃 결과
                 oAuthLogoutInterface.responseLogoutResult(result);
             }
         });
    }


    /**
     * 연동사 해제
     */
    public void requestSNSDelete(@NonNull Activity callBackActivity, @NonNull final OAuthRemoveInterface oAuthRemoveInterface){
        if(oAuthClass == null){return;}
        oAuthClass.requestOAuthRemove(callBackActivity, new OAuthRemoveInterface() {
            @Override
            public void responseRemoveResult(Boolean result, String error) {
                // 연동해제 결과
                oAuthRemoveInterface.responseRemoveResult(result, error);
            }
        });
    }

    /**
     * 연동사 로그인 페이지 연결
     * @return -> true: 연동사 App 또는 웹뷰 진행, fasle : super진행
     */
    public void responseOnActivityResult(int requestCode, int resultCode, Intent data){
        if(oAuthClass == null){return;};
        oAuthClass.requestActivityResult(requestCode, resultCode, data);
    }


    /**
     * 사용자 정보 호출
     */
    public void requestUserFrofileInfo(@NonNull Activity callBackActivity, @NonNull final OAuthUserFrofileInterface oAuthUserFrofileInterface){
        if(oAuthClass == null){return;}
        oAuthClass.requestUserInfo(callBackActivity, new OAuthUserFrofileInterface() {
            @Override
            public void responseUserFrofileInfoResult(Boolean result, String userinfo, String error) {
                // 사용자 정보 요청 결과
                oAuthUserFrofileInterface.responseUserFrofileInfoResult(result, userinfo, error);
            }
        });
    }


    /** OAuth Client Setting Getter
     * @return 호출 이름
     */
    public String getOauthClientId() {
        return OAUTH_NAVER_CLIENT_ID;
    }

    public String getOauthClientSecret() {
        return OAUTH_NAVER_CLIENT_SECRET;
    }

    public String getOauthClientName() {
        return OAUTH_NAVER_CLIENT_NAME;
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
         * @param userinfo : 사용자 정보
         * @param error : 오류 내용
         */
        void responseUserFrofileInfoResult(Boolean result, String userinfo, String error);
    }

}
