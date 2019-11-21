package tw.edu.bircdemo.dao;

import org.hibernate.criterion.DetachedCriteria;
import tw.edu.bircdemo.dto.Limit;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseDAO<T> {
    Optional<T> getByEntity(T entity);

    T getByPK(Serializable pk);

    List<T> searchByEntity(T entity);

    List<T> searchByEntity(T entity, Limit limit);

    List<T> searchAll();

    void insert(T entity);

    Serializable insertAndReturn(T entity);

    void update(T entity);

    void delete(T entity);

    int count(DetachedCriteria detachedCriteria);
}
