package im.zego.publishstreamtest.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tencent.bugly.crashreport.CrashReport;
import com.zego.zegoliveroom.ZegoLiveRoom;
import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import im.zego.publishstreamtest.R;
import im.zego.publishstreamtest.cls.MainWindow;
import im.zego.publishstreamtest.cls.ZegoSDKSingleton;


public class MainActivity extends AppCompatActivity {

    private ZegoLiveRoom zg;
    private MainWindow mainWindow;
    private SharedPreferences sharedPreferences;

    public boolean isPermissionGranted() {
        return permissionGranted;
    }


    private boolean permissionGranted;

    private final int REQUEST_PERMISSION_CODE = 101;
    private static String[] permissions = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE", Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    //创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
    List<String> mPermissionList = new ArrayList<>();


    final static private String BUGLY_APP_KEY = "70580e12bb";
    final static private String KEY_USER_ID = "Hansons";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CrashReport.initCrashReport(this, BUGLY_APP_KEY, false);
        CrashReport.setUserId(KEY_USER_ID);

        sharedPreferences = this.getSharedPreferences("AppidInfo",MODE_PRIVATE);

        mainWindow = new MainWindow(this,sharedPreferences);

        if (Build.VERSION.SDK_INT >= 23) {//6.0才用动态权限
            permissionGranted = checkOrRequestPermission();
        }
    }

    //返回直接销毁SDK对象
    @Override
    public void onBackPressed() {

        zg = ZegoSDKSingleton.getInstance();

        zg.stopPreview();
        zg.logoutRoom();
        zg.unInitSDK();
        super.onBackPressed();

    }

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE", Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    private boolean checkOrRequestPermission(int code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS_STORAGE, code);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (REQUEST_PERMISSION_CODE == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                showPermissionDialog();//跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
            }else{
                permissionGranted = true;

            }
        }


    }

    private boolean checkOrRequestPermission() {

        //清空没有通过的权限
        mPermissionList.clear();

        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        }else {

        }
        return true;


    }
    /**
     * 不再提示权限时的展示对话框
     */
    AlertDialog mPermissionDialog;
    String mPackName = "im.zego.publishstreamtest";

    private void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限，请手动授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            cancelPermissionDialog();

                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    //关闭对话框
    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }

}
