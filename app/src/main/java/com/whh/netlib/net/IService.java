package com.whh.netlib.net;

import com.whh.netlib.bean.BaseData;
import com.whh.netlib.bean.UploadImage;
import com.whh.netlib.bean.User;
import com.whh.netlib.bean.VersionUpdateInfo;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Create by huscarter@163.com on 9/12/18
 * <p>
 * 类说明:<BR/>
 * 基础的数据实体
 */
public interface IService {
    /**
     * 上传图片
     *
     * @param body
     * @return
     */
    @Multipart
    @POST("api/image/upload")
    Observable<BaseData<UploadImage>> uploadImage(@Part MultipartBody.Part body);


    /**
     * 版本跟新的接口 cxf
     *
     * @param system ios 苹果; android 安卓; is_hot 热修复"
     */
    @GET("api/app/update_info")
    Observable<VersionUpdateInfo> getUpdateInfo(@Query("system") String system);

    /**
     * 获取用户、伙伴详情
     *
     * @param id 用户id
     * @return
     */
    @FormUrlEncoded
    @POST("api/member/show")
    Observable<BaseData<User>> getUserInfo(@Field("id") String id);

}
