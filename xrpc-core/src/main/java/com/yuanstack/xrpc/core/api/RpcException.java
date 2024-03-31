package com.yuanstack.xrpc.core.api;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Sylvan
 * @date 2024/03/31  13:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RpcException extends RuntimeException {

    private String errorCode;

    private RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    // X ==> 技术类异常
    // Y ==> 业务类异常
    // Z ==> unknown，暂时搞不清楚，再归类到X或Y
    public static final String SocketTimeOutEx = "X001" + "-" + "http_invoke_timeout";
    public static final String NoSuchMethodEx = "X002" + "-" + "method_not_exists";
    public static final String UnknownEx = "Z001" + "-" + "UnknownEx";

}
