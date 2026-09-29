package com.swift.sportspub.pub.service;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.pub.entity.Region;
import com.swift.sportspub.pub.entity.SubRegion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegionResolverTest {

    @Test
    void resolve_null_returnsEmpty() {
        RegionFilter result = RegionResolver.resolve((List<String>) null);
        assertThat(result.regions()).isEmpty();
        assertThat(result.subRegion()).isNull();
    }

    @Test
    void resolve_emptyList_returnsEmpty() {
        RegionFilter result = RegionResolver.resolve(List.of());
        assertThat(result.regions()).isEmpty();
        assertThat(result.subRegion()).isNull();
    }

    @Test
    void resolve_singleRegionCode_returnsRegionOnly() {
        RegionFilter result = RegionResolver.resolve(List.of("GANGNAM"));
        assertThat(result.regions()).containsExactly(Region.GANGNAM);
        assertThat(result.subRegion()).isNull();
    }

    @Test
    void resolve_singleSubRegionCode_returnsRegionAndSub() {
        RegionFilter result = RegionResolver.resolve(List.of("JAMSIL"));
        assertThat(result.subRegion()).isEqualTo(SubRegion.JAMSIL);
        assertThat(result.regions()).containsExactly(SubRegion.JAMSIL.getRegion());
    }

    @Test
    void resolve_coverageRegion_expandsToPartnerRegions() {
        // NOWON은 empty state 커버를 위해 GANGBUK까지 확장
        RegionFilter result = RegionResolver.resolve(List.of("NOWON"));
        assertThat(result.regions()).containsExactly(Region.NOWON, Region.GANGBUK);
        assertThat(result.subRegion()).isNull();
    }

    @Test
    void resolve_metroCode_expandsToAllRegionsInMetro() {
        RegionFilter result = RegionResolver.resolve(List.of("SEOUL"));
        assertThat(result.regions()).contains(Region.GANGNAM, Region.MAPO, Region.JONGNO);
        assertThat(result.subRegion()).isNull();
    }

    @Test
    void resolve_multipleRegionCodes_unionRegions() {
        RegionFilter result = RegionResolver.resolve(List.of("MAPO", "SEONGDONG"));
        assertThat(result.regions()).containsExactly(Region.MAPO, Region.SEONGDONG);
        assertThat(result.subRegion()).isNull();
    }

    @Test
    void resolve_multipleSubRegionCodes_subNullifiedButRegionsUnioned() {
        RegionFilter result = RegionResolver.resolve(List.of("JAMSIL", "HONGDAE_HAPJEONG"));
        assertThat(result.regions()).containsExactly(
                SubRegion.JAMSIL.getRegion(), SubRegion.HONGDAE_HAPJEONG.getRegion());
        assertThat(result.subRegion()).isNull();
    }

    @Test
    void resolve_invalidCode_throwsBusinessException() {
        assertThatThrownBy(() -> RegionResolver.resolve(List.of("NOT_A_REGION")))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void resolve_blankCode_returnsEmpty() {
        RegionFilter result = RegionResolver.resolve("");
        assertThat(result.regions()).isEmpty();
        assertThat(result.subRegion()).isNull();
    }
}
