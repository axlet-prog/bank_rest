package com.example.bankcards.entity;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * <pre>
 * <div><strong>Project name:</strong> bank_rest </div>
 * <div><strong>Creation date:</strong> 2025-10-09 </div>
 * </pre>
 *
 * @author Ivannikov Alexey
 * @since 1.0.0
 */
@Getter
public class UpdateEntity extends CreateOnlyEntity {

    public static String updatedFieldName = "updatedDatetime";

    @UpdateTimestamp
    @Column(name = "updated_datetime", nullable = false)
    protected LocalDateTime updatedDatetime;
}
