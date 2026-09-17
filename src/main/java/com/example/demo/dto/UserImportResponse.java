package com.example.demo.dto;

public class UserImportResponse {

    private boolean success;

    private int importedCount;

    private String message;


    public UserImportResponse() {
    }


    public UserImportResponse(
            boolean success,
            int importedCount,
            String message) {

        this.success = success;
        this.importedCount = importedCount;
        this.message = message;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public int getImportedCount() {
        return importedCount;
    }

    public void setImportedCount(
            int importedCount) {

        this.importedCount = importedCount;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(
            String message) {

        this.message = message;
    }
}