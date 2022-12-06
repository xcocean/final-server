package test;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
@Component
public class TestBean {
    public void hi() {
        System.out.println("hi" + new Date());
    }
}
