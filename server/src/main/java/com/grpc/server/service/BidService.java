package com.grpc.server.service;

import com.example.grpc.BidServiceGrpc;
import com.example.grpc.BiddingModel;
import com.grpc.server.event.GrpcBidListener;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * version : 1.0
 * author : JUNSEOB
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BidService extends BidServiceGrpc.BidServiceImplBase {
    private double highestBidPrice = 0.0;
    private final GrpcBidListener bidListener;
    private final List<StreamObserver<BiddingModel.BidResponse>> observers = new ArrayList<>();
    // Unary --> 1:1 방식
    @Override
    public void bidding(BiddingModel.BidRequest request, StreamObserver<BiddingModel.BidResponse> responseObserver) {
        try{
            log.info("bidding: request: {}", request);

            /**
             *  이 부분에 서비스 로직 구현
             */

            // gRPC가 구현해준 모델 클래스
            BiddingModel.BidResponse response = BiddingModel.BidResponse
                    .newBuilder()
                    .setSuccess(true)
                    .setMessage("입찰 완료")
                    .setHighestBid(request.getBidPrice())
                    .build();

            // 1개의 요청에 대한 1개의 응답을 처리한다.
            responseObserver.onNext(response);
            // 완료 이벤트
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }
    // 서버 스트리밍 방식 --> 클라이언트의 요청이 시작되면 서버가 완료이벤트를 전송하기 전까지 클라이언트는 서버로부터 데이터를 받을수 있습니다. ( 1:N 방식 )
    @Override
    public void winBidding(BiddingModel.BidRequest request,StreamObserver<BiddingModel.BidResponse> responseObserver) {

        /**
         *  이 부분에 서비스 로직 구현
         */
        String bidder = request.getUserId();
        double bidPrice = request.getBidPrice();

        boolean success = bidPrice > highestBidPrice;
        String message = success ? "Bid placed successfully" : "Bid amount must be greater than the highest bid";

        if(success) {
            highestBidPrice = bidPrice;
        }

        BiddingModel.BidResponse response = BiddingModel.BidResponse
                .newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .setHighestBid(highestBidPrice)
                .build();
        // 클라이언트에의 요청은 1번이지만 서버는 아래처럼 여러번의 데이터를 스트리밍 할 수 있습니다.
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<BiddingModel.BidRequest> clientBidding(StreamObserver<BiddingModel.BidResponse> responseObserver) {
        return new StreamObserver<BiddingModel.BidRequest>() {

            @Override
            public void onNext(BiddingModel.BidRequest value) {
                String bidder = value.getUserId();
                double bidPrice = value.getBidPrice();

                boolean success = bidPrice > highestBidPrice;
                String message = success ? "Bid placed successfully" : "Bid amount must be greater than the highest bid";

                if(success) {
                    highestBidPrice = bidPrice;
                }

                BiddingModel.BidResponse response = BiddingModel.BidResponse.newBuilder()
                        .setSuccess(success)
                        .setMessage(message)
                        .setHighestBid(highestBidPrice)
                        .build();

                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(
                        Status.INTERNAL
                                .withDescription(t.getMessage())
                                .withCause(t)
                                .asRuntimeException()
                );
            }

            @Override
            public void onCompleted() {
                log.info("client Bidding on Completed");
                responseObserver.onCompleted();
            }
        };
    }

    // 양방향 스트리밍 방식 --> 클라이언트에서 완료이벤트가 전송하기 전까지, 서로간의 스트리밍을 진행합니다. ( N:N 방식 )
    @Override
    public StreamObserver<BiddingModel.BidRequest> streamBidding(StreamObserver<BiddingModel.BidResponse> responseObserver) {
        return new StreamObserver<BiddingModel.BidRequest>() {
            @Override
            public void onNext(BiddingModel.BidRequest value) {
                /**
                 *  이 부분에 서비스 로직 구현
                 */

                String bidder = value.getUserId();
                double bidPrice = value.getBidPrice();

                boolean success = bidPrice > highestBidPrice;
                String message = success ? "Bid placed successfully" : "Bid amount must be greater than the highest bid";

                BiddingModel.BidResponse response = BiddingModel.BidResponse.newBuilder()
                        .setSuccess(success)
                        .setMessage(message)
                        .setHighestBid(bidPrice)
                        .build();

                if(success) {
                    highestBidPrice = bidPrice;
                    bidListener.sendBidResponse(response);
                }

                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(
                        Status.INTERNAL
                                .withDescription(t.getMessage())
                                .withCause(t)
                                .asRuntimeException()
                );
            }

            @Override
            public void onCompleted() {
                log.info("streamBidding onCompleted");
                // 클라이언트의 완료이벤트가 오면 서버도 완료이벤트를 진행한다
                responseObserver.onCompleted();
            }
        };
    }
}
