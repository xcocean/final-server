package test.filterchain;

/**
 * @author lingkang
 * Created by 2022/12/7
 * 过滤器实现原理，递归实现
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Filter[] filters = new Filter[2];// 自定义过滤
        filters[0] = new F1();
        filters[1] = new F2();
        FilterChain filterChain = new FilterChain(filters);

        Object req = null, res = null;
        filterChain.doFilter(req, res);// 模拟过滤

        System.out.println("OK");
    }

    static class F1 implements Filter {
        @Override
        public void doFilter(Object req, Object res, FilterChain filterChain) throws Exception {
            System.out.println("F1 前");
            filterChain.doFilter(req, res);
            System.out.println("F1 后");
        }
    }

    static class F2 implements Filter {
        @Override
        public void doFilter(Object req, Object res, FilterChain filterChain) throws Exception {
            System.out.println("F2 前");
            filterChain.doFilter(req, res);
            System.out.println("F2 后");
        }
    }
}
