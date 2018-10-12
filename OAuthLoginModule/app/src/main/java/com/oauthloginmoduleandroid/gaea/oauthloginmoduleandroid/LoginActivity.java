package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthLoginInterface;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.SNSAuthType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OAuthLoginInterface {

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

        getHashKey();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OAuthManager.getsInstance().removeSession();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(OAuthManager.getsInstance().responseOnActivityResult(requestCode,resultCode,data)){
            return;
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.button_login_kakao:
                OAuthManager.getsInstance().requestSNSLogin(SNSAuthType.SNS_KAKAO);
                break;
            case R.id.button_login_naver:
                OAuthManager.getsInstance().requestSNSLogin(SNSAuthType.SNS_NAVER);
                break;
            case R.id.button_login_facebook:
                break;
            case R.id.button_login_google:
                break;
            default:
                break;
        }
    }


    /**
     * 해쉬 키값 생성
     */
    private void getHashKey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("OAuth Login Activity","key_hash="+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }



    /**
     * 로그인 시도 결과 인터페이스
     * @param snsName : 로그인 시도한 연동사
     * @param result : 결과 (true : 연결 성공, false : 실패)
     * @param token : 해당 연동사 로그인 토큰
     */
    @Override
    public void responseLoginResult(SNSAuthType snsName, Boolean result, String token, String error) {

        String mSNSName = "";
        switch (snsName){
            case SNS_KAKAO:
                mSNSName = "SNS_KAKAO";
                break;
            case SNS_NAVER:
                mSNSName = "SNS_NAVER";
                break;
            case SNS_FACEBOOK:
                mSNSName = "SNS_FACEBOOK";
                break;
            case SNS_GOOGLE:
                mSNSName = "SNS_GOOGLE";
                break;
            default:
                break;
        }

        if(result){
            Log.d("OAuth","LOGIN SUCCESS \nSNS NAME ="+mSNSName+"\nTOKEN = "+token);
            finish();
        }else{
            Log.d("OAuth","LOGIN FALE \nSNS NAME ="+mSNSName+"\nERROR = "+error);
        }
    }

}
