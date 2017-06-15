package com.hyphenate.chatuidemo.pa;

import android.text.TextUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private String baseUrl = "";
    private String appKey;

    private PAAPI paAPI;

    /**
     * 私有的构造方法，做一些单例类的初始化操作
     */
    private PAManager() {
        appKey = EMClient.getInstance().getOptions().getAppKey();
        appKey = appKey.replace("#", "/");
        if (TextUtils.isEmpty(appKey)) {
            EMLog.d(TAG, "必须在 AndroidManifest 中配置环信 appkey");
            return;
        }
        baseUrl = "http://123.56.133.67:7077/api/pa/v1/" + appKey + "/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).build();
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
     * 分页获取全部公众号
     *
     * @param pageNum 第几页
     * @param pageSize 每页大小
     */
    public List<PAInfo> getPAALLListFromServer(int pageNum, int pageSize) {
        List<PAInfo> list = new ArrayList<>();
        Call<ResponseBody> call = paAPI.getPAListFromServer(pageNum, pageSize);
        // 同步请求服务器
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                if (jsonObject.optString("status").equals("ok")) {
                    JSONArray array = jsonObject.optJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        PAInfo info = new PAInfo();
                        JSONObject object = array.optJSONObject(i);
                        info.setPaid(object.optString("paid"));
                        info.setName(object.optString("name"));
                        info.setDescription(object.optString("description"));
                        info.setLogo(object.optString("logo"));
                        list.add(info);
                    }
                }
            }
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
        Call<ResponseBody> call = paAPI.getPAListFromServer(username, pageNum, pageSize);
        // 同步请求服务器
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                if (jsonObject.optString("status").equals("ok")) {
                    JSONArray array = jsonObject.optJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        list.add(parsePAObject(object));
                    }
                    return list;
                }
            }
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
    public PAInfo getPAInfoFromServer(String paid) {
        Call<ResponseBody> call = paAPI.getPADetails(paid);
        // 同步请求服务器
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                if (jsonObject.optString("status").equals("ok")) {
                    JSONObject object = jsonObject.optJSONObject("data");
                    return parsePAObject(object);
                }
            }
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
    public PAInfo getPAInfoFullFromServer(String paid) {
        Call<ResponseBody> call = paAPI.getPADetails(paid);
        // 同步请求服务器
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                if (jsonObject.optString("status").equals("ok")) {
                    JSONObject object = jsonObject.optJSONObject("data");
                    return parsePAObject(object);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PAInfo parsePAObject(JSONObject object) {
        PAInfo info = new PAInfo();
        info.setPaid(object.optString(PA_ID));
        info.setPaid(object.optString(PA_NAME));
        info.setPaid(object.optString(PA_DESCRIPTION));
        info.setPaid(object.optString(PA_LOGO));
        if (object.has(PA_AGENT_USER)) {
            info.setAgentUser(object.optString(PA_AGENT_USER));
        }
        if (object.has(PA_MENU)) {
            info.setMenu(object.optJSONArray(PA_MENU));
        }
        return info;
    }
}
