package top.lingkang.finalserver.example.test03.dto;

import com.alibaba.fastjson2.JSONObject;

import java.io.Serializable;

/**
 * @author lingkang
 * 2023/1/12
 **/
public class ResponseResult<T> implements Serializable {
    private int code = 0;
    private String msg;
    private T data;

    public ResponseResult() {
    }

    public ResponseResult(String msg) {
        this.msg = msg;
    }

    public ResponseResult(String msg, T data) {
        this.msg = msg;
        this.data = data;
    }

    public ResponseResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult ok(String msg) {
        this.msg = msg;
        return this;
    }

    public ResponseResult ok(T data) {
        this.data = data;
        return this;
    }

    public ResponseResult ok(String msg,T data) {
        this.data = data;
        this.msg = msg;
        return this;
    }

    public ResponseResult fail(String msg) {
        code = 1;
        this.msg = msg;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
