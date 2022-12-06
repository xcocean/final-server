import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class AppMain {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        System.out.println(context.getBeanDefinitionNames());
        TestBean bean = context.getBean(TestBean.class);
        bean.hi();
    }
}
