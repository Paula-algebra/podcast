package camelpodcast.route;

import camelpodcast.config.AppConfig;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import camelpodcast.processor.ResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class CreateRoute extends RouteBuilder {

    private int payloadIndex = 0;
    @Autowired
    private ResponseProcessor responseProcessor;

    private final String[] payloads = {
            """
            {
              "title": "Camel Created Episode 1",
              "showName": "Camel Integration Show",
              "hosts": "Camel Host",
              "category": "COMEDY",
              "status": "QUEUED",
              "rating": 4,
              "guests": "Guest A",
              "network": "Spotify",
              "episodeNumber": "C1",
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
              "moodTags": "Camel test",
              "mainTopic": "Camel integration",
              "memorableQuote": "Test quote",
              "keyTakeaway": "Test takeaway",
              "review": "Created from Camel route",
              "personalNotes": "Camel generated test"
            }
            """,
            """
            {
              "title": "Camel Created Episode 2",
              "showName": "Camel Integration Show",
              "hosts": "Camel Host",
              "category": "COMEDY",
              "status": "QUEUED",
              "rating": 5,
              "guests": "Guest B",
              "network": "Spotify",
              "episodeNumber": "C2",
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
              "moodTags": "Camel test",
              "mainTopic": "Camel integration",
              "memorableQuote": "Test quote",
              "keyTakeaway": "Test takeaway",
              "review": "Created from Camel route",
              "personalNotes": "Camel generated test"
            }
            """,
            """
            {
              "title": "Camel Created Episode 3",
              "showName": "Camel Integration Show",
              "hosts": "Camel Host",
              "category": "SCIENCE",
              "status": "LISTENING",
              "rating": 3,
              "guests": "Guest C",
              "network": "Spotify",
              "episodeNumber": "C3",
              "seasonNumber": 1,
              "listeningContext": "WORKOUT",
              "playbackSpeed": "SPEED_1_25X",
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
              "moodTags": "Camel test",
              "mainTopic": "Camel integration",
              "memorableQuote": "Test quote",
              "keyTakeaway": "Test takeaway",
              "review": "Created from Camel route",
              "personalNotes": "Camel generated test"
            }
            """
    };

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .log("Create episode failed: ${exception.message}")
                .setBody(simple("{\"error\":\"${exception.message}\"}"))
                .toD("file:" + AppConfig.OUTPUT_DIR + "?fileName=create-episode-error-${date:now:yyyyMMdd-HHmmss}.json");

        from("timer:createEpisodeTimer?period=30000")
                .routeId("create-episode")
                .log("Starting create episode route")
                .setProperty("endpoint", constant(AppConfig.BASE_URL + "/api/episodes"))
                .process(exchange -> {
                    exchange.getMessage().setBody(payloads[payloadIndex]);
                    payloadIndex++;
                    if (payloadIndex >= payloads.length) {
                        payloadIndex = 0;
                    }
                })
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Authorization", constant("Bearer " + AppConfig.ACCESS_TOKEN))
                .to(AppConfig.BASE_URL + "/api/episodes")
                .process(responseProcessor)
                .log("Create episode completed with status ${header.CamelHttpResponseCode}")
                .toD("file:" + AppConfig.OUTPUT_DIR + "?fileName=create-episode-${date:now:yyyyMMdd-HHmmss}.json");
    }
}