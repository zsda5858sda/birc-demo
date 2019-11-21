package tw.edu.bircdemo.service;

import tw.edu.bircdemo.bean.BaseBean;
import tw.edu.bircdemo.dto.Limit;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseService<B extends BaseBean> {
    B createAndReturn(B bean) throws RuntimeException;

    void update(Serializable id, B bean) throws RuntimeException;

    void delete(Serializable id) throws RuntimeException;

    Optional<B> getByBean(B bean);

    Optional<B> getById(Serializable id);

    List<B> searchByBean(B bean);

    List<B> searchByBean(B bean, Limit limit);

    List<B> searchAll();
}
