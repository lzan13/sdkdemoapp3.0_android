package com.hyphenate.chatuidemo.pa;

import android.text.TextUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by lzan13 on 2017/6/13.
 * 公众号相关接口管理类，这里进行同一封装
 */
public class PAManager {
    private String TAG = this.getClass().getSimpleName();

    private final String PA_ID = "paid";
    private final String PA_NAME = "name";
    private final String PA_DESCRIPTION = "description";
    private final String PA_LOGO = "logo";
    private final String PA_AGENT_USER = "agentUser";
    private final String PA_MENU = "menu";

    private static PAManager instance;

    private String baseUrl = "http://121.43.63.92:8810/api/pa/v1/";
    private String accessToken = null;

    private PAAPI paAPI;

    /**
     * 私有的构造方法，做一些单例类的初始化操作
     */
    private PAManager() {
        String appKey = EMClient.getInstance().getOptions().getAppKey();
        appKey = appKey.replace("#", "/");
        if (TextUtils.isEmpty(appKey)) {
            EMLog.d(TAG, "必须在 AndroidManifest 中配置环信 appkey");
            return;
        }
        // 获取当前登录用户 token
        //accessToken = EMClient.getInstance().getAccessToken();
        accessToken = "1e793309-7310-4194-9b9e-51009865880a";

        // 自定义拦截器，在拦截器中添加请求 token 及其他请求头参数
        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = builder.build();

        // 实例化 retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl + appKey + "/").client(client).build();
        // 获取 API 实例，为后边请求接口做准备
        paAPI = retrofit.create(PAAPI.class);
    }

    /**
     * 获取单例类实例
     */
    public static PAManager getInstance() {
        if (instance == null) {
            instance = new PAManager();
        }
        return instance;
    }

