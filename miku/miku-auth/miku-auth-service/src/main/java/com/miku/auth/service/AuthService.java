package com.miku.auth.service;

import com.miku.auth.client.UserClient;
import com.miku.auth.config.JwtProperties;
import com.miku.common.pojo.UserInfo;
import com.miku.common.utils.JwtUtils;
import com.miku.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    public String accredit(String username, String password) {

        //1、根据用户名和密码查询
        User user = this.userClient.queryUser(username, password);

        //2、判断user
        if(user == null){
            return null;
        }


        try {
            //3、jwtUtils生成jwt类型的token
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            return JwtUtils.generateToken(userInfo, this.jwtProperties.getPrivateKey(),this.jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
