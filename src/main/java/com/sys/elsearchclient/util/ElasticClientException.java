package com.sys.elsearchclient.util;

public class ElasticClientException extends RuntimeException {

    public ElasticClientException(String message) {
        super(message);
    }

    public ElasticClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
