package com.hyphenate.chatuidemo.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.pa.PAManager;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by lzan13 on 2017/6/20.
 * 公众号图文混排消息布局展示控件
 */
public class ChatRowPAImageTxt extends EaseChatRow {

    private ImageView paMsgImage;
    private TextView paMsgTitle;
    private RelativeLayout paSingleMsgContainer;
    private LinearLayout paSubMsgContainer;

    public ChatRowPAImageTxt(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override protected void onInflateView() {
        if (message.getBooleanAttribute("em_pa_msg", false)) {
            inflater.inflate(R.layout.em_item_pa_image_txt, this);
        }
    }

    @Override protected void onFindViewById() {
        paMsgImage = (ImageView) findViewById(R.id.img_pa_msg_photo);
        paMsgTitle = (TextView) findViewById(R.id.text_pa_msg_title);
        paSingleMsgContainer = (RelativeLayout) findViewById(R.id.layout_pa_single_container);
        paSubMsgContainer = (LinearLayout) findViewById(R.id.layout_pa_sub_msg_container);
    }

    @Override protected void onUpdateView() {

    }

    @Override protected void onSetUpView() {
        try {
            JSONObject payload = message.getJSONObjectAttribute("payload");
            if (payload.optString("type").equals("single")) {
                loadSingleMsg(payload.optJSONArray("items").optJSONObject(0));
            } else {
                loadSingleMsg(payload.optJSONArray("items").optJSONObject(0));
                loadSubMsg(payload.optJSONArray("items"));
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 装载公众号单条消息
     */
    private void loadSingleMsg(final JSONObject object) {
        paSubMsgContainer.setVisibility(View.GONE);
        paSubMsgContainer.removeAllViews();
        paMsgTitle.setText(object.optString("title"));
        Glide.with(context).load(object.optString("imgUrl")).error(R.drawable.ease_default_image).into(paMsgImage);
        paSingleMsgContainer.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                // 使用系统浏览器打开网址
                Uri uri = null;
                if (object.optString("action").equals("body")) {
                    uri = Uri.parse(object.optString("oriUrl"));
                } else {
                    uri = Uri.parse(object.optString("jumpUrl"));
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    /**
     * 装载公众号更多子消息
     */
    private void loadSubMsg(JSONArray array) {
        paSubMsgContainer.setVisibility(View.VISIBLE);
        paSubMsgContainer.removeAllViews();
        for (int i = 1; i < array.length(); i++) {
            final JSONObject object = array.optJSONObject(i);
            RelativeLayout layout = new RelativeLayout(context);

            int imageSize = context.getResources().getDimensionPixelOffset(R.dimen.pa_image_size);
            int margin = context.getResources().getDimensionPixelOffset(R.dimen.pa_margin);
            // 子消息图片控件
            RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(0, 0);
            imgParams.width = imageSize;
            imgParams.height = imageSize;
            imgParams.setMargins(margin, margin, margin, margin);
            imgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            ImageView imageView = new ImageView(context);
            Glide.with(context)
                    .load(object.optString("imgUrl"))
                    .centerCrop()
                    .error(R.drawable.ease_default_image)
                    .into(imageView);
            imageView.setLayoutParams(imgParams);
            layout.addView(imageView);

            // 子消息 title 控件
            RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(0, 0);
            textParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            textParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            textParams.addRule(RelativeLayout.ALIGN_LEFT, imageView.getId());
            textParams.addRule(RelativeLayout.CENTER_VERTICAL);
            textParams.setMargins(margin, margin, margin, margin);
            TextView textView = new TextView(context);
            textView.setLayoutParams(textParams);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setText(object.optString("title"));
            layout.addView(textView);

            paSubMsgContainer.addView(layout);

            // 设置消息点击事件
            layout.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) {
                    // 使用系统浏览器打开网址
                    Uri uri = null;
                    if (object.optString("action").equals("body")) {
                        uri = Uri.parse(object.optString("oriUrl"));
                    } else {
                        uri = Uri.parse(object.optString("jumpUrl"));
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override protected void onBubbleClick() {

    }
}
