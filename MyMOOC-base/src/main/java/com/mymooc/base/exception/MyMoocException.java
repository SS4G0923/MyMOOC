package com.mymooc.base.exception;

public class MyMoocException extends RuntimeException{

    private String errMessage;

    public MyMoocException() {

    }

    public MyMoocException(String message){
        super(message);
        this.errMessage = message;
    }

    public static void cast(String message) {
        throw new MyMoocException(message);
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public static void cast(CommonError commonError) {
        throw new MyMoocException(commonError.getErrMessage());
    }
}
