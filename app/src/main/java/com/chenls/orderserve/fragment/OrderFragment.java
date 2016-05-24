package com.chenls.orderserve.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chenls.orderserve.CommonUtil;
import com.chenls.orderserve.R;
import com.chenls.orderserve.adapter.OrderRecyclerViewAdapter;
import com.chenls.orderserve.bean.MyUser;
import com.chenls.orderserve.bean.Order;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

public class OrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private ProgressDialog progressDialog;

    public OrderFragment() {
    }

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_list, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view;
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        myRefresh(false);
        return view;
    }

    public void myRefresh(boolean b) {
        swipeRefreshLayout.setProgressViewOffset(false, 0, 40);
        swipeRefreshLayout.setRefreshing(true);
        queryOrder(b);
    }

    @Override
    public void onRefresh() {
        queryOrder(true);
    }

    private void queryOrder(boolean refresh) {
        if (!CommonUtil.checkNetState(getActivity())) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        final BmobQuery<Order> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("username", MyUser.getObjectByKey(getContext(), "username"));
        bmobQuery.setLimit(100);
        bmobQuery.order("-updatedAt");
        //先判断是否强制刷新
        if (refresh) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);        // 强制在从网络中获取
        } else {
            //先判断是否有缓存
            boolean isCache = bmobQuery.hasCachedResult(getActivity(), Order.class);
            if (isCache) {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 先从缓存取数据，如果没有的话，再从网络取。
            } else {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则先从网络中取
            }
        }
        bmobQuery.findObjects(getContext(), new FindListener<Order>() {

            @Override
            public void onSuccess(List<Order> orderList) {
                swipeRefreshLayout.setRefreshing(false);
                initiateView(orderList);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }

            @Override
            public void onError(int code, String msg) {
                swipeRefreshLayout.setRefreshing(false);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }

    void initiateView(List<Order> orderList) {
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new OrderRecyclerViewAdapter(getContext(), orderList, this));
    }


    public void onDeleteButtonClick(String objectId) {
        if (!CommonUtil.checkNetState(getActivity())) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("请稍等...");

        }
        progressDialog.show();
        Order order = new Order();
        order.setObjectId(objectId);
        order.delete(getContext(), objectId, new DeleteListener() {
            @Override
            public void onSuccess() {
                queryOrder(true);
            }

            @Override
            public void onFailure(int i, String s) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Toast.makeText(getContext(), "删除失败，请刷新后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
