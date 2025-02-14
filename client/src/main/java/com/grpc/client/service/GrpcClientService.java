package com.grpc.client.service;

import com.example.grpc.BidServiceGrpc;
import com.example.grpc.BiddingModel;
import com.grpc.client.mapper.ClientMapper;
import com.grpc.client.payload.request.GrpcClientRequest;
import com.grpc.client.payload.response.GrpcClientResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * version : 1.0
 * author : JUNSEOB
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcClientService {
    
    private final BidServiceGrpc.BidServiceBlockingStub blockingStub;
    private final BidServiceGrpc.BidServiceStub stub;

    public GrpcClientResponse bidding(GrpcClientRequest grpcClientRequest) {
        // 받아온 파라미터를 gRPC 모델로 매핑
        BiddingModel.BidRequest bidRequest = ClientMapper.instance.toBiddingModelRequest(grpcClientRequest);
        // 단방향 RPC 호출
        BiddingModel.BidResponse response = blockingStub.bidding(bidRequest);
        return ClientMapper.instance.toGrpcClientResponse(response);
    }

    public StreamObserver<BiddingModel.BidRequest> clientBidding(GrpcClientRequest grpcClientRequest) {
        BiddingModel.BidRequest bidRequest = ClientMapper.instance.toBiddingModelRequest(grpcClientRequest);
        StreamObserver<BiddingModel.BidRequest> requestStreamObserver = stub.clientBidding(new StreamObserver<BiddingModel.BidResponse>() {
            @Override
            public void onNext(BiddingModel.BidResponse value) {
                log.info("value : {}", value);
            }

            @Override
            public void onError(Throwable t) {
                throw new RuntimeException(t);
            }

            @Override
            public void onCompleted() {
                log.info("on completed");
            }
        });
        requestStreamObserver.onNext(bidRequest);
        return requestStreamObserver;
    }

    public void winBidding(GrpcClientRequest grpcClientRequest) {
        // 받아온 파라미터를 gRPC 모델로 매핑
        BiddingModel.BidRequest bidRequest = ClientMapper.instance.toBiddingModelRequest(grpcClientRequest);
        stub.winBidding(bidRequest, new StreamObserver<BiddingModel.BidResponse>() {

            @Override
            public void onNext(BiddingModel.BidResponse value) {
                System.out.println(value);
            }

            @Override
            public void onError(Throwable t) {
                throw new RuntimeException(t);
            }

            @Override
            public void onCompleted() {
                System.out.println("서버 스트리밍 완료");
            }
        });
    }

    public void streamBidding(GrpcClientRequest grpcClientRequest) throws InterruptedException {
        // 받아온 파라미터를 gRPC 모델로 매핑
        BiddingModel.BidRequest bidRequest = ClientMapper.instance.toBiddingModelRequest(grpcClientRequest);
        CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<BiddingModel.BidRequest> requestStreamObserver = stub.streamBidding(new StreamObserver<BiddingModel.BidResponse>() {
            @Override
            public void onNext(BiddingModel.BidResponse value) {
                System.out.println(value);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Bid stream completed.");
                finishLatch.countDown();
            }
        });
        requestStreamObserver.onNext(bidRequest);

        requestStreamObserver.onCompleted();
        finishLatch.await(1, TimeUnit.MINUTES);
    }
}
