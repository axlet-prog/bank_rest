package com.example.bankcards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@MappedSuperclass
public class UpdateEntity extends CreateOnlyEntity {

    public static final String updatedFieldName = "updatedDatetime";

    @UpdateTimestamp
    @Column(name = "updated_datetime", nullable = false)
    protected LocalDateTime updatedDatetime;
}
