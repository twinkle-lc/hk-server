package com.lc.hk.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lc
 **/

@Data
@Configuration
@ConfigurationProperties(prefix = "minio-view")
public class MinioConfigView {
    private String endpoint;
    private String accessKey;
    private String secretKey;

    @Bean(name = "minioClientView")
    public MinioClient minioClientView() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
        return minioClient;
    }
}
