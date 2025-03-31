package org.file.apiResponse;

public class ApiResponse<T> {
    private boolean valid;
    private T data;
    private String err;
    private String message;
    private int code;

    // Constructors
    public ApiResponse(boolean valid, T data, String message) {
        this.valid = valid;
        this.data = data;
        this.message = message;
    }
    public ApiResponse(boolean valid, T data, String message, String err) {
        this.valid = valid;
        this.data = data;
        this.message = message;
        this.err = err;
    }

    public ApiResponse(boolean valid, T data, String message, String err, int _code) {
        this.valid = valid;
        this.data = data;
        this.message = message;
        this.err = err;
        this.code = _code;
    }


    public ApiResponse(boolean valid, T data){
        this.valid = valid;
        this.data = data;
    }

    public ApiResponse(boolean valid, String err, int _code){
        this.valid = valid;
        this.err = err;
        this.code = _code;
    }

    // Getters and setters (important for JSON serialization)
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}