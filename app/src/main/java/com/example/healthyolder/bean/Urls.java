package com.example.healthyolder.bean;

public class Urls {
    // 本地服务器IP地址 - 确保格式正确 http://而不是http:/
    public static final String baseUrl = "http://10.168.1.216:8070/";
    public static final String GPTURL = "https://api.deepseek.com/v1/chat/completions";

    public static final String LOGIN = baseUrl + "login";                   //登录
    public static final String REGISTER = baseUrl + "register";                   //注册
    public static final String USERDATA = baseUrl + "getPersonInfo";              //获取用户个人信息
    public static final String EDITDATA = baseUrl + "editPersonInfo";             //修改用户个人信息
    public static final String EDITPASSWORD = baseUrl + "editPassword";            //修改用户密码
    public static final String DELETEACCOUNT = baseUrl + "deleteUserAccount";      //注销用户账号
    public static final String UPLOADIMGS = baseUrl + "uploadImgs";            //上传多张图片

    public static final String GETALLNOTES = baseUrl + "getAllNotes";   //获取列表
    public static final String NOTEDETAIL = baseUrl + "getNotesDetail";      //获取文章
    public static final String GETNOTECOMMENTSNEW = baseUrl + "getNoteCommentNew";           //获取文章评论
    public static final String COMMENTNOTENEW = baseUrl + "commentNoteNew";         //评论文章
    public static final String CHECKIFFAVNEW = baseUrl + "checkIfLikeNew";            //是否点赞过
    public static final String CLICKFORLIKE = baseUrl + "clickForLike";          //是否点赞
    public static final String METHODARRAY = baseUrl + "getMethodInfo";
    public static final String EDITGOAL = baseUrl + "editGoalPersonInfo";
    public static final String GETTYPEDIC = baseUrl + "getDicType";                         //类型字典

    public static final String SELECTCHAT = baseUrl + "selectChat";                      //获取聊天信息
    public static final String SENDMESSAGE = baseUrl + "sendMessage";                   //发送消息
    public static final String GETFAVESSAY = baseUrl + "getFavEssay";              //点赞过的文章
    public static final String DETAILUSER = baseUrl + "selectAllUser";      //获取医生
    
    // 抑郁自测相关接口 - 修正为正确的URL格式
    public static final String DEPRESSION_API_BASE = baseUrl + "api/depression/";          // 抑郁测试API基础URL
    public static final String INIT_DEPRESSION_TABLES = DEPRESSION_API_BASE + "init";      // 初始化表结构
    public static final String SAVE_DEPRESSION_TEST = DEPRESSION_API_BASE + "save";        // 保存抑郁测试记录
    public static final String GET_DEPRESSION_HISTORY = DEPRESSION_API_BASE + "history/";  // 获取测试历史 - 需附加用户ID
    public static final String GET_LATEST_SCORE = DEPRESSION_API_BASE + "latest/";         // 获取最新得分 - 需附加用户ID
    
    // 预约咨询实际接口
    // public static final String GET_AVAILABLE_TIMES = baseUrl + "getAvailableTimes";     // 获取医生可预约时间
    // public static final String CREATE_APPOINTMENT = baseUrl + "createAppointment";      // 创建预约
}
