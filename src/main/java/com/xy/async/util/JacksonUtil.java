package com.xy.async.util;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.util.StrUtil;

/**
 * Jackson 工具类
 *
 * @author xiongyan
 * @date 2020/06/01
 */
public final class JacksonUtil {

    /**
     * ObjectMapper
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 序列化时候统一日期格式
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 设置null时候不序列化(只针对对象属性)
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 反序列化时，属性不存在的兼容处理
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 单引号处理
        OBJECT_MAPPER.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 不区分大小写
        OBJECT_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    /**
     * 对象序列化json字符串
     *
     * @param object
     * @return
     */
    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json字符串反序列化JsonNode
     *
     * @param json
     * @return
     */
    public static Serializable toJsonNode(String json) {
        try {
            if (StrUtil.isEmpty(json)) {
                return json;
            }
            return (Serializable) OBJECT_MAPPER.readTree(json);
        } catch (Exception e) {
            return json;
        }
    }

    /**
     * json字符串反序列化对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json字符串反序列化对象
     *
     * @param json
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json字符串反序列化对象集合
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * toObjects
     * 
     * @param json
     * @param types
     * @return
     */
    public static Object[] toObjects(String json, Type[] types) {
        try {
            Object[] objects = new Object[types.length];
            JsonNode jsonNode = OBJECT_MAPPER.readTree(json);

            for (int i = 0; i < types.length; i++) {
                JsonNode pJsonNode = jsonNode.get(i);
                Type pType = types[i];
                String pJson = pJsonNode.toString();

                if (pType instanceof ParameterizedType) {
                    objects[i] = OBJECT_MAPPER.readValue(pJson, new TypeReferenceExt<TypeReferenceExt<Object>>(pType));
                } else {
                    objects[i] = OBJECT_MAPPER.readValue(pJson, (Class) pType);
                }
            }
            return objects;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class TypeReferenceExt<T> extends TypeReference<T> {

        protected Type parameterized;

        public TypeReferenceExt(Type parameterized) {
            this.parameterized = parameterized;
        }

        @Override
        public Type getType() {
            return parameterized;
        }
    }

}
