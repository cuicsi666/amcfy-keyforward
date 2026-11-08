package com.cuicsi.keyforward;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

/**
 * 方向盘媒体按键 → Amcfy Music 转发服务。
 *
 * 用无障碍服务捕获 KEYCODE_MEDIA_NEXT / KEYCODE_MEDIA_PREVIOUS，
 * 再通过 MediaController 控制 Amcfy Music（com.amcfy.app）切歌。
 * 免 root，不动车机系统，只依赖「无障碍服务」+「通知使用权」两个普通权限。
 */
public class KeyForwardService extends AccessibilityService {

    private static final String TAG = "KeyForward";
    private static final String AMCFY_PACKAGE = "com.amcfy.app";

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        // 确保开启按键过滤，否则收不到方向盘媒体按键
        AccessibilityServiceInfo info = getServiceInfo();
        if (info != null) {
            info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
            setServiceInfo(info);
        }
        Log.i(TAG, "服务已连接，按键过滤已开启");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 不需要处理窗口事件
    }

    @Override
    public void onInterrupt() {
        Log.w(TAG, "无障碍服务被中断");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int keycode = event.getKeyCode();
        // 只在按键抬起时触发一次，避免长按连发
        if (event.getAction() != KeyEvent.ACTION_UP) {
            // 吞掉 DOWN 事件，避免车机自带播放器也响应
            if (keycode == KeyEvent.KEYCODE_MEDIA_NEXT
                    || keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                return true;
            }
            return super.onKeyEvent(event);
        }

        switch (keycode) {
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                Log.i(TAG, "捕获「下一首」，转发给 Amcfy Music");
                skipToNext();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                Log.i(TAG, "捕获「上一首」，转发给 Amcfy Music");
                skipToPrevious();
                return true;
            default:
                return super.onKeyEvent(event);
        }
    }

    private void skipToNext() {
        MediaController c = findAmcfyController();
        if (c != null) {
            try {
                c.getTransportControls().skipToNext();
                Log.i(TAG, "已发送 skipToNext");
                return;
            } catch (Throwable t) {
                Log.w(TAG, "skipToNext 失败: " + t.getMessage());
            }
        }
        Log.w(TAG, "未找到 Amcfy Music 媒体会话（请先打开 Amcfy Music 播放，并开启通知使用权）");
    }

    private void skipToPrevious() {
        MediaController c = findAmcfyController();
        if (c != null) {
            try {
                c.getTransportControls().skipToPrevious();
                Log.i(TAG, "已发送 skipToPrevious");
                return;
            } catch (Throwable t) {
                Log.w(TAG, "skipToPrevious 失败: " + t.getMessage());
            }
        }
        Log.w(TAG, "未找到 Amcfy Music 媒体会话");
    }

    /**
     * 在活跃媒体会话里找到 Amcfy Music。
     * 需要「通知使用权」开启，否则 getActiveSessions(通知组件) 只能看到自己的会话。
     */
    private MediaController findAmcfyController() {
        try {
            MediaSessionManager msm =
                    (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
            if (msm == null) return null;

            List<MediaController> controllers = msm.getActiveSessions(
                    new ComponentName(this, MediaNotificationListener.class));
            if (controllers == null) return null;

            for (MediaController c : controllers) {
                String pkg = c.getPackageName();
                if (AMCFY_PACKAGE.equals(pkg)) {
                    return c;
                }
            }
            // 兜底：如果没找到 Amcfy，取第一个有播放状态的（避免车机自带占位）
            for (MediaController c : controllers) {
                if (c.getPlaybackState() != null) {
                    return c;
                }
            }
        } catch (Throwable t) {
            Log.w(TAG, "查找媒体会话异常: " + t.getMessage());
        }
        return null;
    }
}
