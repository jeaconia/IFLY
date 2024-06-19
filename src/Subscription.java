package com.example.subscription.model;

import java.time.LocalDate;

public class Subscription {
    private Long id;
    private String userId;
    private String plan;
    private LocalDate startDate;
    private LocalDate endDate;

    public Subscription(Long id, String userId, String plan, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.userId = userId;
        this.plan = plan;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
