package com.hanhou.type;

/**
 * description 脱敏类型枚举
 *
 * @author chenguoku
 * @version 0.0.1
 * @ClassName DesensitizationTypeEnum.java
 * @createTime 2022年03月10日
 */
public enum DesensitizationTypeEnum {
    /**
     * 姓名
     */
    NAME,
    /**
     * 身份证号
     */
    ID_NO,
    /**
     * 手机号
     */
    MOBILE_PHONE,
    /**
     * 地址
     */
    ADDRESS,
    /**
     * 电子邮件
     */
    EMAIL,
    /**
     * 银行卡
     */
    BANK_CARD,
    /**
     * 密码
     */
    PASSWORD,
    /**
     * 出生日期
     */
    BIRTHDAY,
    /**
     * 固定电话
     */
    FIXED_PHONE,
    /**
     * 默认不处理
     */
    TYPE_DEFAULT;
}