package com.hyphenate.chatuidemo.db;

import com.hyphenate.chatuidemo.pa.PAInfo;
import java.util.List;
import java.util.Map;

/**
 * Created by lzan13 on 2017/6/26.
 * 公众号信息数据库操作类
 */
public class PADao {
    public static final String TB_NAME = "pas";
    public static final String COL_PAID = "paid";
    public static final String COL_NAME = "name";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_LOGO = "logo";
    public static final String COL_AGENT_USER = "agentUser";
    public static final String COL_MENU = "menu";

    public void saveFollowPAList(List<PAInfo> list) {
        DemoDBManager.getInstance().saveFollowPAList(list);
    }

    public void savePA(PAInfo info) {
        DemoDBManager.getInstance().savePA(info);
    }

    public void deletePA(String paid) {
        DemoDBManager.getInstance().deletePA(paid);
    }

    public Map<String, PAInfo> getFollowPAMap() {
        return DemoDBManager.getInstance().getFollowPAList();
    }

    public Map<String, PAInfo> getFollowPAAgentUserMap() {
        return DemoDBManager.getInstance().getFollowPAAgentUserMap();
    }
}
