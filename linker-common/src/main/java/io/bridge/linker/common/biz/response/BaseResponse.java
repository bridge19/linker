package io.bridge.linker.common.biz.response;

import io.bridge.linker.common.biz.enums.EcontractEnumBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author : canghai
 * @version V1.0
 * @Project: econtract-parent
 * @Package io.bridge.econtract.service.response
 * @Description: TODO
 * @date Date : 2020年03月09日 15:27
 */
@ApiModel("Response结构说明")
public class BaseResponse<T> {
    @ApiModelProperty("错误码")
    protected Integer code;
    @ApiModelProperty("错误码描述")
    protected String message;
    @ApiModelProperty("业务数据")
    protected T data;

    public static <T> BaseResponse<T> success(T d) {
        BaseResponse<T> result = new BaseResponse(0, null,d);
        return result;
    }
    public static <T> BaseResponse<T> success() {
        BaseResponse<T> result = new BaseResponse(0, null,null);
        return result;
    }

    public static <T> BaseResponse<T> fail(EcontractEnumBase e) {
        BaseResponse<T> result = new BaseResponse(e);
        return result;
    }

    public static <T> BaseResponse<T> fail(EcontractEnumBase e, T data) {
        BaseResponse<T> result = new BaseResponse(e, data);
        return result;
    }

    public BaseResponse(EcontractEnumBase e, T data) {
        this.code = e.getNCode();
        this.message = e.message();
        this.data = data;
    }

    public BaseResponse(EcontractEnumBase e) {
        this.code = e.getNCode();
        this.message = e.message();
        this.data = null;
    }
    public BaseResponse(int code,String msg, T data){
        this.code = code;
        this.message = msg;
        this.data = data;
    }
    public BaseResponse(){}

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
