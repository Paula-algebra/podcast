package camelpodcast.route;

import camelpodcast.config.AppConfig;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import camelpodcast.processor.ResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class UpdateRoute extends RouteBuilder {

    private int updateIndex = 0;
    @Autowired
    private ResponseProcessor responseProcessor;

    private final Long[] ids = {1L, 2L, 3L};

    private final String[] payloads = {
            """
            {
              "title": "Camel Updated Episode 1",
              "showName": "Camel Updated Show",
              "hosts": "Camel Host",
              "category": "COMEDY",
              "status": "QUEUED",
              "rating": 4,
              "guests": "Guest A",
              "network": "Spotify",
              "episodeNumber": "U1",
              "seasonNumber": 1,
              "listeningContext": "COMMUTE",
              "playbackSpeed": "SPEED_0_75X",
              "durationMinutes": 45,
              "minutesListened": 0,
              "contentQuality": 5,
              "audioQuality": 5,
              "hostChemistry": 5,
              "rewatchValue": 5,
              "explicitContent": false,
              "subscribed": false,
              "bookmarkedQuote": false,
              "recommendToFriend": false,
              "releaseDate": "2026-06-28",
              "listenedDate": "2026-06-28",
              "addedDate": "2026-06-28",
              "moodTags": "Camel update",
              "mainTopic": "Camel integration",
              "memorableQuote": "Updated quote",
              "keyTakeaway": "Updated takeaway",
              "review": "Updated from Camel route",
              "personalNotes": "Camel update test"
            }
            """,
            """
            {
              "title": "Camel Updated Episode 2",
              "showName": "Camel Updated Show",
              "hosts": "Camel Host",
              "category": "COMEDY",
              "status": "QUEUED",
              "rating": 5,
              "guests": "Guest B",
              "network": "Spotify",
              "episodeNumber": "U2",
              "seasonNumber": 1,
              "listeningContext": "COMMUTE",
              "playbackSpeed": "SPEED_0_75X",
              "durationMinutes": 55,
              "minutesListened": 10,
              "contentQuality": 5,
              "audioQuality": 5,
              "hostChemistry": 5,
              "rewatchValue": 5,
              "explicitContent": false,
              "subscribed": false,
              "bookmarkedQuote": false,
              "recommendToFriend": false,
              "releaseDate": "2026-06-28",
              "listenedDate": "2026-06-28",
              "addedDate": "2026-06-28",
              "moodTags": "Camel update",
              "mainTopic": "Camel integration",
              "memorableQuote": "Updated quote",
              "keyTakeaway": "Updated takeaway",
              "review": "Updated from Camel route",
              "personalNotes": "Camel update test"
            }
            """,
            """
            {
              "title": "Camel Updated Episode 3",
              "showName": "Camel Updated Show",
              "hosts": "Camel Host",
              "category": "COMEDY",
              "status": "QUEUED",
              "rating": 3,
              "guests": "Guest C",
              "network": "Spotify",
              "episodeNumber": "U3",
              "seasonNumber": 1,
              "listeningContext": "COMMUTE",
              "playbackSpeed": "SPEED_0_75X",
              "durationMinutes": 35,
              "minutesListened": 15,
              "contentQuality": 5,
              "audioQuality": 5,
              "hostChemistry": 5,
              "rewatchValue": 5,
              "explicitContent": false,
              "subscribed": false,
              "bookmarkedQuote": false,
              "recommendToFriend": false,
              "releaseDate": "2026-06-28",
              "listenedDate": "2026-06-28",
              "addedDate": "2026-06-28",
              "moodTags": "Camel update",
              "mainTopic": "Camel integration",
              "memorableQuote": "Updated quote",
              "keyTakeaway": "Updated takeaway",
              "review": "Updated from Camel route",
              "personalNotes": "Camel update test"
            }
            """
    };

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .log("Update episode failed: ${exception.message}")
                .setBody(simple("{\"error\":\"${exception.message}\"}"))
                .toD("file:" + AppConfig.OUTPUT_DIR + "?fileName=update-episode-error-${date:now:yyyyMMdd-HHmmss}.json");

        from("timer:updateEpisodeTimer?period=45000")
                .routeId("update-episode")
                .log("Starting update episode route")
                .process(exchange -> {
                    Long id = ids[updateIndex];
                    exchange.getMessage().setHeader("episodeId", id);
                    exchange.getMessage().setHeader(Exchange.HTTP_PATH, "/api/episodes/" + id);
                    exchange.setProperty("endpoint", AppConfig.BASE_URL + "/api/episodes/" + id);
                    exchange.getMessage().setBody(payloads[updateIndex]);

                    updateIndex++;
                    if (updateIndex >= payloads.length) {
                        updateIndex = 0;
                    }
                })
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Authorization", constant("Bearer " + AppConfig.ACCESS_TOKEN))
                .to(AppConfig.BASE_URL)
                .process(responseProcessor)
                .log("Update episode completed for ID ${header.episodeId} with status ${header.CamelHttpResponseCode}")
                .toD("file:" + AppConfig.OUTPUT_DIR + "?fileName=update-episode-id-${header.episodeId}-${date:now:yyyyMMdd-HHmmss}.json");
    }
}