package com.example.application.data.repository;

import com.example.application.data.entity.Status;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long> {

    @Cacheable("static")
    @Override
    List<Status> findAll();
}
