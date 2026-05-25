package hr.algebra.podcast.service;

import hr.algebra.podcast.dto.EpisodeDto;
import hr.algebra.podcast.entity.Episode;
import hr.algebra.podcast.entity.User;
import hr.algebra.podcast.enums.ListeningStatus;
import hr.algebra.podcast.enums.PodcastCategory;
import hr.algebra.podcast.repository.EpisodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EpisodeService {

    private final EpisodeRepository episodeRepository;

    public EpisodeService(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    public List<EpisodeDto> findAll() {
        return episodeRepository.findAllByOrderByStatusAscReleaseDateDescShowNameAsc()
            .stream()
            .map(EpisodeDto::from)
            .toList();
    }

    public EpisodeDto findById(Long id) {
        return episodeRepository.findById(id)
            .map(EpisodeDto::from)
            .orElseThrow(() -> new NoSuchElementException("Episode not found: " + id));
    }

    public List<EpisodeDto> search(String query, PodcastCategory category, ListeningStatus status, boolean subscribedOnly) {
        String nq = (query != null && query.isBlank()) ? null : query;
        return episodeRepository.search(nq, category, status, subscribedOnly)
            .stream()
            .map(EpisodeDto::from)
            .toList();
    }

    @Transactional
    public EpisodeDto create(EpisodeDto dto, User creator) {
        Episode episode = new Episode();
        dto.applyTo(episode);
        episode.setAddedBy(creator);
        return EpisodeDto.from(episodeRepository.save(episode));
    }

    @Transactional
    public EpisodeDto update(Long id, EpisodeDto dto) {
        Episode episode = episodeRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Episode not found: " + id));
        dto.applyTo(episode);
        return EpisodeDto.from(episodeRepository.save(episode));
    }

    @Transactional
    public void delete(Long id) {
        if (!episodeRepository.existsById(id)) {
            throw new NoSuchElementException("Episode not found: " + id);
        }
        episodeRepository.deleteById(id);
    }
}
