package com.ironman.notificationdelivery.application.repository.redis;

import com.ironman.notificationdelivery.application.model.notification.NotificationRecord;
import com.ironman.notificationdelivery.application.repository.NotificationRecordRepository;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.SetArgs;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class RedisNotificationRecordRepository implements NotificationRecordRepository {

  private final ValueCommands<String, NotificationRecord> commands;
  private final String keyPrefix;
  private final Duration ttl;

  public RedisNotificationRecordRepository(
      RedisDataSource redisDataSource,
      @ConfigProperty(name = "notification.redis.key-prefix") String keyPrefix,
      @ConfigProperty(name = "notification.redis.ttl") Duration ttl) {
    this.commands = redisDataSource.value(NotificationRecord.class);
    this.keyPrefix = keyPrefix;
    this.ttl = ttl;
  }

  @Override
  public boolean saveIfAbsent(NotificationRecord record) {
    return commands.setAndChanged(buildKey(record.getId()), record, new SetArgs().nx().ex(ttl));
  }

  private String buildKey(String id) {
    return keyPrefix + ":" + id;
  }
}
