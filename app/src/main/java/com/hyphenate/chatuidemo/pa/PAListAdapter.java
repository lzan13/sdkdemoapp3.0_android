package com.hyphenate.chatuidemo.pa;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.hyphenate.chatuidemo.R;
import java.util.List;

/**
 * Created by lzan13 on 2017/6/13.
 * 公众号列表适配器，这里自己关注的和全部的通用一个适配器
 */
public class PAListAdapter extends RecyclerView.Adapter<PAListAdapter.PAViewHolder> {

    private Context context;
    private List<PAInfo> list;
    private ItemClickListener itemClickListener;

    public PAListAdapter(Context context, List<PAInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override public PAViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.em_item_pa, parent, false);
        return new PAViewHolder(view);
    }

    @Override public void onBindViewHolder(PAViewHolder holder, final int position) {
        PAInfo info = list.get(position);
        holder.nameView.setText(info.getName());
        holder.descriptionView.setText(info.getDescription());

        if (TextUtils.isEmpty(info.getLogo())) {
            holder.avatarView.setImageResource(R.drawable.em_groups_icon);
        } else {
            Glide.with(context).load(info.getLogo()).crossFade().error(R.drawable.ease_groups_icon).into(holder.avatarView);
        }
        // 设置列表项点击监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(position);
                }
            }
        });
    }

    @Override public int getItemCount() {
        return list.size();
    }

    /**
     * 设置列表点击监听回调
     *
     * @param listener 外部实现的回调接口
     */
    public void setItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    /**
     * 列表点击监听回调
     */
    interface ItemClickListener {
        /**
         * 列表点击回调方法
         *
         * @param position 当前点击位置
         */
        void onClick(int position);
    }

    static class PAViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarView;
        TextView nameView;
        TextView descriptionView;

        public PAViewHolder(View itemView) {
            super(itemView);
            avatarView = (ImageView) itemView.findViewById(R.id.img_avatar);
            nameView = (TextView) itemView.findViewById(R.id.text_name);
            descriptionView = (TextView) itemView.findViewById(R.id.text_description);
        }
    }
}
