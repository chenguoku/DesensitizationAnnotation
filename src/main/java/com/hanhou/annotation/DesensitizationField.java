package com.hanhou.annotation;

import com.hanhou.type.DesensitizationTypeEnum;

import java.lang.annotation.*;

/**
 * 脱敏注解 - 用于修饰字段
 *
 * @author chenguoku
 * @version 0.0.1
 * @ClassName DesensitizationField.java
 * @createTime 2022年03月10日
 */
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DesensitizationField {

    /**
     * 脱敏类型(规则)
     */
    DesensitizationTypeEnum type() default DesensitizationTypeEnum.TYPE_DEFAULT;

}
