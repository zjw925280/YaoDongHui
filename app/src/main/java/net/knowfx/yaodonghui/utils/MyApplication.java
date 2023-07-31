package net.knowfx.yaodonghui.utils;

import android.net.http.HttpResponseCache;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.tencent.mmkv.MMKV;
import com.tencent.tauth.Tencent;

import net.knowfx.yaodonghui.BuildConfig;
import net.knowfx.yaodonghui.R;
import net.knowfx.yaodonghui.base.BaseActivity;
import net.knowfx.yaodonghui.ext.MMKVExtKt;
import net.knowfx.yaodonghui.ext.SystemExtKt;
import net.knowfx.yaodonghui.utils.imageSelector.SelectImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.jiguang.api.utils.JCollectionAuth;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.ups.JPushUPSManager;
import cn.jpush.android.ups.TokenResult;
import cn.jpush.android.ups.UPSRegisterCallBack;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MyApplication extends MultiDexApplication {
    private static MyApplication instance = null;
    public static final int COMMON_PAGE_SIZE = 10;

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    private static final ArrayList<BaseActivity> activities = new ArrayList<>();

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.white);//全局设置主题颜色
            return new MaterialHeader(context).setShowBezierWave(true);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
    }

    @Override
    public void onCreate() {
        setVideoPlayer();
        MMKV.initialize(this);
        SelectImageUtils.INSTANCE.init();
        initHttpCache();
        Tencent.setIsPermissionGranted(true);
        initJPush();
        super.onCreate();
    }

    /**
     * 初始化httpCache
     */
    private void initHttpCache() {
        try {
            File cacheDir = new File(getCacheDir(), "http");
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initJPush() {
        Boolean isPrivacy = MMKVExtKt.readData("isPrivacy", false);
        Log.e("ralph", "privacy is agree ===== " + isPrivacy);
        JPushInterface.init(this);
        if (!SystemExtKt.getToken().isEmpty()){
            JCollectionAuth.setAuth(this, isPrivacy);
        }
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
    }

    public static void addActivity(BaseActivity act) {
        synchronized (activities) {
            activities.add(act);
        }
    }

    public static void deleteActivity(BaseActivity act) {
        synchronized (activities) {
            activities.remove(act);
        }
    }

    public static BaseActivity getLastActivity() {
        synchronized (activities) {
            int count = activities.size();
            return activities.get(count - 1);
        }
    }

    /**
     * 设置视频播放参数
     */
    private void setVideoPlayer() {
        ArrayList<VideoOptionModel> list = new ArrayList<>();
        //此中内容：优化加载速度，降低延迟
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp"));
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp"));
        list.add(new VideoOptionModel(
                IjkMediaPlayer.OPT_CATEGORY_FORMAT,
                "allowed_media_types",
                "video"
        )); //根据媒体类型来配置
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1316));
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1));// 无限读
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100));
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240));
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1));
        //  关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0));
        GSYVideoManager.instance().setOptionModelList(list);
    }

}
