package com.atelier.tasks.controller;
import com.atelier.tasks.dto.ApiDtos.*;import com.atelier.tasks.entity.AppUser;import com.atelier.tasks.repository.UserRepository;import com.atelier.tasks.service.CategoryService;import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.web.bind.annotation.*;import java.security.Principal;import java.util.*;
@RestController @RequestMapping("/api/categories") public class CategoryController {
 private final CategoryService service;private final UserRepository users;public CategoryController(CategoryService s,UserRepository u){service=s;users=u;}private AppUser user(Principal p){return users.findByEmailIgnoreCase(p.getName()).orElseThrow();}
 @GetMapping public List<CategoryView> list(Principal p){return service.list(user(p));}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public CategoryView create(@Valid @RequestBody CategoryInput in,Principal p){return service.create(user(p),in);}
 @PutMapping("/{id}") public CategoryView update(@PathVariable Long id,@Valid @RequestBody CategoryInput in,Principal p){return service.update(user(p),id,in);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id,Principal p){service.delete(user(p),id);}
}
