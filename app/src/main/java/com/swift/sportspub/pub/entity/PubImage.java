package com.swift.sportspub.pub.entity;

import com.swift.sportspub.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pub_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "imageId", callSuper = false)
public class PubImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "pub_id", nullable = false)
    private Long pubId;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "display_order", nullable = false)
    private Short displayOrder;

    @Builder
    private PubImage(Long pubId, String imageUrl, Short displayOrder) {
        this.pubId = pubId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder != null ? displayOrder : (short) 0;
    }
}
