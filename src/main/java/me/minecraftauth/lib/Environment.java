package me.minecraftauth.lib;

public class Environment {

    public static final String KEY_PREFIX = "MINECRAFTAUTH_";

    public static String HOST = string("HOST", "https://minecraftauth.me");

    private static String get(String name) {
        String env = getEnv(name);
        if (env != null && !env.isEmpty()) return env;
        return getProperty(name);
    }
    private static String getEnv(String name) {
        return System.getenv(KEY_PREFIX + name);
    }
    private static String getProperty(String name) {
        return System.getProperty(KEY_PREFIX + name);
    }

    private static String string(String name) {
        return get(name);
    }
    private static String string(String name, String defaultValue) {
        String s = string(name);
        return s != null && !s.isEmpty() ? s : defaultValue;
    }
    private static Integer integer(String name) {
        return integer(name, null);
    }
    private static Integer integer(String name, Integer defaultValue) {
        try {
            return Integer.parseInt(string(name));
        } catch (Exception e) {
            return defaultValue;
        }
    }
    private static boolean bool(String name, boolean defaultValue) {
        String s = string(name);
        if (s == null || s.isEmpty()) return defaultValue;
        s = s.toLowerCase();
        return "true".equals(s) || "yes".equals(s) || "1".equals(s);
    }

}
