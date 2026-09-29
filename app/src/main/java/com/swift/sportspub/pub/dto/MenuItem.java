package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.Menu;
import com.swift.sportspub.pub.entity.MenuCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메뉴 1건")
public record MenuItem(

        @Schema(description = "메뉴 ID", example = "1")
        Long menuId,

        @Schema(description = "메뉴명", example = "치킨 세트")
        String name,

        @Schema(description = "카테고리 (FOOD/DRINK/SET)", example = "SET")
        MenuCategory category,

        @Schema(description = "가격 (null 이면 시가)", example = "25000")
        Integer price,

        @Schema(description = "표시 순서 (0부터)", example = "0")
        Short displayOrder
) {
    public static MenuItem from(Menu menu) {
        return new MenuItem(
                menu.getMenuId(),
                menu.getName(),
                menu.getCategory(),
                menu.getPrice(),
                menu.getDisplayOrder()
        );
    }
}
