package com.atelier.tasks.service;

import com.atelier.tasks.dto.ApiDtos.*;import com.atelier.tasks.entity.*;import com.atelier.tasks.mapper.TaskMapper;import com.atelier.tasks.repository.*;
import jakarta.persistence.criteria.JoinType;import org.springframework.data.domain.*;import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.web.server.ResponseStatusException;
import java.time.*;import java.util.*;

@Service @Transactional
public class TaskService {
 private final TaskRepository tasks;private final CategoryRepository categories;private final TaskActivityRepository activities;private final NotificationService notifications;
 public TaskService(TaskRepository tasks,CategoryRepository categories,TaskActivityRepository activities,NotificationService notifications){this.tasks=tasks;this.categories=categories;this.activities=activities;this.notifications=notifications;}
 public PageView<TaskView> list(AppUser user,String search,String status,String priority,Long categoryId,LocalDate dueFrom,LocalDate dueTo,int page,int size,String sort,String direction){
  if(dueFrom!=null&&dueTo!=null&&dueTo.isBefore(dueFrom))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"dueTo must be on or after dueFrom");
  int safePage=Math.max(0,page),safeSize=Math.min(100,Math.max(1,size));String field=Set.of("createdAt","dueDate","priority","title","updatedAt").contains(sort)?sort:"dueDate";
  Sort.Direction dir="desc".equalsIgnoreCase(direction)?Sort.Direction.DESC:Sort.Direction.ASC;
  Specification<Task> spec=(root,q,cb)->cb.equal(root.get("owner").get("id"),user.id);
  if(search!=null&&!search.isBlank())spec=spec.and((root,q,cb)->cb.like(cb.lower(root.get("title")),"%"+search.trim().toLowerCase()+"%"));
  if(status!=null&&!status.isBlank()){Task.TaskStatus parsed;try{parsed=Task.TaskStatus.valueOf(status.toUpperCase());}catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid task status");}spec=spec.and((root,q,cb)->cb.equal(root.get("status"),parsed));}
  if(priority!=null&&!priority.isBlank()){Task.Priority parsed;try{parsed=Task.Priority.valueOf(priority.toUpperCase());}catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid task priority");}spec=spec.and((root,q,cb)->cb.equal(root.get("priority"),parsed));}
  if(categoryId!=null)spec=spec.and((root,q,cb)->cb.equal(root.get("category").get("id"),categoryId));
  if(dueFrom!=null)spec=spec.and((root,q,cb)->cb.greaterThanOrEqualTo(root.get("dueDate"),dueFrom));
  if(dueTo!=null)spec=spec.and((root,q,cb)->cb.lessThanOrEqualTo(root.get("dueDate"),dueTo));
  Pageable pageable=PageRequest.of(safePage,safeSize,Sort.by(dir,field));Page<Task> result=tasks.findAll(spec,pageable);
  return new PageView<>(result.getContent().stream().map(TaskMapper::toView).toList(),result.getNumber(),result.getSize(),result.getTotalElements(),result.getTotalPages(),result.isFirst(),result.isLast());
 }
 public TaskView get(AppUser user,Long id){return TaskMapper.toView(owned(user,id));}
 public TaskView create(AppUser user,TaskInput input){Task t=new Task();t.owner=user;apply(t,input,user);tasks.save(t);activities.save(new TaskActivity(t,user,"CREATED","Created “"+t.title+"”"));if(t.dueDate!=null)notifications.reminder(user,t);return TaskMapper.toView(t);}
 public TaskView update(AppUser user,Long id,TaskInput input){Task t=owned(user,id);LocalDate oldDue=t.dueDate;apply(t,input,user);activities.save(new TaskActivity(t,user,"UPDATED","Updated task details"));if(t.dueDate!=null&&!Objects.equals(oldDue,t.dueDate))notifications.reminder(user,t);return TaskMapper.toView(t);}
 public TaskView status(AppUser user,Long id,String value){Task t=owned(user,id);Task.TaskStatus next;try{next=Task.TaskStatus.valueOf(value.toUpperCase());}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid task status");}Task.TaskStatus old=t.status;t.status=next;t.completedAt=next==Task.TaskStatus.COMPLETED?(t.completedAt==null?Instant.now():t.completedAt):null;activities.save(new TaskActivity(t,user,"STATUS_CHANGED",old+" → "+next));return TaskMapper.toView(t);}
 public void delete(AppUser user,Long id){Task t=owned(user,id);activities.save(new TaskActivity(t,user,"DELETED","Deleted “"+t.title+"”"));tasks.delete(t);}
 private Task owned(AppUser user,Long id){return tasks.findByIdAndOwnerId(id,user.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found"));}
 private void apply(Task t,TaskInput in,AppUser user){
  t.title=in.title().trim();t.description=in.description();t.priority=in.priority()==null?Task.Priority.MEDIUM:in.priority();
  if(in.status()!=null&&t.status!=in.status())t.status=in.status();if(t.status==Task.TaskStatus.COMPLETED)t.completedAt=t.completedAt==null?Instant.now():t.completedAt;else t.completedAt=null;
  t.startDate=in.startDate();t.dueDate=in.dueDate();t.timeOfDay=in.timeOfDay()==null?"Whenever":in.timeOfDay();
  String name=in.category()==null||in.category().isBlank()?"Personal":in.category().trim();
  t.category=categories.findByNameIgnoreCaseAndOwnerId(name,user.id).orElseGet(()->{Category c=new Category();c.owner=user;c.name=name;return categories.save(c);});
  t.tags.clear();if(in.tags()!=null)in.tags().stream().filter(x->x!=null&&!x.isBlank()).distinct().forEach(tag->t.tags.add(new TaskTag(t,tag.trim())));
 }
}
