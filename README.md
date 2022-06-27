# DesensitizationAnnotation
# 脱敏注解

> 在接口上添加脱敏注解后，敏感信息会被脱敏处理



## 使用

```java
/**
     * 测试方法
     *
     * @return 脱敏后的对象
     * @author chenguoku
     * @date 2022/3/16
     */
@DesensitizationMethod
@GetMapping("test")
public TestRespDto test() {
    TestRespDto testRespDto = new TestRespDto();
    testRespDto.setNormal("正常不脱敏文本");
    testRespDto.setName("测试姓名");
    testRespDto.setIdNo("110101199001011479");
    testRespDto.setMobile("18888888888");
    testRespDto.setAddress("北京市朝阳区测试地址3单元303");
    testRespDto.setEmail("ceshi@163.com");
    testRespDto.setBankCard("6100000000000000");
    testRespDto.setPassword("12345678");
    testRespDto.setBirthday("1990-01-01");
    testRespDto.setFixedPhone("04523000000");

    return testRespDto;
}
```

```java
@Data
public class TestRespDto {

    /**
     * 常规文本
     */
    private String normal;

    /**
     * 姓名 NAME
     */
    @DesensitizationField(type = DesensitizationTypeEnum.NAME)
    private String name;

    /**
     * 身份证号 ID_NO
     */
    @DesensitizationField(type = DesensitizationTypeEnum.ID_NO)
    private String idNo;

    /**
     * 手机号 MOBILE_PHONE
     */
    @DesensitizationField(type = DesensitizationTypeEnum.MOBILE_PHONE)
    private String mobile;

    /**
     * 地址 ADDRESS
     */
    @DesensitizationField(type = DesensitizationTypeEnum.ADDRESS)
    private String address;

    /**
     * 电子邮件 EMAIL
     */
    @DesensitizationField(type = DesensitizationTypeEnum.EMAIL)
    private String email;

    /**
     * 银行卡 BANK_CARD
     */
    @DesensitizationField(type = DesensitizationTypeEnum.BANK_CARD)
    private String bankCard;

    /**
     * 密码 PASSWORD
     */
    @DesensitizationField(type = DesensitizationTypeEnum.PASSWORD)
    private String password;

    /**
     * 出生日期 BIRTHDAY
     */
    @DesensitizationField(type = DesensitizationTypeEnum.BIRTHDAY)
    private String birthday;

    /**
     * 固定电话 FIXED_PHONE
     */
    @DesensitizationField(type = DesensitizationTypeEnum.FIXED_PHONE)
    private String fixedPhone;

}
```

## 效果

```json
{
    "normal": "正常不脱敏文本",
    "name": "测***",
    "idNo": "110101******011479",
    "mobile": "188****8888",
    "address": "北京市朝阳区测试地址*单元***",
    "email": "ces***@163.com",
    "bankCard": "6100 **** **** 0000",
    "password": "********",
    "birthday": "1990-**-01",
    "fixedPhone": "0452*****00"
}
```

