package xiaoming.com.permissiondemo;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zm.permissonslibrary.Permission.PermissionHelper;
import com.zm.permissonslibrary.Permission.PermissionSucceed;

public class MainActivity extends AppCompatActivity {
    private static final int CALL_PHONE_REQUEST_CODE = 0x001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void PhonePermission(View view) {
        PermissionHelper.with(MainActivity.this)
                .requestCode(CALL_PHONE_REQUEST_CODE)
                .requestPermissions(new String[]{Manifest.permission.CALL_PHONE})
                .setPermissionDes("打电话")
                .request();
    }

    @PermissionSucceed(requestCode = CALL_PHONE_REQUEST_CODE)
    private void callPhone(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "110");
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionResult(this,requestCode,grantResults);
    }


}
