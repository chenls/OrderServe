package com.chenls.orderserve.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.chenls.orderserve.CommonUtil;
import com.chenls.orderserve.R;
import com.chenls.orderserve.bean.Notify;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.SaveListener;

public class AddNotify extends AppCompatActivity {

    private boolean isNeedRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notify);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar action = getSupportActionBar();
        if (action != null) {
            action.setDisplayHomeAsUpEnabled(true);
            action.setHomeAsUpIndicator(R.mipmap.ic_clear);
        }
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            if (!CommonUtil.checkNetState(AddNotify.this)) {
                return true;
            }
            final ProgressDialog progressDialog = new ProgressDialog(AddNotify.this);
            progressDialog.setMessage("请稍等...");
            progressDialog.show();
            EditText userId = (EditText) findViewById(R.id.userId);
            final EditText title = (EditText) findViewById(R.id.title);
            EditText content = (EditText) findViewById(R.id.content);
            assert title != null;
            assert content != null;
            assert userId != null;
            Notify notify = new Notify("Android"
                    , userId.getText().toString()
                    , content.getText().toString()
                    , title.getText().toString());

            notify.save(AddNotify.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    pushToAndroid(title.getText().toString());
                    progressDialog.dismiss();
                    Toast.makeText(AddNotify.this, "发布成功", Toast.LENGTH_SHORT).show();
                    isNeedRefresh = true;
                    returnData();
                }

                @Override
                public void onFailure(int i, String s) {
                    progressDialog.dismiss();
                    Toast.makeText(AddNotify.this, "发布失败", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 给android平台终端推送
     *
     * @param message
     */
    private void pushToAndroid(String message) {
        //开启debug服务后，可知晓push服务是否正常启动和运行
        BmobPush.setDebugMode(true);
        //开启推送服务
        BmobPush.startWork(this);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();

        BmobPushManager<BmobInstallation> bmobPush = new BmobPushManager<>(this);
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        query.addWhereEqualTo("deviceType", "android");
        bmobPush.setQuery(query);
        bmobPush.pushMessage(message);
    }

    private void returnData() {
        Intent intent = new Intent(AddNotify.this, NotifyActivity.class);
        intent.putExtra("isNeedRefresh", isNeedRefresh);
        setResult(RESULT_OK, intent);
        finish();
    }
}
