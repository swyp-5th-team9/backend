package com.swift.sportspub.pub.entity;

import com.swift.sportspub.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "menuId", callSuper = false)
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "pub_id", nullable = false)
    private Long pubId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private MenuCategory category;

    @Column(name = "price")
    private Integer price;

    @Column(name = "display_order", nullable = false)
    private Short displayOrder;

    @Builder
    private Menu(Long pubId, String name, MenuCategory category, Integer price, Short displayOrder) {
        this.pubId = pubId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.displayOrder = displayOrder != null ? displayOrder : (short) 0;
    }
}
