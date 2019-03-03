package com.wmj.game.common.errorcode;

public enum SystemErrorCode {
    SUCCESS(0, "成功"),
    //
    ;
    int code;
    String msg;

    SystemErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
