package com.swift.sportspub.pub.controller;

import com.swift.sportspub.common.exception.GlobalExceptionHandler;
import com.swift.sportspub.pub.dto.BusinessDayFilter;
import com.swift.sportspub.pub.dto.PubFilterParams;
import com.swift.sportspub.pub.dto.PubListResponse;
import com.swift.sportspub.pub.dto.PubMapResponse;
import com.swift.sportspub.pub.entity.CapacityRange;
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

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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
        given(pubQueryService.findList(any(), any(), anyInt(), anyInt()))
                .willReturn(PubListResponse.of(List.of(), 0, 20, 0));

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

        ArgumentCaptor<PubFilterParams> filterCaptor = ArgumentCaptor.forClass(PubFilterParams.class);
        org.mockito.Mockito.verify(pubQueryService)
                .findList(eq("치어스"), filterCaptor.capture(), eq(2), eq(15));
        PubFilterParams filter = filterCaptor.getValue();

        assertThat(filter.mergedTeamIds()).containsExactly(1L, 2L);
        assertThat(filter.mergedRegions()).containsExactly("GANGNAM");
        assertThat(filter.facilityCodes()).containsExactly("PARKING", "GROUP_SEAT");
        assertThat(filter.styleCodes()).containsExactly("BIG_SCREEN");
        assertThat(filter.themeCodes()).containsExactly("SPACIOUS_VIEW");
        assertThat(filter.foodCodes()).containsExactly("CHICKEN");
        assertThat(filter.capacityRange()).isEqualTo(CapacityRange.R_50_100);
        assertThat(filter.openNow()).isTrue();
        assertThat(filter.businessDay()).isEqualTo(BusinessDayFilter.WEEKEND);
    }

    @Test
    void getList_teamIdsPreferredOverTeamId() throws Exception {
        given(pubQueryService.findList(any(), any(), anyInt(), anyInt()))
                .willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs")
                        .param("teamId", "99")
                        .param("teamIds", "1")
                        .param("teamIds", "2"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubFilterParams> captor = ArgumentCaptor.forClass(PubFilterParams.class);
        org.mockito.Mockito.verify(pubQueryService)
                .findList(any(), captor.capture(), anyInt(), anyInt());
        assertThat(captor.getValue().mergedTeamIds()).containsExactly(1L, 2L);
    }

    @Test
    void getList_teamIdSingle_wrappedIntoList() throws Exception {
        given(pubQueryService.findList(any(), any(), anyInt(), anyInt()))
                .willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs").param("teamId", "7"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubFilterParams> captor = ArgumentCaptor.forClass(PubFilterParams.class);
        org.mockito.Mockito.verify(pubQueryService)
                .findList(any(), captor.capture(), anyInt(), anyInt());
        assertThat(captor.getValue().mergedTeamIds()).containsExactly(7L);
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
    void getList_regionsMulti_passedThroughAsRawCodes() throws Exception {
        given(pubQueryService.findList(any(), any(), anyInt(), anyInt()))
                .willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs")
                        .param("regions", "MAPO")
                        .param("regions", "SEONGDONG"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubFilterParams> captor = ArgumentCaptor.forClass(PubFilterParams.class);
        org.mockito.Mockito.verify(pubQueryService)
                .findList(any(), captor.capture(), anyInt(), anyInt());
        assertThat(captor.getValue().mergedRegions()).containsExactly("MAPO", "SEONGDONG");
    }

    @Test
    void getList_regionsPreferredOverRegion() throws Exception {
        given(pubQueryService.findList(any(), any(), anyInt(), anyInt()))
                .willReturn(PubListResponse.of(List.of(), 0, 20, 0));

        mockMvc.perform(get("/api/v1/pubs")
                        .param("region", "GANGNAM")
                        .param("regions", "MAPO")
                        .param("regions", "SEONGDONG"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubFilterParams> captor = ArgumentCaptor.forClass(PubFilterParams.class);
        org.mockito.Mockito.verify(pubQueryService)
                .findList(any(), captor.capture(), anyInt(), anyInt());
        assertThat(captor.getValue().mergedRegions()).containsExactly("MAPO", "SEONGDONG");
    }

    @Test
    void getMapMarkers_bindsFilterAndBBox() throws Exception {
        given(pubQueryService.findMapMarkers(any(), any(), any(), any(), any()))
                .willReturn(PubMapResponse.of(List.of()));

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

        ArgumentCaptor<BigDecimal> swLatCap = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> swLngCap = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> neLatCap = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> neLngCap = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<PubFilterParams> filterCap = ArgumentCaptor.forClass(PubFilterParams.class);
        org.mockito.Mockito.verify(pubQueryService).findMapMarkers(
                swLatCap.capture(), swLngCap.capture(), neLatCap.capture(), neLngCap.capture(), filterCap.capture());

        assertThat(swLatCap.getValue().doubleValue()).isEqualTo(37.49);
        assertThat(neLngCap.getValue().doubleValue()).isEqualTo(127.04);
        PubFilterParams filter = filterCap.getValue();
        assertThat(filter.mergedTeamIds()).containsExactly(1L);
        assertThat(filter.mergedRegions()).containsExactly("GANGNAM");
        assertThat(filter.facilityCodes()).containsExactly("PARKING");
        assertThat(filter.openNow()).isTrue();
        assertThat(filter.businessDay()).isEqualTo(BusinessDayFilter.MON);
    }

    @Test
    void getMapMarkers_regionsMulti_passedThroughAsRawCodes() throws Exception {
        given(pubQueryService.findMapMarkers(any(), any(), any(), any(), any()))
                .willReturn(PubMapResponse.of(List.of()));

        mockMvc.perform(get("/api/v1/pubs/map")
                        .param("swLat", "37.49")
                        .param("swLng", "127.02")
                        .param("neLat", "37.51")
                        .param("neLng", "127.04")
                        .param("regions", "MAPO")
                        .param("regions", "SEONGDONG"))
                .andExpect(status().isOk());

        ArgumentCaptor<PubFilterParams> captor = ArgumentCaptor.forClass(PubFilterParams.class);
        org.mockito.Mockito.verify(pubQueryService).findMapMarkers(
                any(), any(), any(), any(), captor.capture());
        assertThat(captor.getValue().mergedRegions()).containsExactly("MAPO", "SEONGDONG");
    }
}
