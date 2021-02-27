package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {
    @Select("select * from user where id= #{id}")
    public User getById(@Param("id") int id);    //参数ID来通过@Param来定义，然后就可以在SQL中引用了

    @Insert("insert into user(id, name)values(#{id}, #{name})")
    public int insert(User user);
}
