package com.chenls.orderserve.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chenls.orderserve.CommonUtil;
import com.chenls.orderserve.R;
import com.chenls.orderserve.bean.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;


public class LoginDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText et_userpwd;
    private EditText et_user_data;
    private ProgressBar progressBar;
    private TextView login;
    LoginDialogFragment loginDialogFragment;
    public LoginDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_login_dialog, container, false);
        et_user_data = (EditText) view.findViewById(R.id.et_user_data);
        et_userpwd = (EditText) view.findViewById(R.id.et_userpwd);
        login = (TextView) view.findViewById(R.id.login);
        login.setOnClickListener(this);
        TextView register = (TextView) view.findViewById(R.id.cancel);
        register.setOnClickListener(this);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        loginDialogFragment = this;
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                final String userData = et_user_data.getText().toString().trim();
                final String userPwd = et_userpwd.getText().toString().trim();
                if (TextUtils.isEmpty(userData) || TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(getActivity(), "用户名或密码为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtil.checkNetState(getActivity())) {
                    return;
                }
                login.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                if (userData.matches("^1\\d{10}$"))
                    loginByPhonePwd(userData, userPwd);
                else
                    loginByUserName(userData, userPwd);
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }
    /**
     * 通过手机号登录
     *
     * @param phoneNum 手机号
     * @param userPwd  密码
     */
    private void loginByPhonePwd(String phoneNum, String userPwd) {
        MyUser.loginByAccount(getContext(), phoneNum, userPwd, new LogInListener<MyUser>() {
            @Override
            public void done(MyUser user, BmobException e) {
                if (user != null) {
                    Toast.makeText(getContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                    loginDialogFragment.dismiss();
                } else {
//                    toast("错误码："+e.getErrorCode()+",错误原因："+e.getLocalizedMessage());
                    Toast.makeText(getContext(), "登陆失败:", Toast.LENGTH_SHORT).show();
                    login.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 登陆用户
     *
     * @param userName 用户名
     * @param userPwd  密码
     */
    private void loginByUserName(String userName, String userPwd) {
        final MyUser bu2 = new MyUser();
        bu2.setUsername(userName);
        bu2.setPassword(userPwd);
        bu2.login(getContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                loginDialogFragment.dismiss();
            }

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(getContext(), "登陆失败:" + msg, Toast.LENGTH_SHORT).show();
                login.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
