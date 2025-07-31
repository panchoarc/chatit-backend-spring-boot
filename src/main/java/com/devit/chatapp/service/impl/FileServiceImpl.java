package com.devit.chatapp.service.impl;

import com.devit.chatapp.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {


    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;


    @Override
    public String uploadFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
        String uniqueFileName = UUID.randomUUID() + fileExtension;

        try {
            byte[] fileBytes = file.getBytes();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(fileBytes));

            return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(uniqueFileName)).toExternalForm();

        } catch (IOException e) {
            log.error("❌ Error reading file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read file: " + originalFileName);
        } catch (SdkException e) {
            log.error("❌ AWS SDK Exception: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to S3: " + originalFileName);
        } catch (Exception e) {
            log.error("❌ Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error uploading file: " + originalFileName);
        }
    }

}
