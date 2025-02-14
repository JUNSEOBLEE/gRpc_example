package com.grpc.server.payload.response;

/**
 * version : 1.0
 * author : JUNSEOB
 */
public record GrpcBidResponse(
        boolean success,
        String message,
        double highestBid
) {
}
