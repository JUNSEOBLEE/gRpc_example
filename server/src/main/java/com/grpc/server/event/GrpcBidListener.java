package com.grpc.server.event;

import com.example.grpc.BiddingModel;
import com.grpc.server.mapper.GrpcServerMapper;
import com.grpc.server.payload.response.GrpcBidResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * version : 1.0
 * author : JUNSEOB
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcBidListener {
    private final StreamBridge streamBridge;

    public void sendBidResponse(BiddingModel.BidResponse response) {
        System.out.println("################################");
        System.out.println(response);
        System.out.println("################################");
        GrpcBidResponse bidResponse = GrpcServerMapper.instance.toBidResponse(response);
        Message<GrpcBidResponse> bidResponseMessage = MessageBuilder.withPayload(bidResponse)
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString()).build();
        streamBridge.send("grpc-bidding-response", bidResponseMessage);
    }
}
