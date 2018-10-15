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

import java.util.concurrent.Executor;


public class OAuthGoogleManager extends OAuthCovenantClass {

    private static final String TAG = "OAuth Google";

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
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


    }


    /**
     * Google 로그인 상태 호출
     */
    public Boolean requestLoginInfo() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);
        if(account == null){
            return false;
        }else{
            return true;
        }
    }


    /**
     * Google 로그인
     */
    public void googleLogin() {
        googleSetting();
        signIn();
    }

    //** Google
    private void googleSetting(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
        account = GoogleSignIn.getLastSignedInAccount(mContext);
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mContext.startActivityForResult(signInIntent,OAUTH_GOOGLE_CLIENT_FOR_RESULT);
    }

    public void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.w("GOOGLE", "successfully");
        } catch (ApiException e) {
            Log.w("GOOGLE", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /**
     * Google 로그아웃
     */
    public void googleLogout() {
        Log.d(TAG, " Logout TRY");

        if(requestLoginInfo()){
            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "Logout SUCCESS");
                }
            });
        }else{
            Log.d(TAG, "Logout FAIL IS NOT LOGIN");
        }

    }


    /**
     * Google 연동 해제
     */
    public void googleDelete() {
        Log.d(TAG, " Delete TRY");
        if(requestLoginInfo()) {
            mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "Delete SUCCESS");
                        }
                    });
        }else{
            Log.d(TAG, "Delete FAIL IS NOT LOGIN");
        }
    }


    /**
     * Google 사용자 정보 조회
     */
    public void requestUserInfo() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(mContext);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
        }
    }


    /**
     * Google 토큰 갱신
     */
    public void googleRefreshToken() {

    }


    /**
     * Google 저장되어 있는 토큰 정보 호출
     *
     * @return
     */
    public String getrequestToken() {

        return "";
    }


    /**
     * 구글 연동 화면 생성 여부
     * true : kakao 호출, fasle : 미호출
     */
    public Boolean checkActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == OAUTH_GOOGLE_CLIENT_FOR_RESULT){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            return true;
        }else{
            return false;
        }
    }


}

