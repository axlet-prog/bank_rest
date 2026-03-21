package com.example.bankcards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;


@Getter
@MappedSuperclass
public class CreateOnlyEntity {

    public static final String createdFieldName = "createdDatetime";

    @CreationTimestamp
    @Column(name = "created_datetime", nullable = false, updatable = false)
    protected LocalDateTime createdDatetime;
}
