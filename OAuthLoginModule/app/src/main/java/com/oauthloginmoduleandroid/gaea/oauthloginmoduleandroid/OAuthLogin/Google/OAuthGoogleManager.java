package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.Google;

import android.app.Activity;
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
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthUserInfo;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.R;


public class OAuthGoogleManager extends OAuthBaseClass {

    protected static int OAUTH_GOOGLE_CLIENT_FOR_RESULT = 9901; // Google Activity ForResult

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mAccount;
    private OAuthManager.OAuthLoginInterface mOAuthLoginInterface;

    @Override
    public void requestStartAppOAuth() {

    }

    @Override
    public void requestDidAppOAuth() {

    }

    @Override
    public void initOAuthSDK(Activity callBackActivity) {
        // Google 셋팅
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(callBackActivity.getString(R.string.server_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(callBackActivity, gso);
        mAccount = GoogleSignIn.getLastSignedInAccount(callBackActivity);
    }


    // Google 로그인 상태 호출
    @Override
    public void requestIsLogin(Activity callBackActivity, OAuthManager.OAuthIsLoginInterface oAuthIsLoginInterface) {
        mAccount = GoogleSignIn.getLastSignedInAccount(callBackActivity);
        if (mAccount == null) {
            oAuthIsLoginInterface.responseIsLoginResult(false,"Google Is not login");
        } else {
            oAuthIsLoginInterface.responseIsLoginResult(true,null);
        }
    }

    // Google 로그인
    @Override
    public void requestOAuthLogin(Activity callBackActivity, OAuthManager.OAuthLoginInterface oAuthLoginInterface) {
        this.mOAuthLoginInterface = oAuthLoginInterface;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        callBackActivity.startActivityForResult(signInIntent, OAUTH_GOOGLE_CLIENT_FOR_RESULT);
    }

    // Google 로그아웃
    @Override
    public void requestOAuthLogout(Activity callBackActivity, final OAuthManager.OAuthLogoutInterface oAuthLogoutInterface) {
        requestIsLogin(callBackActivity, new OAuthManager.OAuthIsLoginInterface() {
            @Override
            public void responseIsLoginResult(Boolean result, String error) {
                if(result) {
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            oAuthLogoutInterface.responseLogoutResult(true);
                        }
                    });
                }else {
                    oAuthLogoutInterface.responseLogoutResult(false);
                }
            }
        });
    }

    /**
     * Google 연동 해제
     * @param callBackActivity
     * @param oAuthRemoveInterface
     */
    @Override
    public void requestOAuthRemove(Activity callBackActivity, final OAuthManager.OAuthRemoveInterface oAuthRemoveInterface) {
        requestIsLogin(callBackActivity, new OAuthManager.OAuthIsLoginInterface() {
            @Override
            public void responseIsLoginResult(Boolean result, String error) {
                if (result) {
                    mGoogleSignInClient.revokeAccess()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    oAuthRemoveInterface.responseRemoveResult(true,null);
                                }
                            });
                }else{
                    oAuthRemoveInterface.responseRemoveResult(false,null);
                }
            }
        });
    }

    /**
     * 구글 연동 화면 생성 여부
     * true : 호출, fasle : 미호출
     */
    @Override
    public void requestActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OAUTH_GOOGLE_CLIENT_FOR_RESULT) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            // 구글 핸들러 사용으로 인한 반환값 False;
        }
    }


    /**
     * Google 사용자 정보 조회
     * @param callBackActivity
     * @param oAuthUserFrofileInterface
     */
    @Override
    public void requestUserInfo(Activity callBackActivity, OAuthManager.OAuthUserFrofileInterface oAuthUserFrofileInterface) {
        mAccount = GoogleSignIn.getLastSignedInAccount(callBackActivity);
        if (mAccount != null) {

            OAuthUserInfo oAuthUserInfo = new OAuthUserInfo(
                    mAccount.getFamilyName()+mAccount.getGivenName(),
                    mAccount.getId(),
                    null,
                    mAccount.getEmail(),
                    mAccount.getDisplayName(),
                    null,
                    null,
                    mAccount.getPhotoUrl().toString(),
                    mAccount.getIdToken(),
                    null,
                    null
            );

            oAuthUserFrofileInterface.responseUserFrofileInfoResult(true, oAuthUserInfo,null);
        }else{
            oAuthUserFrofileInterface.responseUserFrofileInfoResult(false,null,null);
        }
    }


    /**
     * 로그인 핸들러
     * @param task Intent
     */
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            mAccount = task.getResult(ApiException.class);
            mOAuthLoginInterface.responseLoginResult(true, mAccount.getIdToken(),null);
        } catch (ApiException e) {
            mOAuthLoginInterface.responseLoginResult(false,"",String.valueOf(e.getStatusCode()));
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
                        // TODO : RefreshToken Setting
                    }
                });
    }
}

