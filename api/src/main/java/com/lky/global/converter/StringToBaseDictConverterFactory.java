package com.lky.global.converter;

import com.lky.commons.base.BaseDict;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * 字符串转枚举，请求入参
 *
 *
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/12
 */
public class StringToBaseDictConverterFactory implements ConverterFactory<String, BaseDict> {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseDict> Converter<String, T> getConverter(Class<T> aClass) {
        if (!aClass.isEnum()) {
            throw new UnsupportedOperationException("只支持转换到枚举类型");
        }
        return new StringToBaseDictConverter(aClass);
    }

    private class StringToBaseDictConverter<T extends BaseDict> implements Converter<String, T> {

        private final Class<T> enumType;

        public StringToBaseDictConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String s) {
            for (T t : enumType.getEnumConstants()) {
                if (s.equals(t.getKey())) {
                    return t;
                }
            }
            return null;
        }
    }
}
