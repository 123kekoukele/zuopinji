package com.hadluo.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具：加盐 + BCrypt 哈希，用于管理员/教师/学生密码存储与校验。
 * 新密码统一存为 BCrypt 密文；兼容已有明文密码（首次登录成功后自动升级为密文）。
 */
public final class PasswordUtils {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(10);
    /** BCrypt 密文前缀，用于判断是否为已哈希存储 */
    private static final String BCRYPT_PREFIX = "$2";

    private PasswordUtils() {}

    /**
     * 对明文密码加盐并哈希，存入数据库时调用。
     */
    public static String encode(CharSequence rawPassword) {
        if (rawPassword == null) return null;
        return ENCODER.encode(rawPassword.toString());
    }

    /**
     * 校验明文与库中密文是否匹配。
     * 若库中为 BCrypt 密文则用 BCrypt 校验；否则按明文兼容（用于旧数据），匹配成功不在此处写回库，由调用方决定是否升级。
     */
    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null && encodedPassword == null) return true;
        if (rawPassword == null || encodedPassword == null) return false;
        String raw = rawPassword.toString();
        if (encodedPassword.startsWith(BCRYPT_PREFIX)) {
            return ENCODER.matches(raw, encodedPassword);
        }
        return encodedPassword.equals(raw);
    }

    /**
     * 是否为已哈希存储（BCrypt），用于登录后是否需要写回库做“明文→密文”升级。
     */
    public static boolean isEncoded(String storedPassword) {
        return storedPassword != null && storedPassword.startsWith(BCRYPT_PREFIX);
    }
}
