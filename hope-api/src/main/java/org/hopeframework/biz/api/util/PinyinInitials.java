package org.hopeframework.biz.api.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinInitials {
    public static String getPinyinInitials(String chinese) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.toCharArray();
        for (char c : chars) {
            if (Character.isWhitespace(c)) {
                continue;
            }
            String[] pinyin = new String[0];
            try {
                pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
            if (pinyin != null) {
                if(pinyin.length>0) {
                    sb.append(pinyin[0].charAt(0));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString().replaceAll("[^a-zA-Z]", "").toLowerCase();
    }

    public static void main(String[] args)  {
        String chinese = "白鲢";
        String initials = getPinyinInitials(chinese);
        System.out.println(initials); // 输出 "ZHRMGHG"
    }
}
