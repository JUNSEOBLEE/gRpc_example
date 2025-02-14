package com.grpc.client.config;

import com.example.grpc.BidServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * version : 1.0
 * author : JUNSEOB
 */
@Component
@RequiredArgsConstructor
public class ClientConfig {

    @Value("${grpc.host}")
    private String host;
    @Value("${grpc.port}")
    private Integer port;

    @Bean
    public BidServiceGrpc.BidServiceBlockingStub blockingStub() {
        ManagedChannel managedChannel = getChannel();
        return BidServiceGrpc.newBlockingStub(managedChannel);
    }

    @Bean
    public BidServiceGrpc.BidServiceStub stub() {
        ManagedChannel managedChannel = getChannel();
        return BidServiceGrpc.newStub(managedChannel);
    }

    private ManagedChannel getChannel() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        return managedChannel;
    }

}
