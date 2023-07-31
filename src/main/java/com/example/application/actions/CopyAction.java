package com.example.application.actions;

import com.example.application.data.entity.AbstractEntity;
import com.example.application.frame.Action;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public class CopyAction<T extends AbstractEntity> implements Action<T> {
    JpaRepository<T, Long> repository;

    public CopyAction(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    @Override
    public void perform(List<T> models) {
        List<Long> ids = new ArrayList<>();
        for (T model : models) {
            ids.add(model.getId());
            model.setId(null);
        }
        repository.saveAll(models);
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setId(ids.get(i));
        }
    }

    @Override
    public boolean isEnabled(List<T> models) {
        return models.size() > 0;
    }

    @Override
    public String getName() {
        return "Copy";
    }
}
