package com.yuanstack.xrpc.core.api;

import com.yuanstack.xrpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * 负载均衡，weightedRR，AAWR-自适应，
 * 8081， w=100, 25次
 * 8082， w=300, 75次
 * 0-99，random，<25， -8081， else  8082
 * UserService  10，，，
 * 8081， 10ms，
 * 8082， 100ms，
 * avg * 0.3 + last * 0.7 = W* ~
 *
 * @author Sylvan
 * @date 2024/03/20  23:15
 */
public interface Loadbalancer<T> {

    T choose(List<T> providers);

    Loadbalancer<InstanceMeta> Default = p -> (p == null || p.isEmpty()) ? null : p.get(1);
}
