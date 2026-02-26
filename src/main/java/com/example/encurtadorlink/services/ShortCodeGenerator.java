package com.example.encurtadorlink.services;

import com.example.encurtadorlink.util.Base62;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final SecureRandom random = new SecureRandom();

    /**
     * <p>
     *     Retorna um número positivo do resto da divisão de um número aleatório e 100_000_000_000L
     *     e depois encoda para Base62, o que diminui bastante a possibilidade de ocorrer repetição
     *     código para link curto
     * </p>
     * @return O código que vai ser associado ao link na API
     */
    public String generate(){
        long randomNumber = Math.abs(random.nextLong() % 100_000_000_000L);
        return Base62.encode(randomNumber);
    }
}
