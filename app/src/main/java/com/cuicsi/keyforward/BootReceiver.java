package com.cuicsi.keyforward;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机自启：车机上电后自动激活无障碍服务（无需手动打开 App）。
 * 无障碍服务是否激活由系统记录，开机后系统会自动重建服务，
 * 此 Receiver 仅作为兜底，确保服务状态正常。
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 无障碍服务由系统自动重建，这里无需额外操作
    }
}
