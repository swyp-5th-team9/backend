package com.swift.sportspub.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "failed_profile_image_deletions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FailedProfileImageDeletion {

    @Id
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    public FailedProfileImageDeletion(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
