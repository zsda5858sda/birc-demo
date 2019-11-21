package tw.edu.bircdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class BircDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BircDemoApplication.class, args);
    }

}
