package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthKakao.SessionCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen(); // 시즌 상태 체크 이후 로그인 시도
        requestMe();
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

    /**
     * get kakao user info
     */
    public void requestMe() {
        //유저의 정보를 받아오는 함수

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("TAG0101", "error message=" + errorResult);
//                super.onFailure(errorResult);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("TAG0101", "onSessionClosed1 =" + errorResult);
            }

            @Override
            public void onNotSignedUp() {
                //카카오톡 회원이 아닐시
                Log.d("TAG0101", "onNotSignedUp ");
            }

            @Override
            public void onSuccess(UserProfile result) {
                Log.d("TAG0101", result.toString());
                Log.d("TAG0101", result.getId() + "");
            }
        });
    }

    protected void redirectSignupActivity() {
//        final Intent intent = new Intent(this, SampleSignupActivity.class);
//        startActivity(intent);
//        finish();
        Log.d("TAG0101","redirectSignupActivity");
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
                Log.d("TAG0101","key_hash="+Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}
