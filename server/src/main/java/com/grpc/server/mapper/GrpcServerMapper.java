package com.grpc.server.mapper;

import com.example.grpc.BiddingModel;
import com.grpc.server.payload.response.GrpcBidResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * version : 1.0
 * author : JUNSEOB
 */
@Mapper(componentModel = "spring")
public interface GrpcServerMapper {
    GrpcServerMapper instance = Mappers.getMapper(GrpcServerMapper.class);

    GrpcBidResponse toBidResponse(BiddingModel.BidResponse response);
}
