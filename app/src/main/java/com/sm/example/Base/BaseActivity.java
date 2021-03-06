package com.sm.example.Base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.sm.example.R;
import com.sm.example.utils.ActivityUtil;
import com.sm.example.utils.ToastUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.sm.example.Contant.Contants.WIFINAME;

public abstract class BaseActivity extends AppCompatActivity {
    public static Dialog dialog;
    public static String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.GET_TASKS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION

    };//申请的权限
    public static List<String> mPermissionList = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;
    public Context Basecontext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Basecontext=this;
        initQx();
//        getWindows();//防止应用锁屏
        setContentView(getViewLayout());//设置布局
//        setStatBar();//根据版本设置沉浸式状态栏
        findViews();//初始化组件
        try {
            init();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getWindows() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,//防止锁屏,只有当前为Activity时生效
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected abstract void initQx();

    protected abstract void init() throws UnsupportedEncodingException;//初始化的方法

    protected abstract void findViews();//初始化组件

    protected abstract int getViewLayout();//布局

    @SuppressLint("NewApi")
    public void setStatBar() {//根据版本设置沉浸式状态栏
        View decorView = getWindow().getDecorView();
        int option =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT
        );
    }
    public void LoadingTrue(String text) {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.item_dibushow, null);
        TextView tv=inflate.findViewById(R.id.tv);
        tv.setText(""+text);
        //将布局设置给Dialog
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);//点击屏幕外不消失
        dialog.show();//显示对话框
    }

    public void LoadingFalse() {
        if (dialog!=null){
            dialog.dismiss();
            dialog.cancel();
        }

    }

    private long mPressedTime = 0;

    @Override
    public void onBackPressed() {//重写返回键方法
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        Log.d("nzq", "onBackPressed: "+mNowTime);
        if ((mNowTime - mPressedTime) > 2000) {//比较两次按键时间差
            ToastUtils.showToast("再按一次退出程序");
            mPressedTime = mNowTime;
        } else {//退出程序
            this.finish();
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        // Activity销毁时，提示系统回收
        // System.gc();
        // 移除Activity
        ActivityUtil.getInstance().removeActivity(this);
        if (dialog!=null){
            dialog.cancel();
        }
        super.onDestroy();
    }
    /**
     * 申请权限
     */
    public void getPermissions() {//获取手机权限
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
//            Toast.makeText(LoginActivity.this,"已经授权",Toast.LENGTH_LONG).show();
        } else {//请求权限方法
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (dialog!=null){

        }
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.fontScale = 1;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
