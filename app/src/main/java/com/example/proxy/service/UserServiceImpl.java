package com.example.proxy.service;

/**
 * Created by chenfeiyue on 2018/10/17.
 * Email:feiyuechen@pptv.com
 * Description ：
 */
public class UserServiceImpl implements UserService
{
    @Override
    public void addUser(String name)
    {
        System.out.println("UserServiceImpl: addUser " + name);
    }

    @Override
    public void remove(String name)
    {
        System.out.println("UserServiceImpl: remove " + name);
    }
}
