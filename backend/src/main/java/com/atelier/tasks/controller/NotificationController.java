package com.atelier.tasks.controller;
import com.atelier.tasks.dto.ApiDtos.NotificationView;import com.atelier.tasks.entity.AppUser;import com.atelier.tasks.repository.UserRepository;import com.atelier.tasks.service.NotificationService;import org.springframework.web.bind.annotation.*;import java.security.Principal;import java.util.Map;
@RestController @RequestMapping("/api/notifications") public class NotificationController {
 private final NotificationService service;private final UserRepository users;public NotificationController(NotificationService s,UserRepository u){service=s;users=u;}private AppUser user(Principal p){return users.findByEmailIgnoreCase(p.getName()).orElseThrow();}
 @GetMapping public Map<String,Object> list(Principal p){return service.list(user(p));}
 @PatchMapping("/{id}/read") public NotificationView read(@PathVariable Long id,Principal p){return service.read(user(p),id);}
 @PatchMapping("/read-all") public Map<String,Object> readAll(Principal p){return service.readAll(user(p));}
}
