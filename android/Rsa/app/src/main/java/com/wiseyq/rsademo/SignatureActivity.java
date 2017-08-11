package com.wiseyq.rsademo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.luffyjet.netapi.model.Result;
import com.wiseyq.rsademo.signature.Constants;
import com.wiseyq.rsademo.signature.RSAException;
import com.wiseyq.rsademo.signature.SignatureUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignatureActivity extends Activity {
    private static final String TAG = "SignatureActivity";

    EditText userEt;

    EditText nickEt;

    EditText despEt;

    Button mButton;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private static final String privateKeySign = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALeFktGVLAe1QGuVprY87PfGRv3ZXOGwWH1eAKkN+liFeqAH89bLng2zl4WTrSiyAC88YTn+5QqEamrDnSf3Tuz6fdWcRvhFUqkASCdhOucI8gl3gStxBhYZuAKsl2qi7o9AFJtt3e6Lo+FvRI1s0ZC4FmobZiHrGiRZ8gE1KzpbAgMBAAECgYEAn8I01CPSp4ceZElrTjttYiiGBlehJorYDZK2anRTmZng7MzfdP3eQjkzz0GGPOXviS87yDvfcS9iYDyXY4JDfkjJ /fi/ckVtc2uJtZI6zK9OAGh+eUsdSsBdph1nM8+W4lS7Kvr9LFfHLYWSzLEBQBDk6Yd/OYUZFfFP9pcI4SkCQQDhayEuEVLIOn9dCrAE8EeOUDFT7FvAJfIwYCyTcNrRQRoLCtZrnVV3akdLZW9USxOvsW13SXFLP4wTPcoG0OYfAkEA0Gta8ppYCjfuzABZlENpktwt6hG341qkTRndoc2EPjPCr2SsPkALZ4lpC/CW7s7YKt2Hi585D6neNSaBW/RMRQJAeb1jw/9zF9QP6O3WtjQWUROaMFrcCl/z9pBaQp6WbqCcMg5+Usw71iw9qMh1Ya7SSPanyd6OIzeErPeX3ip/vQJAB8AGOME+htq/mXxl2FqNYXWoi2yvPtgPBgLxN+QRh9Ka6bS/puzwv5/fdR80LZspdKaaNLnuAEQbzDQrWUUDVQJBAL+nzBlRp5HWeGThcju17raX9u3qI5UrnvJLBP5K+nWrOU849F331XPowV8fwkApMygC3z1NWgq0kfWkG6iXcIg=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        userEt = (EditText) findViewById(R.id.userTv);
        nickEt = (EditText) findViewById(R.id.nickTv);
        despEt = (EditText) findViewById(R.id.despTv);

        mButton = (Button) findViewById(R.id.submit);
        mButton.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String uname = userEt.getText().toString();
            final String nickname = nickEt.getText().toString();
            final String desp = nickEt.getText().toString();
            final String timestamp = format.format(new Date());

            final String content = "desp=" + desp + "&nickname=" + nickname + "&timestamp=" + timestamp + "&username=" + uname;

            try {
                final String sign = SignatureUtil.rsa256Sign(content, privateKeySign, Constants.CHARSET_UTF8);

                Log.i(TAG, "params: " + uname + "  " + nickname + "  " + desp + "  " + timestamp + "  " + sign);

                App.api.signature(uname, nickname, desp, timestamp, sign, new Callback<Result<String>>() {
                    @Override
                    public void success(Result<String> stringResult, Response response) {
                        Log.i(TAG, "success: " + stringResult.toJSON());
                        Toast.makeText(SignatureActivity.this, stringResult.toJSON(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

            } catch (RSAException e) {
                e.printStackTrace();
            }

        }
    };
}
