package com.example.testservice.client;

import com.example.testservice.dto.QuestionInternalDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "test-service", url = "${internal.test-service.url}")
public interface TestServiceInternalClient {
    @GetMapping("/internal/mock-tests/{id}/questions/answer-key")
    List<QuestionInternalDto> getAnswerKey(@PathVariable("id") String id,
                                           @RequestHeader("X-Internal-Auth") String internalSecret);
}
