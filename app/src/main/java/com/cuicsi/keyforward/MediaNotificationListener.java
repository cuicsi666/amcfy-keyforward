package com.cuicsi.keyforward;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * 通知监听服务。
 * 用途：本 App 要控制第三方音乐 App（Amcfy Music）的切歌，
 * 需要「通知使用权」权限。开启后，KeyForwardService 才能通过
 * MediaSessionManager.getActiveSessions(本组件) 看到 Amcfy Music 的媒体会话。
 * 本类无需处理具体通知内容，只作为「已授权」的标记组件。
 */
public class MediaNotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // 无需处理
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // 无需处理
    }
}
