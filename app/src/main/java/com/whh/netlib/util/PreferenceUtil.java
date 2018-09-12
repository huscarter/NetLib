package com.whh.netlib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 曾悦然 on 2014/8/7.
 * 首选项存取工具类
 */
public class PreferenceUtil {
    private static SharedPreferences preferences = null;
    private static final String SHARED_INFO="NET_LIB_SPF";

    private PreferenceUtil() {
        //
    }

    /**
     * @param context
     * @param isface  强制初始化
     */
    public static void initialize(Context context, boolean isface) {
        if (preferences == null || isface) {
            preferences = context.getSharedPreferences(SHARED_INFO, Context.MODE_PRIVATE);
        }
    }

    public static void initialize(Context context) {
        initialize(context, false);
    }

    public static SharedPreferences getPreference() {
        return preferences;
    }

    /**
     * 保存Sring型数据
     *
     * @param key   键
     * @param value 数值
     */
    public static void setStringValue(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 保存int型数据
     *
     * @param key   键
     * @param value 数值
     */
    public static void setIntValue(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 保存int型数据
     *
     * @param key   键
     * @param value 数值
     */
    public static void setLongValue(String key, Long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 保存Boolean型数据
     *
     * @param key   键
     * @param value 数值
     */
    public static void setBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获取String类型数据
     *
     * @param key          键
     * @param defaultValue 数值
     * @return String类型数据
     */
    public static String getString(String key, String defaultValue) {
        if (preferences == null) {
            return "";
        }
        return preferences.getString(key, defaultValue);
    }

    /**
     * 获取Int类型数据
     *
     * @param key          键
     * @param defaultValue
     * @return Int类型数据
     */
    public static int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public static long getLong(String key, Long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    /**
     * 获取Boolean类型数据
     *
     * @param key          键
     * @param defaultValue
     * @return Boolean类型数据
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * 除清首选项数据
     *
     * @param context 界面上下文
     */
    public static void clearData(Context context) {
        preferences.edit().clear().commit();
    }

    /**
     * 是否存在key键值
     *
     * @param key 键
     * @return 是否存在
     */
    public static boolean contains(String key) {
        return preferences.contains(key);
    }

    /**
     * 从Preferences获取存储对象
     *
     * @param key 键值
     * @param <T>
     * @return
     */
    public static <T> T getSerializables(String key) {
        String temp = preferences.getString(key, "");
        if (temp==null||temp.isEmpty()) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        T t = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            t = (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 将对象存储到Preferences
     *
     * @param key
     * @param t
     * @param <T>
     */
    public static <T> void setSerializables(final String key, final T t) {
        if (t instanceof Serializable) {
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Object> observable) throws Exception {
                    SharedPreferences.Editor editor = preferences.edit();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(t);//把对象写到流里
                        String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                        editor.putString(key, temp);

                        editor.commit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            //
                        }

                        @Override
                        public void onNext(@NonNull Object o) {
//                            JLog.i(TAG, "save success");
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            //
                        }
                    });


        } else {
            new Exception("T must implements Serializable").printStackTrace();
        }
    }

}
