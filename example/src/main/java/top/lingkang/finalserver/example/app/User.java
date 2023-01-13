package top.lingkang.finalserver.example.app;

import top.lingkang.finalsql.annotation.Column;
import top.lingkang.finalsql.annotation.Id;
import top.lingkang.finalsql.annotation.Table;

import java.util.Date;

/**
 * @author lingkang
 * 2023/1/11
 **/
@Table
public class User {
    @Id
    private Integer id;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String nickname;
    @Column
    private Date createTime;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