    /**
     * 发送公众号菜单事件
     *
     * @param paid 公众号 id
     * @param username 当前账户 id
     * @param eventid 公众号菜单事件 id
     * @return 返回是否发送成功
     */
    public boolean sendPAMenuAutoMessageToServer(String paid, String username, String eventid) {
        try {
            JSONObject object = new JSONObject();
            object.put("username", username);
            object.put("eventid", eventid);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), object.toString());
            Call<ResponseBody> call = paAPI.postPAMenuAutoMsg(paid, body);
            Response<ResponseBody> response = call.execute();
            String result = null;
            if (response.isSuccessful()) {
                result = response.body().string();
                EMLog.i(TAG, result);
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("status").equals("ok")) {
                    return true;
                }
            }
            result = response.errorBody().string();
            EMLog.e(TAG, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送图文消息已读统计消息到服务器
     *
     * @param paid 公众号 id
     * @param username 当前账户 id
     * @param resourceId 消息 id
     * @return 返回是否发送成功
     */
    public boolean sendPAMessageReadToServer(String paid, String username, String resourceId) {
        try {
            JSONObject object = new JSONObject();
            object.put("username", username);
            object.put("resourceid", resourceId);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), object.toString());
            Call<ResponseBody> call = paAPI.postPAMsgRead(paid, body);
            Response<ResponseBody> response = call.execute();
            String result = null;
            if (response.isSuccessful()) {
                result = response.body().string();
                EMLog.i(TAG, result);
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("status").equals("ok")) {
                    return true;
                }
            }
            result = response.errorBody().string();
            EMLog.e(TAG, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关注公众号
     *
     * @param paid 公众号 id
     * @param username 当前账户 id
     * @return 关注结果
     */
    public boolean addPAFollowersToServer(String paid, String username) {
        try {
            JSONObject object = new JSONObject();
            object.put("username", username);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), object.toString());
            Call<ResponseBody> call = paAPI.postPAFollowers(paid, body);
            Response<ResponseBody> response = call.execute();
            String result = null;
            if (response.isSuccessful()) {
                result = response.body().string();
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("status").equals("ok")) {
                    return true;
                }
            }
            result = response.errorBody().string();
            EMLog.i(TAG, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 取消关注公众号
     *
     * @param paid 公众号 id
     * @param username 当前账户 id
     * @return 关注结果
     */
    public boolean removePAFollowersToServer(String paid, String username) {
        Call<ResponseBody> call = paAPI.deletePAFollowers(paid, username);
        try {
            Response<ResponseBody> response = call.execute();
            String result = null;
            if (response.isSuccessful()) {
                result = response.body().string();
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("status").equals("ok")) {
                    return true;
                }
            }
            result = response.errorBody().string();
            EMLog.i(TAG, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 分页获取全部公众号
     *
     * @param pageNum 第几页
     * @param pageSize 每页大小
     */
    public List<PAInfo> getPAALLListFromServer(int pageNum, int pageSize) {
        List<PAInfo> list = new ArrayList<>();
        Call<ResponseBody> call = paAPI.getPAAllList(pageNum, pageSize);
        // 同步请求服务器
        try {
            Response<ResponseBody> response = call.execute();
            String result = null;
            if (response.isSuccessful()) {
                result = response.body().string();
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("status").equals("ok")) {
                    JSONArray array = jsonObject.optJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        list.add(parsePAObject(object));
                    }
                }
            } else {
                result = response.errorBody().string();
            }
            EMLog.i(TAG, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 分页获取自己关注的公众号
     *
     * @param username 当前账户
     * @param pageNum 第几页
     * @param pageSize 分页大小
     */
    public List<PAInfo> getPAFollowListFromServer(String username, int pageNum, int pageSize) {
        List<PAInfo> list = new ArrayList<>();
        Call<ResponseBody> call = paAPI.getPAFollowList(username, pageNum, pageSize);
        // 同步请求服务器
        try {
            Response<ResponseBody> response = call.execute();
            String result = null;
            if (response.isSuccessful()) {
                result = response.body().string();
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("status").equals("ok")) {
                    JSONArray array = jsonObject.optJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        list.add(parsePAObject(object));
                    }
                }
            } else {
                result = response.errorBody().string();
            }
            EMLog.w(TAG, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 从服务器获取公众号详情，不包含菜单
     *
     * @param paid 公众号 id
     */
    public PAInfo getPADetailsFromServer(String paid) {
        Call<ResponseBody> call = paAPI.getPADetails(paid);
        // 同步请求服务器
        try {
            Response<ResponseBody> response = call.execute();
            String result = null;
            if (response.isSuccessful()) {
                result = response.body().string();
                EMLog.i(TAG, result);
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("status").equals("ok")) {
                    JSONObject object = jsonObject.optJSONObject("data");
                    return parsePAObject(object);
                }
            } else {
                result = response.errorBody().string();
            }
            EMLog.w(TAG, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从服务器获取公众号详情，包含菜单
     *
     * @param paid 公众号 id
     */
    public PAInfo getPADetailsFullFromServer(String paid) {
        Call<ResponseBody> call = paAPI.getPADetailsFull(paid);
        // 同步请求服务器
        try {
            Response<ResponseBody> response = call.execute();
            String result = null;
            if (response.isSuccessful()) {
                result = response.body().string();
                EMLog.i(TAG, result);
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("status").equals("ok")) {
                    JSONObject object = jsonObject.optJSONObject("data");
                    return parsePAObject(object);
                }
            } else {
                result = response.errorBody().string();
            }
            EMLog.w(TAG, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析公众号返回数据
     *
     * @param object 请求到的数据体
     */
    private PAInfo parsePAObject(JSONObject object) {
        PAInfo info = new PAInfo();
        info.setPaid(object.optString(PA_ID));
        info.setName(object.optString(PA_NAME));
        info.setDescription(object.optString(PA_DESCRIPTION));
        info.setLogo(object.optString(PA_LOGO));
        if (object.has(PA_AGENT_USER)) {
            info.setAgentUser(object.optString(PA_AGENT_USER));
        }
        if (object.has(PA_MENU)) {
            info.setMenu(object.optJSONArray(PA_MENU));
        }
        return info;
    }
}
