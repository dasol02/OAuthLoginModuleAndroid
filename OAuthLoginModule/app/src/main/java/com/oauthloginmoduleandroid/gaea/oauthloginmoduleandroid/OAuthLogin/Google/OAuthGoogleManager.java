package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Google;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthBaseClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.R;


public class OAuthGoogleManager extends OAuthBaseClass {

    private static final String TAG = "OAuth Google";

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mAccount;


    @Override
    public void requestStartAppOAuth() {

    }

    @Override
    public void requestDidAppOAuth() {

    }

    @Override
    public void initOAuthSDK() {
        // Google 셋팅
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(mContext.getString(R.string.server_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
        mAccount = GoogleSignIn.getLastSignedInAccount(mContext);
    }


    // Google 로그인 상태 호출
    @Override
    public Boolean requestIsLogin() {
        mAccount = GoogleSignIn.getLastSignedInAccount(mContext);
        if (mAccount == null) {
            return false;
        } else {
            return true;
        }
    }

    // Google 로그인
    @Override
    public void requestOAuthLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mContext.startActivityForResult(signInIntent, OAUTH_GOOGLE_CLIENT_FOR_RESULT);
    }

    // Google 로그아웃
    @Override
    public void requestOAuthLogout() {
        if (requestIsLogin()) {
            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mResponseOAuthCovenantInterface.responseOAuthLogoutResult(OAuthType.OAuth_GOOGLE,true);
                }
            });
        } else {
            mResponseOAuthCovenantInterface.responseOAuthLogoutResult(OAuthType.OAuth_GOOGLE,false);
        }

    }

    /**
     * Google 연동 해제
     */
    @Override
    public void requestOAuthremove() {
        Log.d(TAG, " Delete TRY");
        if (requestIsLogin()) {
            mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mResponseOAuthCovenantInterface.responseOAuthRemoveResult(OAuthType.OAuth_GOOGLE,true,null);
                        }
                    });
        } else {
            mResponseOAuthCovenantInterface.responseOAuthRemoveResult(OAuthType.OAuth_GOOGLE,false,null);
        }
    }

    /**
     * 구글 연동 화면 생성 여부
     * true : 호출, fasle : 미호출
     */
    @Override
    public Boolean requestActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OAUTH_GOOGLE_CLIENT_FOR_RESULT) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            return false; // 구글 핸들러 사용으로 인한 반환값 False;
        } else {
            return true;
        }
    }


    /**
     * Google 사용자 정보 조회
     */
    @Override
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

            mResponseOAuthCovenantInterface.responseOAuthUserFrofileInfoResult(OAuthType.OAuth_GOOGLE,true,userdata,null);
            Log.d(TAG, userdata);
        }else{
            mResponseOAuthCovenantInterface.responseOAuthUserFrofileInfoResult(OAuthType.OAuth_GOOGLE,false,null,null);
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
            mResponseOAuthCovenantInterface.responseOAuthCovenantLoginResult(OAuthType.OAuth_GOOGLE,true,result,null);
        } catch (ApiException e) {
            Log.w(TAG, " login Fail \ncode=" + e.getStatusCode());
            mResponseOAuthCovenantInterface.responseOAuthCovenantLoginResult(OAuthType.OAuth_GOOGLE,false,null,String.valueOf(e.getStatusCode()));
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

