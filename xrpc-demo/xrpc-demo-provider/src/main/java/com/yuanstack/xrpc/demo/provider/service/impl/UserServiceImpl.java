package com.yuanstack.xrpc.demo.provider.service.impl;

import com.yuanstack.xrpc.core.annotation.XProvider;
import com.yuanstack.xrpc.core.api.RpcContext;
import com.yuanstack.xrpc.demo.api.dto.User;
import com.yuanstack.xrpc.demo.api.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 用户服务实现类
 *
 * @author Sylvan
 * @date 2024/03/09  22:37
 */
@Service
@XProvider
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private Environment environment;

    @Override
    public User findById(Integer userId) {
        return new User(userId, "Sylvan-" + environment.getProperty("server.port") + "-" + System.currentTimeMillis());
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
        log.info(String.valueOf(userId));
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
    public List<User> getUserIds(List<User> users) {
        return null;
    }

    @Override
    public Map<String, User> getUserIds(Map<String, User> userMap) {
        return null;
    }

    @Override
    public int getId(Integer userId) {
        return userId;
    }

    String ports = "8081";

    @Override
    public User findTimeOut(int timeout) {
        String property = environment.getProperty("server.port");
        if (Arrays.asList(ports.split(",")).contains(property)) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return new User(Integer.parseInt(property == null ? "1000" : property),
                "Sylvan-" + environment.getProperty("server.port"));
    }


    public String setPorts(String ports) {
        this.ports = ports;
        return this.ports;
    }

    @Override
    public String echoParameter(String key) {
        RpcContext.ContextParameters.get().forEach(
                (k, v) -> log.info("RpcContext.ContextParameters key:value is [{}:{}]", k, v));
        return RpcContext.getContextParameter(key);
    }
}
