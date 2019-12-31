package com.yongkang.dubbo;

import com.yongkang.domain.User;

/**
 * Created by Donghua.Chen on 2018/5/29.
 */
public interface UserDubboService {

    User findUserById(Long userId);
}
