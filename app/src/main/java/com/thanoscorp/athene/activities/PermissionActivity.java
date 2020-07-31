package com.thanoscorp.athene.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.thanoscorp.athene.R;
import com.thanoscorp.athene.models.ActivityX;

public class PermissionActivity extends ActivityX {

    private final static int MAGIC_CONSTANT = 694; // Nice
    String[] PERMISSIONS = {
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Button grant;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_activity);
        grant = findViewById(R.id.grant_permissions);
        grant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });

    }

    private void requestPermissions() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, MAGIC_CONSTANT);
        }else{
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MAGIC_CONSTANT: {
                for (int i = grantResults.length; i > 0; i--) {
                    if (grantResults[i - 1] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        Toast.makeText(getApplicationContext(), "Please grant the permissions to proceed", Toast.LENGTH_SHORT).show();
                        //requestPermissions();

                    }
                }

                return;
            }
        }
    }
}
