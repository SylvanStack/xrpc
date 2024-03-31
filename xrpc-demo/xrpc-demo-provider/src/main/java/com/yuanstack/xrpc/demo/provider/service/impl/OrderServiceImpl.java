package com.yuanstack.xrpc.demo.provider.service.impl;

import com.yuanstack.xrpc.core.annotation.XProvider;
import com.yuanstack.xrpc.core.api.RpcException;
import com.yuanstack.xrpc.demo.api.dto.Order;
import com.yuanstack.xrpc.demo.api.service.OrderService;
import org.springframework.stereotype.Service;

/**
 * 订单服务实现类
 *
 * @author Sylvan
 * @date 2024/03/09  22:37
 */
@Service
@XProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer orderId) {
        if (orderId == 404) {
            throw new RpcException("404 Exception");
        }

        return new Order(orderId.longValue(), 99.99F);
    }
}
