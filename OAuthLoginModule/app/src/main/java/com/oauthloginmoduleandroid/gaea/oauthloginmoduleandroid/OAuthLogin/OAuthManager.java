package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import android.app.Activity;
import android.content.Intent;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Facebook.OAuthFacebookManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Google.OAuthGoogleManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao.OAuthKakaoManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Naver.OAuthNaverManager;

public class OAuthManager implements OAuthBaseClass.ResponseOAuthCovenantInterface {

    // Naver Client 정보
    private static String OAUTH_NAVER_CLIENT_ID;
    private static String OAUTH_NAVER_CLIENT_SECRET;
    private static String OAUTH_NAVER_CLIENT_NAME;

    // Interface
    private static OAuthLoginInterface oAuthLoginInterface;
    private static OAuthLogoutInterface oAuthLogoutInterface;
    private static OAuthUserFrofileInterface oAuthUserFrofileInterface;

    private static OAuthManager sInstance;
    private static Activity mActivity;
    private static OAuthBaseClass oAuthClass;


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
    public void requestStartApp(){
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


    public void initOAuthManagerInfoSettingScheme(String oAuthNaverClientID, String oAuthNaverClientSecret, String oAuthNaverClientName){
        OAUTH_NAVER_CLIENT_ID = oAuthNaverClientID;
        OAUTH_NAVER_CLIENT_SECRET = oAuthNaverClientSecret;
        OAUTH_NAVER_CLIENT_NAME = oAuthNaverClientName;
        requestStartApp();
    }


    // OAuth Class 생성
    protected void initOAuth(OAuthBaseClass.OAuthType oAuthType){
        oAuthClass = null;

        switch (oAuthType){
            case OAuth_KAKAO:
                oAuthClass = new OAuthKakaoManager();
                break;
            case OAuth_NAVER:
                oAuthClass = new OAuthNaverManager();
                break;
            case OAuth_FACEBOOK:
                oAuthClass = new OAuthFacebookManager();
                break;
            case OAuth_GOOGLE:
                oAuthClass = new OAuthGoogleManager();
                break;
            default:
                break;
        }
        oAuthClass.setResponseOAuthCovenantContext(mActivity);
        oAuthClass.setResponseOAuthCovenantInterface(this);
        oAuthClass.initOAuthSDK();

    }


    public void setCallBackActivity(Activity mainActivity) {
        this.mActivity = mainActivity;
    }


    /**
     * 로그인 여부
     */
    public Boolean getLoginState() {
        if(oAuthClass == null){return false;}
        return oAuthClass.requestIsLogin();
    }


    /**
     * 로그인 시도
     * @param oAuthType : 연동사 이름 변수
     */
    public void requestSNSLogin(OAuthBaseClass.OAuthType oAuthType) {
        initOAuth(oAuthType);
        if(oAuthClass == null){return;}
        oAuthClass.requestOAuthLogin();
    }


    /**
     * 로그 아웃
     */
    public void requestSNSLogOut(){
        if(oAuthClass == null){return;}
        if(oAuthClass.requestIsLogin()){
            oAuthClass.requestOAuthLogout();
        }else{

        }
    }


    /**
     * 연동사 해제
     */
    public void requestSNSDelete(){
        if(oAuthClass == null){return;}
        if(oAuthClass.requestIsLogin()){
            oAuthClass.requestOAuthremove();
        }else{
            oAuthLogoutInterface.responseDeleteResult(null,false,"로그인 접속 정보 조회 오류");
        }
    }


    /**
     * 연동사 로그인 페이지 연결
     * @return -> true: 연동사 App 또는 웹뷰 진행, fasle : super진행
     */
    public Boolean responseOnActivityResult(int requestCode, int resultCode, Intent data){
        return oAuthClass.requestActivityResult(requestCode, resultCode, data);
    }


    /**
     * 사용자 정보 호출
     */
    public void responseUserFrofileInfo(){
        if(oAuthClass == null){return;}
        if(oAuthClass.requestIsLogin()) {
            oAuthClass.requestUserInfo();
        }else{

        }
    }


    // Set OAuth Manager Response InterFace
    /**
     * 인터페이스 추가 (로그인)
     * @param oAuthLoginInterface
     */
    public void setoAuthLoginInterface(OAuthLoginInterface oAuthLoginInterface){
        this.oAuthLoginInterface = oAuthLoginInterface;
    }

    /**
     * 인터페이스 추가 (로그아웃)
     * @param oAuthLogoutInterface
     */
    public void setoAuthLogoutInterface(OAuthLogoutInterface oAuthLogoutInterface){
        this.oAuthLogoutInterface = oAuthLogoutInterface;
    }

    /**
     * 인터페이스 추가 (사용자 정보)
     * @param oAuthUserFrofileInterface
     */
    public void setoAuthUserFrofileInterface(OAuthUserFrofileInterface oAuthUserFrofileInterface){
        this.oAuthUserFrofileInterface = oAuthUserFrofileInterface;
    }


    /**
     * OAuth Manger Response Interface
     */
    public interface OAuthLoginInterface {
        void responseLoginResult(OAuthBaseClass.OAuthType oAuthType, Boolean result, String token, String error);
    }


    public interface OAuthLogoutInterface {
        void responseLogoutResult(OAuthBaseClass.OAuthType oAuthType, Boolean result);
        void responseDeleteResult(OAuthBaseClass.OAuthType oAuthType, Boolean result, String error);
    }


    public interface OAuthUserFrofileInterface {
        void responseUserFrofileInfoResult(OAuthBaseClass.OAuthType oAuthType, Boolean result, String userinfo, String error);
    }

    /**
     * 로그인 시도 결과 인터페이스 전달
     * @param oAuthType : 로그인 시도한 연동사
     * @param result : 결과 (true : 연결 성공, false : 실패)
     * @param token : 해당 연동사 로그인 토큰
     */
    @Override // 로그인 결과
    public void responseOAuthCovenantLoginResult(OAuthBaseClass.OAuthType oAuthType, Boolean result, String token, String error) {
        oAuthLoginInterface.responseLoginResult(oAuthType,result,token,error);
    }

    @Override // 로그아웃 결과
    public void responseOAuthLogoutResult(OAuthBaseClass.OAuthType oAuthType, Boolean result) {
        oAuthLogoutInterface.responseLogoutResult(oAuthType,result);
    }

    @Override // 연동해제 결과
    public void responseOAuthRemoveResult(OAuthBaseClass.OAuthType oAuthType, Boolean result, String error) {
        oAuthLogoutInterface.responseDeleteResult(oAuthType, result, error);
    }

    @Override // 사용자 정보 요청 결과
    public void responseOAuthUserFrofileInfoResult(OAuthBaseClass.OAuthType oAuthType, Boolean result, String userinfo, String error) {
        oAuthUserFrofileInterface.responseUserFrofileInfoResult(oAuthType, result, userinfo, error);
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
}
