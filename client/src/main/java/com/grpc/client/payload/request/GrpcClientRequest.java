package com.grpc.client.payload.request;

import lombok.Data;

/**
 * version : 1.0
 * author : JUNSEOB
 */
@Data
public class GrpcClientRequest {
    private String userId;
    private Integer bidPrice;
}
