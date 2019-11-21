package tw.edu.bircdemo.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.hibernate.SessionFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
public class PersistenceConfig {

    @Value("${db.driver}")
    private String driver;
    @Value("${db.username}")
    private String username;
    @Value("${db.password}")
    private String password;
    @Value("${db.url}")
    private String url;
    @Value("${root}")
    private String root;
    @Value("${jpa.hibernate.dialect}")
    private String dialect;
    @Value("${jpa.hibernate.show.sql}")
    private boolean isShowSQL;
    @Value("${jpa.hibernate.format.sql}")
    private boolean isFormatSQL;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlStatus;

    @Bean
    public DataSource dataSource() {
        //    	datasource 設定
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(driver);
            dataSource.setJdbcUrl(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);
            // 當 connection pool 空時，自動增加連線數量
            dataSource.setAcquireIncrement(3);
            // connection pool 保持最小的連接數量
            dataSource.setMinPoolSize(10);
            // connection 最大連線數量
            dataSource.setMaxPoolSize(100);
            // 連線生存時間
            dataSource.setMaxConnectionAge(1800);
            // 初始 connection pool size
            dataSource.setInitialPoolSize(15);
            // 檢查 connection pool 的 connection 是否使用，已秒為單位
            dataSource.setIdleConnectionTestPeriod(120);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Bean
    public SessionFactory sessionFactory() {
//    	Hibernate SessionFactory 設定
        LocalSessionFactoryBean lsfb = new LocalSessionFactoryBean();
        try {
            lsfb.setDataSource(dataSource());
            lsfb.setPackagesToScan(root);
            lsfb.setHibernateProperties(hibernateProperties());
            lsfb.afterPropertiesSet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lsfb.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
//    	TransactioManager 設定
        HibernateTransactionManager htm = new HibernateTransactionManager();
        htm.setDataSource(dataSource());
        htm.setSessionFactory(sessionFactory());
        return htm;
    }

    @Bean
    public HibernateTemplate hibernateTemplate() {
        return new HibernateTemplate(sessionFactory());
    }

    private Properties hibernateProperties() {
//    	Hibernate 設定
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", ddlStatus);
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.show_sql", isShowSQL);
        properties.put("hibernate.format_sql", isFormatSQL);
        return properties;
    }

    @Bean
    public TransactionInterceptor transactionInterceptor() {
//    	交易攔截器，設定 service method 交易行為
        Properties props = new Properties();
        props.setProperty("add*", "PROPAGATION_REQUIRED,-java.lang.RuntimeException");
        props.setProperty("create*", "PROPAGATION_REQUIRED,-java.lang.RuntimeException");
        props.setProperty("update*", "PROPAGATION_REQUIRED,-java.lang.RuntimeException");
        props.setProperty("delete*", "PROPAGATION_REQUIRED,-java.lang.RuntimeException");
        props.setProperty("get*", "PROPAGATION_NOT_SUPPORTED,readOnly");
        props.setProperty("search*", "PROPAGATION_NOT_SUPPORTED,readOnly");
        props.setProperty("count*", "PROPAGATION_NOT_SUPPORTED,readOnly");
        props.setProperty("*", "PROPAGATION_NOT_SUPPORTED,readOnly");
        return new TransactionInterceptor(transactionManager(), props);
    }

    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor() {
        // AOP advisor：AOP 切入點，在 service method 增加交易
        AspectJExpressionPointcut expression = new AspectJExpressionPointcut();
        expression.setExpression("execution(* tw.org.sunlight.service.*.*(..))");
        return new DefaultPointcutAdvisor(expression, transactionInterceptor());
    }
}
