package com.wiseyq.rsademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.luffyjet.netapi.model.Result;
import com.wiseyq.rsademo.signature.codec.Base64;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RSAActivity extends AppCompatActivity {
    private static final String TAG = "RSAActivity";

    private static final String publickeyInRSA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYHKR0ABEmHFGkEnOZDKlFjbDFtvBiTqZLe7pTLMERHGz4VknWgLqylbI6ezYNIbKBYSLUeGcx8jFS5roirJXJZGzzO597QbrWoWcV2t1oq9KHzjzvvSL/QlncnNtY5eLG/Lj8UpD8yFIJ5/o8w7FX4kUnLTlfVle3xdJ+TjAMxwIDAQAB";

    EditText userEt;

    EditText passEt;

    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userEt = (EditText) findViewById(R.id.userTv);
        passEt = (EditText) findViewById(R.id.pwdTv);
        mButton = (Button) findViewById(R.id.submit);
        mButton.setOnClickListener(mOnClickListener);
    }


    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String user = userEt.getText().toString();
            final String pass = passEt.getText().toString();

            final String enc = enc(publickeyInRSA , pass);

            App.api.decode(user, enc, new Callback<Result<String>>() {
                @Override
                public void success(Result<String> stringResult, Response response) {
                    Log.i(TAG, "success: " + stringResult.toJSON());
                    Toast.makeText(RSAActivity.this, "服务端解码后的密码：\n" + stringResult.data, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        }
    };


    private String enc(String publicKeyStr, String pass) {
        try {
            /**
             * 不要使用Android自带的base64工具
             * 对Base64的公钥进行一次解码
             */
            byte[] buffer = Base64.decodeBase64(publicKeyStr.getBytes());

            byte[] out = RSAUtil.encrypt(pass.getBytes(), buffer);

            /**
             * 对加密结果进行Base64编码
             */
            String enc = new String(Base64.encodeBase64(out));

            Log.i(TAG, "enc: " + enc);

            return enc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
