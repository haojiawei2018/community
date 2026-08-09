package org.hopeframework.core.response;


import org.hopeframework.core.constant.ResponseConst;

public class ResultUtil {

    /**
     * 程序默认错误状态码 500
     * @param message 错误信息
     * @return
     */
    public static RespBody error(String message){
        return new RespBody(500,message);
    }

    /**
     * 错误返回对象
     * @param code 状态码
     * @param message 错误信息
     * @return
     */
    public static RespBody error(int code,String message){
        return new RespBody(code,message);
    };

    /**
     * 返回错误消息
     * @return 操作失败
     */
    public static RespBody error()
    {
        return new RespBody(500,"操作失败");
    }

    /**
     * 错误返回对象
     * @param code 状态码
     * @param message 错误信息
     * @param data  数据
     * @return
     */
    public static RespBody error(int code,String message,Object data){
        return new RespBody(code,message,data);
    };

    /**
     * 操作成功
     * @param message 成功信息
     * @return
     */
    public static RespBody success(String message){
        return new RespBody(ResponseConst.SUCCESS,message);
    };

    public static RespBody success(Object data)
    {
        return new RespBody(ResponseConst.SUCCESS,"操作成功",data);
    }

    /**
     * 返回成功消息
     * @return 成功消息
     */
    public static RespBody success()
    {
        return new RespBody(ResponseConst.SUCCESS,"操作成功");
    }

    /**
     * 操作成功
     * @param message 成功信息
     * @param data  返回数据
     * @return
     */
    public static RespBody success(String message,Object data){
        return new RespBody(ResponseConst.SUCCESS, message,data);
    };

    public static RespBody ok(Object data){
        return new RespBody(ResponseConst.SUCCESS,"请求成功",data);
    };

    public static RespBody ok(){
        return new RespBody(ResponseConst.SUCCESS,"请求成功",null);
    };

}
