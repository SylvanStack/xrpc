package com.yuanstack.xrpc.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/23  17:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    List<String> data;
}
