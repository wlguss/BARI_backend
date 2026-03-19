package com.bari.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import lombok.Getter;
import lombok.Setter;

@Configuration
public class AwsS3Config {

    @Bean
    @ConfigurationProperties(prefix = "cloud.aws")
    public AwsProperties awsProperties() {
        return new AwsProperties();
    }

    @Bean
    public S3Presigner s3Presigner(AwsProperties awsProperties) {
        return S3Presigner.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Getter
    @Setter
    public static class AwsProperties {
        private String region;
        private S3Properties s3 = new S3Properties();
    }

    @Getter
    @Setter
    public static class S3Properties {
        private String bucket;
    }
}
