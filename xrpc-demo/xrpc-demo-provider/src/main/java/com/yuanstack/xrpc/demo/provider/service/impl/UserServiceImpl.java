package com.yuanstack.xrpc.demo.provider.service.impl;

import com.yuanstack.xrpc.core.annotation.XProvider;
import com.yuanstack.xrpc.demo.api.dto.User;
import com.yuanstack.xrpc.demo.api.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 *
 * @author Sylvan
 * @date 2024/03/09  22:37
 */
@Service
@XProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(Integer userId) {
        return new User(userId, "Sylvan-" + System.currentTimeMillis());
    }
}
