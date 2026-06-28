package hr.algebra.podcast.service;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DatabaseBackupService {

    private final DataSource dataSource;

    public DatabaseBackupService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String backup() throws Exception {
        Path dir = Paths.get("backups");
        Files.createDirectories(dir);

        String fileName = "podcast-backup-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) +
                ".sql";

        Path file = dir.resolve(fileName).toAbsolutePath();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SCRIPT TO '" + file.toString().replace("\\", "/") + "'");
        }

        return file.toString();
    }

    public void restore(String backupPath) throws Exception {
        Path file = Paths.get(backupPath).toAbsolutePath();

        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Backup file does not exist: " + file);
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("RUNSCRIPT FROM '" + file.toString().replace("\\", "/") + "'");
        }
    }
}