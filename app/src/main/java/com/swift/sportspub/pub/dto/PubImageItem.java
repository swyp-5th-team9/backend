package com.swift.sportspub.pub.dto;

import com.swift.sportspub.pub.entity.PubImage;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "펍 이미지 1건")
public record PubImageItem(

        @Schema(description = "이미지 ID", example = "11")
        Long imageId,

        @Schema(description = "이미지 URL")
        String imageUrl,

        @Schema(description = "표시 순서 (0부터)", example = "0")
        Short displayOrder
) {
    public static PubImageItem from(PubImage image) {
        return new PubImageItem(image.getImageId(), image.getImageUrl(), image.getDisplayOrder());
    }
}
