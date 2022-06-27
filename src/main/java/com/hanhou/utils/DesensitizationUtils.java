package com.hanhou.utils;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import com.hanhou.annotation.DesensitizationField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 脱敏工具类
 *
 * @author chenguoku
 * @version 0.0.1
 * @ClassName DesensitizationUtils.java
 * @createTime 2022年03月10日
 */
public class DesensitizationUtils {

    private DesensitizationUtils() {
    }

    /**
     * 对象脱敏
     */
    public static void format(Object obj) {
        DesensitizationUtils.formatMethod(obj);
    }

    /**
     * 判断字段类型，进行脱敏处理
     */
    private static void formatMethod(Object obj) {
        if (obj == null || isPrimitive(obj.getClass())) {
            return;
        }
        if (obj.getClass().isArray()) {
            for (Object object : (Object[]) obj) {
                formatMethod(object);
            }
        } else if (Collection.class.isAssignableFrom(obj.getClass())) {
            for (Object o : ((Collection) obj)) {
                formatMethod(o);
            }
        } else if (Map.class.isAssignableFrom(obj.getClass())) {
            for (Object o : ((Map) obj).values()) {
                formatMethod(o);
            }
        } else {
            objFormat(obj);
        }
    }

    /**
     * 对象脱敏
     */
    private static void objFormat(Object obj) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            DesensitizationField annotation = field.getAnnotation(DesensitizationField.class);
            if (annotation == null) {
                continue;
            }
            if (isPrimitive(field.getType())) {
                fieldSetSensitiveValue(obj, field);
            } else {
                //对象类型进行下一级处理
                ReflectionUtils.makeAccessible(field);
                Object fieldValue = ReflectionUtils.getField(field, obj);
                if (fieldValue != null) {
                    formatMethod(fieldValue);
                }
            }
        }
    }

    /**
     * 进行脱敏处理的方法
     */
    private static void fieldSetSensitiveValue(Object obj, Field field) {
        DesensitizationField annotation = field.getAnnotation(DesensitizationField.class);
        if (annotation != null && String.class.isAssignableFrom(field.getType())) {
            ReflectionUtils.makeAccessible(field);
            Object fieldValue = ReflectionUtils.getField(field, obj);
            if (fieldValue != null) {
                String DesensitizationString = getDesensitizationString(annotation, fieldValue);
                ReflectionUtils.setField(field, obj, DesensitizationString);
            }
        }
    }

    private static String getDesensitizationString(DesensitizationField annotation, Object fieldValue) {
        String valueStr = String.valueOf(fieldValue);
        if (StringUtils.isNotBlank(valueStr)) {
            switch (annotation.type()) {
                case NAME: {
                    valueStr = name(valueStr);
                    break;
                }
                case ID_NO: {
                    valueStr = idNo(valueStr);
                    break;
                }
                case MOBILE_PHONE: {
                    // 普通替换字符串脱敏
                    valueStr = mobilePhone(valueStr);
                    break;
                }
                case ADDRESS: {
                    valueStr = address(valueStr);
                    break;
                }
                case EMAIL: {
                    valueStr = email(valueStr);
                    break;
                }
                case BANK_CARD: {
                    valueStr = bankCard(valueStr);
                    break;
                }
                case PASSWORD: {
                    valueStr = password(valueStr);
                    break;
                }
                case BIRTHDAY: {
                    valueStr = birthday(valueStr);
                    break;
                }
                case FIXED_PHONE: {
                    valueStr = fixedPhone(valueStr);
                    break;
                }
                default:
                    break;
            }
        }
        return valueStr;
    }

    /**
     * 基本数据类型和String类型判断
     */
    public static boolean isPrimitive(Class<?> clz) {
        try {
            if (String.class.isAssignableFrom(clz) || Date.class.isAssignableFrom(clz) || BigDecimal.class.isAssignableFrom(clz) || LocalDateTime.class.isAssignableFrom(clz)
                    || LocalDate.class.isAssignableFrom(clz) || LocalTime.class.isAssignableFrom(clz) || Year.class.isAssignableFrom(clz)
                    || YearMonth.class.isAssignableFrom(clz) || Month.class.isAssignableFrom(clz) || clz.isPrimitive()) {
                return true;
            } else {
                return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 【中文姓名】中文：保留首字；英文：保留首词，其余星号
     *
     * @param fullName 姓名
     * @return 脱敏后姓名
     */
    public static String name(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return "";
        }
        if (isChinese(fullName)) {
            String name = StringUtils.left(fullName, 1);
            return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
        }

        String[] split = fullName.split(" ");
        String englishName = split[0];
        for (int i = 1; i < split.length; i++) {
            String s = StringUtils.rightPad("", StringUtils.length(split[i]), "*");
            englishName += (" " + s);
        }
        return englishName;
    }

    /**
     * 判断是不是中文名
     *
     * @param str 姓名
     * @return 是否为中文名
     * @author chenguoku
     * @date 2021/9/18
     */
    public static boolean isChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find())
            flg = true;

        return flg;
    }

    /**
     * 【身份证号】身份证号码显示头三位和末两位(110**************22)；其他证件号码使用缺省规则（显示前1/3和后1/3)
     */
    public static String idNo(String id) {
        if (StringUtils.isBlank(id)) {
            return "";
        }

        //身份证
        if (IdcardUtil.isValidCard(id)) {
            return cn.hutool.core.util.DesensitizedUtil.idCardNum(id, 3, 2);
        }

        //其他证件号
        int num = id.length() / 3;
        if (num * 2 > id.length()) {
            return id;
        }

        return StrUtil.hide(id, num, id.length() - num);
    }

    /**
     * 【手机号码】前三位，后四位，其他隐藏，比如135****6810
     *
     * @param num 手机号
     * @return 返回隐藏中间4位的手机号
     */
    public static String mobilePhone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }

        return cn.hutool.core.util.DesensitizedUtil.mobilePhone(num);
    }

    /**
     * 【固定电话】前四位，后两位
     *
     * @param num 固定电话
     * @return 脱敏后的固定电话
     */
    public static String fixedPhone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }

        return cn.hutool.core.util.DesensitizedUtil.fixedPhone(num);
    }


    /**
     * 【地址】隐藏门牌号（数字隐藏）
     *
     * @param address 地址
     * @return 脱敏后的地址
     */
    public static String address(String address) {
        if (StringUtils.isBlank(address)) {
            return "";
        }

        return address.replaceAll("[0-9]", "*");
    }

    /**
     * 【电子邮箱】
     * "@前面的字符显示3位，3位后显示3个 *，@后面完整显示如：con***@163.com
     * 如果少于三位，则全部显示，@前加***，例如tt@163.com则显示为tt***@163.com"
     *
     * @param email 电子邮箱
     * @return 脱敏后电子邮箱
     */
    public static String email(String email) {
        if (StringUtils.isBlank(email)) {
            return "";
        }

        int index = StringUtils.indexOf(email, "@");
        if (index <= 1) {
            return email;
        } else {
            String[] split = email.split("@");
            if (split.length != 2) {
                return email;
            }

            String prefix = "";
            if (split[0].length() < 3) {
                prefix = split[0];
            } else {
                prefix = split[0].substring(0, 3);
            }

            return prefix + "***@" + split[1];
        }
    }

    /**
     * 【银行卡号】前六位，后四位，其他用星号隐藏每位1个星号，比如：6222600**********1234
     */
    public static String bankCard(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }

        return cn.hutool.core.util.DesensitizedUtil.bankCard(cardNum);
    }

    /**
     * 【密码】密码的全部字符都用*代替，比如：******
     */
    public static String password(String password) {
        if (StringUtils.isBlank(password)) {
            return "";
        }

        return cn.hutool.core.util.DesensitizedUtil.password(password);
    }

    /**
     * 【生日】显示年、日，隐藏月。如：1986-**-28
     *
     * @param birthday 生日：格式 yyyy-MM-dd
     * @return 脱敏后的生日
     */
    public static String birthday(String birthday) {
        if (StringUtils.isBlank(birthday)) {
            return "";
        }

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            formatter.parse(birthday);
        } catch (ParseException e) {
            return birthday;
        }

        String[] split = birthday.split("-");
        String newBirthday = split[0] + "-**-" + split[2];
        return newBirthday;
    }

}
