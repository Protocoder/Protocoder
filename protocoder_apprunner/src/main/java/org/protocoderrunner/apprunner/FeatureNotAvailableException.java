package org.protocoderrunner.apprunner;

/**
 * Created by biquillo on 14/08/16.
 */
public class FeatureNotAvailableException extends RuntimeException {

    public FeatureNotAvailableException() {}

    //Constructor that accepts a message
    public FeatureNotAvailableException(String message) {
        super("your device doesn't have " + message + " is not ");
    }

    public FeatureNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeatureNotAvailableException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }

}
