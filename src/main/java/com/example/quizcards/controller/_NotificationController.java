package com.example.quizcards.controller;

import com.example.quizcards.service._INotificaitonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class _NotificationController {
   _INotificaitonService notificaitonService;

   @PostMapping("/save-subscription")
   public ResponseEntity<?> saveSubscription(@RequestBody Map<String, Object> subscription) {
       notificaitonService.saveSubscription(subscription);
       return ResponseEntity.ok().build();
   }

    @GetMapping("/new-notification")
    public ResponseEntity<?> triggerNotification() throws Exception {
        notificaitonService.sendNotification("Xin chào", "Thông báo từ Java!");
        return ResponseEntity.ok().build();
    }
}
