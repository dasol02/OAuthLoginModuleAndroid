package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao.SessionCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SessionCallback callback;
    Button button_lougot,button_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_lougot = (Button)findViewById(R.id.button_lougot);
        button_lougot.setOnClickListener(this);

        button_login = (Button)findViewById(R.id.button_login);
        button_login.setOnClickListener(this);

        getHashKey();

        requestAccessTokenInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.button_login:
                kakaoLogin();

                break;
            case R.id.button_lougot:
                kakaoLogOut();
//                kakaoDelete();
                break;
            default:
                break;
        }
    }


    /**
     * kakao Login
     */
    public void kakaoLogin(){
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        // 우선권 부여 : AuthType.KAKAO_TALK
        // kakao talk 미 설치시 KAKAO_ACCOUNT로 연경
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL,MainActivity.this);
    }


    /**
     * kakao LogOut
     */
    public void kakaoLogOut(){

        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                // 로그아웃을 성공한 경우 불립니다. 서버에 로그아웃 도달과 무관하게 항상 성공
                Log.d("OAuth KAKAO","requestLogout onCompleteLogout");
            }
        });
    }


    /*
    ** kakao 연동 해제
     */
    public void kakaoDelete(){
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Log.d("OAuth KAKAO",errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        Log.d("OAuth KAKAO",errorResult.toString());
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        Log.d("OAuth KAKAO","onNotSignedUp");
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        Log.d("OAuth KAKAO","onSuccess");
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }


    /*
     ** kakao 토근 정보 얻기
     */
    private void requestAccessTokenInfo() {
        String accToken = Session.getCurrentSession().getTokenInfo().getAccessToken().toString();
        String refreshToken = Session.getCurrentSession().getTokenInfo().getRefreshToken().toString();
        Log.d("OAuth KAKAO", "onAccessTokenReceived =\naccToken = "+accToken+"\nrefreshToken = "+refreshToken);
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
                Log.d("OAuth KAKAO","key_hash="+Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
