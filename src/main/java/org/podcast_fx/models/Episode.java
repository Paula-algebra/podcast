package org.podcast_fx.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Episode {
    private Long id;
    private String title;
    private String showName;
    private String hosts;
    private String guests;
    private String network;
    private String episodeNumber;
    private Integer seasonNumber;
    private String category;
    private String status;
    private String listeningContext;
    private String playbackSpeed;
    private Integer durationMinutes;
    private Integer minutesListened;
    private Integer rating;
    private Integer contentQuality;
    private Integer audioQuality;
    private Integer hostChemistry;
    private Integer rewatchValue;
    private Boolean explicitContent;
    private Boolean subscribed;
    private Boolean bookmarkedQuote;
    private Boolean recommendToFriend;
    private String releaseDate;
    private String listenedDate;
    private String addedDate;
    private String moodTags;
    private String mainTopic;
    private String memorableQuote;
    private String keyTakeaway;
    private String review;
    private String personalNotes;

    public Episode() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getGuests() {
        return guests;
    }

    public void setGuests(String guests) {
        this.guests = guests;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getListeningContext() {
        return listeningContext;
    }

    public void setListeningContext(String listeningContext) {
        this.listeningContext = listeningContext;
    }

    public String getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed(String playbackSpeed) {
        this.playbackSpeed = playbackSpeed;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getMinutesListened() {
        return minutesListened;
    }

    public void setMinutesListened(Integer minutesListened) {
        this.minutesListened = minutesListened;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getContentQuality() {
        return contentQuality;
    }

    public void setContentQuality(Integer contentQuality) {
        this.contentQuality = contentQuality;
    }

    public Integer getAudioQuality() {
        return audioQuality;
    }

    public void setAudioQuality(Integer audioQuality) {
        this.audioQuality = audioQuality;
    }

    public Integer getHostChemistry() {
        return hostChemistry;
    }

    public void setHostChemistry(Integer hostChemistry) {
        this.hostChemistry = hostChemistry;
    }

    public Integer getRewatchValue() {
        return rewatchValue;
    }

    public void setRewatchValue(Integer rewatchValue) {
        this.rewatchValue = rewatchValue;
    }

    public Boolean getExplicitContent() {
        return explicitContent;
    }

    public void setExplicitContent(Boolean explicitContent) {
        this.explicitContent = explicitContent;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    public Boolean getBookmarkedQuote() {
        return bookmarkedQuote;
    }

    public void setBookmarkedQuote(Boolean bookmarkedQuote) {
        this.bookmarkedQuote = bookmarkedQuote;
    }

    public Boolean getRecommendToFriend() {
        return recommendToFriend;
    }

    public void setRecommendToFriend(Boolean recommendToFriend) {
        this.recommendToFriend = recommendToFriend;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getListenedDate() {
        return listenedDate;
    }

    public void setListenedDate(String listenedDate) {
        this.listenedDate = listenedDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getMoodTags() {
        return moodTags;
    }

    public void setMoodTags(String moodTags) {
        this.moodTags = moodTags;
    }

    public String getMainTopic() {
        return mainTopic;
    }

    public void setMainTopic(String mainTopic) {
        this.mainTopic = mainTopic;
    }

    public String getMemorableQuote() {
        return memorableQuote;
    }

    public void setMemorableQuote(String memorableQuote) {
        this.memorableQuote = memorableQuote;
    }

    public String getKeyTakeaway() {
        return keyTakeaway;
    }

    public void setKeyTakeaway(String keyTakeaway) {
        this.keyTakeaway = keyTakeaway;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getPersonalNotes() {
        return personalNotes;
    }

    public void setPersonalNotes(String personalNotes) {
        this.personalNotes = personalNotes;
    }
}