package com.example.application.frame;

import java.util.List;

public interface Action<T> {
    void perform(List<T> model);
    boolean isEnabled(List<T> models);
    String getName();
}
