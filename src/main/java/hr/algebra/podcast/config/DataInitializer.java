package hr.algebra.podcast.config;

import hr.algebra.podcast.entity.Episode;
import hr.algebra.podcast.entity.User;
import hr.algebra.podcast.enums.ListeningContext;
import hr.algebra.podcast.enums.ListeningStatus;
import hr.algebra.podcast.enums.PlaybackSpeed;
import hr.algebra.podcast.enums.PodcastCategory;
import hr.algebra.podcast.enums.Role;
import hr.algebra.podcast.repository.EpisodeRepository;
import hr.algebra.podcast.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final EpisodeRepository episodeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
        UserRepository userRepository,
        EpisodeRepository episodeRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.episodeRepository = episodeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) return;

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@podcast.hr");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin = userRepository.save(admin);

        User user = new User();
        user.setUsername("user");
        user.setEmail("user@podcast.hr");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setRole(Role.USER);
        userRepository.save(user);

        createEpisode("The Wellness Industrial Complex",
            "Maintenance Phase", "Aubrey Gordon, Michael Hobbes", null,
            "Spotify", "S3E12", 3,
            PodcastCategory.HEALTH_WELLNESS, ListeningStatus.FINISHED,
            ListeningContext.COMMUTE, PlaybackSpeed.SPEED_1_25X,
            92, 92, 5, 10, 9, 10, 9,
            false, true, true, true,
            LocalDate.of(2025, 11, 4), LocalDate.of(2025, 11, 5), LocalDate.of(2025, 10, 28),
            "Investigative, sharp, devastating in the best way",
            "Wellness industry, weight loss myths, junk science",
            "The wellness industry isn't selling you health, it's selling you the anxiety that you don't have it.",
            "Health is a social construct, weight is not a measure of worth",
            "Aubrey and Michael are the only ones who debunk wellness grifters this thoroughly. Listened on my Tuesday commute, missed my stop because I was too invested." +
            "Recommended to everyone. Their backlog is gold.", admin);

        createEpisode("The Joe Schmo Show Phenomenon",
            "You're Wrong About", "Sarah Marshall", "Blair Braverman",
            "Spotify", "EP 178", null,
            PodcastCategory.SOCIETY_CULTURE, ListeningStatus.FINISHED,
            ListeningContext.HOUSEWORK, PlaybackSpeed.SPEED_1_5X,
            78, 78, 5, 9, 8, 10, 8,
            false, true, true, true,
            LocalDate.of(2025, 10, 22), LocalDate.of(2025, 10, 25), LocalDate.of(2025, 10, 20),
            "Nostalgic deep-dive, cozy, hilarious analysis",
            "Reality TV history, 2003 culture, prank shows",
            "We treat the past like another country, but it's really just a place where we used to live.",
            "Reality TV is more constructed than fiction",
            "Sarah Marshall makes EVERYTHING interesting. The Joe Schmo show analysis was genuinely revelatory. I love how she finds the humanity in trash TV." +
            "Sarah's takes are always thoughtful. Subscribed for years.", admin);

        createEpisode("Annie Mac on Hosting Cool Girl Energy",
            "Off Menu", "Ed Gamble, James Acaster", "Annie Mac",
            "Plosive Productions", "EP 287", null,
            PodcastCategory.COMEDY, ListeningStatus.FINISHED,
            ListeningContext.WALKING, PlaybackSpeed.SPEED_1_0X,
            71, 71, 4, 8, 9, 10, 6,
            true, true, false, true,
            LocalDate.of(2025, 9, 15), LocalDate.of(2025, 9, 16), LocalDate.of(2025, 9, 14),
            "Pure chaos, dream restaurant format, British wit",
            "Food preferences, hospitality, comedy podcast format",
            "Welcome to The Dream Restaurant.",
            "The best comedy comes from genuine specificity",
            "James Acaster's absolute commitment to the bit is a national treasure. Annie Mac was a perfect guest. I listened during my evening walk and got weird looks for laughing alone." +
            "Comfort listen. Their dynamic is unmatched.", admin);

        createEpisode("Episode 1: Cassie Bernall",
            "Last Podcast on the Left", "Marcus Parks, Henry Zebrowski, Ed Larson", null,
            "Spotify", "EP 590", null,
            PodcastCategory.TRUE_CRIME, ListeningStatus.LISTENING,
            ListeningContext.WORKOUT, PlaybackSpeed.SPEED_1_5X,
            134, 67, null, null, null, null, null,
            true, true, false, false,
            LocalDate.of(2026, 5, 1), null, LocalDate.of(2026, 5, 2),
            "Heavy material, well-researched, dark humor balance",
            "Columbine, religious mythology, school shootings",
            null, null,
            "Half-way through. Their Columbine series is heavy. Listening during workouts to dilute the intensity.", admin);

        createEpisode("How To Stop Doomscrolling",
            "Hidden Brain", "Shankar Vedantam", "Dr. Anna Lembke",
            "Hidden Brain Media", "EP 412", null,
            PodcastCategory.SCIENCE, ListeningStatus.FINISHED,
            ListeningContext.SLEEP, PlaybackSpeed.SPEED_1_0X,
            58, 58, 5, 10, 10, 9, 8,
            false, true, true, true,
            LocalDate.of(2025, 12, 3), LocalDate.of(2025, 12, 7), LocalDate.of(2025, 12, 1),
            "Soothing, science-backed, paradigm-shifting",
            "Dopamine, addiction, screen time, behavioral psychology",
            "The relentless pursuit of pleasure leads to pain. The deliberate pursuit of pain leads to pleasure.",
            "Dopamine fasting actually works, your phone is the problem",
            "Anna Lembke's book Dopamine Nation rewired my brain and this episode was a perfect intro. Shankar's voice is the most calming thing in audio." +
            "Listen before bed. Deleted TikTok for a week after this.", admin);

        createEpisode("The Founder's Mode Phenomenon",
            "Acquired", "Ben Gilbert, David Rosenthal", "Brian Chesky",
            "Acquired LLC", "S15E4", 15,
            PodcastCategory.BUSINESS, ListeningStatus.SAVED_FOR_LATER,
            null, null,
            245, 0, null, null, null, null, null,
            false, true, false, false,
            LocalDate.of(2026, 3, 20), null, LocalDate.of(2026, 4, 1),
            null,
            "Startup management, founder vs manager mode, Airbnb",
            null, null,
            "Saved for a long flight. 4+ hour episodes are an Acquired signature. Brian Chesky has been everywhere this year.", admin);

        createEpisode("Anyone Can Be A Stoic",
            "Huberman Lab", "Andrew Huberman", "Ryan Holiday",
            "Scicomm Media", "EP 195", null,
            PodcastCategory.HEALTH_WELLNESS, ListeningStatus.FINISHED,
            ListeningContext.WORKOUT, PlaybackSpeed.SPEED_2_0X,
            178, 178, 3, 7, 9, 7, 5,
            false, false, false, false,
            LocalDate.of(2025, 7, 14), LocalDate.of(2025, 7, 18), LocalDate.of(2025, 7, 12),
            "Bro philosophy, dense, occasionally insightful",
            "Stoicism, ancient philosophy, modern life",
            "You don't rise to the level of your goals, you fall to the level of your systems.",
            "Stoicism without the marketing is just discipline",
            "Listened at 2x because Huberman's pacing is glacial. Ryan Holiday has good points but the bro-coded delivery is exhausting. 3 stars max." +
            "Not subscribing. One was enough.", admin);

        createEpisode("Episode 1: We're All Going to the Olympics",
            "Normal Gossip", "Kelsey McKinney", "Connor Franta",
            "Defector Media", "S5E1", 5,
            PodcastCategory.STORYTELLING, ListeningStatus.FINISHED,
            ListeningContext.COOKING, PlaybackSpeed.SPEED_1_25X,
            62, 62, 5, 10, 9, 10, 9,
            false, true, true, true,
            LocalDate.of(2026, 1, 8), LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 5),
            "Anonymous gossip, sapphic chaos, perfect format",
            "Gossip culture, relationship drama, anonymous stories",
            "Friend, this story has so many twists I am going to need to lie down.",
            "The best stories are real stories from regular people",
            "Kelsey McKinney's gasps are the soundtrack of my life. The Olympics story was UNHINGED in the best way. I literally screamed at my pasta sauce." +
            "Comfort listen. Sundays are for Normal Gossip and dinner prep.", admin);

        createEpisode("How Tinder Killed The Meet Cute",
            "Search Engine", "PJ Vogt", null,
            "Odyssey", "EP 67", null,
            PodcastCategory.TECHNOLOGY, ListeningStatus.QUEUED,
            null, null,
            54, 0, null, null, null, null, null,
            false, false, false, false,
            LocalDate.of(2026, 4, 28), null, LocalDate.of(2026, 5, 1),
            null,
            "Dating apps, modern romance, tech criticism",
            null, null,
            "PJ Vogt's voice can read me the phone book. In my queue for tomorrow's commute.", admin);

        createEpisode("Lex Speaks With Jeff Bezos",
            "Lex Fridman Podcast", "Lex Fridman", "Jeff Bezos",
            "Lex Fridman Productions", "EP 405", null,
            PodcastCategory.INTERVIEW, ListeningStatus.SKIPPED,
            ListeningContext.BACKGROUND, PlaybackSpeed.SPEED_1_75X,
            132, 28, 2, 5, 8, 3, 2,
            false, false, false, false,
            LocalDate.of(2024, 12, 14), LocalDate.of(2024, 12, 20), LocalDate.of(2024, 12, 14),
            "Long-winded, hagiographic, surface-level",
            "Amazon, space exploration, productivity",
            null,
            "Lex needs to push back more on his guests",
            "Skipped at 28 minutes. Lex's interviews have become uncritical CEO infomercials. The whole tech-bro philosophical performance gets old fast." +
            "Probably done with this show. Used to be good circa episode 100.", admin);
    }

    private void createEpisode(
        String title, String showName, String hosts, String guests,
        String network, String episodeNumber, Integer seasonNumber,
        PodcastCategory category, ListeningStatus status,
        ListeningContext context, PlaybackSpeed speed,
        Integer duration, Integer listened,
        Integer rating, Integer contentQuality, Integer audioQuality,
        Integer hostChemistry, Integer rewatchValue,
        boolean explicit, boolean subscribed, boolean bookmarkedQuote, boolean recommend,
        LocalDate release, LocalDate listenedDate, LocalDate added,
        String mood, String topic, String quote,
        String takeaway, String review, User addedBy
    ) {
        Episode e = new Episode();
        e.setTitle(title);
        e.setShowName(showName);
        e.setHosts(hosts);
        e.setGuests(guests);
        e.setNetwork(network);
        e.setEpisodeNumber(episodeNumber);
        e.setSeasonNumber(seasonNumber);
        e.setCategory(category);
        e.setStatus(status);
        e.setListeningContext(context);
        e.setPlaybackSpeed(speed);
        e.setDurationMinutes(duration);
        e.setMinutesListened(listened);
        e.setRating(rating);
        e.setContentQuality(contentQuality);
        e.setAudioQuality(audioQuality);
        e.setHostChemistry(hostChemistry);
        e.setRewatchValue(rewatchValue);
        e.setExplicitContent(explicit);
        e.setSubscribed(subscribed);
        e.setBookmarkedQuote(bookmarkedQuote);
        e.setRecommendToFriend(recommend);
        e.setReleaseDate(release);
        e.setListenedDate(listenedDate);
        e.setAddedDate(added);
        e.setMoodTags(mood);
        e.setMainTopic(topic);
        e.setMemorableQuote(quote);
        e.setKeyTakeaway(takeaway);
        e.setReview(review);
        e.setAddedBy(addedBy);
        episodeRepository.save(e);
    }
}
