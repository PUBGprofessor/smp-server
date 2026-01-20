package com.pubg.smp.smpinfo.util;

import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 实体映射工具类 (适配 Java 21)
 * 使用 Spring BeanUtils 替代 Orika，彻底解决 InaccessibleObjectException
 */
public class BeanCopyUtils {

    /**
     * 将两个实体合并为一个DTO
     *
     * @param input1 第一个输入实体
     * @param input2 第二个输入实体
     * @param result DTO类型
     */
    @NonNull
    public static <R, A, B> R doubleEntity2Dto(@Nullable A input1, @Nullable B input2, @NonNull Class<R> result) {
        try {
            R r = result.getDeclaredConstructor().newInstance();
            if (input1 != null) {
                BeanUtils.copyProperties(input1, r);
            }
            if (input2 != null) {
                BeanUtils.copyProperties(input2, r);
            }
            return r;
        } catch (Exception e) {
            throw new RuntimeException("实体转换失败: " + e.getMessage());
        }
    }

    /**
     * A 实体 转换 B 实体
     */
    public static <A, B> B a2b(A a, Class<B> bClass) {
        if (a == null) return null;
        try {
            B b = bClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(a, b);
            return b;
        } catch (Exception e) {
            throw new RuntimeException("实体转换失败: " + e.getMessage());
        }
    }
}