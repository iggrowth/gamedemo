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

    //todo 1.4、App Application配置-start
    /**
     * 该方法已废弃，SDK<=4.1.1使用该方法
     * 建议使用下面新方法 getConfigParams
     */
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

    /**
     * 必接
     *
     * SDK4.1.2新增，后续版本请使用该方法
     * 在assets目录下放入 ig_game_config 游戏参数配置文件，改文件找运营同学获取
     */
    @Override
    public ConfigParams getConfigParams() {
        return new ConfigParams()
                .setDebug(true);
    }

    /**
     * 选接
     *
     * 是否需要在SplashActivity内进行权限申请：
     * 重要说明：
     *      重度游戏请严格配置为 true
     * true：在SplashActivity内进行权限申请
     * false：初始化完成后进行权限申请
     */
    @Override
    public boolean needRequestPermissionInSplash() {
        return true;
    }

    /**
     * 选接
     *
     * 是否申请权限
     * 默认：true
     * 只有特殊情况下，需要自行申请权的可以关闭 SDK内部的权限申请
     * SDK4.1.3新增，如果需要使用请更新至4.1.3版本
     *
     */
    @Override
    public boolean requestPermission() {
        return super.requestPermission();
    }

    /**
     * 选接
     *
     * 游戏启动希望要获取到的，如没有获取到，仅会影响部分功能，但不影响游戏正常运行的权限
     * 重要说明：
     *      为响应工信部合规要求，以及简化游戏启动过程，请尽量减少此类权限，将权限放置在具体使用场景中申请。
     */
    @Override
    public ArrayList<String> getNecessaryPermissionList() {
        ArrayList<String> per = new ArrayList<>();
        per.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return per;
    }
    //todo 1.4、App Application配置-end
}
