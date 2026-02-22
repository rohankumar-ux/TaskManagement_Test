package org.example.util;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final Pattern Email_Pattern =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static boolean isValid(String email){
        return Email_Pattern.matcher(email).matches();
    }
}
