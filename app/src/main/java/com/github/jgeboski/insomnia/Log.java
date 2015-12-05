package com.github.jgeboski.insomnia;

public class Log
{
    public static final String NAME = Log.class.getName();

    public static void debug(String format, Object ... args)
    {
        log(android.util.Log.DEBUG, format, args);
    }

    public static void error(String format, Object ... args)
    {
        log(android.util.Log.ERROR, format, args);
    }

    public static void info(String format, Object ... args)
    {
        log(android.util.Log.INFO, format, args);
    }

    public static void verbose(String format, Object ... args)
    {
        log(android.util.Log.VERBOSE, format, args);
    }

    public static void warn(String format, Object ... args)
    {
        log(android.util.Log.WARN, format, args);
    }

    private static void log(int priority, String format, Object ... args)
    {
        Thread thread = Thread.currentThread();
        StackTraceElement stack[] = thread.getStackTrace();
        StackTraceElement frame = null;

        for (int i = 0; i < stack.length; i++) {
            String name = stack[i].getClassName();

            if (name.equals(NAME)) {
                frame = stack[i + 2];
                break;
            }
        }

        String klass[] = frame.getClassName().split("\\.");
        String tag = klass[klass.length - 1];
        String msg = String.format(format, args);
        android.util.Log.println(priority, tag, msg);
    }
}
