package com.shiftly.dto;

public class SignUpResponse {
    private boolean success;
    private String error;

    public SignUpResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }



    public boolean getSuccess() {return  success;}
    public String getError() {return  error;}

}
