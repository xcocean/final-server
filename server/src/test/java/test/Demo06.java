package test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author lingkang
 * Created by 2022/12/10
 */
public class Demo06 {
    public static void main(String[] args) throws Exception {
        Method test = Test.class.getMethod("test", String.class, int.class);
        System.out.println(test.getParameters());
        for (Parameter parameter : test.getParameters())
            System.out.println(parameter.getName());// arg0 arg1
    }

    class Test {
        public void test(String name, int number) {

        }
    }
}
