package com.atelier.tasks.entity;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="categories",uniqueConstraints=@UniqueConstraint(name="uq_categories_user_name",columnNames={"user_id","name"}))
public class Category {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) public AppUser owner;
 @Column(nullable=false,length=80) public String name; @Column(length=500) public String description;
 @Column(nullable=false,length=32) public String color="#dce8df"; @Column(nullable=false,length=64) public String icon="Flower2";
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now(); @Column(name="updated_at",nullable=false) public Instant updatedAt=Instant.now();
 @PreUpdate void touch(){updatedAt=Instant.now();}
}
