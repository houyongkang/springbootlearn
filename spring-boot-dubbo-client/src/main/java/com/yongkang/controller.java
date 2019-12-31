package com.yongkang;

import com.yongkang.aa.UserDubboConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: Yongkang Hou
 * @Date: 2019-07-24
 */
@RestController

public class controller {
    @Autowired
    private UserDubboConsumerService userDubboConsumerService;

    @RequestMapping("/hello")
    public String hello() {
        userDubboConsumerService.printUser ();
        return "hello";
    }
}
