package com.grpc.client.controller;

import com.example.grpc.BiddingModel;
import com.grpc.client.payload.request.GrpcClientRequest;
import com.grpc.client.payload.response.GrpcClientResponse;
import com.grpc.client.service.GrpcClientService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * version : 1.0
 * author : JUNSEOB
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class GrpcClientController {
    private final GrpcClientService clientService;

    @PostMapping("/bidding")
    public GrpcClientResponse bidding(@RequestBody GrpcClientRequest grpcClientRequest) throws Exception {
        return clientService.bidding(grpcClientRequest);
    }

    @PostMapping("/winBidding")
    public void winBidding(@RequestBody GrpcClientRequest grpcClientRequest) throws Exception {
        clientService.winBidding(grpcClientRequest);
    }

    @PostMapping("/clientBidding")
    public void clientBidding(@RequestBody GrpcClientRequest grpcClientRequest) {
        clientService.clientBidding(grpcClientRequest);
    }

    @PostMapping("/streamBidding")
    public void streamBidding(@RequestBody GrpcClientRequest grpcClientRequest) throws Exception {
        clientService.streamBidding(grpcClientRequest);
    }

}
