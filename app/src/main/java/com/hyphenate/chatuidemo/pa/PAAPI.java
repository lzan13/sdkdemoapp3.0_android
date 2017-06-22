package com.hyphenate.chatuidemo.pa;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lzan13 on 2017/6/13.
 * 使用 Retrofit 封装公众号与服务器相关请求接口
 */
public interface PAAPI {

    /**
     * 给公众号发送菜单事件
     *
     * @param paid 公众号 id
     * @param body 请求体
     */
    @POST("pas/{paid}/menu/automsg") Call<ResponseBody> postPAMenuAutoMsg(@Path("paid") String paid, @Body RequestBody body);

    /**
     * 发送图文消息已读统计到服务器
     *
     * @param paid 公众号 id
     * @param body 请求体
     */
    @POST("pas/{paid}/msgread") Call<ResponseBody> postPAMsgRead(@Path("paid") String paid, @Body RequestBody body);

    /**
     * 关注公众号
     *
     * @param paid 公众号 id
     * @param body 请求体
     */
    @POST("pas/{paid}/followers") Call<ResponseBody> postPAFollowers(@Path("paid") String paid, @Body RequestBody body);

    /**
     * 取消关注公众号
     *
     * @param username 当前用户即粉丝 id
     */
    @DELETE("pas/{paid}/followers/{username}") Call<ResponseBody> deletePAFollowers(@Path("paid") String paid,
            @Path("username") String username);

    /**
     * 分页获取全部公众号列表
     *
     * @param pagenum 第几页
     * @param pagesize 每页大小
     */
    @GET("pas") Call<ResponseBody> getPAAllList(@Query("pagenum") int pagenum, @Query("pagesize") int pagesize);

    /**
     * 获取用户关注的公众号列表
     *
     * @param username 要获取的用户账户，一般是自己
     * @param pagenum 第几页
     * @param pagesize 每页获取多少
     */
    @GET("pas") Call<ResponseBody> getPAFollowList(@Query("username") String username, @Query("pagenum") int pagenum,
            @Query("pagesize") int pagesize);

    /**
     * 获取指定公众号详情
     *
     * @param paid 公众号 id
     */
    @GET("pas/{paid}") Call<ResponseBody> getPADetails(@Path("paid") String paid);

    /**
     * 获取指定公众号详情，包含菜单
     *
     * @param paid 公众号 id
     */
    @GET("pas/{paid}?detail=yes") Call<ResponseBody> getPADetailsFull(@Path("paid") String paid);
}
