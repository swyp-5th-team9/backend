package com.swift.sportspub.pub.controller;

import com.swift.sportspub.common.exception.GlobalExceptionHandler;
import com.swift.sportspub.pub.dto.BusinessDayFilter;
import com.swift.sportspub.pub.dto.PubListResponse;
import com.swift.sportspub.pub.dto.PubListSearchCondition;
import com.swift.sportspub.pub.dto.PubMapResponse;
import com.swift.sportspub.pub.dto.PubMapSearchCondition;
import com.swift.sportspub.pub.entity.CapacityRange;
import com.swift.sportspub.pub.entity.Region;
import com.swift.sportspub.pub.entity.SubRegion;
import com.swift.sportspub.pub.service.PubQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PubControllerTest {

    @Mock
    private PubQueryService pubQueryService;

    @InjectMocks
    private PubController pubController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pubController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getList_bindsAllFilterParams() throws Exception {
        given(pubQueryService.findList(any())).willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs")
                        .param("keyword", "치어스")
                        .param("teamIds", "1")
                        .param("teamIds", "2")
                        .param("region", "GANGNAM")
                        .param("facilityCodes", "PARKING")
                        .param("facilityCodes", "GROUP_SEAT")
                        .param("styleCodes", "BIG_SCREEN")
                        .param("themeCodes", "SPACIOUS_VIEW")
                        .param("foodCodes", "CHICKEN")
                        .param("capacityRange", "R_50_100")
                        .param("openNow", "true")
                        .param("businessDay", "WEEKEND")
                        .param("page", "2")
                        .param("size", "15"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubListSearchCondition> captor = ArgumentCaptor.forClass(PubListSearchCondition.class);
        org.mockito.Mockito.verify(pubQueryService).findList(captor.capture());
        PubListSearchCondition condition = captor.getValue();

        assertThat(condition.keyword()).isEqualTo("치어스");
        assertThat(condition.teamIds()).containsExactly(1L, 2L);
        assertThat(condition.regions()).containsExactly(Region.GANGNAM);
        assertThat(condition.subRegion()).isNull();
        assertThat(condition.facilityCodes()).containsExactly("PARKING", "GROUP_SEAT");
        assertThat(condition.styleCodes()).containsExactly("BIG_SCREEN");
        assertThat(condition.themeCodes()).containsExactly("SPACIOUS_VIEW");
        assertThat(condition.foodCodes()).containsExactly("CHICKEN");
        assertThat(condition.capacityRange()).isEqualTo(CapacityRange.R_50_100);
        assertThat(condition.openNow()).isTrue();
        assertThat(condition.businessDay()).isEqualTo(BusinessDayFilter.WEEKEND);
        assertThat(condition.page()).isEqualTo(2);
        assertThat(condition.size()).isEqualTo(15);
    }

    @Test
    void getList_teamIdsPreferredOverTeamId() throws Exception {
        given(pubQueryService.findList(any())).willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs")
                        .param("teamId", "99")
                        .param("teamIds", "1")
                        .param("teamIds", "2"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubListSearchCondition> captor = ArgumentCaptor.forClass(PubListSearchCondition.class);
        org.mockito.Mockito.verify(pubQueryService).findList(captor.capture());
        assertThat(captor.getValue().teamIds()).containsExactly(1L, 2L);
    }

    @Test
    void getList_teamIdSingle_wrappedIntoList() throws Exception {
        given(pubQueryService.findList(any())).willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs").param("teamId", "7"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubListSearchCondition> captor = ArgumentCaptor.forClass(PubListSearchCondition.class);
        org.mockito.Mockito.verify(pubQueryService).findList(captor.capture());
        assertThat(captor.getValue().teamIds()).containsExactly(7L);
    }

    @Test
    void getList_subRegionCode_resolvedToRegionAndSub() throws Exception {
        given(pubQueryService.findList(any())).willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs").param("region", "JAMSIL"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubListSearchCondition> captor = ArgumentCaptor.forClass(PubListSearchCondition.class);
        org.mockito.Mockito.verify(pubQueryService).findList(captor.capture());
        PubListSearchCondition condition = captor.getValue();
        assertThat(condition.subRegion()).isEqualTo(SubRegion.JAMSIL);
        assertThat(condition.regions()).containsExactly(SubRegion.JAMSIL.getRegion());
    }

    @Test
    void getList_invalidPage_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/pubs").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @Test
    void getList_sizeOver50_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/pubs").param("size", "51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @Test
    void getList_invalidRegion_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/pubs").param("region", "NOT_A_REGION"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @Test
    void getList_regionsMulti_unionRegions() throws Exception {
        given(pubQueryService.findList(any())).willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs")
                        .param("regions", "MAPO")
                        .param("regions", "SEONGDONG"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubListSearchCondition> captor = ArgumentCaptor.forClass(PubListSearchCondition.class);
        org.mockito.Mockito.verify(pubQueryService).findList(captor.capture());
        PubListSearchCondition condition = captor.getValue();
        assertThat(condition.regions()).containsExactly(Region.MAPO, Region.SEONGDONG);
        assertThat(condition.subRegion()).isNull();
    }

    @Test
    void getList_regionsPreferredOverRegion() throws Exception {
        given(pubQueryService.findList(any())).willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs")
                        .param("region", "GANGNAM")
                        .param("regions", "MAPO")
                        .param("regions", "SEONGDONG"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubListSearchCondition> captor = ArgumentCaptor.forClass(PubListSearchCondition.class);
        org.mockito.Mockito.verify(pubQueryService).findList(captor.capture());
        assertThat(captor.getValue().regions()).containsExactly(Region.MAPO, Region.SEONGDONG);
    }

    @Test
    void getList_multipleSubRegions_subNullifiedButRegionsExpanded() throws Exception {
        given(pubQueryService.findList(any())).willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs")
                        .param("regions", "JAMSIL")
                        .param("regions", "HONGDAE_HAPJEONG"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubListSearchCondition> captor = ArgumentCaptor.forClass(PubListSearchCondition.class);
        org.mockito.Mockito.verify(pubQueryService).findList(captor.capture());
        PubListSearchCondition condition = captor.getValue();
        assertThat(condition.regions()).containsExactly(Region.SONGPA, Region.MAPO);
        assertThat(condition.subRegion()).isNull();
    }

    @Test
    void getMapMarkers_regionsMulti() throws Exception {
        given(pubQueryService.findMapMarkers(any())).willReturn(PubMapResponse.of(List.of()));

        mockMvc.perform(get("/api/v1/pubs/map")
                        .param("swLat", "37.49")
                        .param("swLng", "127.02")
                        .param("neLat", "37.51")
                        .param("neLng", "127.04")
                        .param("regions", "MAPO")
                        .param("regions", "SEONGDONG"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubMapSearchCondition> captor = ArgumentCaptor.forClass(PubMapSearchCondition.class);
        org.mockito.Mockito.verify(pubQueryService).findMapMarkers(captor.capture());
        assertThat(captor.getValue().regions()).containsExactly(Region.MAPO, Region.SEONGDONG);
    }

    @Test
    void getMapMarkers_bindsFilterAndBBox() throws Exception {
        given(pubQueryService.findMapMarkers(any())).willReturn(PubMapResponse.of(List.of()));

        mockMvc.perform(get("/api/v1/pubs/map")
                        .param("swLat", "37.49")
                        .param("swLng", "127.02")
                        .param("neLat", "37.51")
                        .param("neLng", "127.04")
                        .param("teamIds", "1")
                        .param("region", "GANGNAM")
                        .param("facilityCodes", "PARKING")
                        .param("openNow", "true")
                        .param("businessDay", "MON"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubMapSearchCondition> captor = ArgumentCaptor.forClass(PubMapSearchCondition.class);
        org.mockito.Mockito.verify(pubQueryService).findMapMarkers(captor.capture());
        PubMapSearchCondition condition = captor.getValue();

        assertThat(condition.swLat().doubleValue()).isEqualTo(37.49);
        assertThat(condition.neLng().doubleValue()).isEqualTo(127.04);
        assertThat(condition.teamIds()).containsExactly(1L);
        assertThat(condition.regions()).containsExactly(Region.GANGNAM);
        assertThat(condition.facilityCodes()).containsExactly("PARKING");
        assertThat(condition.openNow()).isTrue();
        assertThat(condition.businessDay()).isEqualTo(BusinessDayFilter.MON);
    }
}
