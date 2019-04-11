package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthBaseClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;
import static com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthBaseClass.OAuthType.*;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button button_login_kakao,button_login_naver,button_login_google,button_login_facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViewBinding();
    }

    // XML 이벤트 바인딩
    public void initViewBinding(){
        button_login_kakao = (Button)findViewById(R.id.button_login_kakao);
        button_login_naver = (Button)findViewById(R.id.button_login_naver);
        button_login_google = (Button)findViewById(R.id.button_login_google);
        button_login_facebook = (Button)findViewById(R.id.button_login_facebook);

        button_login_kakao.setOnClickListener(this);
        button_login_naver.setOnClickListener(this);
        button_login_google.setOnClickListener(this);
        button_login_facebook.setOnClickListener(this);
    }

    // Action OAuth Login Button
    @Override
    public void onClick(View view) {
        OAuthBaseClass.OAuthType oAuthType = null;
        switch (view.getId()){
            case R.id.button_login_kakao:
                oAuthType = OAuth_KAKAO;
                break;
            case R.id.button_login_naver:
                oAuthType = OAuth_NAVER;
                break;
            case R.id.button_login_facebook:
                oAuthType = OAuth_FACEBOOK;
                break;
            case R.id.button_login_google:
                oAuthType = OAuth_GOOGLE;
                break;
            default:
                break;
        }
        requestOAuthLogin(oAuthType);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OAuthManager.getsInstance().responseOnActivityResult(requestCode,resultCode,data);
    }

    /**
     * 로그인 시도
     * @param oAuthType Login OAuth 연동사 종류
     */
    public void requestOAuthLogin(OAuthBaseClass.OAuthType oAuthType){
        OAuthManager.getsInstance().requestSNSLogin(oAuthType, this, new OAuthManager.OAuthLoginInterface() {
            @Override
            public void responseLoginResult(Boolean result, String token, String error) {
                if(result){
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_LONG);
                }
            }
        });
    }
}
