package com.yongkang.dao;

import com.yongkang.model.UserDomain;

import java.util.List;

/**
 * Created by Donghua.Chen on 2018/5/29.
 */
public interface UserDao {

    int insert(UserDomain record);



    List<UserDomain> selectUsers();
}
