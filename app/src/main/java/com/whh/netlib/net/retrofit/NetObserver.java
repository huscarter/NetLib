package com.whh.netlib.net.retrofit;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CallSuper;

import java.net.UnknownHostException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

/**
 * Create by huscarter@163.com on 9/12/18
 * <p>
 * 类说明:<BR/>
 * <li>
 * 1、如果想在出错的情况下finish当前页面，实例化时请将force_close赋值为true，
 * 建议onError给出提示信息，即需要在重写onError的时候传入nulll值。
 * </li>
 * <li>
 * 2、如果不想提示任何信息，有两种方法：1)、实例化时Context/Activity传入null；2)、请重写onError传入null。
 * </li>
 * </ul>
 * 接受传值:<BR/>
 * 对外传值:<BR/>
 */

public abstract class NetObserver<T> extends DisposableObserver<T> {
    private final static String TAG = NetObserver.class.getSimpleName();

    private static final int DELAY_TIME = 200;

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int NOT_IMPLEMENTED = 501;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    private Activity activity;
    private Context context;
    private boolean force_close = false;

    /**
     * @param activity    用于提示信息
     * @param force_close 用于判断报错误时是否需要关闭activity
     */
    public NetObserver(Context activity, boolean force_close) {
        this.activity = (Activity) activity;
        this.force_close = force_close;
    }

    /**
     * @param context 用于提示信息
     */
    public NetObserver(Context context) {
        this.context = context;
    }

    /**
     * 如果不需要提示信息可通过此方法实例化
     */
    public NetObserver() {
        //
    }

    @CallSuper
    @Override
    public void onComplete() {
        //
    }


    /**
     * 如果需要在出错时对页面组建做处理，请重载此方法
     *
     * @param e
     */
    @CallSuper
    @Override
    public void onError(Throwable e) {
        handleError(e, activity == null ? context : activity, force_close);
    }

    public abstract void onNext(T t);

    public void handleError(Throwable e, Context context, boolean force_close) {
        if (e != null) {
            e.printStackTrace();
        }
        if (context == null) {
            return;
        }
        if (e instanceof HttpException) { // is http exception
            HttpException error = (HttpException) e;
            switch (error.code()) {
                case UNAUTHORIZED:
                    // todo
                    break;
                case FORBIDDEN: // 网络禁止访问
                case NOT_FOUND:
                    // todo
                    break;
                case REQUEST_TIMEOUT:
                    // todo
                    break;
                case INTERNAL_SERVER_ERROR:
                case NOT_IMPLEMENTED: // 服务器不支持访问的方法
                case BAD_GATEWAY:
                    // todo
                    break;
                case SERVICE_UNAVAILABLE:
                    // todo
                    break;
                default:
                    // todo
                    break;
            }
        } else if (e instanceof UnknownHostException) { // is not http exception
            // todo
        } else {
            // todo
        }

        if (force_close) {
            // todo
        }
    }

}
