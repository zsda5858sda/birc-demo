package tw.edu.bircdemo.dao.implement;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.util.Assert;
import tw.edu.bircdemo.dao.BaseDAO;
import tw.edu.bircdemo.dto.Limit;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@NoRepositoryBean
public abstract class BaseDAOImpl<T> extends HibernateDaoSupport implements BaseDAO<T> {
    static final String notNullMessage = "template不能為null";

    protected Map<String, String> alias = new HashMap<>();
    protected List<Criterion> criterion = new ArrayList<>();

    private Class<T> entityClass;

    @Resource(name = "sessionFactory")
    public void setSuperSessionFactory(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }

    public BaseDAOImpl() {
        entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public Optional<T> getByEntity(T entity) {
        return getByOne(searchByEntity(entity));
    }

    private Optional<T> getByOne(List<T> entityList) {
        if (entityList.size() == 1) {
            return Optional.of(entityList.get(0));
        }
        return Optional.empty();
    }

    @Override
    public T getByPK(Serializable pk) {
        Assert.notNull(this.getHibernateTemplate(), notNullMessage);
        return this.getHibernateTemplate().get(entityClass, pk);
    }

    @Override
    public List<T> searchByEntity(T entity) {
        return searchByEntity(entity, null);
    }

    @Override
    public List<T> searchByEntity(T entity, Limit limit) {
        List<String> aliasList = new ArrayList<>();
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(entityClass);

        for (Method entityMethod : entityClass.getDeclaredMethods()) {
            String entityMethodName = entityMethod.getName();
            if (isGetter(entityMethodName)) {
                String entityParameterName = getParameterName(entityMethodName);
                if (isJoinColumnMethod(entityMethod)) {
                    Class aliasClass = entityMethod.getReturnType();
                    for (Field aliasField : aliasClass.getDeclaredFields()) {
                        try {
                            Object entityParameter = entityMethod.invoke(entity);
                            String aliasFieldName = getGetterParameterName(aliasField.getName());
                            Method aliasReadMethod = getGetterMethod(aliasClass, aliasFieldName);
                            if (isParameterAndReturnValueNotNull(entityParameter, aliasReadMethod)) {
                                Object value = aliasReadMethod.invoke(entityParameter);
                                if (!aliasList.contains(entityParameterName)) {
                                    detachedCriteria.createAlias(entityParameterName, entityParameterName, JoinType.LEFT_OUTER_JOIN);
                                    aliasList.add(entityParameterName);
                                }
                                if (!aliasReadMethod.isAnnotationPresent(Id.class)) {
                                    detachedCriteria.add(Restrictions.eq(String.format("%s.%s", entityParameterName, aliasField.getName()), value));
                                }
                            }
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (entityMethod.isAnnotationPresent(Column.class)) {
                    try {
                        Object value;
                        if ((value = entityMethod.invoke(entity)) != null) {
                            detachedCriteria.add(Restrictions.eq(entityParameterName, value));
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        detachedCriteria.add(Example.create(entity));
        Assert.notNull(this.getHibernateTemplate(), notNullMessage);
        if (limit == null) {
            return (List<T>) this.getHibernateTemplate().findByCriteria(detachedCriteria);
        } else {
            return (List<T>) this.getHibernateTemplate().findByCriteria(detachedCriteria, limit.getMin(), limit.getMax());
        }
    }

    private boolean isGetter(String methodName) {
        return methodName.substring(0, 3).equals("get");
    }

    private boolean isJoinColumnMethod(Method method) {
        return method.isAnnotationPresent(OneToOne.class) || method.isAnnotationPresent(ManyToOne.class);
    }

    private String getParameterName(String methodName) {
        String parameterName = methodName.substring(3);
        String parameterFirstCharacter = parameterName.substring(0, 1);
        parameterName = parameterName.replaceFirst(parameterFirstCharacter, parameterFirstCharacter.toLowerCase());
        return parameterName;
    }

    private String getGetterParameterName(String parameterName) {
        String aliasFieldFirstCharacter = parameterName.substring(0, 1);
        parameterName = parameterName.replaceFirst(aliasFieldFirstCharacter, aliasFieldFirstCharacter.toUpperCase());
        return parameterName;
    }

    private Method getGetterMethod(Class clazz, String parameterName) throws NoSuchMethodException {
        return clazz.getMethod(String.format("get%s", parameterName));
    }

    private Method getSetterMethod(Class clazz, String parameterName) throws NoSuchMethodException {
        return clazz.getMethod(String.format("set%s", parameterName));
    }

    private boolean isParameterAndReturnValueNotNull(Object parameter, Method getterMethod) throws InvocationTargetException, IllegalAccessException {
        return parameter != null && (getterMethod.invoke(parameter)) != null;
    }

    @Override
    public List<T> searchAll() {
        Assert.notNull(this.getHibernateTemplate(), notNullMessage);
        return this.getHibernateTemplate().loadAll(entityClass);
    }

    @Override
    public void insert(T entity) {
        Assert.notNull(this.getHibernateTemplate(), notNullMessage);
        this.getHibernateTemplate().save(entity);
        this.getHibernateTemplate().flush();
    }

    @Override
    public Serializable insertAndReturn(T entity) {
        Assert.notNull(this.getHibernateTemplate(), notNullMessage);
        Serializable serializable = this.getHibernateTemplate().save(entity);
        this.getHibernateTemplate().flush();
        return serializable;
    }

    @Override
    public void update(T entity) {
        Assert.notNull(this.getHibernateTemplate(), notNullMessage);
        this.getHibernateTemplate().merge(entity);
        this.getHibernateTemplate().flush();
    }

    @Override
    public void delete(T entity) {
        Assert.notNull(this.getHibernateTemplate(), notNullMessage);
        this.getHibernateTemplate().delete(entity);
    }

    @Override
    public int count(DetachedCriteria detachedCriteria) {
        Assert.notNull(this.getHibernateTemplate(), notNullMessage);
        detachedCriteria.setProjection(Projections.rowCount());
        return DataAccessUtils.intResult(this.getHibernateTemplate().findByCriteria(detachedCriteria));
    }

    boolean notEmpty(Object object) {
        if (object == null)
            return false;
        if (object instanceof Character) {
            Character temp = (Character) object;
            return !Character.isSpaceChar(temp);
        } else if (object instanceof String) {
            String temp = (String) object;
            return !temp.isBlank();
        } else if (object instanceof Byte) {
            Byte temp = (Byte) object;
            return temp != 0;
        } else if (object instanceof Short) {
            Short temp = (Short) object;
            return temp != 0;
        } else if (object instanceof Integer) {
            Integer temp = (Integer) object;
            return temp != 0;
        } else if (object instanceof Long) {
            Long temp = (Long) object;
            return temp != 0;
        } else if (object instanceof Float) {
            Float temp = (Float) object;
            return temp != 0.0;
        } else if (object instanceof Double) {
            Double temp = (Double) object;
            return temp != 0.0;
        } else {
            return true;
        }
    }
}
