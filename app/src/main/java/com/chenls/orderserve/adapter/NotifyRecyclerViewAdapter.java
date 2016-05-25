package com.chenls.orderserve.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chenls.orderserve.R;
import com.chenls.orderserve.bean.Notify;

import java.util.List;

import cn.bmob.v3.listener.DeleteListener;

public class NotifyRecyclerViewAdapter extends
        RecyclerView.Adapter<NotifyRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private final List<Notify> notifyList;

    public NotifyRecyclerViewAdapter(Context context, List<Notify> notifyList) {
        this.context = context;
        this.notifyList = notifyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_notify_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Notify notify = notifyList.get(position);
        holder.tv_title.setText(notify.getTitle());
        holder.tv_content.setText(notify.getContent());
        holder.tv_date.setText(notify.getCreatedAt());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.delete_notify));
                builder.setMessage(context.getString(R.string.sure_delete_notify));
                builder.setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("请稍等...");
                        progressDialog.show();
                        Notify myNotify = new Notify();
                        myNotify.setObjectId(notify.getObjectId());
                        myNotify.delete(context, new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                notifyList.remove(notify);
                                notifyItemRemoved(holder.getLayoutPosition());
                                progressDialog.dismiss();
                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        });
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
        return notifyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tv_title, tv_content, tv_date;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
        }
    }
}
