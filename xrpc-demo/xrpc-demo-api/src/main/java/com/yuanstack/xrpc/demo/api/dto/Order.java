package com.yuanstack.xrpc.demo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户模型类
 *
 * @author Sylvan
 * @date 2024/03/09  22:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    private Long Id;

    private Float amount;
}
