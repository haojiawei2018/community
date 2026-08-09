package org.hopeframework.biz.api.auto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateConverter implements Converter<String, Date> {
    @Nullable
    @Override
    public Date convert(String json) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }
}
