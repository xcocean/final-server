import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import top.lingkang.finalserver.example.test04.ParamInfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2023/1/14
 */
public class Demo01 {
    public static void main(String[] args) throws Exception{
        Map<String,String> map=new HashMap<>();
        map.put("id","123");
        // BeanUtil.isBean()
        ParamInfo info=new ParamInfo();
        for (Map.Entry<String,String> entry:map.entrySet()){
            Field field = ParamInfo.class.getDeclaredField(entry.getKey());
            if (field==null)
                continue;
            field.setAccessible(true);
            field.set(info,entry.getValue());
        }
        System.out.println(info);
    }
}
