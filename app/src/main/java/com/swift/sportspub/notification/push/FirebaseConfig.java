package com.swift.sportspub.notification.push;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.swift.sportspub.config.FcmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app.fcm", name = "enabled", havingValue = "true")
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(FcmProperties fcmProperties) throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(resolveCredentialsJson(fcmProperties).getBytes(StandardCharsets.UTF_8))
            );
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized for FCM");
            return app;
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    private String resolveCredentialsJson(FcmProperties properties) {
        if (StringUtils.hasText(properties.getCredentialsJson())) {
            return properties.getCredentialsJson();
        }
        if (StringUtils.hasText(properties.getCredentialsBase64())) {
            return new String(
                    Base64.getDecoder().decode(properties.getCredentialsBase64()),
                    StandardCharsets.UTF_8
            );
        }
        throw new IllegalStateException(
                "app.fcm.enabled=true 이지만 FCM_CREDENTIALS_JSON / FCM_CREDENTIALS_BASE64 가 비어 있습니다."
        );
    }
}
