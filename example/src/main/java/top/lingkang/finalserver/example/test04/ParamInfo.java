package top.lingkang.finalserver.example.test04;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author lingkang
 * Created by 2023/1/14
 */
@Data
public class ParamInfo {
    private int id;
    private String name;

    // 序列化时忽略输出此字段
    @JsonIgnore
    private String ig;
}
