package com.cuicsi.keyforward;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 启动入口：引导用户开启「无障碍服务」和「通知使用权」两个权限。
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView hint = findViewById(R.id.hint);
        hint.setText("安装后只需开启下面两个权限，方向盘切歌即可生效：\n\n"
                + "1. 无障碍服务（捕获方向盘按键）\n"
                + "2. 通知使用权（控制 Amcfy Music）\n\n"
                + "开启后，打开 Amcfy Music 播放，按方向盘上一首/下一首即可切歌。\n"
                + "本应用无界面、无广告，纯后台运行。");

        Button btnAccessibility = findViewById(R.id.btn_accessibility);
        btnAccessibility.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));

        Button btnNotification = findViewById(R.id.btn_notification);
        btnNotification.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
    }
}
