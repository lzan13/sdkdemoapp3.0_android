package com.hyphenate.chatuidemo.pa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.ui.BaseActivity;
import com.hyphenate.chatuidemo.ui.ChatActivity;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.widget.EaseTitleBar;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2017/6/13.
 * 公众号详情界面
 */
public class PADetailsActivity extends BaseActivity {

    private PADetailsActivity activity;
    private EaseTitleBar titleBar;

    private String currentUser;
    private String paid;
    private PAInfo paInfo;
    // 当前账户关注的公众号列表
    private List<PAInfo> followList = new ArrayList<>();
    // 是否已关注
    private boolean isFollowed;

    private ImageView avatarView;
    private TextView nameView;
    private TextView paidView;
    private TextView descriptionView;
    private Button followBtn;
    private Button unFollowBtn;
    private Button startChatBtn;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_pa_details);

        initView();
    }

    private void initView() {
        activity = this;

        currentUser = EMClient.getInstance().getCurrentUser();

        paid = getIntent().getStringExtra("paid");
        paInfo = DemoHelper.getInstance().getFollowPAMap().get(paid);

        avatarView = (ImageView) findViewById(R.id.img_avatar);
        nameView = (TextView) findViewById(R.id.text_name);
        paidView = (TextView) findViewById(R.id.text_id);
        descriptionView = (TextView) findViewById(R.id.text_description);
        followBtn = (Button) findViewById(R.id.btn_follow);
        unFollowBtn = (Button) findViewById(R.id.btn_un_follow);
        startChatBtn = (Button) findViewById(R.id.btn_start_chat);

        followBtn.setOnClickListener(viewListener);
        unFollowBtn.setOnClickListener(viewListener);
        startChatBtn.setOnClickListener(viewListener);

        loadFollowPAList();
        if (paInfo == null) {
            updatePADetailsFromServer();
            paInfo = new PAInfo();
        }
        initTitleBarClick();
        refresh();
    }

    /**
     * 初始化 titlebar 相关图标和点击事件
     */
    private void initTitleBarClick() {
        titleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle(paInfo.getName());
        titleBar.setLeftImageResource(R.drawable.em_mm_title_back);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 界面控件点击监听
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_follow:
                    followPA();
                    break;
                case R.id.btn_un_follow:
                    unFollowPA();
                    break;
                case R.id.btn_start_chat:
                    Intent intent = new Intent();
                    intent.setClass(activity, ChatActivity.class);
                    intent.putExtra(EaseConstant.EXTRA_USER_ID, paInfo.getAgentUser());
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_PA);
                    intent.putExtra("paid", paid);
                    startActivity(intent);
                    break;
            }
        }
    };

    /**
     * 关注公众号
     */
    private void followPA() {
        new Thread(new Runnable() {
            @Override public void run() {
                boolean result = PAManager.getInstance().addPAFollowersToServer(paid, currentUser);
                if (result) {
                    DemoHelper.getInstance().savePA(paInfo);
                    updatePADetailsFromServer();
                }
            }
        }).start();
    }

    /**
     * 取消关注公众号
     */
    private void unFollowPA() {
        new Thread(new Runnable() {
            @Override public void run() {
                boolean result = PAManager.getInstance().removePAFollowersToServer(paid, currentUser);
                if (result) {
                    DemoHelper.getInstance().deletePA(paInfo);
                    updatePADetailsFromServer();
                }
            }
        }).start();
    }

    /**
     * 从服务器更新公众号信息到本地
     */
    private void updatePADetailsFromServer() {
        new Thread(new Runnable() {
            @Override public void run() {
                paInfo = PAManager.getInstance().getPADetailsFromServer(paid);
                handler.sendMessage(handler.obtainMessage());
            }
        }).start();
    }

    /**
     * 更新公众号信息后刷新 ui
     */
    private void refresh() {
        titleBar.setTitle(paInfo.getName());
        nameView.setText(paInfo.getName());
        paidView.setText(paInfo.getPaid());
        descriptionView.setText(paInfo.getDescription());
        if (!TextUtils.isEmpty(paInfo.getLogo())) {
            Glide.with(activity).load(paInfo.getLogo()).error(R.drawable.ease_groups_icon).into(avatarView);
        }
        isFollowed = false;
        for (int i = 0; i < followList.size(); i++) {
            PAInfo info = followList.get(i);
            if (info.getPaid().equals(paid)) {
                isFollowed = true;
            }
        }
        if (isFollowed) {
            followBtn.setVisibility(View.GONE);
            startChatBtn.setVisibility(View.VISIBLE);
            unFollowBtn.setVisibility(View.VISIBLE);
        } else {
            followBtn.setVisibility(View.VISIBLE);
            startChatBtn.setVisibility(View.GONE);
            unFollowBtn.setVisibility(View.GONE);
        }
    }

    private void loadFollowPAList() {
        if (followList == null) {
            followList = new ArrayList<>();
        }
        followList.clear();
        followList.addAll(DemoHelper.getInstance().getFollowPAMap().values());
    }

    Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            loadFollowPAList();
            refresh();
        }
    };
}
