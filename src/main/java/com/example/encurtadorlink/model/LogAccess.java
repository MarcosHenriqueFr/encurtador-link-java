package com.example.encurtadorlink.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_access")
public class LogAccess implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "link_id", referencedColumnName = "id", nullable = false)
    private Link link;

    @Column(name = "access_date", nullable = false)
    private LocalDateTime accessDate;

    @Column(name = "user_agent", length = 50)
    private String userAgent;

    @Column(name = "user_ip", length = 80)
    private String userIp;

    @Column
    private String referrer;
}
