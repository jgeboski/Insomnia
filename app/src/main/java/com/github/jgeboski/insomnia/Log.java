/*
 * Copyright 2015 James Geboski <jgeboski@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
