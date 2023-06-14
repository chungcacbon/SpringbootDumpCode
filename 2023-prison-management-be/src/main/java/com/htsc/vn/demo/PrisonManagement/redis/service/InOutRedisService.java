package com.htsc.vn.demo.PrisonManagement.redis.service;

import com.google.gson.Gson;
import com.htsc.vn.demo.PrisonManagement.Uils.Common;
import com.htsc.vn.demo.PrisonManagement.Uils.StatusUser;
import com.htsc.vn.demo.PrisonManagement.model.InOutResponse;
import com.htsc.vn.demo.PrisonManagement.redis.model.CheckingLogDTORedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InOutRedisService {


    private final RedisTemplate<String, CheckingLogDTORedis> redisTemplateNew;
    private final RedisTemplate<String, InOutResponse> redisTemplateJson;

    public InOutRedisService(RedisTemplate<String, CheckingLogDTORedis> redisTemplateNew,RedisTemplate<String, InOutResponse> redisTemplateJson) {
        this.redisTemplateNew = redisTemplateNew;
        this.redisTemplateJson = redisTemplateJson;
        redisTemplateNew.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(CheckingLogDTORedis.class));
        redisTemplateNew.setValueSerializer(new Jackson2JsonRedisSerializer<>(CheckingLogDTORedis.class));
        redisTemplateNew.setHashKeySerializer(redisTemplateNew.getStringSerializer());
        redisTemplateNew.setKeySerializer(redisTemplateNew.getStringSerializer());
    }

    public void addDataRenderTORedis(String redisKey, InOutResponse data) {
        // Xóa bản ghi cũ nếu tồn tại
        redisTemplateJson.delete(redisKey);

        // Lưu bản ghi mới
        redisTemplateJson.opsForValue().set(redisKey, data);
    }
    public InOutResponse getDataRenderFromRedis(String redisKey) {
        Gson gson = new Gson();
        String json = gson.toJson(redisTemplateJson.opsForValue().get(redisKey));
        return gson.fromJson(json, InOutResponse.class);
    }
    public void deteleDataRedis(Set<String> key){
        redisTemplateNew.delete(key);
    }

    public List<CheckingLogDTORedis> getDataFromRedisCommon(String redisKey) {
        return redisTemplateNew.opsForList().range(redisKey, 0, -1);
    }
    public void saveOrUpdateUser(CheckingLogDTORedis user) {
        redisTemplateNew.opsForHash().put(Common.REDIS_KEY_COMMON, user.getId(), user);
    }
    public void deleteUserById(String userId) {
        redisTemplateNew.opsForHash().delete(Common.REDIS_KEY_COMMON, userId);
    }

    public List<CheckingLogDTORedis> getCheckingLogsByGroupUserId(int groupUserId) {
        Map<Object, Object> checkingLogsMap = redisTemplateNew.opsForHash().entries(Common.REDIS_KEY_COMMON);
        List<CheckingLogDTORedis> checkingLogs = new ArrayList<>();
        for (Object value : checkingLogsMap.values()) {
            checkingLogs.add((CheckingLogDTORedis) value);
        }
        return checkingLogs.stream()
                .filter(log -> log.getGroupUserId() == groupUserId)
                .collect(Collectors.toList());
    }
    public List<CheckingLogDTORedis> getAllDataFromRedis() {
        List<Object> dataList = redisTemplateNew.opsForHash().values(Common.REDIS_KEY_COMMON);
        List<CheckingLogDTORedis> result = new ArrayList<>();
        for (Object data : dataList) {
            result.add((CheckingLogDTORedis) data);
        }
        return result;
    }

}
