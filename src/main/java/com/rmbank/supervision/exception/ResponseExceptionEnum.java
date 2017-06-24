package com.rmbank.supervision.exception;

/**
 * Created by sam on 2016/3/25.
 */
public enum ResponseExceptionEnum implements IExceptionEnumRoot{

    NULL_USER_PASSWD("RE-0001-001","用户名或密码不能为空"),
    USER_DISABLED("RE-0001-002","帐号异常，请联系客服"),
    NoAuthorization("RE-0001-003","对不起，您无权进行此项操作"),
    PasswordNotCorrect("RE-0001-004","用户名或者密码不正确，请重新输入"),
    USER_NO_REGISTER("RE-0001-005","该用户尚未注册，请先注册后再登录"),
    SERVER_ERROR("RE-0001-006","服务器内部错误"),
    INVALID_TOKEN("RE-0001-007","无效的token"),
    DEVICE_LOGIN_NOT_ALLOW("RE-0001-008","该账号为指定设备登录，禁止其他设备登录"),
    ;

    ResponseExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private String code;

    private String message;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
