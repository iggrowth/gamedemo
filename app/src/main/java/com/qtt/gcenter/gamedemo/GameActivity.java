package com.qtt.gcenter.gamedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jifen.qu.open.upload.utils.ToastUtils;
import com.kyleduo.switchbutton.SwitchButton;
import com.qtt.gcenter.sdk.GCenterSDK;
import com.qtt.gcenter.sdk.ads.options.AggregateAdOption;
import com.qtt.gcenter.sdk.ads.options.BannerAdOption;
import com.qtt.gcenter.sdk.ads.options.ExtraRewardResultOption;
import com.qtt.gcenter.sdk.ads.options.FeedAdOption;
import com.qtt.gcenter.sdk.ads.options.FloatSpecialRewardOption;
import com.qtt.gcenter.sdk.ads.options.InterActionAdOption;
import com.qtt.gcenter.sdk.ads.options.InteractivePageAdOption;
import com.qtt.gcenter.sdk.ads.options.PullLiveAdOption;
import com.qtt.gcenter.sdk.ads.options.RewardVideoOption;
import com.qtt.gcenter.sdk.ads.options.SmallAmountWithDrawOption;
import com.qtt.gcenter.sdk.ads.options.SmallVideoAdOption;
import com.qtt.gcenter.sdk.common.GCCode;
import com.qtt.gcenter.sdk.entities.GCReportInfo;
import com.qtt.gcenter.sdk.entities.GCUserInfo;
import com.qtt.gcenter.sdk.impl.AdRewardVideoCallBackAdapter;
import com.qtt.gcenter.sdk.interfaces.IAdBannerCallBack;
import com.qtt.gcenter.sdk.interfaces.IAdDefaultCallBack;
import com.qtt.gcenter.sdk.interfaces.IAdInterActionCallBack;
import com.qtt.gcenter.sdk.interfaces.IAuthCallback;
import com.qtt.gcenter.sdk.interfaces.IExtraRewardCallBack;
import com.qtt.gcenter.sdk.interfaces.IGCCallBack;
import com.qtt.gcenter.sdk.interfaces.IGCViewStateListener;
import com.qtt.gcenter.sdk.interfaces.IWithDrawStateListener;

import java.util.HashMap;

