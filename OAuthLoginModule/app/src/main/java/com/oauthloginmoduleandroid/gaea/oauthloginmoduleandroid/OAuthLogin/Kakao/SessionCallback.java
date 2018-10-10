package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Kakao;

import android.util.Log;

import com.kakao.auth.AccessTokenCallback;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.util.ArrayList;
import java.util.List;

/**
 *  카카오 콜백 리스너
 */
public class SessionCallback implements ISessionCallback {

    @Override
    public void onSessionOpened() {
        Log.e("OAuth KAKAO", "Login onSessionOpened");
        // 로그인 성공
        requestUserInfo();
    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        Log.e("OAuth KAKAO", "Login onSessionOpenFailed");
        // 로그인 실패
    }


    /**
     * 사용자 정보 호출
     */
    public void requestUserInfo() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys,new MeV2ResponseCallback() {

            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("OAuth KAKAO", "onFailure error message=" + errorResult);
                super.onFailure(errorResult);
            }


            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //세션이 닫혀 실패한 경우로 에러 결과를 받습니다. 재로그인 / 토큰발급이 필요합니다.
                Log.e("OAuth KAKAO", "onSessionClosed error message=" + errorResult);

            }

            @Override
            public void onSuccess(MeV2Response result) {
                Log.d("OAuth KAKAO", result.toString());
                Log.d("OAuth KAKAO id = ", result.getId() + "");
                Log.d("OAuth KAKAO email = ", result.getKakaoAccount().getEmail());
            }

        });

    }

}
