package com.sistemmanajemenpenyewaanperlengkapanpendakian.exception;

public class DataTidakLengkapException extends Exception {

    private static final long serialVersionUID = 1L;

    public DataTidakLengkapException() {
        super("Data belum lengkap.");
    }

    public DataTidakLengkapException(String message) {
        super(message);
    }
}
