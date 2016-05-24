package com.chenls.orderserve.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chenls.orderserve.R;
import com.chenls.orderserve.bean.MyUser;
import com.chenls.orderserve.fragment.LoginDialogFragment;
import com.chenls.orderserve.fragment.OrderFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFabDialog();
            }
        });

        //判断用户名是否已经登录
        String username = (String) MyUser.getObjectByKey(this, "username");
        if (TextUtils.isEmpty(username)) {
            //未登录 弹出登录对话框
            LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
            loginDialogFragment.show(getSupportFragmentManager(), "loginDialogFragment");
        } else {
            //已登录
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getString(R.string.order));
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.relativeLayout, OrderFragment.newInstance());
            fragmentTransaction.commit();
        }

    }
    private void showFabDialog() {
        new AlertDialog.Builder(MainActivity.this).setTitle("点赞")
                .setMessage("去项目地址给作者个Star，鼓励下作者。")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse(getString(R.string.app_html));   //指定网址
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);           //指定Action
                        intent.setData(uri);                            //设置Uri
                        MainActivity.this.startActivity(intent);        //启动Activity
                    }
                })
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
