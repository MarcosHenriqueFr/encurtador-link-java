package com.example.encurtadorlink.fixtures;

import com.example.encurtadorlink.model.RoleName;
import com.example.encurtadorlink.model.User;

public class UserFixture {

    public static User createUserFix(){
        return User.builder()
                .id(1L)
                .name("Teste teste")
                .email("emailvalido@email.com")
                .links(null)
                .role(RoleName.BASIC)
                .password("12345")
                .build();
    }
}
