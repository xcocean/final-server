import com.alibaba.fastjson2.JSON;
import top.lingkang.finalserver.server.utils.TypeUtils;

import java.math.BigDecimal;

/**
 * @author lingkang
 * Created by 2023/1/15
 */
public class Demo02 {
    public static void main(String[] args) {
        BigDecimal decimal=new BigDecimal(66);
        System.out.println(TypeUtils.isBaseType(decimal.getClass()));
        System.out.println(JSON.toJSONString("asdasd"));
    }
}
