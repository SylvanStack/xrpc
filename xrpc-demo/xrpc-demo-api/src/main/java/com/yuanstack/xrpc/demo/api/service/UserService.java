package com.yuanstack.xrpc.demo.api.service;

import com.yuanstack.xrpc.demo.api.dto.User;

/**
 * 用户服务类
 *
 * @author Sylvan
 * @date 2024/03/09日 22:16
 */
public interface UserService {

    /**
     * 根据用户Id查询用户
     *
     * @param userId 用户Id
     * @return 用户信息
     */
    User findById(Integer userId);
}
