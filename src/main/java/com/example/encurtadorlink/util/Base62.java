package com.example.encurtadorlink.util;

// Classe necessária para o encode da url
public class Base62 {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = ALPHABET.length();

    public static String encode(long value){
        StringBuilder sb = new StringBuilder();
        if (value == 0){
            return "0";
        }

        while(value > 0){
            int remainder = (int) (value % BASE);
            sb.append(ALPHABET.charAt(remainder));
            value /= BASE;
        }

        return sb.reverse().toString();
    }

    public static long decode(String value){
        long result = 0;

        for(int i = 0; i < value.length(); i++){
            result = result * BASE + ALPHABET.indexOf(value.charAt(i));
        }

        return result;
    }
}
