package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin;

enum SNSAuthType {

    SNS_KAKAO(0),

    SNS_NAVER(1),

    SNS_FACEBOOK(2),

    SNS_GOOGLE(4);

    private final int number;

    SNSAuthType(int i) {
        this.number = i;
    }

    public int getNumber() {
        return number;
    }

    public static SNSAuthType valueOf(int number){
        if(number == SNS_KAKAO.getNumber()) {
            return SNS_KAKAO;
        } else if (number == SNS_NAVER.getNumber()) {
            return SNS_NAVER;
        } else if (number == SNS_FACEBOOK.getNumber()) {
            return SNS_FACEBOOK;
        } else if (number == SNS_GOOGLE.getNumber()) {
            return SNS_GOOGLE;
        } else {
            return null;
        }
    }
}


public class OAuthManager {

    private static OAuthManager sInstance;

    public static OAuthManager getInstance() {
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
     * 로그인 시도
     * @param snsName : 연동사 이름 변수
     */
    public void requestSNSLogin(SNSAuthType snsName){

    }
}
