package org.prms.kdt.customer;

import java.time.LocalDateTime;
import java.util.UUID;

public class Customer {
    // DB는 Under
    // Application은 카멜 케이스 선호

    private final UUID customerId;
    private String name;
    private final String email;
    private LocalDateTime lastLoginAt; // 로그인은 마지막 접속이 바뀔수있으므로 final 제외
    private final LocalDateTime createdAt;

    // final이 들어간것 -> 필수적 + name은 만들어져있으면 좋다.
    public Customer(UUID customerId, String email, LocalDateTime createdAt, String name) {
        validate(name);

        this.customerId = customerId;
        this.email = email;
        this.createdAt = createdAt;
        this.name=name;
    }

    private void validate(String name) {
        if (name.isBlank()) {
            throw new RuntimeException("Name should not be blank");
        }
    }

    public Customer(UUID customerId, String name, String email, LocalDateTime lastLoginAt, LocalDateTime createdAt) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
    }

    //Setter는 무분별하게 사용하지 않는다. -> 또한 명확히 바뀔려는 메소드를 명시한다.
    public void changeName(String name) {
        validate(name);
        this.name=name;
    }

    // 로그인을 해야지
    public void login() {
        this.lastLoginAt=LocalDateTime.now();
    }

    //Geter는 다 만든다.
    public UUID getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
