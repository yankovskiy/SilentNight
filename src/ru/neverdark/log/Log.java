package ru.neverdark.log;

public class Log {
    /** Key for logging in the LogCat */
    private static final String APP = "ru.neverdark.silentnight";
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
            android.util.Log.i(APP, message);
        }
    }
}
