package hr.algebra.podcast.controller.rest;

import hr.algebra.podcast.service.DatabaseBackupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
@RequestMapping("/api/database")
@Tag(name = "Database", description = "Database backup and restore")
public class DatabaseRestController {

    private final DatabaseBackupService databaseBackupService;
    public record RestoreRequest(String path) {}

    public DatabaseRestController(DatabaseBackupService databaseBackupService) {
        this.databaseBackupService = databaseBackupService;
    }

    @PostMapping("/backup")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a full database backup (admin only)")
    public ResponseEntity<Map<String, String>> backup() throws Exception {
        String path = databaseBackupService.backup();

        return ResponseEntity.ok(Map.of(
                "message", "Database backup created",
                "path", path
        ));
    }

    @PostMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restore database from a backup file")
    public ResponseEntity<Map<String, String>> restore(@RequestBody RestoreRequest request) throws Exception {
        databaseBackupService.restore(request.path());

        return ResponseEntity.ok(Map.of(
                "message", "Database restored",
                "path", request.path()
        ));
    }
}