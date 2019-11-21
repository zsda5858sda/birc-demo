
package tw.edu.bircdemo.service.implement;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tw.edu.bircdemo.bean.BaseBean;
import tw.edu.bircdemo.dao.BaseDAO;
import tw.edu.bircdemo.dto.Limit;
import tw.edu.bircdemo.service.BaseService;
import tw.edu.bircdemo.utility.BeanUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseServiceImpl<D extends BaseDAO<E>, E, B extends BaseBean> implements BaseService<B> {
    private D baseDAO;

    public BaseServiceImpl(D baseDAO) {
        Assert.notNull(baseDAO, "baseDAO不能為null");
        this.baseDAO = baseDAO;
    }

    List<B> createBeanList(List<E> entityList) {
        List<B> beanList = new ArrayList<>();
        for (E entity : entityList) {
            beanList.add(createBean(entity));
        }
        return beanList;
    }

    @Transactional
    @Override
    public void update(Serializable pk, B bean) throws RuntimeException {
        try {
            E entity = baseDAO.getByPK(pk);
            BeanUtility.copyProperties(bean, entity);
            baseDAO.update(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public void delete(Serializable pk) throws RuntimeException {
        try {
            E entity = baseDAO.getByPK(pk);
            baseDAO.delete(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<B> getByBean(B bean) {
        if (bean == null)
            return Optional.empty();
        Optional<E> optional = baseDAO.getByEntity(createVO(bean));
        if (optional.isPresent()) {
            E entity = optional.get();
            return Optional.of(createBean(entity));
        }
        return Optional.empty();
    }

    @Override
    public Optional<B> getById(Serializable pk) {
        E entity = baseDAO.getByPK(pk);
        if (entity != null) {
            return Optional.of(createBean(entity));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<B> searchByBean(B bean) {
        return createBeanList(baseDAO.searchByEntity(createVO(bean)));
    }

    @Override
    public List<B> searchByBean(B bean, Limit limit) {
        return createBeanList(baseDAO.searchByEntity(createVO(bean), limit));
    }

    @Override
    public List<B> searchAll() {
        List<B> beanList = new ArrayList<>();
        for (E entity : baseDAO.searchAll()) {
            beanList.add(createBean(entity));
        }
        return beanList;
    }

    protected abstract E createVO(B bean);

    protected abstract B createBean(E entity);

    protected void copy(Object orig, Object dest) {
        if (orig != null && dest != null) {
            BeanUtility.copyProperties(orig, dest);
        }
    }
}
