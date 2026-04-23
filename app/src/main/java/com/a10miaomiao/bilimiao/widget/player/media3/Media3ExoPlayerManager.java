package com.a10miaomiao.bilimiao.widget.player.media3;

import android.content.Context;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Message;
import android.view.Surface;

import com.shuyu.gsyvideoplayer.cache.ICacheManager;
import com.shuyu.gsyvideoplayer.model.GSYModel;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.BasePlayerManager;

import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 名字虽然叫 Media3ExoPlayerManager，但内部已经换成 IjkPlayer
 * 用于测试 ijkplayer 是否能正常工作
 */
public class Media3ExoPlayerManager extends BasePlayerManager {

    private Context context;

    private IjkMediaPlayer mediaPlayer;

    private Surface surface;

    private long lastTotalRxBytes = 0;

    private long lastTimeStamp = 0;

    @Override
    public IMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    protected IjkMediaPlayer buildMediaPlayer(Context context) {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        
        // --- IJK 基础配置 ---
        // 开启硬解 (0软解，1硬解)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        
        // 缓冲优化
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        
        // B站视频通常需要这个来处理重定向或特殊的Range请求
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        
        return ijkMediaPlayer;
    }

    @Override
    public void initVideoPlayer(Context context, Message msg, List<VideoOptionModel> optionModelList, ICacheManager cacheManager) {
        this.context = context.getApplicationContext();
        
        // 1. 加载 native 库（非常重要，如果 so 没加载会崩或者没反应）
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        mediaPlayer = buildMediaPlayer(context);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        GSYModel gsyModel = (GSYModel) msg.obj;
        try {
            // 2. 设置数据源
            mediaPlayer.setLooping(gsyModel.isLooping());
            
            // ijkplayer 设置 DataSource (支持 Header)
            mediaPlayer.setDataSource(context, Uri.parse(gsyModel.getUrl()), gsyModel.getMapHeadData());

            if (gsyModel.getSpeed() != 1 && gsyModel.getSpeed() > 0) {
                mediaPlayer.setSpeed(gsyModel.getSpeed());
            }
            
            // 3. 设置监听器（GSY 框架依赖这些回调）
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            
            // 4. 异步准备
            mediaPlayer.prepareAsync();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initSuccess(gsyModel);
    }

    @Override
    public void showDisplay(final Message msg) {
        if (mediaPlayer == null) {
            return;
        }
        if (msg.obj == null) {
            mediaPlayer.setSurface(null);
        } else {
            Surface holder = (Surface) msg.obj;
            surface = holder;
            mediaPlayer.setSurface(holder);
        }
    }

    @Override
    public void setSpeed(final float speed, final boolean soundTouch) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setSpeed(speed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setNeedMute(final boolean needMute) {
        if (mediaPlayer != null) {
            if (needMute) {
                mediaPlayer.setVolume(0, 0);
            } else {
                mediaPlayer.setVolume(1, 1);
            }
        }
    }

    @Override
    public void setVolume(float left, float right) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(left, right);
        }
    }

    @Override
    public void releaseSurface() {
        if (surface != null) {
            surface = null;
        }
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.setSurface(null);
            mediaPlayer.release();
            mediaPlayer = null;
        }
        lastTotalRxBytes = 0;
        lastTimeStamp = 0;
    }

    @Override
    public int getBufferedPercentage() {
        return 0; // IjkMediaPlayer 通常通过 OnBufferingUpdateListener 更新
    }

    @Override
    public long getNetSpeed() {
        if (mediaPlayer != null) {
            return getNetSpeed(context);
        }
        return 0;
    }

    @Override
    public void setSpeedPlaying(float speed, boolean soundTouch) {
        if (mediaPlayer != null) {
            mediaPlayer.setSpeed(speed);
        }
    }

    @Override
    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public int getVideoWidth() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoHeight();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void seekTo(long time) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(time);
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getVideoSarNum() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoSarNum();
        }
        return 1;
    }

    @Override
    public int getVideoSarDen() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoSarDen();
        }
        return 1;
    }

    @Override
    public boolean isSurfaceSupportLockCanvas() {
        return false;
    }

    private long getNetSpeed(Context context) {
        if (context == null) {
            return 0;
        }
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
        long nowTimeStamp = System.currentTimeMillis();
        long calculationTime = (nowTimeStamp - lastTimeStamp);
        if (calculationTime == 0) {
            return 0;
        }
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / calculationTime);
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return speed;
    }
}