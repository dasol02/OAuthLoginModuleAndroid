package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button_login_view_go,button_user_frofile;
    OAuthManager oAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로그인
        button_login_view_go = (Button)findViewById(R.id.button_login_view_go);
        button_login_view_go.setOnClickListener(this);

        // 사용자 정보
        button_user_frofile= (Button)findViewById(R.id.button_user_frofile);
        button_user_frofile.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiUpdate(); //로그인 여부에 따라 버튼 토글 노출
    }

    /**
     * 로그인 여부에 따라 버튼 토글 노출
     */
    public void uiUpdate(){
        if(oAuthManager == null){
            oAuthManager = OAuthManager.getsInstance();
        }
        Boolean loginStateSuccess = oAuthManager.getLoginState();
        button_user_frofile.setVisibility(loginStateSuccess ? View.VISIBLE : View.GONE); // 유저 정보 버튼
        button_login_view_go.setVisibility(loginStateSuccess ? View.GONE : View.VISIBLE); // 로그인 버튼
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login_view_go :
                Intent intent_login = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent_login);
                break;
            case R.id.button_user_frofile :
                Intent intent_userfrofile = new Intent(getApplicationContext(),UserFrofileActivity.class);
                startActivity(intent_userfrofile);
                break;
            default:
                break;
        }
    }
}
