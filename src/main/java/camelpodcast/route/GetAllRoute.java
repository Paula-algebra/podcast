package camelpodcast.route;

import camelpodcast.config.AppConfig;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import camelpodcast.processor.ResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class GetAllRoute extends RouteBuilder {

    @Autowired
    private ResponseProcessor responseProcessor;

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .log("GET all episodes failed: ${exception.message}")
                .setBody(simple("{\"error\":\"${exception.message}\"}"))
                .toD("file:" + AppConfig.OUTPUT_DIR + "?fileName=get-all-episodes-error-${date:now:yyyyMMdd-HHmmss}.json");

        from("timer:getAllEpisodesTimer?period=10000")
                .routeId("get-all-episodes")
                .log("Starting GET all episodes route")
                .setProperty("endpoint", constant(AppConfig.BASE_URL + "/api/episodes"))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader("Authorization", constant("Bearer " + AppConfig.ACCESS_TOKEN))
                .to(AppConfig.BASE_URL + "/api/episodes")
                .process(responseProcessor)
                .log("GET all episodes completed with status ${header.CamelHttpResponseCode}")
                .toD("file:" + AppConfig.OUTPUT_DIR + "?fileName=get-all-episodes-${date:now:yyyyMMdd-HHmmss}.json");
    }
}