package com.chenls.orderserve.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenls.orderserve.MapSerializable;
import com.chenls.orderserve.R;
import com.chenls.orderserve.activity.AckOrderActivity;
import com.chenls.orderserve.bean.Dish;
import com.chenls.orderserve.bean.Order;
import com.chenls.orderserve.fragment.OrderFragment;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class OrderRecyclerViewAdapter extends
        RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder> {
    private final OrderFragment orderFragment;
    private Context context;
    private final List<Order> orderList;

    public OrderRecyclerViewAdapter(Context context, List<Order> orderList, OrderFragment orderFragment) {
        this.context = context;
        this.orderList = orderList;
        this.orderFragment = orderFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Order order = orderList.get(position);
        Map<Integer, Dish> dishMap = order.getDishMap();
        Dish dish = null;
        for (Dish d : dishMap.values()) {
            dish = d;
            if (dish != null)
                break;
        }
        Glide.with(context)
                .load(dish.getPic().getFileUrl(context))
                .placeholder(R.mipmap.loading)
                .into(holder.iv_dish);
        holder.tv_dish_name.setText(String.format("%s...", dish.getName()));
        holder.tv_time.setText(order.getCreatedAt());
        String[] state = context.getResources().getStringArray(R.array.state);
        holder.tv_state.setText(state[order.getState()]);
        holder.tv_price.setText(context.getString(R.string.total_price, order.getPrice() + ""));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AckOrderActivity.class);
                Bundle bundle = new Bundle();
                MapSerializable map = new MapSerializable(order.getDishMap());
                bundle.putSerializable("map", (Serializable) map.getMap());
                bundle.putString("consigneeMessage",  order.getConsigneeMessage());
                bundle.putString("mark", order.getMark());
                bundle.putString("price", order.getPrice() + "");
                bundle.putString("objectId", order.getObjectId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除订单
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.delete_order));
                builder.setMessage(context.getString(R.string.sure_delete));
                builder.setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        orderFragment.onDeleteButtonClick(order.getObjectId());
                    }
                });
                builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView iv_dish, iv_delete;
        public final TextView tv_dish_name, tv_time, tv_state, tv_price;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            iv_dish = (ImageView) view.findViewById(R.id.image);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            tv_dish_name = (TextView) view.findViewById(R.id.tv_title);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_state = (TextView) view.findViewById(R.id.tv_state);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
        }
    }
}
