package com.chenls.orderserve.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenls.orderserve.R;
import com.chenls.orderserve.bean.Dish;

import java.util.Arrays;
import java.util.Map;


public class AckOrderRecyclerViewAdapter extends RecyclerView.Adapter<AckOrderRecyclerViewAdapter.ViewHolder> {

    private final Map<Integer, Dish> dishMap;
    private final String consigneeMessage;
    private final String mark;
    private Context context;
    private OnClickListenerInterface mListener;

    public AckOrderRecyclerViewAdapter(Context context, Map<Integer, Dish> dishMap, String mark, String consigneeMessage) {
        this.context = context;
        this.dishMap = dishMap;
        this.mark = mark;
        this.consigneeMessage = consigneeMessage;
        if (context instanceof OnClickListenerInterface) {
            mListener = (OnClickListenerInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnClickListenerInterface");
        }
    }

    private int[] getKey(Map<Integer, Dish> map) {
        int[] re = new int[map.size()];
        int index = 0;
        for (Integer i : map.keySet()) {
            re[index++] = i;
        }
        Arrays.sort(re);
        return re;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.activity_ack_order_item_first, parent, false);
            return new ViewHolder(view, viewType);
        }
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.activity_ack_order_item_other, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {
            String consignee[] = new String[3];
            if (consigneeMessage.contains(",")) {
                consignee = consigneeMessage.split(",");
            } else {
                consignee[0] = "本店";
                consignee[1] = consigneeMessage;
                consignee[2] = "";
            }
            holder.consignee_name.setText(consignee[0]);
            holder.consignee_address.setText(consignee[1]);
            holder.consignee_tel.setText(consignee[2]);
            holder.tv_mark.setText(TextUtils.isEmpty(mark) ? "无" : mark);
            return;
        }
        holder.dish = dishMap.get(getKey(dishMap)[position - 1]);
        holder.iv_dish.setImageResource(R.mipmap.loading);
        Glide.with(context)
                .load(holder.dish.getPic().getFileUrl(context))
                .placeholder(R.mipmap.loading)
                .into(holder.iv_dish);
        holder.tv_dish_name.setText(holder.dish.getName());
        int num = holder.dish.getNumber();
        holder.tv_dish_num.setText(context.getString(R.string.product_sign, num));
        holder.tv_dish_price.setText(context.getString(R.string.rmb, Double.parseDouble(holder.dish.getPrice()) * num));
    }

    @Override
    public int getItemCount() {
        return dishMap.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_mark;
        public ImageView iv_dish;
        public TextView tv_dish_name;
        public TextView tv_dish_num;
        public TextView tv_dish_price;
        public Dish dish;
        public RelativeLayout consignee_message;
        public TextView consignee_name, consignee_tel, consignee_address;

        public ViewHolder(View view, int viewType) {
            super(view);
            if (viewType == 1) {
                consignee_message = (RelativeLayout) view.findViewById(R.id.consignee_message);
                consignee_name = (TextView) view.findViewById(R.id.order_name);
                consignee_tel = (TextView) view.findViewById(R.id.pay_num);
                consignee_address = (TextView) view.findViewById(R.id.consignee_address);
                tv_mark = (TextView) view.findViewById(R.id.tv_mark);
                return;
            }
            iv_dish = (ImageView) view.findViewById(R.id.image);
            tv_dish_name = (TextView) view.findViewById(R.id.tv_title);
            tv_dish_num = (TextView) view.findViewById(R.id.tv_content);
            tv_dish_price = (TextView) view.findViewById(R.id.tv_date);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        return 0;
    }

    public interface OnClickListenerInterface {
        void OnClickListener(int id, String name, String tel, String address);
    }
}
