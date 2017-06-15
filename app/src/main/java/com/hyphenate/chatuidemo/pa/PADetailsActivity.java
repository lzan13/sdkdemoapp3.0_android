package com.hyphenate.chatuidemo.pa;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.ui.BaseActivity;

/**
 * Created by lzan13 on 2017/6/13.
 * 公众号详情界面
 */
public class PADetailsActivity extends BaseActivity {

    private PADetailsActivity activity;
    private String paid;
    private PAInfo paInfo;

    private ImageView avatarView;
    private TextView nameView;
    private TextView paidView;
    private TextView descriptionView;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_pa_details);

        initView();
    }

    private void initView() {
        activity = this;

        paid = getIntent().getStringExtra("paid");

        paInfo = new PAInfo();

        avatarView = (ImageView) findViewById(R.id.img_avatar);
        nameView = (TextView) findViewById(R.id.text_name);
        paidView = (TextView) findViewById(R.id.text_id);
        descriptionView = (TextView) findViewById(R.id.text_description);
        
        getPADetailsFromServer();

        refresh();
    }

    /**
     * 从服务器更新公众号信息到本地
     */
    private void getPADetailsFromServer() {
        new Thread(new Runnable() {
            @Override public void run() {
                paInfo = PAManager.getInstance().getPAInfoFullFromServer(paid);
                handler.sendMessage(handler.obtainMessage());
            }
        }).start();
    }

    /**
     * 更新公众号信息后刷新 ui
     */
    private void refresh() {
        nameView.setText(paInfo.getName());
        paidView.setText(paInfo.getPaid());
        descriptionView.setText(paInfo.getDescription());
        if (!TextUtils.isEmpty(paInfo.getLogo())) {
            Glide.with(activity).load(paInfo.getLogo()).into(avatarView);
        }
    }

    Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refresh();
        }
    };
}
