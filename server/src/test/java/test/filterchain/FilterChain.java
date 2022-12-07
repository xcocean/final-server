package test.filterchain;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public class FilterChain {
    public Filter[] filters;
    public int length = 0, current = 0;

    public FilterChain(Filter[] filters) {
        this.filters = filters;
        length = filters.length;
    }

    void doFilter(Object req, Object res) throws Exception {
        if (current < length) {
            current++;// 自增
            filters[current - 1].doFilter(req, res, this);
        } else {
            // 在此处调用处理逻辑方法
            System.out.println("finish");
        }
    }
}
