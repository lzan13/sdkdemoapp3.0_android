package com.hyphenate.chatuidemo.pa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.ui.BaseActivity;

/**
 * Created by lzan13 on 2017/6/13.
 * 公众号聊天界面
 */
public class PAChatActivity extends BaseActivity {

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_pa_chat);
    }
}
