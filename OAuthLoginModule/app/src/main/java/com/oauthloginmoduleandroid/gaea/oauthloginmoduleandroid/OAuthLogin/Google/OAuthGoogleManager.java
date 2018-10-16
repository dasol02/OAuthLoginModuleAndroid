package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Google;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthCovenantClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.SNSAuthType;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.R;


public class OAuthGoogleManager extends OAuthCovenantClass {

    private static final String TAG = "OAuth Google";

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mAccount;
    private static OAuthGoogleManager sInstance;

    public static OAuthGoogleManager getInstance() {
        if (sInstance == null) {
            sInstance = new OAuthGoogleManager();
        }
        return sInstance;
    }


    /**
     * Google 셋팅
     */
    public void setGoogleOAuthSetting() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(mContext.getString(R.string.server_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
        mAccount = GoogleSignIn.getLastSignedInAccount(mContext);

    }


    /**
     * Google 로그인 상태 호출
     */
    public Boolean requestLoginInfo() {
        mAccount = GoogleSignIn.getLastSignedInAccount(mContext);
        if (mAccount == null) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * Google 로그인
     */
    public void googleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mContext.startActivityForResult(signInIntent, OAUTH_GOOGLE_CLIENT_FOR_RESULT);
    }


    /**
     * 구글 연동 화면 생성 여부
     * true : kakao 호출, fasle : 미호출
     */
    public Boolean checkActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OAUTH_GOOGLE_CLIENT_FOR_RESULT) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 로그인 핸들러
     * @param task Intent
     */
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            mAccount = task.getResult(ApiException.class);
            String accessToken = mAccount.getIdToken();
            String userId = mAccount.getId();
            String result = "\nuserId : " + userId + "\naccessToken : " + accessToken;
            mOAuthCovenantInterface.responseCovenantLoginResult(SNSAuthType.SNS_GOOGLE,true,result,null);
        } catch (ApiException e) {
            Log.w(TAG, " login Fail \ncode=" + e.getStatusCode());
            mOAuthCovenantInterface.responseCovenantLoginResult(SNSAuthType.SNS_GOOGLE,false,null,String.valueOf(e.getStatusCode()));
        }
    }


    /**
     * Google 로그아웃
     */
    public void googleLogout() {
        if (requestLoginInfo()) {
            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mOAuthCovenantInterface.responseLogoutResult(SNSAuthType.SNS_GOOGLE,true);
                }
            });
        } else {
            mOAuthCovenantInterface.responseLogoutResult(SNSAuthType.SNS_GOOGLE,false);
        }

    }


    /**
     * Google 연동 해제
     */
    public void googleDelete() {
        Log.d(TAG, " Delete TRY");
        if (requestLoginInfo()) {
            mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mOAuthCovenantInterface.responseDeleteResult(SNSAuthType.SNS_GOOGLE,true,null);
                        }
                    });
        } else {
            mOAuthCovenantInterface.responseDeleteResult(SNSAuthType.SNS_GOOGLE,false,null);
        }
    }


    /**
     * Google 사용자 정보 조회
     */
    public void requestUserInfo() {
        mAccount = GoogleSignIn.getLastSignedInAccount(mContext);
        if (mAccount != null) {

            String userdata = "\n";
            userdata = userdata+"\n"+"email = "+mAccount.getEmail();
            userdata = userdata+"\n"+"name = "+mAccount.getDisplayName();
            String accessToken = mAccount.getIdToken();
            String userId = mAccount.getId();
            String result = "\nuserId : " + userId + "\n\naccessToken : " + accessToken;
            userdata = userdata+result;

            mOAuthCovenantInterface.responseUserFrofileInfoResult(SNSAuthType.SNS_GOOGLE,true,userdata,null);
            Log.d(TAG, userdata);
        }else{
            mOAuthCovenantInterface.responseUserFrofileInfoResult(SNSAuthType.SNS_GOOGLE,false,null,null);
        }
    }


    /**
     * Google 토큰 갱신
     */
    public void googleRefreshToken() {
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        handleSignInResult(task);
                    }
                });
    }


    /**
     * Google 저장되어 있는 토큰 정보 호출
     *
     * @return
     */
    public String getrequestToken() {
        mAccount = GoogleSignIn.getLastSignedInAccount(mContext);
        String accessToken = mAccount.getIdToken();
        String userId = mAccount.getId();
        String result = "\nuserId : " + userId + "\n\naccessToken : " + accessToken;
        return result;
    }



}

