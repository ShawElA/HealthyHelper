package com.example.healthyolder.bean;

public class Urls {
    //手机热点
    //public static final String baseUrl = "http://192.168.43.144:8070/";
    //校园网
    public static final String baseUrl = "http://172.28.128.238:8070/";
    public static final String GPTURL = "https://api.rcouyi.com/v1/completions";

    public static final String LOGIN = baseUrl + "login";                   //登录
    public static final String REGISTER = baseUrl + "register";                   //注册
    public static final String USERDATA = baseUrl + "getPersonInfo";              //获取用户个人信息
    public static final String EDITDATA = baseUrl + "editPersonInfo";             //修改用户个人信息
    public static final String EDITPASSWORD = baseUrl + "editPassword";            //修改用户密码
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
}
