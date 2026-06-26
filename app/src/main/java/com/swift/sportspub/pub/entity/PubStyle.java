package com.swift.sportspub.pub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "pub_styles")
@IdClass(PubStyleId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"pubId", "styleCode"})
@EntityListeners(AuditingEntityListener.class)
public class PubStyle {

    @Id
    @Column(name = "pub_id", nullable = false)
    private Long pubId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "style_code", nullable = false, length = 30)
    private PubStyleCode styleCode;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private PubStyle(Long pubId, PubStyleCode styleCode) {
        this.pubId = pubId;
        this.styleCode = styleCode;
    }
}
