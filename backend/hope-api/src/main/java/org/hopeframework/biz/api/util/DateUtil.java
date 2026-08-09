package org.hopeframework.biz.api.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.hopeframework.utils.HttpClientUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: haojiawei
 * @Description:
 */
public class DateUtil {
    /**
     * 年月日时分秒毫秒(无下划线) yyyyMMddHHmmssSSS\
     *
     */
    public static final String dtLongs = "yyyyMMddHHmmssSSS";

    /**
     * 年月日时分秒(无下划线) yyyyMMddHHmmss
     */
    public static final String dtLong = "yyyyMMddHHmmss";

    /**
     * 完整时间 yyyy-MM-dd HH:mm:ss
     */
    public static final String simple = "yyyy-MM-dd HH:mm:ss";

    /**
     * 年月日   yyyy-MM-dd
     */
    public static final String dtShort_ = "yyyy-MM-dd";

    /**
     * 年月日(无下划线) yyyyMMdd
     */
    public static final String dtShort = "yyyyMMdd";

    /**
     * 时分秒(无下划线) HHmmss
     */
    public static final String dtTime = "HHmmss";

    /**
     * 获当前日期
     *
     * @param date
     * @param dateFormat
     * @return String
     */
    public static String getCurrentDate(String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(new Date());
    }

    /**
     * 获取自定义格式化日期
     *
     * @param date
     * @param dateFormat
     * @return String
     */
    public static String getDateFormat(Date date, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(date);
    }

    /**
     * 获取当前日期前一天
     * 格式：YYYYMMDD
     */
    public static String getCurrentDateFront() {
        String strDate = new SimpleDateFormat("yyyyMMdd").format(addDays(new Date(), -1));
        strDate = strDate.substring(0, 4) + strDate.substring(4, 6) + strDate.substring(6);
        return strDate;
    }

    /**
     * 获取当前日期后一天
     * 格式：YYYYMMDD
     */
    public static String getCurrentDateYesterday() {
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(addDays(new Date(), +1));
        strDate = strDate.substring(0, 4) + strDate.substring(4, 6) + strDate.substring(6);
        return strDate;
    }

    /**
     * 按日加减日期
     *
     * @param date：日期
     * @param num：要加减的日数
     * @return：成功，则返回加减后的日期；失败，则返回null
     */
    public static Date addDays(Date date, int num) {
        if (date == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, num);

        return c.getTime();
    }

