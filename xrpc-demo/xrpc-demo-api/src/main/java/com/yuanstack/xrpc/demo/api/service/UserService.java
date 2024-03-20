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

    /**
     * 方法重载Case
     *
     * @param userId 用户Id
     * @return 用户信息
     */
    User findById(Integer userId, String nickname);

    /**
     * 方法重载Case
     */
    String getName(Integer userId);

    /**
     * 方法重载Case
     */
    String getName(String nickname);

    /**
     * 类型转换Case
     */
    Long getUserId(Long userId);

    /**
     * 类型转换Case
     */
    Long getUserId(User user);

    /**
     * 类型转换Case
     */
    Long getUserId(Float userId);

    /**
     * 类型转换Case
     */
    Long getUserId();

    /**
     * 类型转换Case
     */
    int[] getUserIds(int[] userIds);

    /**
     * 返回基本类型Case
     *
     * @param userId 用户Id
     * @return int
     */
    int getId(Integer userId);
}
