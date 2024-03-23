package com.yuanstack.xrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述服务元数据
 *
 * @author Sylvan
 * @date 2024/03/23  18:44
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMeta {

    private String app;
    private String namespace;
    private String env;
    private String name;

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }
}
