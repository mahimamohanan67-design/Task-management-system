package com.atelier.tasks.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="users")
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=80) public String name;
 @Column(nullable=false,unique=true,length=180) public String email;
 @Column(name="password_hash",nullable=false,length=100) public String passwordHash;
 @Column(name="profile_image_url",length=1000) public String profileImageUrl;
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) public Instant updatedAt=Instant.now();
 public AppUser() {}
 public AppUser(String name,String email,String passwordHash){this.name=name;this.email=email;this.passwordHash=passwordHash;}
 @PreUpdate void touch(){updatedAt=Instant.now();}
}
