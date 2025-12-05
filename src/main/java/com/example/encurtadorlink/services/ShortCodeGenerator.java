package com.example.encurtadorlink.services;

import com.example.encurtadorlink.util.Base62;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final SecureRandom random = new SecureRandom();

    public String generate(){
        long randomNumber = Math.abs(random.nextLong() % 100_000_000_000L);
        return Base62.encode(randomNumber);
    }
}
