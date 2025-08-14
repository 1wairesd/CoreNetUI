package com.wairesd.discordbm.host.common.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.table.TableUtils;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.database.entities.IpBlockEntry;
import com.wairesd.discordbm.host.common.database.entities.MessageLabelEntry;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {
    static {
        System.setProperty("com.j256.ormlite.logger.type", "SLF4J");
        Logger.setGlobalLogLevel(Level.WARNING);
    }

    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private final Dao<IpBlockEntry, String> ipBlockDao;
    private final Dao<MessageLabelEntry, Long> messageLabelDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Database(String dbUrl) {
        try {
            JdbcConnectionSource source = new JdbcConnectionSource(dbUrl);
            this.ipBlockDao = com.j256.ormlite.dao.DaoManager.createDao(source, IpBlockEntry.class);
            this.messageLabelDao = com.j256.ormlite.dao.DaoManager.createDao(source, MessageLabelEntry.class);
            TableUtils.createTableIfNotExists(source, IpBlockEntry.class);
            TableUtils.createTableIfNotExists(source, MessageLabelEntry.class);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public CompletableFuture<Boolean> isBlocked(String ip) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                IpBlockEntry entry = ipBlockDao.queryForId(ip);
                if (entry != null && entry.getBlockUntil() != null) {
                    return entry.getBlockUntil().after(Timestamp.from(Instant.now()));
                }
            } catch (SQLException e) {
                logger.error("Error checking blocked IP {}: {}", ip, e);
            }
            return false;
        }, executor);
    }

    public CompletableFuture<Void> incrementFailedAttempt(String ip) {
        return CompletableFuture.runAsync(() -> {
            try {
                IpBlockEntry entry = ipBlockDao.queryForId(ip);
                if (entry == null) {
                    entry = new IpBlockEntry();
                    entry.setIp(ip);
                    entry.setAttempts(1);
                    entry.setCurrentBlockTime(5 * 60 * 1000);
                } else {
                    entry.setAttempts(entry.getAttempts() + 1);
                }

                if (entry.getAttempts() >= 10) {
                    long now = System.currentTimeMillis();
                    long newBlockTime = Math.min(entry.getCurrentBlockTime() * 2, 60 * 60 * 1000);
                    entry.setBlockUntil(new Timestamp(now + entry.getCurrentBlockTime()));
                    entry.setAttempts(0);
                    entry.setCurrentBlockTime(newBlockTime);
                }

                ipBlockDao.createOrUpdate(entry);
            } catch (SQLException e) {
                logError("Error incrementing failed attempt for IP {}: {}", ip, e);
            }
        }, executor);
    }

    public CompletableFuture<Void> resetAttempts(String ip) {
        return CompletableFuture.runAsync(() -> {
            try {
                ipBlockDao.deleteById(ip);
            } catch (SQLException e) {
                logError("Error resetting attempts for IP {}: {}", ip, e);
            }
        }, executor);
    }

    private void logError(String message, String ip, Throwable e) {
        logger.error(message.formatted(ip), e);
    }

    public void close() {
        executor.shutdownNow();
    }

    public CompletableFuture<Void> upsertMessageLabel(String labelKey, String channelId, String messageId) {
        return CompletableFuture.runAsync(() -> {
            try {
                MessageLabelEntry e = new MessageLabelEntry();
                e.setLabelKey(labelKey);
                e.setChannelId(channelId);
                e.setMessageId(messageId);
                e.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                messageLabelDao.create(e);
            } catch (SQLException ex) {
                logger.error("Error upserting message label {}: {}", labelKey, ex);
            }
        }, executor);
    }

    public CompletableFuture<String[]> getLastMessageReference(String labelKey) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var qb = messageLabelDao.queryBuilder();
                qb.where().eq("label_key", labelKey);
                qb.orderBy("id", false).limit(1L);
                MessageLabelEntry e = messageLabelDao.queryForFirst(qb.prepare());
                if (e != null) return new String[]{e.getChannelId(), e.getMessageId()};
            } catch (SQLException ex) {
                logger.error("Error querying message label {}: {}", labelKey, ex);
            }
            return null;
        }, executor);
    }

    public CompletableFuture<Void> removeMessageReference(String labelKey, String channelId, String messageId) {
        return CompletableFuture.runAsync(() -> {
            try {
                var qb = messageLabelDao.deleteBuilder();
                qb.where().eq("label_key", labelKey).and().eq("channel_id", channelId).and().eq("message_id", messageId);
                messageLabelDao.delete(qb.prepare());
            } catch (SQLException ex) {
                logger.error("Error deleting message label {}: {}", labelKey, ex);
            }
        }, executor);
    }
}
