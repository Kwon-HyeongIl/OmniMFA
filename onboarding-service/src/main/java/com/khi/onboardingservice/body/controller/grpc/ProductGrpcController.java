package com.khi.onboardingservice.body.controller.grpc;

import com.khi.onboardingservice.body.service.ProductService;
import com.khi.product.grpc.ProductGrpcServiceGrpc;
import com.khi.product.grpc.ProductRequest;
import com.khi.product.grpc.ProductResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ProductGrpcController extends ProductGrpcServiceGrpc.ProductGrpcServiceImplBase {

    private final ProductService productService;

    @Override
    public void getProductName(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {

        String productId = request.getProductId();
        log.info("gRPC 요청 수신 - 제품 이름 조회, productId: {}", productId);

        String productName = productService.getProductNameByProductId(productId)
                .orElse("Unknown Product");

        ProductResponse response = ProductResponse.newBuilder()
                .setProductName(productName)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
