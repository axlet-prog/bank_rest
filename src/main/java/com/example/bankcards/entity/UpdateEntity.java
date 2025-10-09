package com.example.bankcards.entity;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
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
public class UpdateEntity {

    @CreationTimestamp
    @Column(name = "created_datetime", nullable = false, updatable = false)
    protected LocalDateTime createdDatetime;

    @UpdateTimestamp
    @Column(name = "updated_datetime", nullable = false)
    protected LocalDateTime updatedDatetime;
}
