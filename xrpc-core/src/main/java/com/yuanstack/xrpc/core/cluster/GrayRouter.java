package com.yuanstack.xrpc.core.cluster;

import com.yuanstack.xrpc.core.api.Router;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Sylvan
 * @date 2024/04/04  19:28
 */
@Slf4j
public class GrayRouter implements Router<InstanceMeta> {

    /**
     * 灰度流量占比
     */
    @Getter
    @Setter
    private Integer grayRatio;

    private final Random random = new Random();

    public GrayRouter(Integer grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {
        if (providers == null || providers.size() <= 1) {
            return providers;
        }

        List<InstanceMeta> normalNodes = new ArrayList<>();
        List<InstanceMeta> grayNodes = new ArrayList<>();

        providers.forEach(p -> {
            if ("true".equals(p.getParameters().get("gray"))) {
                grayNodes.add(p);
            } else {
                normalNodes.add(p);
            }
        });

        if (normalNodes.isEmpty() || grayNodes.isEmpty()) {
            return providers;
        }

        log.debug("grayRouter grayNodes/normalNodes is {}/{}, grayRatio is {}",
                grayNodes.size(), normalNodes.size(), grayRatio);

        if (grayRatio <= 0) {
            return normalNodes;
        } else if (grayRatio > 100) {
            return grayNodes;
        }

        if (random.nextInt(100) < grayRatio) {
            return grayNodes;
        } else {
            return normalNodes;
        }
    }
}
