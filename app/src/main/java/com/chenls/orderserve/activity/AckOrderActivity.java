package com.chenls.orderserve.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chenls.orderserve.CommonUtil;
import com.chenls.orderserve.R;
import com.chenls.orderserve.adapter.AckOrderRecyclerViewAdapter;
import com.chenls.orderserve.bean.Dish;
import com.chenls.orderserve.bean.Order;

import java.util.Map;

import cn.bmob.v3.listener.UpdateListener;

public class AckOrderActivity extends AppCompatActivity {
    private String mObjectId;
    private int state;
    private boolean isNeedRefresh;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ack_order);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mObjectId = bundle.getString("objectId");
        final Map<Integer, Dish> dishMap = (Map<Integer, Dish>)
                bundle.getSerializable("map");
        final TextView tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        final String total_price = bundle.getString("price");
        final String mark = bundle.getString("mark");
        state = bundle.getInt("state");
        final String consigneeMessage = bundle.getString("consigneeMessage");
        assert tv_total_price != null;
        tv_total_price.setText(getString(R.string.rmb, total_price));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        //如果每个item大小固定，设置这个属性可以提高性能
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AckOrderActivity.this));
        recyclerView.setAdapter(new AckOrderRecyclerViewAdapter(AckOrderActivity.this, dishMap, mark, consigneeMessage));
        final Button ack_button = (Button) findViewById(R.id.ack_button);
        assert ack_button != null;
        final String[] s = getResources().getStringArray(R.array.state);
        ack_button.setText(s[state]);
        ack_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.checkNetState(AckOrderActivity.this)) {
                    if (state == 1 || state == 2) {
                        //修改成已支付
                        AlertDialog.Builder builder = new AlertDialog.Builder(AckOrderActivity.this);
                        builder.setTitle(getString(R.string.changePayState));
                        if (state == 1)
                            builder.setMessage(getString(R.string.sure_have_pay));
                        else
                            builder.setMessage(getString(R.string.sure_pay_over));
                        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changePayState(state + 1);
                            }
                        });
                        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.create().show();
                    } else
                        Toast.makeText(AckOrderActivity.this, s[state], Toast.LENGTH_SHORT).show();
                }
            }

            //改变支付状态
            private void changePayState(final int my_state) {
                final ProgressDialog progressDialog = new ProgressDialog(AckOrderActivity.this);
                progressDialog.setMessage("请稍等...");
                progressDialog.show();
                final Order order = new Order();
                order.setState(my_state);
                order.update(AckOrderActivity.this, mObjectId, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AckOrderActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                        ack_button.setText(s[my_state]);
                        state = my_state;
                        progressDialog.dismiss();
                        isNeedRefresh = true;
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(AckOrderActivity.this, "更新失败，请重试", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            returnData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            returnData();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void returnData() {
        Intent intent = new Intent(AckOrderActivity.this, MainActivity.class);
        intent.putExtra("isNeedRefresh", isNeedRefresh);
        setResult(RESULT_OK, intent);
        finish();
    }
}