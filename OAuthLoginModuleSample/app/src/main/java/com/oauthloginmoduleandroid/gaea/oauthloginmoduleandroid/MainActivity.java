package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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


        //해시키 생성
        getHashKey();
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
        oAuthManager.requestOAuthIsLogin(this, new OAuthManager.OAuthIsLoginInterface() {
            @Override
            public void responseIsLoginResult(Boolean result, String error) {
                button_user_frofile.setVisibility(result ? View.VISIBLE : View.GONE); // 유저 정보 버튼
                button_login_view_go.setVisibility(result ? View.GONE : View.VISIBLE); // 로그인 버튼
            }
        });

    }

    /**
     * 화면 이동 이벤트
     * @param view
     */
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

    /**
     * 해시 키값 생성
     */
    private void getHashKey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("OAuth Main Activity","key_hash : "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
