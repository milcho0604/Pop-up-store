package com.store.popup.member.repository;//package com.padaks.todaktodak.member.repository;
//
//import com.padaks.todaktodak.member.dto.EmailVerificationDto;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import org.springframework.stereotype.Repository;
//
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//
//@Repository
//public class EmailVerificationRepositoryRedis {
//    private static final String KEY_PREFIX = "emailVerification"; // emailVerification{name}:{verificationToken}
//    private final RedisTemplate<Object, Object> redisTemplate;
//
//    public EmailVerificationRepositoryRedis(RedisTemplate<Object, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
//        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//    }
//
//    public void save(String name, EmailVerificationDto emailVerificationDto, long timeout) {
//        String key = KEY_PREFIX + name + ":" + emailVerificationDto.getVerificationToken();
//        redisTemplate.opsForValue().set(key, emailVerificationDto, timeout, TimeUnit.MINUTES);
//    }
//
//    public EmailVerificationDto findByVerificationToken(String name, String verificationToken) {
//        String keyPattern = KEY_PREFIX + name + ":" + verificationToken;
//        String key = (String) Objects.requireNonNull(redisTemplate.keys(keyPattern)).stream().findFirst().orElse(null);
//        return key != null ? (EmailVerificationDto) redisTemplate.opsForValue().get(key) : null;
//    }
//
//    public void deleteByVerificationToken(String name, String verificationToken) {
//        String keyPattern = KEY_PREFIX + name + ":" + verificationToken;
//        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(keyPattern)));
//    }
//}
