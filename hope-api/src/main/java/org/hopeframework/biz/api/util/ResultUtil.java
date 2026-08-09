package org.hopeframework.biz.api.util;

import org.hopeframework.core.response.RespBody;

public class ResultUtil {
    /**
     * 成功不带数据
     * @return
     */
    public static RespBody success(){
        return success(null);
    }

    /**
     * 成功带数据
     * @param object 数据
     * @return
     */
    public static RespBody success(Object object){
        RespBody result = new RespBody();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMessage(ResultEnum.SUCCESS.getMsg());
        result.setData(object);
        return result;
    }

    /**
     * 成功带提示及数据
     * @param object 数据
     * @return
     */
    public static RespBody success(String msg, Object object){
        RespBody result = new RespBody();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMessage(msg);
        result.setData(object);
        return result;
    }

    /**
     * 失败
     * @param code 错误码
     * @param msg 提示信息
     * @return
     */
    public static RespBody error(Integer code , String msg){
        RespBody result = new RespBody();
        result.setCode(code);
        result.setMessage(msg);
        return result;
    }
}
