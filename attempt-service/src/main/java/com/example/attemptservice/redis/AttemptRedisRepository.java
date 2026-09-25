package com.example.attemptservice.redis;

import org.springframework.data.repository.CrudRepository;

public interface AttemptRedisRepository extends CrudRepository<AttemptRedisHash, String> {
}