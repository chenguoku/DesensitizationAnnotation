package com.hanhou.annotation;

import java.lang.annotation.*;

/**
 * 脱敏注解 - 用于修饰方法
 *
 * @author chenguoku
 * @version 0.0.1
 * @ClassName DeSensitiveField.java
 * @createTime 2022年03月10日
 */
@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DesensitizationMethod {

}
