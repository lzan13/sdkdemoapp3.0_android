package com.hyphenate.chatuidemo.pa;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lzan13 on 2017/6/13.
 * 使用 Retrofit 封装公众号与服务器相关请求接口
 */
public interface PAAPI {

    /**
     * 分页获取全部公众号列表
     *
     * @param pagenum 第几页
     * @param pagesize 每页大小
     */
    @GET("pas") Call<ResponseBody> getPAListFromServer(@Query("pagenum") int pagenum, @Query("pagesize") int pagesize);

    /**
     * 获取用户关注的公众号列表
     *
     * @param username 要获取的用户账户，一般是自己
     * @param pagenum 第几页
     * @param pagesize 每页获取多少
     */
    @GET("pas") Call<ResponseBody> getPAListFromServer(@Query("username") String username, @Query("pagenum") int pagenum,
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
    @GET("pas/{paid}?details=yes") Call<ResponseBody> getPADetailsFull(@Path("paid") String paid);
}
