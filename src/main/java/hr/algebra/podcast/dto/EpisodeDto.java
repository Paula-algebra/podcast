package hr.algebra.podcast.dto;

import hr.algebra.podcast.entity.Episode;
import hr.algebra.podcast.enums.ListeningContext;
import hr.algebra.podcast.enums.ListeningStatus;
import hr.algebra.podcast.enums.PlaybackSpeed;
import hr.algebra.podcast.enums.PodcastCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Podcast episode data transfer object")
public record EpisodeDto(

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    Long id,

    @NotBlank @Size(max = 250)
    @Schema(description = "Episode title", example = "The Wellness Industrial Complex")
    String title,

    @NotBlank @Size(max = 200)
    @Schema(description = "Podcast show name", example = "Maintenance Phase")
    String showName,

    @NotBlank @Size(max = 200)
    @Schema(description = "Hosts", example = "Aubrey Gordon, Michael Hobbes")
    String hosts,

    @Size(max = 300)
    @Schema(description = "Guests (if any)")
    String guests,

    @Size(max = 50)
    @Schema(description = "Network or platform", example = "Spotify")
    String network,

    @Size(max = 30)
    @Schema(description = "Episode number", example = "S3E12")
    String episodeNumber,

    @Min(1) @Max(50)
    @Schema(description = "Season number", example = "3")
    Integer seasonNumber,

    @NotNull
    @Schema(description = "Podcast category")
    PodcastCategory category,

    @NotNull
    @Schema(description = "Listening status")
    ListeningStatus status,

    @Schema(description = "Where / how you listened")
    ListeningContext listeningContext,

    @Schema(description = "Playback speed used")
    PlaybackSpeed playbackSpeed,

    @Min(1) @Max(900)
    @Schema(description = "Total episode duration in minutes", example = "92")
    Integer durationMinutes,

    @Min(0) @Max(900)
    @Schema(description = "Minutes you listened so far", example = "45")
    Integer minutesListened,

    @Min(1) @Max(5)
    @Schema(description = "Personal rating 1-5", example = "5")
    Integer rating,

    @Min(1) @Max(10)
    @Schema(description = "Content quality score", example = "9")
    Integer contentQuality,

    @Min(1) @Max(10)
    @Schema(description = "Audio production quality", example = "8")
    Integer audioQuality,

    @Min(1) @Max(10)
    @Schema(description = "Host chemistry / dynamic", example = "10")
    Integer hostChemistry,

    @Min(1) @Max(10)
    @Schema(description = "Re-listen value", example = "7")
    Integer rewatchValue,

    @Schema(description = "Contains explicit content")
    boolean explicitContent,

    @Schema(description = "Subscribed to this show")
    boolean subscribed,

    @Schema(description = "Bookmarked for the quote")
    boolean bookmarkedQuote,

    @Schema(description = "Would recommend to a friend")
    boolean recommendToFriend,

    @Schema(description = "Original release date")
    LocalDate releaseDate,

    @Schema(description = "Date you listened to it")
    LocalDate listenedDate,

    @Schema(description = "Date added to your queue")
    LocalDate addedDate,

    @Size(max = 200)
    @Schema(description = "Mood / vibe tags", example = "Chaotic, hilarious, mid-week therapy")
    String moodTags,

    @Size(max = 100)
    @Schema(description = "Main topic or theme")
    String mainTopic,

    @Size(max = 500)
    @Schema(description = "Memorable quote from the episode")
    String memorableQuote,

    @Size(max = 300)
    @Schema(description = "Key takeaway / lesson learned")
    String keyTakeaway,

    @Size(max = 2000)
    @Schema(description = "Personal review")
    String review,

    @Size(max = 2000)
    @Schema(description = "Private notes")
    String personalNotes,

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    String addedBy,

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    LocalDateTime createdAt,

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    LocalDateTime updatedAt
) {
    public static EpisodeDto from(Episode e) {
        return new EpisodeDto(
            e.getId(),
            e.getTitle(),
            e.getShowName(),
            e.getHosts(),
            e.getGuests(),
            e.getNetwork(),
            e.getEpisodeNumber(),
            e.getSeasonNumber(),
            e.getCategory(),
            e.getStatus(),
            e.getListeningContext(),
            e.getPlaybackSpeed(),
            e.getDurationMinutes(),
            e.getMinutesListened(),
            e.getRating(),
            e.getContentQuality(),
            e.getAudioQuality(),
            e.getHostChemistry(),
            e.getRewatchValue(),
            e.isExplicitContent(),
            e.isSubscribed(),
            e.isBookmarkedQuote(),
            e.isRecommendToFriend(),
            e.getReleaseDate(),
            e.getListenedDate(),
            e.getAddedDate(),
            e.getMoodTags(),
            e.getMainTopic(),
            e.getMemorableQuote(),
            e.getKeyTakeaway(),
            e.getReview(),
            e.getPersonalNotes(),
            e.getAddedBy() != null ? e.getAddedBy().getUsername() : null,
            e.getCreatedAt(),
            e.getUpdatedAt()
        );
    }

    public void applyTo(Episode e) {
        e.setTitle(title);
        e.setShowName(showName);
        e.setHosts(hosts);
        e.setGuests(guests);
        e.setNetwork(network);
        e.setEpisodeNumber(episodeNumber);
        e.setSeasonNumber(seasonNumber);
        e.setCategory(category);
        e.setStatus(status);
        e.setListeningContext(listeningContext);
        e.setPlaybackSpeed(playbackSpeed);
        e.setDurationMinutes(durationMinutes);
        e.setMinutesListened(minutesListened);
        e.setRating(rating);
        e.setContentQuality(contentQuality);
        e.setAudioQuality(audioQuality);
        e.setHostChemistry(hostChemistry);
        e.setRewatchValue(rewatchValue);
        e.setExplicitContent(explicitContent);
        e.setSubscribed(subscribed);
        e.setBookmarkedQuote(bookmarkedQuote);
        e.setRecommendToFriend(recommendToFriend);
        e.setReleaseDate(releaseDate);
        e.setListenedDate(listenedDate);
        e.setAddedDate(addedDate);
        e.setMoodTags(moodTags);
        e.setMainTopic(mainTopic);
        e.setMemorableQuote(memorableQuote);
        e.setKeyTakeaway(keyTakeaway);
        e.setReview(review);
        e.setPersonalNotes(personalNotes);
    }
}
