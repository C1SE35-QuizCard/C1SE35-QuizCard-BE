package com.example.quizcards.service.impl;

import com.example.quizcards.entities._Subscription;
import com.example.quizcards.repository._ISubscriptionRepository;
import com.example.quizcards.service._INotificaitonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class _NotificationServiceImpl implements _INotificaitonService {

    _ISubscriptionRepository subscriptionRepository;

    @Value("${spring.web-push-notifications.public-key}")
    @NonFinal
    String push_publicKey;

    @Value("${spring.web-push-notifications.private-key}")
    @NonFinal
    String push_privateKey;

    @Override
    public void saveSubscription(Map<String, Object> subscription) {
        try {
            _Subscription sub = new _Subscription();
            sub.setEndpoint(subscription.get("endpoint").toString());
            sub.setKeys(new ObjectMapper().writeValueAsString(subscription.get("keys")));
            subscriptionRepository.save(sub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNotification(String title, String body) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        PushService pushService = new PushService()
                .setPublicKey(Utils.loadPublicKey(push_publicKey))
                .setPrivateKey(Utils.loadPrivateKey(push_privateKey))
//                .setSubject("mailto:your-email@example.com");
                .setSubject("hello, baby cute");

        List<_Subscription> subs = subscriptionRepository.findAll();

        for (_Subscription sub : subs) {
            Map<String, Object> payload = Map.of("title", title, "body", body);
            String jsonPayload = new ObjectMapper().writeValueAsString(payload);

            Subscription.Keys keys = new ObjectMapper().readValue(sub.getKeys(), Subscription.Keys.class);

            nl.martijndwars.webpush.Subscription subscription =
                    new nl.martijndwars.webpush.Subscription(
                            sub.getEndpoint(),
                            new nl.martijndwars.webpush.Subscription.Keys(
                                    keys.p256dh,
                                    keys.auth
                            )
                    );

            Notification notification = new Notification(subscription, jsonPayload);
            pushService.send(notification);
        }
    }
}
