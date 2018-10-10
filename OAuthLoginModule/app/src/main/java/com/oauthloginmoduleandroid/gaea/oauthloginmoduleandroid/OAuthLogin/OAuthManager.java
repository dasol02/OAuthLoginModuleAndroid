package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

import android.app.Activity;
import android.content.Intent;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao.OAuthKakaoManager;

public class OAuthManager implements OAuthCovenantInterface{

    private static OAuthManager sInstance;
    private static Activity mActivity;
    private static OAuthManagerInterface oAuthManagerInterface;
    private OAuthKakaoManager oAuthKakaoManager;

    public OAuthManager(Activity mainActivity) {
        this.mActivity = mainActivity;

        // kakao 생성
        oAuthKakaoManager = OAuthKakaoManager.getInstance();
        oAuthKakaoManager.setoAuthCovenantInterface(this);
    }


    public static OAuthManager getsInstance() {
        return sInstance;
    }


    public static OAuthManager newInstance(Activity mainActivity) {
        if (sInstance == null) {
            synchronized (OAuthManager.class) {
                if (sInstance == null) {
                    sInstance = new OAuthManager(mainActivity);
                }
            }
        }
        return sInstance;
    }


    /**
     * 인터페이스 추가 (추후 로그인, 로그아웃 등 데이터 전달)
     * @param oAuthManagerInterface
     */
    public void setoAuthInterface(OAuthManagerInterface oAuthManagerInterface){
        this.oAuthManagerInterface = oAuthManagerInterface;
    }

    /**
     * 메인 엑티비티 전달
     * @return : onActivityResult 전달 받을 액티비티
     */
    public static Activity getmActivity() {
        return mActivity;
    }


    /**
     * 앱 종료시 연결되어 있던 시즌 제거
     */
    public void removeSession(){
        oAuthKakaoManager.removeKakaoSession();
    }


    /**
     * 로그인 시도
     * @param snsName : 연동사 이름 변수
     */
    public void requestSNSLogin(SNSAuthType snsName){
        switch (snsName){
            case SNS_KAKAO:
                oAuthKakaoManager.kakaoLogin();
                break;
            case SNS_NAVER:
                break;
            case SNS_FACEBOOK:
                break;
            case SNS_GOOGLE:
                break;
            default:
                break;
        }
    }


    /**
     * 로그 아웃
     * @param snsName : 연동사 이름 변수
     */
    public void requestSNSLogOut(SNSAuthType snsName){
        switch (snsName){
            case SNS_KAKAO:
                oAuthKakaoManager.kakaoLogOut();
                break;
            case SNS_NAVER:
                break;
            case SNS_FACEBOOK:
                break;
            case SNS_GOOGLE:
                break;
            default:
                break;
        }
    }


    /**
     * 연동사 해제
     * @param snsName : 연동사 이름 변수
     */
    public void requestSNSDelete(SNSAuthType snsName){
        switch (snsName){
            case SNS_KAKAO:
                oAuthKakaoManager.kakaoDelete();
                break;
            case SNS_NAVER:
                break;
            case SNS_FACEBOOK:
                break;
            case SNS_GOOGLE:
                break;
            default:
                break;
        }
    }


    /**
     * 연동사 로그인 페이지 연결
     * @return -> true: 연동사 App 또는 웹뷰 진행, fasle : super진행
     */
    public Boolean responseOnActivityResult(int requestCode, int resultCode, Intent data){
        if(oAuthKakaoManager.checkActivityResult(requestCode,resultCode,data)){
            return true;
        }else{
            return false;
        }
    }



    /**
     * 로그인 시도 결과 인터페이스 전달
     * @param snsName : 로그인 시도한 연동사
     * @param result : 결과 (true : 연결 성공, false : 실패)
     * @param token : 해당 연동사 로그인 토큰
     */
    @Override
    public void responseCovenantLoginResult(SNSAuthType snsName, Boolean result, String token, String error) {
        // 로그인 화면으로 전달
        oAuthManagerInterface.responseLoginResult(snsName,result,token,error);
    }

}
