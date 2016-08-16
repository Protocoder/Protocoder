package org.protocoderrunner.apprunner;

/**
 * Created by biquillo on 14/08/16.
 */
public class PermissionNotGrantedException extends RuntimeException {

    public PermissionNotGrantedException() {}

    //Constructor that accepts a message
    public PermissionNotGrantedException(String message) {
        super("You need to grant permissions to use the " + message);
    }

    public PermissionNotGrantedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionNotGrantedException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
}
