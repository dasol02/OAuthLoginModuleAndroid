package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Facebook.OAuthFacebookManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Google.OAuthGoogleManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao.OAuthKakaoManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Naver.OAuthNaverManager;

public class OAuthManager implements OAuthCovenantInterface{

    private static OAuthManager sInstance;
    private static Activity mActivity;
    private static OAuthLoginInterface oAuthLoginInterface;
    private static OAuthLogoutInterface oAuthLogoutInterface;
    private static OAuthUserFrofileInterface oAuthUserFrofileInterface;

    private OAuthKakaoManager oAuthKakaoManager;
    private OAuthNaverManager oAuthNaverManager;
    private OAuthFacebookManager oAuthFacebookManager;
    private OAuthGoogleManager oAuthGoogleManager;

    public OAuthManager() {
        // kakao 생성
        oAuthKakaoManager = OAuthKakaoManager.getInstance();
        oAuthKakaoManager.setoAuthCovenantInterface(this);

        // naver 생성
        oAuthNaverManager = OAuthNaverManager.getInstance();
        oAuthNaverManager.setoAuthCovenantInterface(this);

        // 페이스북 생성
        oAuthFacebookManager = OAuthFacebookManager.getInstance();
        oAuthFacebookManager.setoAuthCovenantInterface(this);

        // 페이스북 생성
        oAuthGoogleManager = OAuthGoogleManager.getInstance();
        oAuthGoogleManager.setoAuthCovenantInterface(this);
    }

    public void setCallBackActivity(Activity mainActivity) {
        this.mActivity = mainActivity;
        oAuthKakaoManager.setoAuthCovenantContext(mActivity);
        oAuthNaverManager.setoAuthCovenantContext(mActivity);
        oAuthNaverManager.setNaverOAuthSetting();

        oAuthFacebookManager.setoAuthCovenantContext(mainActivity);
        oAuthFacebookManager.setFacebookOAuthSetting();

        oAuthGoogleManager.setoAuthCovenantContext(mainActivity);
        oAuthGoogleManager.setGoogleOAuthSetting();
    }


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
     * 앱 종료시 연결되어 있던 시즌 제거
     */
    public void removeSession(){
        oAuthKakaoManager.removeKakaoSession();
    }


    /**
     * 로그인 여부
     */
    public Boolean getLoginState() {

        if(oAuthKakaoManager.requestLoginInfo()){
            Log.d("OAuth Manger","Login State info Success \nSNS NAME = KAKAO");
            return true;
        }else if(oAuthNaverManager.requestLoginInfo()){
            Log.d("OAuth Manger","Login State info Success \nSNS NAME = NAVER");
            return true;
        }else if(oAuthFacebookManager.requestLoginInfo()){
            Log.d("OAuth Manger","Login State info Success \nSNS NAME = FACEBOOK");
            return true;
        }else if(oAuthGoogleManager.requestLoginInfo()){
            Log.d("OAuth Manger","Login State info Success \nSNS NAME = GOOGLE");
            return true;
        }else{
            return false;
        }
    }


    /**
     * 로그인 시도
     * @param snsName : 연동사 이름 변수
     */
    public void requestSNSLogin(SNSAuthType snsName) {
        switch (snsName) {
            case SNS_KAKAO:
                oAuthKakaoManager.kakaoLogin();
                break;
            case SNS_NAVER:
                oAuthNaverManager.naverLogin();
                break;
            case SNS_FACEBOOK:
                oAuthFacebookManager.facebookLogin();
                break;
            case SNS_GOOGLE:
                oAuthGoogleManager.googleLogin();
                break;
            default:
                break;
        }
    }


    /**
     * 로그 아웃
     */
    public void requestSNSLogOut(){
        if(oAuthKakaoManager.requestLoginInfo()){
            oAuthKakaoManager.kakaoLogOut();
        }else if(oAuthNaverManager.requestLoginInfo()){
            oAuthNaverManager.naverLogout();
        }else if(oAuthFacebookManager.requestLoginInfo()){
            oAuthFacebookManager.facebookLogout();
        }else if(oAuthGoogleManager.requestLoginInfo()){
            oAuthGoogleManager.googleLogout();
        }else{
            oAuthLogoutInterface.responseLogoutResult(null,false);
        }
    }


    /**
     * 연동사 해제
     */
    public void requestSNSDelete(){
        if(oAuthKakaoManager.requestLoginInfo()){
            oAuthKakaoManager.kakaoDelete();
        }else if(oAuthNaverManager.requestLoginInfo()){
            oAuthNaverManager.naverDelete();
        }else if(oAuthFacebookManager.requestLoginInfo()){ // 연동해제 기능 재확인 필요
            oAuthFacebookManager.facebookLogout();
        }else if(oAuthGoogleManager.requestLoginInfo()){
            oAuthGoogleManager.googleDelete();
        }else{
            oAuthLogoutInterface.responseDeleteResult(null,false,"로그인 접속 정보 조회 오류");
        }
    }


    /**
     * 연동사 로그인 페이지 연결
     * @return -> true: 연동사 App 또는 웹뷰 진행, fasle : super진행
     */
    public Boolean responseOnActivityResult(int requestCode, int resultCode, Intent data){
        if(oAuthKakaoManager.checkActivityResult(requestCode,resultCode,data)){
            return true;
        }else if(oAuthFacebookManager.checkActivityResult(requestCode, resultCode, data)){
            return true;
        }else if(oAuthGoogleManager.checkActivityResult(requestCode, resultCode, data)){
            return false;  // 구글 핸들러 사용으로 인한 반환값 False;
        }else{
            return false;
        }
        // naver 콜백 없음
    }


    /**
     * 사용자 정보 호출
     */
    public void responseUserFrofileInfo(){
        if(oAuthKakaoManager.requestLoginInfo()) {
            oAuthKakaoManager.requestUserInfo();
        }else if(oAuthNaverManager.requestLoginInfo()){
            oAuthNaverManager.requestUserInfo();
        }else if(oAuthFacebookManager.requestLoginInfo()){
            oAuthFacebookManager.requestUserInfo();
        }else if(oAuthGoogleManager.requestLoginInfo()){
            oAuthGoogleManager.requestUserInfo();
        }else{
            oAuthUserFrofileInterface.responseUserFrofileInfoResult(null,false,"로그인 접속 오류",null);
        }
    }



    // InterFace Override

    /**
     * 로그인 시도 결과 인터페이스 전달
     * @param snsName : 로그인 시도한 연동사
     * @param result : 결과 (true : 연결 성공, false : 실패)
     * @param token : 해당 연동사 로그인 토큰
     */
    @Override // 로그인 결과
    public void responseCovenantLoginResult(SNSAuthType snsName, Boolean result, String token, String error) {
        oAuthLoginInterface.responseLoginResult(snsName,result,token,error);
    }

    @Override // 로그아웃 결과
    public void responseLogoutResult(SNSAuthType snsName, Boolean result) {
        oAuthLogoutInterface.responseLogoutResult(snsName,result);
    }

    @Override // 연동해제 결과
    public void responseDeleteResult(SNSAuthType snsName, Boolean result, String error) {
        oAuthLogoutInterface.responseDeleteResult(snsName, result, error);
    }

    @Override // 사용자 정보 요청 결과
    public void responseUserFrofileInfoResult(SNSAuthType snsName, Boolean result, String userinfo, String error) {
        oAuthUserFrofileInterface.responseUserFrofileInfoResult(snsName, result, userinfo, error);
    }
}
