package com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthBaseClass;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;


public class UserFrofileActivity extends AppCompatActivity implements View.OnClickListener{

    Button button_lougot,button_delete;
    TextView text_title_user_frofile,text_user_frofile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_frofile);

        // 사용자 정보 조회
        requestUserFrofile();
    }

    // XML 이벤트 바인딩
    public void initViewBinding(){
        button_lougot = (Button)findViewById(R.id.button_lougot);
        button_lougot.setOnClickListener(this);

        button_delete = (Button)findViewById(R.id.button_delete);
        button_delete.setOnClickListener(this);

        text_title_user_frofile = (TextView)findViewById(R.id.text_title_user_frofile);
        text_user_frofile = (TextView)findViewById(R.id.text_user_frofile);
    }

    // Alert View 호출
    public void createAlertView(final int requestType, String msg){

        showAlertView("로그아웃 하시겠습니까?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (requestType == 0){
                    requestLogout();
                }else if (requestType == 1){
                    requestRemove();
                }

                dialogInterface.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

    }

    // Alert View 생성
    public void showAlertView(@NonNull String message, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener OnCancelListener){
        new AlertDialog.Builder(UserFrofileActivity.this)
                .setMessage(message)
                .setPositiveButton("확인", onClickListener)
                .setNegativeButton("취소", OnCancelListener).show();
    }

    // Log 생성 (GetOAuthName)
    public String getSNSname(OAuthBaseClass.OAuthType oAuthType){
        String mSNSName = "";
        switch (oAuthType){
            case OAuth_KAKAO:
                mSNSName = "SNS_KAKAO";
                break;
            case OAuth_NAVER:
                mSNSName = "SNS_NAVER";
                break;
            case OAuth_FACEBOOK:
                mSNSName = "SNS_FACEBOOK";
                break;
            case OAuth_GOOGLE:
                mSNSName = "SNS_GOOGLE";
                break;
            default:
                break;
        }

        return mSNSName;
    }


    // 버튼 이벤트 생성 (로그아웃, 로그인)
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.button_lougot:
                createAlertView(0,"로그아웃 하시겠습니까?");
                break;
            case R.id.button_delete:
                createAlertView(1,"이앱의 연결을 해제 하시겠습니까?");
                break;
            default:
                break;
        }
    }


    /**
     * OAuth 로그아웃
     */
    private void requestLogout() {

        // 로그아웃 결과 리스너
        OAuthManager.getsInstance().setoAuthLogoutInterface(new OAuthManager.OAuthLogoutInterface() {
            @Override
            public void responseLogoutResult(OAuthBaseClass.OAuthType oAuthType, Boolean result) {
                if(result){
                    finish();
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"로그아웃 실패",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // 로그아웃
        OAuthManager.getsInstance().requestSNSDelete();
    }



    /**
     * OAuth 연동해제
     */
    private void requestRemove() {

        // 연동해제 결과 리스너
        OAuthManager.getsInstance().setoAuthRemoveInterface(new OAuthManager.OAuthRemoveInterface() {
            @Override
            public void responseRemoveResult(OAuthBaseClass.OAuthType oAuthType, Boolean result, final String error) {
                if(result){
                    finish();
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"연동해제 실패 : "+error,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // 연동해제
        OAuthManager.getsInstance().requestSNSLogOut();
    }


    /**
     * OAuth 사용자 정보 조회
     */
    private void requestUserFrofile() {

        // 사용자 정보 조회 리스너
        OAuthManager.getsInstance().setoAuthUserFrofileInterface(new OAuthManager.OAuthUserFrofileInterface() {
            @Override
            public void responseUserFrofileInfoResult(final OAuthBaseClass.OAuthType oAuthType, Boolean result, final String userinfo, String error) {

                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_title_user_frofile.setText(getSNSname(oAuthType));
                            text_user_frofile.setText(userinfo);
                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(),"정보 조회 실패 : "+userinfo,Toast.LENGTH_SHORT).show();
                }

            }
        });

        // 사용자 정보 조회
        OAuthManager.getsInstance().responseUserFrofileInfo();
    }
}
