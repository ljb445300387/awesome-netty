package com.anthonyzero.common;

import lombok.Data;

/**
 * netty客户端接收的结果
 */
@Data
public class Response {
    private String requestId;
    private Throwable error;
    //返回结果（客户端需要的结果）
    private Object result;
}
