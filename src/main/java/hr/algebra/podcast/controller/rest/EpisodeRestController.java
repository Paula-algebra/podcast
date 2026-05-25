package hr.algebra.podcast.controller.rest;

import hr.algebra.podcast.dto.EpisodeDto;
import hr.algebra.podcast.entity.User;
import hr.algebra.podcast.enums.ListeningStatus;
import hr.algebra.podcast.enums.PodcastCategory;
import hr.algebra.podcast.service.EpisodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/episodes")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Episodes", description = "Podcast episode CRUD and search")
public class EpisodeRestController {

    private final EpisodeService episodeService;

    public EpisodeRestController(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }

    @GetMapping
    @Operation(summary = "Get all episodes")
    public ResponseEntity<List<EpisodeDto>> getAll() {
        return ResponseEntity.ok(episodeService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single episode by ID")
    public ResponseEntity<EpisodeDto> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(episodeService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search episodes by title, show, hosts, guests, category, status, or subscribed filter")
    public ResponseEntity<List<EpisodeDto>> search(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) PodcastCategory category,
        @RequestParam(required = false) ListeningStatus status,
        @RequestParam(defaultValue = "false") boolean subscribedOnly
    ) {
        return ResponseEntity.ok(episodeService.search(query, category, status, subscribedOnly));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new episode (admin only)")
    public ResponseEntity<EpisodeDto> create(
        @Valid @RequestBody EpisodeDto dto,
        @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(episodeService.create(dto, currentUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an episode (admin only)")
    public ResponseEntity<EpisodeDto> update(
        @PathVariable Long id,
        @Valid @RequestBody EpisodeDto dto
    ) {
        try {
            return ResponseEntity.ok(episodeService.update(id, dto));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an episode (admin only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            episodeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
