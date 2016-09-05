package com.imcczy.tencent2016c_keygen;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.provider.Settings.Secure;

import net.glxn.qrgen.android.QRCode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ_PHONE_STATE = 1;
    String android_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView)findViewById(R.id.deviceid);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            android_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        }
        android_id = keygen.b(android_id);
        android_id = String.format("%s-%s-%s-%s-%s", android_id.substring(0, 8), android_id.substring(8, 12), android_id.substring(12, 16),
                android_id.substring(16, 20), android_id.substring(20, 32));
        String lisence = keygen.decrypt_id(android_id);
        tv.setText(lisence);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PHONE_STATE)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                android_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
    }

}
