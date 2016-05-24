package com.chenls.orderserve;

import android.app.Application;
import android.content.Context;

import com.bmob.BmobConfiguration;
import com.bmob.BmobPro;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

public class MyApplication extends Application {

    public static final String APP_ID = "f008a5fd6a125fa1fdc75a526e582c7e";

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化BmobSDK
        Bmob.initialize(getApplicationContext(), APP_ID);
        initConfig(getApplicationContext());
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this);
    }

    /**
     * 初始化文件配置
     *
     * @param context context
     */
    public static void initConfig(Context context) {
        BmobConfiguration config = new BmobConfiguration.Builder(context).customExternalCacheDir("dish").build();
        BmobPro.getInstance(context).initConfig(config);
    }
}
