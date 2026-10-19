package com.atelier.tasks.controller;
import com.atelier.tasks.dto.ApiDtos.*;import com.atelier.tasks.entity.AppUser;import com.atelier.tasks.repository.UserRepository;import com.atelier.tasks.service.TaskService;import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;import java.security.Principal;import java.time.LocalDate;
@RestController @RequestMapping("/api/tasks") public class TaskController {
 private final TaskService service;private final UserRepository users;public TaskController(TaskService service,UserRepository users){this.service=service;this.users=users;}
 private AppUser user(Principal p){return users.findByEmailIgnoreCase(p.getName()).orElseThrow();}
 @GetMapping public PageView<TaskView> list(Principal p,@RequestParam(required=false) String search,@RequestParam(required=false) String status,@RequestParam(required=false) String priority,@RequestParam(required=false) Long categoryId,@RequestParam(required=false) LocalDate dueFrom,@RequestParam(required=false) LocalDate dueTo,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="100") int size,@RequestParam(defaultValue="dueDate") String sort,@RequestParam(defaultValue="asc") String direction){return service.list(user(p),search,status,priority,categoryId,dueFrom,dueTo,page,size,sort,direction);}
 @GetMapping("/{id}") public TaskView one(@PathVariable Long id,Principal p){return service.get(user(p),id);}
 @PostMapping public TaskView create(@Valid @RequestBody TaskInput in,Principal p){return service.create(user(p),in);}
 @PutMapping("/{id}") public TaskView update(@PathVariable Long id,@Valid @RequestBody TaskInput in,Principal p){return service.update(user(p),id,in);}
 @PatchMapping("/{id}/status") public TaskView status(@PathVariable Long id,@Valid @RequestBody StatusInput body,Principal p){return service.status(user(p),id,body.status());}
 public record StatusInput(@jakarta.validation.constraints.NotBlank String status){}
 @DeleteMapping("/{id}") @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id,Principal p){service.delete(user(p),id);}
}
