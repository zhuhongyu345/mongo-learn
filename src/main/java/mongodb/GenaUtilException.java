package mongodb;

public class GenaUtilException extends Exception {
    private static final long serialVersionUID = 8838038369463555518L;

    /**
     * Create a new FatalBeanException with the specified message.
     *
     * @param msg the detail message
     */
    public GenaUtilException(String msg) {
        super(msg);
    }

    /**
     * Create a new FatalBeanException with the specified message and root
     * cause.
     *
     * @param msg the detail message
     * @param ex  the root cause
     */
    public GenaUtilException(String msg, Throwable ex) {
        super(msg, ex);
    }
}