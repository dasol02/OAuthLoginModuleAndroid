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

import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthLogoutInterface;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthManager;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.OAuthUserFrofileInterface;
import com.oauthloginmoduleandroid.gaea.oauthloginmoduleandroid.OAuthLogin.SNSAuthType;

public class UserFrofileActivity extends AppCompatActivity implements View.OnClickListener, OAuthLogoutInterface, OAuthUserFrofileInterface {

    Button button_lougot,button_delete;
    TextView text_title_user_frofile,text_user_frofile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_frofile);

        button_lougot = (Button)findViewById(R.id.button_lougot);
        button_lougot.setOnClickListener(this);

        button_delete = (Button)findViewById(R.id.button_delete);
        button_delete.setOnClickListener(this);

        text_title_user_frofile = (TextView)findViewById(R.id.text_title_user_frofile);
        text_user_frofile = (TextView)findViewById(R.id.text_user_frofile);

        OAuthManager.getsInstance().setoAuthUserFrofileInterface(UserFrofileActivity.this);
        OAuthManager.getsInstance().responseUserFrofileInfo();

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.button_lougot: // 로그아웃

                showAlertView("로그아웃 하시겠습니까?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OAuthManager.getsInstance().setoAuthLogoutInterface(UserFrofileActivity.this);
                        OAuthManager.getsInstance().requestSNSLogOut();
                        dialogInterface.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                break;

            case R.id.button_delete: // 로그인

                showAlertView("이앱의 연결을 해제 하시겠습니까?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OAuthManager.getsInstance().setoAuthLogoutInterface(UserFrofileActivity.this);
                        OAuthManager.getsInstance().requestSNSDelete();
                        dialogInterface.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                break;
            default:
                break;
        }
    }


    @Override
    public void responseLogoutResult(SNSAuthType snsName, Boolean result) {

        if(result){
            Log.d("OAuth","LOGOUT SUCCESS \nSNS NAME ="+getSNSname(snsName));
            finish();
        }else{
            Log.d("OAuth","LOGOUT FALE \nSNS NAME ="+getSNSname(snsName));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"로그아웃 실패",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void responseDeleteResult(final SNSAuthType snsName, Boolean result, final String error) {

        if(result){
            Log.d("OAuth","DELETE SUCCESS \nSNS NAME ="+getSNSname(snsName));
            finish();
        }else{
            Log.d("OAuth","DELETE FALE \nSNS NAME ="+getSNSname(snsName)+"\nERROR ="+error);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"연동해제 실패 : "+error,Toast.LENGTH_SHORT).show();
                }
            });


        }
    }


    @Override
    public void responseUserFrofileInfoResult(final SNSAuthType snsName, Boolean result, final String userinfo, String error) {


        if(result){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text_title_user_frofile.setText(getSNSname(snsName));
                    text_user_frofile.setText(userinfo);
                }
            });

        }else{
            Log.d("OAuth","DELETE FALE \nSNS NAME ="+getSNSname(snsName)+"\nERROR ="+error);
            Toast.makeText(getApplicationContext(),"정보 조회 실패 : "+userinfo,Toast.LENGTH_SHORT).show();
        }
    }



    public void showAlertView(@NonNull String message, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener OnCancelListener){
        new AlertDialog.Builder(UserFrofileActivity.this)
                .setMessage(message)
                .setPositiveButton("확인", onClickListener)
                .setNegativeButton("취소", OnCancelListener).show();
    }



    public String getSNSname(SNSAuthType snsAuthType){
        String mSNSName = "";
        switch (snsAuthType){
            case SNS_KAKAO:
                mSNSName = "SNS_KAKAO";
                break;
            case SNS_NAVER:
                mSNSName = "SNS_NAVER";
                break;
            case SNS_FACEBOOK:
                mSNSName = "SNS_FACEBOOK";
                break;
            case SNS_GOOGLE:
                mSNSName = "SNS_GOOGLE";
                break;
            default:
                break;
        }

        return mSNSName;
    }

}
