package com.parashift.modelGen.args;

/**
 * Created by ye.yan on 5/7/2014.
 */
public class OptionException extends Exception {
    public OptionException() {
    }

    public OptionException(String message) {
        super(message);
    }

    public OptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptionException(Throwable cause) {
        super(cause);
    }

    public OptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
