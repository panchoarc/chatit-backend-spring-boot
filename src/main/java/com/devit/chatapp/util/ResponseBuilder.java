package com.devit.chatapp.util;

import java.util.Collections;
import java.util.Map;


public class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static <T> ResponseAPI<T> success(String message, T data) {

        ResponseAPI<T> apiResponse = new ResponseAPI<>();
        apiResponse.setStatus(ResponseAPI.Status.SUCCESS);
        apiResponse.setMessage(message);
        apiResponse.setData(data);
        return apiResponse;
    }

    public static <T> ResponseAPI<T> successPaginated(String message, T data, Pagination pagination) {
        ResponseAPI<T> apiResponse = new ResponseAPI<>();
        apiResponse.setStatus(ResponseAPI.Status.SUCCESS);
        apiResponse.setMessage(message);
        apiResponse.setData(data);
        if (pagination != null) {
            apiResponse.setPagination(pagination);
        }
        return apiResponse;
    }


    public static <T> ResponseAPI<T> error(String message, Map<String, String> errors) {

        ResponseAPI<T> apiResponse = new ResponseAPI<>();
        apiResponse.setStatus(ResponseAPI.Status.ERROR);
        apiResponse.setMessage(message);
        apiResponse.setErrors(errors);
        return apiResponse;
    }

    public static <T> ResponseAPI<T> error(String message, String field, String errorMessage) {

        ResponseAPI<T> apiResponse = new ResponseAPI<>();
        apiResponse.setStatus(ResponseAPI.Status.ERROR);
        apiResponse.setMessage(message);
        apiResponse.setErrors(Collections.singletonMap(field, errorMessage));
        return apiResponse;
    }
}
