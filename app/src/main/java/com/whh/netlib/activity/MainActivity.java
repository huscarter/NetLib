package com.whh.netlib.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.whh.netlib.R;
import com.whh.netlib.bean.BaseData;
import com.whh.netlib.bean.User;
import com.whh.netlib.net.retrofit.NetObserver;
import com.whh.netlib.net.retrofit.RxRequest;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Create by huscarter@163.com on 9/12/18
 * <p>
 * 类说明:<BR/>
 * 主页面
 * 接受传值:<BR/>
 * 对外传值:<BR/>
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 发起网络请求
     */
    private void toRequest() {
        RxRequest.createLib(4).getUserInfo("2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NetObserver<BaseData<User>>() {
                    @Override
                    public void onNext(BaseData<User> obj) {
                        // todo
                    }
                });
    }
}
