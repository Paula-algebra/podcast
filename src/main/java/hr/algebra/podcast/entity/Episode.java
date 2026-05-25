package hr.algebra.podcast.entity;

import hr.algebra.podcast.enums.ListeningContext;
import hr.algebra.podcast.enums.ListeningStatus;
import hr.algebra.podcast.enums.PlaybackSpeed;
import hr.algebra.podcast.enums.PodcastCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "episodes")
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 250)
    @Column(nullable = false, length = 250)
    private String title;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String showName;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String hosts;

    @Size(max = 300)
    private String guests;

    @Size(max = 50)
    private String network;

    @Size(max = 30)
    private String episodeNumber;

    @Min(1) @Max(50)
    private Integer seasonNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PodcastCategory category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListeningStatus status;

    @Enumerated(EnumType.STRING)
    private ListeningContext listeningContext;

    @Enumerated(EnumType.STRING)
    private PlaybackSpeed playbackSpeed;

    @Min(1) @Max(900)
    private Integer durationMinutes;

    @Min(0) @Max(900)
    private Integer minutesListened;

    @Min(1) @Max(5)
    private Integer rating;

    @Min(1) @Max(10)
    private Integer contentQuality;

    @Min(1) @Max(10)
    private Integer audioQuality;

    @Min(1) @Max(10)
    private Integer hostChemistry;

    @Min(1) @Max(10)
    private Integer rewatchValue;

    @Column(nullable = false)
    private boolean explicitContent = false;

    @Column(nullable = false)
    private boolean subscribed = false;

    @Column(nullable = false)
    private boolean bookmarkedQuote = false;

    @Column(nullable = false)
    private boolean recommendToFriend = false;

    private LocalDate releaseDate;
    private LocalDate listenedDate;
    private LocalDate addedDate;

    @Size(max = 200)
    private String moodTags;

    @Size(max = 100)
    private String mainTopic;

    @Size(max = 500)
    @Column(length = 500)
    private String memorableQuote;

    @Size(max = 300)
    @Column(length = 300)
    private String keyTakeaway;

    @Size(max = 2000)
    @Column(length = 2000)
    private String review;

    @Size(max = 2000)
    @Column(length = 2000)
    private String personalNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_id")
    private User addedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (addedDate == null) addedDate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId()                                  { return id; }
    public void setId(Long id)                           { this.id = id; }
    public String getTitle()                             { return title; }
    public void setTitle(String title)                   { this.title = title; }
    public String getShowName()                          { return showName; }
    public void setShowName(String showName)             { this.showName = showName; }
    public String getHosts()                             { return hosts; }
    public void setHosts(String hosts)                   { this.hosts = hosts; }
    public String getGuests()                            { return guests; }
    public void setGuests(String guests)                 { this.guests = guests; }
    public String getNetwork()                           { return network; }
    public void setNetwork(String network)               { this.network = network; }
    public String getEpisodeNumber()                     { return episodeNumber; }
    public void setEpisodeNumber(String e)               { this.episodeNumber = e; }
    public Integer getSeasonNumber()                     { return seasonNumber; }
    public void setSeasonNumber(Integer s)               { this.seasonNumber = s; }
    public PodcastCategory getCategory()                 { return category; }
    public void setCategory(PodcastCategory category)    { this.category = category; }
    public ListeningStatus getStatus()                   { return status; }
    public void setStatus(ListeningStatus status)        { this.status = status; }
    public ListeningContext getListeningContext()        { return listeningContext; }
    public void setListeningContext(ListeningContext l)  { this.listeningContext = l; }
    public PlaybackSpeed getPlaybackSpeed()              { return playbackSpeed; }
    public void setPlaybackSpeed(PlaybackSpeed p)        { this.playbackSpeed = p; }
    public Integer getDurationMinutes()                  { return durationMinutes; }
    public void setDurationMinutes(Integer d)            { this.durationMinutes = d; }
    public Integer getMinutesListened()                  { return minutesListened; }
    public void setMinutesListened(Integer m)            { this.minutesListened = m; }
    public Integer getRating()                           { return rating; }
    public void setRating(Integer rating)                { this.rating = rating; }
    public Integer getContentQuality()                   { return contentQuality; }
    public void setContentQuality(Integer c)             { this.contentQuality = c; }
    public Integer getAudioQuality()                     { return audioQuality; }
    public void setAudioQuality(Integer a)               { this.audioQuality = a; }
    public Integer getHostChemistry()                    { return hostChemistry; }
    public void setHostChemistry(Integer h)              { this.hostChemistry = h; }
    public Integer getRewatchValue()                     { return rewatchValue; }
    public void setRewatchValue(Integer r)               { this.rewatchValue = r; }
    public boolean isExplicitContent()                   { return explicitContent; }
    public void setExplicitContent(boolean e)            { this.explicitContent = e; }
    public boolean isSubscribed()                        { return subscribed; }
    public void setSubscribed(boolean subscribed)        { this.subscribed = subscribed; }
    public boolean isBookmarkedQuote()                   { return bookmarkedQuote; }
    public void setBookmarkedQuote(boolean b)            { this.bookmarkedQuote = b; }
    public boolean isRecommendToFriend()                 { return recommendToFriend; }
    public void setRecommendToFriend(boolean r)          { this.recommendToFriend = r; }
    public LocalDate getReleaseDate()                    { return releaseDate; }
    public void setReleaseDate(LocalDate d)              { this.releaseDate = d; }
    public LocalDate getListenedDate()                   { return listenedDate; }
    public void setListenedDate(LocalDate d)             { this.listenedDate = d; }
    public LocalDate getAddedDate()                      { return addedDate; }
    public void setAddedDate(LocalDate d)                { this.addedDate = d; }
    public String getMoodTags()                          { return moodTags; }
    public void setMoodTags(String moodTags)             { this.moodTags = moodTags; }
    public String getMainTopic()                         { return mainTopic; }
    public void setMainTopic(String mainTopic)           { this.mainTopic = mainTopic; }
    public String getMemorableQuote()                    { return memorableQuote; }
    public void setMemorableQuote(String m)              { this.memorableQuote = m; }
    public String getKeyTakeaway()                       { return keyTakeaway; }
    public void setKeyTakeaway(String keyTakeaway)       { this.keyTakeaway = keyTakeaway; }
    public String getReview()                            { return review; }
    public void setReview(String review)                 { this.review = review; }
    public String getPersonalNotes()                     { return personalNotes; }
    public void setPersonalNotes(String p)               { this.personalNotes = p; }
    public User getAddedBy()                             { return addedBy; }
    public void setAddedBy(User addedBy)                 { this.addedBy = addedBy; }
    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(LocalDateTime t)            { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()                  { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)            { this.updatedAt = t; }
}
