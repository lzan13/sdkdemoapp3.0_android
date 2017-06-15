package com.hyphenate.chatuidemo.pa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.ui.BaseActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2017/6/13.
 * 展示当前账户关注的公众号列表界面
 */
public class PAFollowListActivity extends BaseActivity {

    private PAFollowListActivity activity;

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private PAListAdapter adapter;

    private List<PAInfo> list;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_pa_follow_list);

        init();
    }

    private void init() {
        activity = this;

        list = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PAListAdapter(activity, list);
        recyclerView.setAdapter(adapter);

        updateDataFromServer();
        setItemClickListener();
    }

    private void setItemClickListener() {
        adapter.setItemClickListener(new PAListAdapter.ItemClickListener() {
            @Override public void onClick(int position) {
                startActivity(new Intent(activity, PADetailsActivity.class).putExtra("paid", list.get(position).getPaid()));
            }
        });
    }

    private void updateDataFromServer() {
        new Thread(new Runnable() {
            @Override public void run() {
                list.clear();
                list.addAll(PAManager.getInstance().getPAALLListFromServer(1, 100));
                handler.sendMessage(handler.obtainMessage());
            }
        }).start();
    }

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
