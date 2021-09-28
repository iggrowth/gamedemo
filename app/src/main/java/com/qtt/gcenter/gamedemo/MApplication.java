package com.qtt.gcenter.gamedemo;

import android.Manifest;

import com.qtt.gcenter.sdk.GCParams;
import com.qtt.gcenter.sdk.activity.GameApplication;

import java.util.ArrayList;

/**
 * Create by xukai03
 * Date:2020/9/9
 * Description:
 */
public class MApplication extends GameApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public GCParams getParams() {
        //         设置参数
        return new GCParams.Builder()
                .setAppId("a4jtVxvVdZDb")
                .setAccountId("iggame.igkjqlzsgame")
                .setClientId("igkjqlzsgame")
                .setNativeId("1073")
                .setVersionId("502")
                .setAppNameEn("igkjqlzsgame")
                .setDebug(true)
                .build();

    }

    @Override
    public boolean needRequestPermissionInSplash() {
        return true;
    }

    @Override
    public ArrayList<String> getNecessaryPermissionList() {
        ArrayList<String> per = new ArrayList<>();
        per.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return per;
    }
}
