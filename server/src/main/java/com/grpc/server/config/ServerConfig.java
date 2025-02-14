package com.grpc.server.config;

import com.grpc.server.service.BidService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * version : 1.0
 * author : JUNSEOB
 */
@Configuration
@RequiredArgsConstructor
public class ServerConfig {
    @Value("${grpc.port}")
    private Integer port;
    private final BidService bidService;

    @Bean
    public Server grpcServer() {
           return ServerBuilder
                   .forPort(port)
                   .addService(bidService)
                   .build();
    }
}
