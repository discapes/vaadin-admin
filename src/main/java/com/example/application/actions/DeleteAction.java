package com.example.application.actions;

import com.example.application.frame.Action;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class DeleteAction<T> implements Action<T> {
    JpaRepository<T, Long> repository;

    public DeleteAction(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    @Override
    public void perform(List<T> models) {
        repository.deleteAll(models);
    }

    @Override
    public boolean isEnabled(List<T> models) {
        return models.size() > 0;
    }

    @Override
    public String getName() {
        return "Delete";
    }

}
