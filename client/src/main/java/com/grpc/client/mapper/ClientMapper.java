package com.grpc.client.mapper;

import com.example.grpc.BiddingModel;
import com.grpc.client.payload.request.GrpcClientRequest;
import com.grpc.client.payload.response.GrpcClientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * version : 1.0
 * author : JUNSEOB
 */
@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientMapper instance = Mappers.getMapper(ClientMapper.class);

    BiddingModel.BidRequest toBiddingModelRequest(GrpcClientRequest grpcClientRequest);

    GrpcClientResponse toGrpcClientResponse(BiddingModel.BidResponse value);
}
