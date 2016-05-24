package com.chenls.orderserve.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chenls.orderserve.CommonUtil;
import com.chenls.orderserve.R;
import com.chenls.orderserve.adapter.AckOrderRecyclerViewAdapter;
import com.chenls.orderserve.bean.Dish;

import java.util.Map;

public class AckOrderActivity extends AppCompatActivity implements AckOrderRecyclerViewAdapter.OnClickListenerInterface {
    public static final String CONSIGNEE_NAME = "consignee_name";
    public static final String CONSIGNEE_TEL = "consignee_tel";
    public static final String CONSIGNEE_ADDRESS = "consignee_address";
    public static final String CONSIGNEE_MARK = "consigneeMark";
    private RecyclerView recyclerView;
    private String consigneeMark;
    private String consigneeMessage;
    private String mObjectId;

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
        final String consigneeMessage = bundle.getString("consigneeMessage");
        tv_total_price.setText(total_price);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        //如果每个item大小固定，设置这个属性可以提高性能
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AckOrderActivity.this));
        recyclerView.setAdapter(new AckOrderRecyclerViewAdapter(AckOrderActivity.this, dishMap, mark, consigneeMessage));
        Button ack_button = (Button) findViewById(R.id.ack_button);
        assert ack_button != null;
        ack_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.checkNetState(AckOrderActivity.this)) {
                    return;
                }
                // TODO: 16-5-24
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnClickListener(int id, String name, String tel, String address) {

    }
}