package camelpodcast.route;

import camelpodcast.config.AppConfig;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import camelpodcast.processor.ResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class GetOneRoute extends RouteBuilder {

    private int currentId = 1;
    @Autowired
    private ResponseProcessor responseProcessor;

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .log("GET one episode failed: ${exception.message}")
                .setBody(simple("{\"error\":\"${exception.message}\"}"))
                .toD("file:" + AppConfig.OUTPUT_DIR + "?fileName=get-one-episode-error-${date:now:yyyyMMdd-HHmmss}.json");

        from("timer:getOneEpisodeTimer?period=15000")
                .routeId("get-one-episode")
                .log("Starting GET one episode route")
                .process(exchange -> {
                    exchange.getMessage().setHeader("episodeId", currentId);
                    exchange.getMessage().setHeader(Exchange.HTTP_PATH, "/api/episodes/" + currentId);
                    exchange.setProperty("endpoint", AppConfig.BASE_URL + "/api/episodes/" + currentId);

                    currentId++;
                    if (currentId > 5) {
                        currentId = 1;
                    }
                })
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader("Authorization", constant("Bearer " + AppConfig.ACCESS_TOKEN))
                .to(AppConfig.BASE_URL)
                .process(responseProcessor)
                .log("GET one episode completed for ID ${header.episodeId} with status ${header.CamelHttpResponseCode}")
                .toD("file:" + AppConfig.OUTPUT_DIR + "?fileName=get-one-episode-id-${header.episodeId}-${date:now:yyyyMMdd-HHmmss}.json");
    }
}