package com.hyphenate.chatuidemo.pa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.ui.BaseActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2017/6/13.
 * 展示当前账户关注的公众号列表界面
 */
public class PAFollowListActivity extends BaseActivity {

    private PAFollowListActivity activity;

    private EaseTitleBar titleBar;

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private PAListAdapter adapter;

    // 当前用户关注的公众号列表
    private List<PAInfo> followList;
    private String currentUser;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_pa_follow_list);

        init();
    }

    private void init() {
        activity = this;

        currentUser = EMClient.getInstance().getCurrentUser();

        followList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PAListAdapter(activity, followList);
        recyclerView.setAdapter(adapter);

        updateDataFromServer();
        setItemClickListener();
        initTitleBarClick();
    }

    /**
     * 初始化 titlebar 相关图标和点击事件
     */
    private void initTitleBarClick() {
        titleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle("关注的公众号");
        titleBar.setLeftImageResource(R.drawable.em_mm_title_back);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });
        titleBar.setRightImageResource(R.drawable.em_add);
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(activity, PAAllListActivity.class));
            }
        });
    }

    /**
     * 设置列表项点击监听
     */
    private void setItemClickListener() {
        adapter.setItemClickListener(new PAListAdapter.ItemClickListener() {
            @Override public void onClick(int position) {
                startActivity(new Intent(activity, PADetailsActivity.class).putExtra("paid", followList.get(position).getPaid()));
            }
        });
    }

    /**
     * 从服务器更新数据
     */
    private void updateDataFromServer() {
        new Thread(new Runnable() {
            @Override public void run() {
                followList.clear();
                followList.addAll(PAManager.getInstance().getPAFollowListFromServer(currentUser, 1, 100));
                handler.sendMessage(handler.obtainMessage());
            }
        }).start();
    }

    /**
     * 刷新列表
     */
    private void refresh() {
        adapter.notifyDataSetChanged();
    }

    Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refresh();
        }
    };
}
