package com.swift.sportspub.notification.push;

import com.google.firebase.messaging.FirebaseMessaging;
import com.swift.sportspub.config.FcmProperties;
import com.swift.sportspub.notification.entity.DeepLinkType;
import com.swift.sportspub.user.repository.UserFcmTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FcmPushServiceTest {

    @Mock
    private FcmProperties fcmProperties;

    @Mock
    private UserFcmTokenRepository userFcmTokenRepository;

    @Mock
    private ObjectProvider<FirebaseMessaging> firebaseMessaging;

    @InjectMocks
    private FcmPushService fcmPushService;

    @Test
    void sendGameReminder_skipsWhenNotConfigured() {
        given(fcmProperties.isConfigured()).willReturn(false);

        fcmPushService.sendGameReminder(
                10L,
                "오늘 경기 있어요!",
                "본문",
                DeepLinkType.TODAY_PUBS,
                List.of(1L, 2L)
        );

        verify(userFcmTokenRepository, never()).findAllByUserUserId(any());
        verify(firebaseMessaging, never()).getIfAvailable();
    }
}
