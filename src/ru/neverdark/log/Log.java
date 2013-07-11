package ru.neverdark.log;

public class Log {
    /** true if DEBUG enabled or false if DEBUG disable */
    private static final boolean DEBUG = false;

    /**
     * Function logged message to the LogCat as information message
     * 
     * @param message
     *            message for logging
     */
    public static void message(String message) {
        if (DEBUG == true) {
            log(message);
        }
    }

    /**
     * Function logged values to the LogCat as information message
     * 
     * @param variable
     *            variable name for logging
     * @param value
     *            value of the variable
     */
    public static void variable(String variable, String value) {
        if (DEBUG == true) {
            String message = variable + " = " + value;
            log(message);
        }
    }
    
    /**
     * Logs message with class name, method name and line number
     * @param message message for logging
     */
    private static void log(String message) {
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();
        String APP = trace[2].getClassName() + "." + trace[2].getMethodName() + ":" + trace[2].getLineNumber();
        android.util.Log.i(APP, message);
    }
}
