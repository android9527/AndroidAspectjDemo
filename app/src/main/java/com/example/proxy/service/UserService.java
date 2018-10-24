package com.example.proxy.service;

import com.example.proxy.annotation.Log;

public interface UserService
{
    @Log
    void addUser(String name);

    void remove(String name);
}