package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthBaseClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OAuthManager.OAuthLoginInterface {

    Button button_login_kakao,button_login_naver,button_login_google,button_login_facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button_login_kakao = (Button)findViewById(R.id.button_login_kakao);
        button_login_naver = (Button)findViewById(R.id.button_login_naver);
        button_login_google = (Button)findViewById(R.id.button_login_google);
        button_login_facebook = (Button)findViewById(R.id.button_login_facebook);

        button_login_kakao.setOnClickListener(this);
        button_login_naver.setOnClickListener(this);
        button_login_google.setOnClickListener(this);
        button_login_facebook.setOnClickListener(this);


        OAuthManager.getsInstance().setCallBackActivity(LoginActivity.this);
        OAuthManager.getsInstance().setoAuthLoginInterface(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OAuthManager.getsInstance().requestDidApp();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(OAuthManager.getsInstance().responseOnActivityResult(requestCode,resultCode,data)){
            return;
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    // Google end

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.button_login_kakao:
                OAuthManager.getsInstance().requestSNSLogin(OAuthBaseClass.OAuthType.OAuth_KAKAO);
                break;
            case R.id.button_login_naver:
                OAuthManager.getsInstance().requestSNSLogin(OAuthBaseClass.OAuthType.OAuth_NAVER);
                break;
            case R.id.button_login_facebook:
                OAuthManager.getsInstance().requestSNSLogin(OAuthBaseClass.OAuthType.OAuth_FACEBOOK);
                break;
            case R.id.button_login_google:
                OAuthManager.getsInstance().requestSNSLogin(OAuthBaseClass.OAuthType.OAuth_GOOGLE);
                break;
            default:
                break;
        }
    }


    /**
     * 로그인 시도 결과 인터페이스
     * @param oAuthType : 로그인 시도한 연동사
     * @param result : 결과 (true : 연결 성공, false : 실패)
     * @param token : 해당 연동사 로그인 토큰
     */
    @Override
    public void responseLoginResult(OAuthBaseClass.OAuthType oAuthType, Boolean result, String token, String error) {

        String mSNSName = "";
        switch (oAuthType){
            case OAuth_KAKAO:
                mSNSName = "SNS_KAKAO";
                break;
            case OAuth_NAVER:
                mSNSName = "SNS_NAVER";
                break;
            case OAuth_FACEBOOK:
                mSNSName = "SNS_FACEBOOK";
                break;
            case OAuth_GOOGLE:
                mSNSName = "SNS_GOOGLE";
                break;
            default:
                break;
        }

        if(result){
            Log.d("OAuth","LOGIN SUCCESS \nSNS NAME ="+mSNSName+"\n==TOKEN=="+token);
            finish();
        }else{
            Log.d("OAuth","LOGIN FALE \nSNS NAME ="+mSNSName+"\nERROR = "+error);
        }
    }
}
