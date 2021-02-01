package com.redistemplate.web.rest;

import com.redistemplate.model.TemplateEntity;
import com.redistemplate.repository.TemplateEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api")
public class StatusController {

    private static final String REDIS_KEY_PAYLOAD = "payload";

    private final Logger log = LoggerFactory.getLogger(StatusController.class);
    public static final String SERVER_NAME = InetAddress.getLoopbackAddress().getCanonicalHostName();
    private final RedisTemplate<String, TemplateEntity> redisTemplate;
    private final TemplateEntityRepository repository;

    public StatusController(RedisTemplate<String, TemplateEntity> redisTemplate, TemplateEntityRepository repository) {
        this.redisTemplate = redisTemplate;
        this.repository = repository;
        testRedisTemplate();
    }

    private boolean testRedisTemplate() {
        try {
            line();
            log.info("Key '{}' exists = {}", REDIS_KEY_PAYLOAD, redisTemplate.hasKey(REDIS_KEY_PAYLOAD));
            redisTemplate.opsForValue().set(REDIS_KEY_PAYLOAD, new TemplateEntity("default_id", "default_value"));
            TemplateEntity readEntity = redisTemplate.opsForValue().get(REDIS_KEY_PAYLOAD);
            log.info("Entity for key {} = {}", REDIS_KEY_PAYLOAD, readEntity.toString());
            line();
            return true;
        } catch (Exception e) {
            line();
            log.error("Exception: ", e);
            line();
        }
        return false;
    }

    private void line() {
        log.info("-------------------------");
    }

    @GetMapping("status")
    public Status getStatus() {
        return new Status(testRedisTemplate() ? "OK" : "Error", SERVER_NAME, getUpTime());
    }

    @GetMapping("entity")
    public List<TemplateEntity> getAll() {
        try {
            String id = UUID.randomUUID().toString();
            TemplateEntity entity = new TemplateEntity(id, "value_for_" + id);
            repository.save(entity);
            repository.findById(entity.getId()).ifPresent(e -> log.info(e.toString()));
            return readRedisRepository();
        } catch (Exception e) {
            line();
            log.error("Exception: ", e);
            line();
        }
        return new ArrayList<>();
    }

    @GetMapping("clear")
    public void deleteAll() {
        try {
            repository.deleteAll();
        } catch (Exception e) {
            line();
            log.error("Exception: ", e);
            line();
        }
    }

    private List<TemplateEntity> readRedisRepository() {
        try {
            List<TemplateEntity> list = new ArrayList<>();
            repository.findAll().forEach(list::add);
            return list;
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ArrayList<>();
        }
    }

    private String getUpTime() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long millis = uptime % 1000;
        long seconds = (uptime / 1000) % 60;
        long minutes = (uptime / (1000 * 60)) % 60;
        long hours = (uptime / (1000 * 60 * 60)) % 24;
        long days = uptime / (1000 * 60 * 60 * 24);
        return String.format("%d.%02d:%02d:%02d:%03d", days, hours, minutes, seconds, millis);
    }

    public static class Status {
        String applicationState;
        String serverName;
        String uptime;

        public Status(String applicationState, String serverName, String uptime) {
            this.applicationState = applicationState;
            this.serverName = serverName;
            this.uptime = uptime;
        }

        public String getApplicationState() {
            return applicationState;
        }

        public String getServerName() {
            return serverName;
        }

        public String getUptime() {
            return uptime;
        }
    }

}