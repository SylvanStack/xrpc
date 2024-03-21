package com.yuanstack.xrpc.core.cluster;

import com.yuanstack.xrpc.core.api.Loadbalancer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * @author Sylvan
 * @date 2024/03/20  23:15
 */
@Component
public class RandomLoadbalancer<T> implements Loadbalancer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if (CollectionUtils.isEmpty(providers)) {
            return null;
        }

        if (providers.size() == 1) {
            return providers.get(0);
        }

        return providers.get(random.nextInt(providers.size()));
    }
}
