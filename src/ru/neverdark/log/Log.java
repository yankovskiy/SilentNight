package ru.neverdark.log;

public class Log {
    /** true if DEBUG enabled or false if DEBUG disable */
    private static final boolean DEBUG = true;

    /**
     * Function logged message to the LogCat as information message
     * 
     * @param message
     *            message for logging
     */
    public static void message(String message) {
        if (DEBUG == true) {
            Throwable stack = new Throwable().fillInStackTrace();
            StackTraceElement[] trace = stack.getStackTrace();
            String APP = trace[1].getClassName() + "." + trace[1].getMethodName() + ":" + trace[1].getLineNumber();
            android.util.Log.i(APP, message);
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
            Throwable stack = new Throwable().fillInStackTrace();
            StackTraceElement[] trace = stack.getStackTrace();
            String APP = trace[1].getClassName() + "." + trace[1].getMethodName() + ":" + trace[1].getLineNumber();
            android.util.Log.i(APP, message);
        }
    }
    
    
}
