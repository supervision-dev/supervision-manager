package com.rmbank.supervision.exception;

/**
 * 定义一个接口可以获取异常的code和message,这样所有的enum都可以继承这个接口
 * Created by sam on 2016/3/21.
 */
public interface IExceptionEnumRoot {
    /**
     * 获取异常编码
     *
     * @return
     */
    String getCode();

    /**
     * 获取异常信息
     *
     * @return
     */
    String getMessage();
}
