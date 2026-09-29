package com.swift.sportspub.pub.service;

import com.swift.sportspub.pub.entity.Region;
import com.swift.sportspub.pub.entity.SubRegion;

import java.util.List;

public record RegionFilter(List<Region> regions, SubRegion subRegion) {

    public static RegionFilter empty() {
        return new RegionFilter(List.of(), null);
    }
}
