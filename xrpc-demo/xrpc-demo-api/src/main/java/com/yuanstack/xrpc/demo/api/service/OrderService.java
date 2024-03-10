package com.yuanstack.xrpc.demo.api.service;

import com.yuanstack.xrpc.demo.api.dto.Order;

/**
 * 订单服务类
 *
 * @author Sylvan
 * @date 2024/03/09日 22:16
 */
public interface OrderService {

    /**
     * 根据订单Id查询订单
     *
     * @param userId 订单Id
     * @return 订单信息
     */
    Order findById(Integer userId);
}