public class GameActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "GAME_DEMO";

    private Activity activity;

    private TextView tvStatus;

    private ViewGroup adContainer;
    private ViewGroup smallVideoAdContainer;

    private EditText etVideo;
    private EditText etInteraction;
    private EditText etFeed;
    private EditText etBanner;
    private EditText etPl;
    private EditText etAggregate;

    private SwitchButton switchSmallAmount;
    private SwitchButton switchFloatSpecialReward;

    private String openId = "";//

    private boolean hasLogin = false; // IG SDK是否已登录
    private GCUserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View
                    .SYSTEM_UI_FLAG_IMMERSIVE_STICKY;//修复布局首次点击无效
            decorView.setSystemUiVisibility(uiOptions);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        activity = GameActivity.this;
        setContentView(R.layout.activity_game);
        //todo 1.5、初始化-start
        //初始化
        GCenterSDK.getInstance().init(GameActivity.this, sdkCallBack);
        //todo 1.5、初始化-end

        //todo 1.8、设置游戏内部热更版本号-start
        //这里参数1： 游戏内部框架版本号,选传，没有可传”“，参数2：游戏内部资源版本号,必传（游戏自己定义的版本号），没有热更可以不用调用该api
        GCenterSDK.getInstance().setGameHotFixedVersion("", "");
        //todo 1.8、设置游戏内部热更版本号-end

        Button btLogin = findViewById(R.id.bt_login);
        Button btFeedBack = findViewById(R.id.bt_feedback);
        Button btLogout = findViewById(R.id.bt_logout);
        Button btAdVideo = findViewById(R.id.bt_video_ad);
        Button btAdBanner = findViewById(R.id.bt_banner_ad);
        Button btUpLoadInfo = findViewById(R.id.bt_uploadInfo);
        tvStatus = findViewById(R.id.tv_monitor);
        etVideo = findViewById(R.id.et_video);
        etInteraction = findViewById(R.id.et_interaction);

        etFeed = findViewById(R.id.et_feed_ad);
        etBanner = findViewById(R.id.et_banner_ad);
        etPl = findViewById(R.id.et_pl_ad);
        etAggregate = findViewById(R.id.et_aggregate_ad);

        adContainer = findViewById(R.id.view_ad_container);
        smallVideoAdContainer = findViewById(R.id.view_small_video_ad_container);

        switchSmallAmount = findViewById(R.id.switch_smallAmountWithdraw);
        switchFloatSpecialReward = findViewById(R.id.switch_float_reward);
        switchSmallAmount.setCheckedImmediately(true);

        findViewById(R.id.bt_interaction_ad).setOnClickListener(this);
        findViewById(R.id.bt_feed_ad).setOnClickListener(this);
        findViewById(R.id.bt_banner_ad).setOnClickListener(this);
        findViewById(R.id.bt_pl_ad).setOnClickListener(this);
        findViewById(R.id.bt_aggregate_ad).setOnClickListener(this);
        findViewById(R.id.bt_interactive_page_ad).setOnClickListener(this);
        findViewById(R.id.bt_small_video_ad).setOnClickListener(this);
        findViewById(R.id.bt_small_video_ad_stop).setOnClickListener(this);
        findViewById(R.id.bt_small_video_ad_pause).setOnClickListener(this);
        findViewById(R.id.bt_small_video_ad_resume).setOnClickListener(this);
        findViewById(R.id.bt_small_video_ad_recycle).setOnClickListener(this);
        findViewById(R.id.bt_smallAmountWithdraw).setOnClickListener(this);

        btLogin.setOnClickListener(this);
        btFeedBack.setOnClickListener(this);
        btLogout.setOnClickListener(this);
        btAdVideo.setOnClickListener(this);
        btAdBanner.setOnClickListener(this);
        btUpLoadInfo.setOnClickListener(this);

        //todo 3.1、IG游戏中心金币-提现-start
        //IG金币提现
        findViewById(R.id.bt_withdraw_ig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转一个提现页面，内部提现
                GCenterSDK.getInstance().openWithDrawPage();
            }
        });
        //todo 3.1、IG游戏中心金币-提现-end

        //todo 3.2、游戏自己货币-提现-start
        //游戏自己金币提现：客户端调用authWithdraw发起授权校验，成功后通知游戏服务器向IG服务器发起提现订单（具体见服务端提现文档）
        findViewById(R.id.bt_withdraw_cp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasLogin) {
                    GCenterSDK.getInstance().login();
                    return;
                }
                GCenterSDK.getInstance().authWithdraw(activity,
                        GCCode.AUTH_WX,
                        1, // 提现金额（单位：分）
                        new IAuthCallback() {
                            @Override
                            public void success(int i, String s, HashMap<String, String> hashMap) {
                                // TODO 微信授权成功，通知游戏服务器向IG服务器发起提现订单

                            }

                            @Override
                            public void failure(int i, String s) {

                            }
                        }
                );
            }
        });
        //todo 3.2、游戏自己货币-提现-end


        //todo 4.1、开屏广告-start
        GCenterSDK.getInstance().showSplashAd(this, new IGCViewStateListener() {
            @Override
            public void show(View view, String msg) {
                toast("开屏广告展示");
            }

            @Override
            public void hide(View view, String msg) {
                toast("开屏广告关闭");
            }
        });
        //todo 4.1、开屏广告-end
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //todo 2.1、登录-start
            case R.id.bt_login:
                // 登录（默认游客登录，如果需要微信或手机号登录，找游戏中心运营更改配置即可）
                GCenterSDK.getInstance().login();
                break;
            case R.id.bt_logout:
                // 登出
                GCenterSDK.getInstance().logout();
                break;
            //todo 2.1、登录-end

            //todo 2.2、客服-start
            case R.id.bt_feedback:
                // 客服
                GCenterSDK.getInstance().openFeedBackPage();
                break;
            //todo 2.2、客服-end

            //todo 4、广告-start
            case R.id.bt_video_ad:
                // 激励视频广告 >> 获取index
                String videoIndex = etVideo.getEditableText().toString();
                showRewardAd(videoIndex);
                break;
            case R.id.bt_interaction_ad:
                //插屏广告
                String interactionIndex = etInteraction.getEditableText().toString();
                showInteractionAd(interactionIndex);
                break;
            case R.id.bt_feed_ad:
                String index = etFeed.getText().toString();
                if (TextUtils.isEmpty(index)) {
                    index = "feed1";
                }
                showFeedAd(index);
                break;
            case R.id.bt_banner_ad:
                String bannerIndex = etBanner.getText().toString();
                showBannerAd(bannerIndex);
                break;
            case R.id.bt_pl_ad:
                String plIndex = etPl.getText().toString();
                if (TextUtils.isEmpty(plIndex)) {
                    plIndex = "pl1";
                }
                showPlAd(plIndex);
                break;
            case R.id.bt_aggregate_ad:
                String agIndex = etAggregate.getText().toString();
                if (TextUtils.isEmpty(agIndex)) {
                    agIndex = "aggregate1";
                }
                showAggregateAd(agIndex);
                break;
            //todo 4.6、小视频广告-start
            case R.id.bt_small_video_ad:
                showSmallVideoAd();
                break;
            case R.id.bt_small_video_ad_stop:
                GCenterSDK.getInstance().getSmallVideoPlayController().stop();
                break;
            case R.id.bt_small_video_ad_pause:
                GCenterSDK.getInstance().getSmallVideoPlayController().pause();
                break;
            case R.id.bt_small_video_ad_resume:
                GCenterSDK.getInstance().getSmallVideoPlayController().resume();
                break;
            case R.id.bt_small_video_ad_recycle:
                GCenterSDK.getInstance().getSmallVideoPlayController().recycle();
                smallVideoAdContainer.removeAllViews();
                break;
            //todo 4.6、小视频广告-end

            //todo 4.9、互动广告-start
            case R.id.bt_interactive_page_ad:
                adContainer.removeAllViews();
                InteractivePageAdOption interActionAdOption = new InteractivePageAdOption();
                //cp定义好需要场景下的index给运营同学，运营同学根据index配置对应的广告(互动广告支持多种页面，sdk通过index来识别对应的广告)
                interActionAdOption.index = "intpage1";
                GCenterSDK.getInstance().showInteractivePageAd(this, interActionAdOption);
                break;
            //todo 4.9、互动广告-end
            //todo 4、广告-end

            case R.id.bt_uploadInfo:
                // 数据上报
                reportRoleInfo();
                break;

            case R.id.bt_smallAmountWithdraw:
                //小额提现任务
                startSmallAmountTask();
                break;
            case R.id.bt_float_reward:
                //激励视频超级翻倍
                startFloatSpecialReward();
                break;
            //todo 8.2、翻卡活动-start
            case R.id.bt_cards:
                //翻卡活动-点击打开页面
                GCenterSDK.getInstance().openPage(activity, "ig://com.ig.game?class=dialog_cards");
                break;
            //todo 8.2、翻卡活动-end

            //todo 8.3、提现券提现-start
            case R.id.bt_coupon:
                //提现券提现-点击打开页面
                //todo 这里coupon_key请向游戏中心运营获取
                GCenterSDK.getInstance().openPage(this, "ig://com.ig.game?class=activity_coupon&coupon_key=COUPON_KEY");
                //todo 服务端接入见接入文档
                break;
            //todo 8.3、提现券提现-end

        }
    }

    //todo 4.2、激励视频广告-start
    private void showRewardAd(String index) {
        RewardVideoOption option = new RewardVideoOption();
        option.index = index;   // 请cp同学在不同的视频广告位置定义一个index
        option.orientation = 1;//1-竖屏，2-横屏，默认1
        GCenterSDK.getInstance().showRewardVideoAd(GameActivity.this, option, new AdRewardVideoCallBackAdapter() {
            @Override
            public void onAdLoadStart() {
                super.onAdLoadStart();
                toast("广告开始加载");
            }

            @Override
            public void onAdLoadSuccess() {
                super.onAdLoadSuccess();
                toast("广告加载成功");
            }

            @Override
            public void onAdLoadSuccess(double rate) {
                super.onAdLoadSuccess(rate);

            }

            @Override
            public void onAdLoadFailure(String msg) {
                super.onAdLoadFailure(msg);
                toast("广告加载失败：" + msg);
            }

            @Override
            public void onAdShow() {
                super.onAdShow();
                toast("广告开始显示");
            }

            @Override
            public void onAdClose(boolean rewardSuccess) {
                super.onAdClose(rewardSuccess);
                toast("广告关闭， rewardSuccess = " + rewardSuccess);
                if (rewardSuccess) {
                    // TODO: 激励成功处理后续逻辑
                }
            }

            @Override
            public void onAdError(String msg) {
                super.onAdError(msg);
                toast("广告播放出错， msg = " + msg);
            }

            @Override
            public void onAdComplete() {
                super.onAdComplete();
                toast("广告播放完成");
            }

            @Override
            public void onReward() {
                super.onReward();
                toast("广告激励成功");
            }
        });
    }
    //todo 4.2、激励视频广告-end

    //todo 4.3、插屏广告-start
    private void showInteractionAd(String index) {
        InterActionAdOption option = new InterActionAdOption();
        option.index = index;
        GCenterSDK.getInstance().showInteractionAd(this, option, new IAdInterActionCallBack() {
            @Override
            public void onAdLoadStart() {
                toast("插屏广告开始加载");
            }

            @Override
            public void onAdLoadSuccess() {
                toast("插屏广告加载成功");
            }

            @Override
            public void onAdLoadFailure(String s) {

            }

            @Override
            public void onAdClick() {

            }

            @Override
            public void onADExposed() {

            }

            @Override
            public void onAdFailed(String s) {

            }
        });
    }
    //todo 4.3、插屏广告-end

    //todo 4.4、banner广告-start
    private void showBannerAd(String index) {
        adContainer.removeAllViews();
        BannerAdOption bannerAdOption = new BannerAdOption();
        bannerAdOption.index = index;
        GCenterSDK.getInstance().showBannerAd(adContainer, bannerAdOption, new IAdBannerCallBack() {
            @Override
            public void onAdLoadStart() {
                Log.d(TAG, "banner onAdLoadStart");
            }

            @Override
            public void onAdLoadSuccess() {
                Log.d(TAG, "banner onAdLoadSuccess");
            }

            @Override
            public void onAdLoadFailure(String s) {
                Log.d(TAG, "banner onAdLoadFailure, msg = " + s);
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "banner onAdClick");
            }

            @Override
            public void onADExposed() {
                Log.d(TAG, "banner onADExposed");
            }

            @Override
            public void onAdFailed(String s) {
                Log.d(TAG, "banner onAdFailed, msg = " + s);
            }

        });
    }
    //todo 4.4、banner广告-end

    //todo 4.5、信息流广告-start
    private void showFeedAd(String index) {
        adContainer.removeAllViews();
        FeedAdOption feedAdOption2 = new FeedAdOption();
        feedAdOption2.index = index;
        GCenterSDK.getInstance().showFeedAd(adContainer, feedAdOption2, new IAdDefaultCallBack() {
            @Override
            public void onAdLoadStart() {
                Log.d(TAG, "feed onAdLoadStart");
            }

            @Override
            public void onAdLoadSuccess() {
                Log.d(TAG, "feed onAdLoadSuccess");
            }

            @Override
            public void onAdLoadFailure(String s) {
                Log.d(TAG, "feed onAdLoadFailure, msg = " + s);
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "feed onAdClick");
            }

            @Override
            public void onADExposed() {
                Log.d(TAG, "feed onADExposed");
            }

            @Override
            public void onAdFailed(String s) {
                Log.d(TAG, "feed onAdFailed, msg = " + s);
            }

            @Override
            public void onAdEvent(int i) {
                Log.d(TAG, "feed onAdEvent = " + i);
            }
        });
    }
    //todo 4.5、信息流广告-end

    //todo 4.6、小视频广告-start
    private void showSmallVideoAd() {
        smallVideoAdContainer.removeAllViews();
        SmallVideoAdOption smallVideoAdOption = new SmallVideoAdOption();
        smallVideoAdOption.index = "small_video";//自行定义，用于查看广告数据及转化
        GCenterSDK.getInstance().getSmallVideoPlayController();
        GCenterSDK.getInstance().showSmallVideoAd(smallVideoAdContainer, smallVideoAdOption, new IAdDefaultCallBack() {
            @Override
            public void onAdLoadStart() {
                Log.d(TAG, "small onAdLoadStart");
            }

            @Override
            public void onAdLoadSuccess() {
                Log.d(TAG, "small onAdLoadSuccess");
            }

            @Override
            public void onAdLoadFailure(String s) {
                Log.d(TAG, "small onAdLoadFailure, msg = " + s);
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "small onAdClick");
            }

            @Override
            public void onADExposed() {
                Log.d(TAG, "small onADExposed");
            }

            @Override
            public void onAdFailed(String s) {
                Log.d(TAG, "small onAdFailed, msg = " + s);
            }

            @Override
            public void onAdEvent(int i) {
                Log.d(TAG, "small onAdEvent = " + i);
            }
        });
    }
    //todo 4.6、小视频广告-end

    //todo 4.7、拉活/次留广告-start
    private void showPlAd(String index) {
        adContainer.removeAllViews();
        PullLiveAdOption pullLiveAdOption = new PullLiveAdOption();
        pullLiveAdOption.index = index;
        GCenterSDK.getInstance().showPullLiveAd(adContainer, pullLiveAdOption, new IAdDefaultCallBack() {
            @Override
            public void onAdLoadStart() {
                Log.d(TAG, "pl onAdLoadStart");
            }

            @Override
            public void onAdLoadSuccess() {
                Log.d(TAG, "pl onAdLoadSuccess");
            }

            @Override
            public void onAdLoadFailure(String s) {
                Log.d(TAG, "pl onAdLoadFailure, msg = " + s);
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "pl onAdClick");
            }

            @Override
            public void onADExposed() {
                Log.d(TAG, "pl onADExposed");
            }

            @Override
            public void onAdFailed(String s) {
                Log.d(TAG, "pl onAdFailed, msg = " + s);
            }

            @Override
            public void onAdEvent(int i) {
                Log.d(TAG, "pl onAdEvent = " + i);
            }

        });
    }
    //todo 4.7、拉活/次留广告-end

    //todo 4.8、聚合广告-start
    private void showAggregateAd(String index) {
        adContainer.removeAllViews();
        AggregateAdOption aggregateAdOption = new AggregateAdOption();
        aggregateAdOption.index = index;
        aggregateAdOption.adCount = 5;
        GCenterSDK.getInstance().showAggregateAd(adContainer, aggregateAdOption, new IAdDefaultCallBack() {
            @Override
            public void onAdLoadStart() {
                Log.d(TAG, "ag onAdLoadStart");
            }

            @Override
            public void onAdLoadSuccess() {
                Log.d(TAG, "ag onAdLoadSuccess");
            }

            @Override
            public void onAdLoadFailure(String s) {
                Log.d(TAG, "ag onAdLoadFailure, msg = " + s);
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "ag onAdClick");
            }

            @Override
            public void onADExposed() {
                Log.d(TAG, "ag onADExposed");
            }

            @Override
            public void onAdFailed(String s) {
                Log.d(TAG, "ag onAdFailed, msg = " + s);
            }

            @Override
            public void onAdEvent(int i) {
                Log.d(TAG, "ag onAdEvent = " + i);
            }

        });
    }
    //todo 4.8、聚合广告-end

    //todo 5、数据上报-start
    private void reportRoleInfo() {
        GCReportInfo info = new GCReportInfo.Builder()
                .setInfoType(GCReportInfo.TYPE_LOGIN)       //【必传】上传信息类型：【TYPE_LOGIN 登陆】【TYPE_INFO 信息上报】【TYPE_LOGON 创角】
                .setOpenId(openId)                          //【必传】登录完成后ticket换取openID后必传,未获取到openId时请传""
                .setRoleID("11")                            //【必传】游戏内的角色id,有角色信息的游戏必传,无则传 ""
                .setRoleName("轻舞飞扬")                     //【必传】游戏内的角色名称,有角色信息的游戏必传,无则传 ""
                .setRoleLevel("31")                         //【必传】角色等级,无等级请传"0"
                .setVipLevel("V15")                         //【必传】角色VIP等级,无VIP等级请传"0"
                .setSectID("1002")                          //【必传】公会/帮派ID,无工会/帮派请传"0"
                .setSectName("趣玩之家")                     //【必传】公会/帮派名称,无工会/帮派请传""
                .setServerID("s201")                        //【必传】服务器ID,无服务器ID请传"0"
                .setServerName("世界大门")                   //【必传】服务器名称,无服务器ID请传"0"
                .setRoleLevelUpTime("1520777467000")        //【必传】最近一次升级时间,无等级请传递创角时间
                .setRoleCreateTime("1520259067000")         //【必传】角色创建时间,无创角游戏请传用户首次游玩时间
                .setExtension("ext")                        //【选传】拓展信息
                .build();
        GCenterSDK.getInstance().reportInfo(info);
        toast(info.toString());
    }
    //todo 5、数据上报-end

    //todo 6、小额提现任务-start
    //具体任务内容见接入文档
    private void startSmallAmountTask() {
        SmallAmountWithDrawOption option = new SmallAmountWithDrawOption();
        option.index = "sqq_test";

        //深度激励，选接--start
        //以下为深度激励参数，选接，具体看文档
        option.extraReward = 1;//0-不接，1-接入(如果设置1接入后以下三个参入必传，否则报错)
        option.extraRewardAmount = "0.4";//额外奖励发放金额，请传入数字类型的字符串请勿使用其他字符串
        option.extraRewardUnit = "元";//额外奖励展示单位，由产品商定后自己定义：元，金币，红包等
        //发放奖励的回调，收到该回调后，cp发放奖励，发放成功后再告知SDK，具体见以下接入代码
        option.extraRewardCallBack = new IExtraRewardCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "小额提现 额外任务完成，游戏客户端发放奖励并告知sdk");
                toast("额外任务完成，游戏客户端发放奖励并告知sdk");
                //todo 游戏发放奖励，发放完成调用以下接口
                //模拟游戏发奖励，发放完成调用
                ExtraRewardResultOption rewardResultOption = new ExtraRewardResultOption();
                if (switchSmallAmount.isChecked()) {
                    rewardResultOption.specialRewardAmount = "0.4";
                    rewardResultOption.rewardResult = ExtraRewardResultOption.RESULT_SUCCESS;
                } else {
                    rewardResultOption.rewardResult = ExtraRewardResultOption.RESULT_FAILED;
                    rewardResultOption.errorMsg = "奖励发放失败";
                }
                GCenterSDK.getInstance().requestSmallAmountExtraReward(rewardResultOption);
            }
        };
        //深度激励，选接--end

        GCenterSDK.getInstance().showSmallAmountWithdrawAd(this, option, new IWithDrawStateListener() {
            @Override
            public void onFail() {
                Log.d(TAG, "小额提现 onfail");
                toast("提现失败");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "小额提现 onComplete");
                toast("提现任务完成");
            }
        });
    }
    //todo 6、小额提现任务-end

    //todo 7.1、激励视频超级翻倍-start
    //具体任务内容见接入文档
    private void startFloatSpecialReward() {
        FloatSpecialRewardOption floatSpecialRewardOption = new FloatSpecialRewardOption();
        floatSpecialRewardOption.index = "floatSpecialReward";
        GCenterSDK.getInstance().showFloatSpecialReward(this, floatSpecialRewardOption, new IExtraRewardCallBack() {
            @Override
            public void onSuccess() {
                toast("任务完成，游戏客户端发放奖励并告知sdk, 发放-" + (switchFloatSpecialReward.isChecked() ? "成功" : "失败"));
                //todo 游戏发放奖励，发放完成调用以下接口
                //模拟游戏发奖励，发放完成调用
                ExtraRewardResultOption rewardResultOption = new ExtraRewardResultOption();
                if (switchFloatSpecialReward.isChecked()) {
                    rewardResultOption.specialRewardAmount = "500";//具体值向戏中心产品获取
                    rewardResultOption.rewardResult = ExtraRewardResultOption.RESULT_SUCCESS;
                } else {
                    rewardResultOption.rewardResult = ExtraRewardResultOption.RESULT_SUCCESS;
                    rewardResultOption.errorMsg = "奖励发放失败";
                }
                GCenterSDK.getInstance().requestFloatSpecialReward(rewardResultOption);
            }
        });
    }
    //todo 7.1、激励视频超级翻倍-end

    private IGCCallBack sdkCallBack = new IGCCallBack() {
        @Override
        public void initCallBack(int code, String msg) {
            //todo 1.5、初始化-start
            if (code == GCCode.CODE_INIT_SUCCESS) {
                tvStatus.append("初始化成功\n");
            } else if (code == GCCode.CODE_INIT_FAILURE) {
                toast("初始化失败");
            } else {
                toast("初始化:未知错误");
            }
            //todo 1.5、初始化-end
        }

        @Override
        public void loginCallBack(int code, String msg, GCUserInfo entity) {
            //todo 2.1、登录-start
            if (code == GCCode.CODE_LOGIN_SUCCESS) {
                userInfo = entity;
                hasLogin = true;
                String ticket = entity.ticket;
                String appId = entity.appId;
                String platform = entity.platform;
                tvStatus.append("登录成功 :\n" + entity.toString() + " " + msg + "\n");
                // TODO: 登录成功，游戏方请在此处登录走自己的登录，前往游戏服务器端进行验证，换取openId
                // openId = "xxx";

                //微信登录返回的信息（SDK版本：4.1.1新增，请更新至该版本获取）
                if (entity.ext != null) {
                    String loginType = entity.ext.get("loginType");//登录方式
                    String wechatNickname = entity.ext.get("wechatNickname");//微信昵称
                    String wechatAvatar = entity.ext.get("wechatAvatar");//微信头像
                }
            } else if (code == GCCode.CODE_NO_INIT) {
                toast("未初始化");
            } else if (code == GCCode.CODE_LOGIN_FAILURE) {
                toast("登录失败：" + msg);
            } else {
                toast(code + ": " + msg);
            }
            //todo 2.1、登录-end
        }

        @Override
        public void logoutCallBack(int code, String msg) {
            //todo 2、设置游戏内部热更版本号-start
            if (code == GCCode.CODE_NO_INIT) {
                toast("未初始化");
            } else if (code == GCCode.CODE_LOGOUT_SUCCESS) {
                hasLogin = false;
                toast("登出成功");
                tvStatus.setText("");
                //  清除当前角色信息 >>> 回到登陆场景 >>> 重新调用登录
                // TODO: 请在此处处理游戏的登出逻辑
            } else if (code == GCCode.CODE_LOGOUT_FAILURE) {
                toast("登出失败");
            } else {
                toast("未知错误");
            }
            //todo 2、设置游戏内部热更版本号-end
        }

        @Override
        public void exitCallBack(int code, String msg) {
            if (code == GCCode.CODE_EXIT_SUCCESS) {
                //结束游戏
                GCenterSDK.getInstance().closeApp();
            }
        }

        /**
         * 金币活动-接入游戏中心的金币体系才能使用，选接
         */
        @Override
        public void businessCallBack(int code, Object msg) {
            switch (code) {
                //todo 8.1、七天签到-start
                //7天签到
                case GCCode.CODE_SEVEN_SIGN_SUCCESS://签到领金币
                    Log.d(TAG, "CODE_SEVEN_SIGN_SUCCESS" + msg);
                    getCoin(userInfo.appId, userInfo.platform, userInfo.ticket, openId);
                    break;
                case GCCode.CODE_SEVEN_SIGN_VIDEO_SUCCESS://签到看激励视频翻倍领金币
                    Log.d(TAG, "CODE_SEVEN_SIGN_VIDEO_SUCCESS" + msg);
                    getCoin(userInfo.appId, userInfo.platform, userInfo.ticket, openId);
                    break;
                //todo 8.1、七天签到-end

                //todo 8.2、翻卡活动-start
                case GCCode.CODE_CARDS_COIN_ADD://翻卡领取金币
                    int coinAdd = (int) msg;
                    toast("翻卡领取金币" + coinAdd);
                    getCoin(userInfo.appId, userInfo.platform, userInfo.ticket, openId);
                    break;
                case GCCode.CODE_CARDS_TODAY_TASK_DONE://今日翻卡任务全部完成
                    toast("今日翻卡已完成 " + msg);
                    break;
                //todo 8.2、翻卡活动-end
            }
        }

    };

    public void getCoin(final String app_id, final String platform, final String ticket, final String open_id) {
        //todo 游戏客户端调用游戏服务端，然后游戏服务端再调用游戏中心服务端发放金币
    }

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GameActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //todo 1.6、退出游戏-设置挽留弹窗-start
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GCenterSDK.getInstance().exitGame();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //todo 1.6、退出游戏-设置挽留弹窗-end

    //todo 1.7、生命周期设置-start

    /********************以下是生命周期方法的调用 start********************/
    @Override
    public void onBackPressed() {
        GCenterSDK.getInstance().onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        GCenterSDK.getInstance().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GCenterSDK.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GCenterSDK.getInstance().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GCenterSDK.getInstance().onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GCenterSDK.getInstance().onRestart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        GCenterSDK.getInstance().onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GCenterSDK.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        GCenterSDK.getInstance().onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        GCenterSDK.getInstance().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GCenterSDK.getInstance().onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        GCenterSDK.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /********************以上是生命周期方法的调用  end ********************/
    //todo 1.7、生命周期设置-end
}
