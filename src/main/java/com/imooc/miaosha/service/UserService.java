package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.UserDao;
import com.imooc.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id){
        return userDao.getById(id);
    }

    @Transactional   //加这个注解的作用是让事务起作用，主键冲突的情况下的数据库会回滚
    // 如果不加这个注解，则2这条记录会插入成功，
    public Boolean tx() {
        User user1=new User();
        user1.setId(2);
        user1.setName("xiaoxiao");
        userDao.insert(user1);
        User user2=new User();
        user2.setId(1);
        user2.setName("xiao");
        userDao.insert(user2);

        return true;
    }
}
