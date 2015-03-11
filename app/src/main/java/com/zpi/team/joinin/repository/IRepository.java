package com.zpi.team.joinin.repository;

import java.util.List;

/**
 * Created by MK on 2015-03-11.
 */
public interface IRepository<T> {
    T getById();
    List<T> getAll();
    void create(T entity);
    void delete(T entity);
    void update(T entity);
}