    /**
     * 按月加减日期
     *
     * @param date：日期
     * @param num：要加减的月数
     * @return：成功，则返回加减后的日期；失败，则返回null
     */
    public static Date addMonths(Date date, int num) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, num);
        return c.getTime();
    }

    /**
     * 按年加减日期
     *
     * @param date：日期
     * @param num：要加减的年数
     * @return：成功，则返回加减后的日期；失败，则返回null
     */
    public static Date addYears(Date date, int num) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, num);
        return c.getTime();
    }

    /**
     * 按秒 加减日期
     *
     * @param date：日期
     * @param num：要加减的秒
     * @return：成功，则返回加减后的日期；失败，则返回null
     */
    public static Date addSeconds(Date date, int num) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, num);
        return c.getTime();
    }

    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
     */
    public static int getRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }



    /**
     * 检查日期字符串是否合法
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return 布尔
     */
    //'yyyyMMdd'  'HHmmss' 所以年月日不是yyyymmdd
    @SuppressWarnings("unused")
    public static boolean isValidDate(String dateStr, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setLenient(false);//来强调严格遵守该格式
        Date date = null;
        try {
            date = df.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 取当前时间 格式为 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getDateNow() {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return myFormat.format(calendar.getTime());
    }

    /**
     * 取当前时间 格式为 yyyyMMdd
     *
     * @return
     */
    public static String getDateNowYmd() {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        return myFormat.format(calendar.getTime());
    }

    /**
     * 取日期是星期几
     *
     * @return
     */
    public static String formatStringToDate(String time) {
       /* SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Integer WeekToday=controlSaleMapper.getWeekToday(formatter.format(date));
        return myFormat.format(calendar.getTime());*/
        return "";
    }

    /**
     * @return
     * @功能描述：生成msgId
     */
    public static String getMsgId() {
        int ran = getRandom(10);
        String msgId = getCurrentDate(dtLong) + "-" + String.valueOf(ran);
        return msgId;
    }

   /* public static void main(String[] args) throws Exception {
        Map paramMap=new HashMap<>();
        //name=张三&cardNo=341622123456784317&token=xxx&version=v1&businessId=xxx&timestamp=1638180222235&nonce=xxx&secretId=xxx&signature=xxx
        paramMap.put("name","郝佳伟");
        paramMap.put("cardNo","140202199202186030");
        paramMap.put("token","98a64bf9b4df4277b12deb821a7ea382");
        paramMap.put("version","v1");
        paramMap.put("businessId","f385e230fe494e74afa517b46399590a");
        Long time=new Date().getTime();
        paramMap.put("timestamp",time.toString());
        paramMap.put("nonce","123");
        paramMap.put("secretId","dd5730a43e12c1d31f345863c855efc4");
        paramMap.put("signature",genSignature("f5f5a677fcf8bd9181bb09b8c5f8eb0d",paramMap));
        String out= HttpClientUtils.postParam2String("https://verify.dun.163.com/v1/liveperson/audit",paramMap,"UTF-8");
        JSONObject jsonObject = JSON.parseObject(out);
        String code = (jsonObject.get("code").toString());
        if(code.equals("200")){
            String result = (jsonObject.get("result").toString());
            JSONObject resultObject = JSON.parseObject(result);
            String status = (resultObject.get("status").toString());
            if(status.equals("1")){
                System.out.println("通过");
            }else{
                System.out.println("不通过");
            }
        }else{
            String msg = (jsonObject.get("msg").toString());
            System.out.println(msg);
        }
        System.out.println(out);
    }
*/
    /**
     * 生成签名信息
     * @param secretKey 产品私钥
     * @param params 接口请求参数名和参数值map，不包括signature参数名
     * @return
     */
    public static String genSignature(String secretKey, Map<String, String> params) throws UnsupportedEncodingException {
        // 1. 参数名按照ASCII码表升序排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 2. 按照排序拼接参数名与参数值
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key).append(params.get(key));
        }
        // 3. 将secretKey拼接到最后
        sb.append(secretKey);

        // 4. MD5是128位长度的摘要算法，转换为十六进制之后长度为32字符
        return DigestUtils.md5Hex(sb.toString().getBytes("UTF-8"));
    }


    public static final String REGEX_MOBILE = "(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}";

    public static String regexMobile(String content){
        Pattern p = Pattern.compile(REGEX_MOBILE);
        Matcher m = p.matcher(content);
        String paramStr = new String(content);
        while (m.find()) { //一定需要先查找再调用group获取电话号码
            String group = m.group();
            paramStr = paramStr.replaceAll(group, group.substring(0,3)+"****"+group.substring(7,11));
        }

        return paramStr;
    }

    public static void main(String[] args){
        System.out.println(regexMobile("招B2，驾驶员3名 车型：9.6米单桥 线路：陕西省内 薪水：放假前付清 不拖欠 春节期间薪水翻倍 要求：来打酱油的免谈 必须有真实的资格证 必须有真实快递行业驾驶经验2年以上。必须要懂手机APP操作 有较强的安全责任心 无不良嗜好 无重大交通事故案底 熟练快递行业操作流程 服从公司管理，吃苦耐劳。联系电话：19945134555\n" +
                " \n" +
                "招聘A2司机，西安至银川 西安至郑州，工资面议，联系方式18712345678\n" +
                " \n" +
                "招聘A2 侧翻半挂熟练司机，西安周边 或者 柞水之六村堡 每天两趟 工资月结，工资12000-12500，联系方式13891441877\n"));
    }


}
