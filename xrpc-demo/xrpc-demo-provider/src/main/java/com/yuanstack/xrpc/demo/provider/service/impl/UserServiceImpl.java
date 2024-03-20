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

    @Override
    public User findById(Integer userId, String nickname) {
        return new User(userId, "Sylvan-" + nickname + "-" + System.currentTimeMillis());
    }

    @Override
    public String getName(Integer userId) {
        return "Sylvan-" + userId + "-" + System.currentTimeMillis();
    }

    @Override
    public String getName(String nickname) {
        return "Sylvan-" + nickname + "-" + System.currentTimeMillis();
    }

    @Override
    public Long getUserId(Long userId) {
        return userId;
    }

    @Override
    public Long getUserId(User user) {
        return Long.valueOf(user.getId());
    }

    @Override
    public Long getUserId(Float userId) {
        System.out.println(userId);
        ;
        return 10L;
    }

    @Override
    public Long getUserId() {
        return 10L;
    }

    @Override
    public int[] getUserIds(int[] userIds) {
        return userIds;
    }

    @Override
    public int getId(Integer userId) {
        return userId;
    }
}
