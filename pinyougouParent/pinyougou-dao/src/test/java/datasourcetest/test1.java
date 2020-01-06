package datasourcetest;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class test1 {
    @Test
    public void  test(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext-dao.xml");
        Object dataSource =context.getBean("dataSource");
        System.out.println(dataSource);
    }
}
