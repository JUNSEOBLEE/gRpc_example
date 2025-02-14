package com.grpc.server.runner;

import io.grpc.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * version : 1.0
 * author : JUNSEOB
 */
@Component
@RequiredArgsConstructor
public class ServerRunner implements ApplicationRunner, DisposableBean {

    private final Server grpcServer;

    @Override
    public void destroy() throws Exception {
        if(!ObjectUtils.isEmpty(grpcServer)) {
            grpcServer.shutdown();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        grpcServer.start();
        grpcServer.awaitTermination();
    }
}
