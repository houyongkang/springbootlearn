package com.yongkang.service;

import com.github.pagehelper.PageInfo;
import com.yongkang.model.UserDomain;

/**
 * Created by Donghua.Chen on 2018/5/29.
 */
public interface UserService {

    int addUser(UserDomain user);

    PageInfo<UserDomain> findAllUser(int pageNum, int pageSize);
}
