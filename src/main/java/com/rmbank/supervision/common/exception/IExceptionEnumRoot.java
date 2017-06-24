package com.rmbank.supervision.common.exception;

/**
 * 瀹氫箟涓�釜鎺ュ彛鍙互鑾峰彇寮傚父鐨刢ode鍜宮essage,杩欐牱鎵�湁鐨別num閮藉彲浠ョ户鎵胯繖涓帴鍙�
 * Created by sam on 2016/3/21.
 */
public interface IExceptionEnumRoot {
    /**
     * 鑾峰彇寮傚父缂栫爜
     *
     * @return
     */
    String getCode();

    /**
     * 鑾峰彇寮傚父淇℃伅
     *
     * @return
     */
    String getMessage();
}
