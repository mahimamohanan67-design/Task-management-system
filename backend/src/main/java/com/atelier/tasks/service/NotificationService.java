package com.atelier.tasks.service;
import com.atelier.tasks.dto.ApiDtos.NotificationView;import com.atelier.tasks.entity.*;import com.atelier.tasks.repository.*;import org.springframework.http.HttpStatus;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.web.server.ResponseStatusException;import java.util.*;
@Service @Transactional public class NotificationService {
 private final NotificationRepository notifications;public NotificationService(NotificationRepository n){notifications=n;}
 public Map<String,Object> list(AppUser u){List<NotificationView> items=notifications.findAllByOwnerIdOrderByCreatedAtDesc(u.id).stream().map(this::view).toList();return Map.of("items",items,"unreadCount",notifications.countByOwnerIdAndReadFalse(u.id));}
 public NotificationView read(AppUser u,Long id){Notification n=notifications.findByIdAndOwnerId(id,u.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Notification not found"));n.read=true;return view(n);}
 public Map<String,Object> readAll(AppUser u){notifications.findAllByOwnerIdOrderByCreatedAtDesc(u.id).stream().filter(n->!n.read).forEach(n->n.read=true);return Map.of("unreadCount",0);}
 public void reminder(AppUser u,Task t){if(t.dueDate==null)return;Notification n=new Notification();n.owner=u;n.task=t;n.title="Task deadline reminder";n.message="“"+t.title+"” is due "+t.dueDate+".";n.type="DEADLINE_REMINDER";notifications.save(n);}
 private NotificationView view(Notification n){return new NotificationView(n.id,n.task==null?null:n.task.id,n.title,n.message,n.type,n.read,n.createdAt);}
}
