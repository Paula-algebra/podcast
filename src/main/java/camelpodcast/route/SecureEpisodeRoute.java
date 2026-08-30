package camelpodcast.route;

import camelpodcast.config.ApplicationConstants;
import camelpodcast.config.CamelSecurityPolicies;
import camelpodcast.service.EventGeneratorService;
import org.apache.camel.CamelAuthorizationException;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SecureEpisodeRoute extends RouteBuilder {

    private final SpringSecurityAuthorizationPolicy adminPolicy;
    private final EventGeneratorService generator;

    public SecureEpisodeRoute(
            @Qualifier(CamelSecurityPolicies.ADMIN_POLICY)
            SpringSecurityAuthorizationPolicy adminPolicy,
            EventGeneratorService generator) {
        this.adminPolicy = adminPolicy;
        this.generator = generator;
    }

    @Override
    public void configure() {

        onException(CamelAuthorizationException.class)
                .handled(true)
                .log("AuthZ denied for ${header.CamelHttpUrl}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(403)
                .setHeader("Content-Type").constant("application/json")
                .setBody().constant(
                        "{\"error\":\"forbidden\",\"reason\":\"ROLE_ADMIN required\"}");

        from("platform-http:/camel/secure/admin-episode?httpMethodRestrict=POST")
                .routeId("secure-admin-episode")
                .log("Admin episode request received")
                .policy(adminPolicy)
                .log("Inside ADMIN-secured segment")
                .setBody(exchange -> generator.buildRandomEpisodeRequest())
                .to(ExchangePattern.InOnly,
                        ApplicationConstants.DIRECT_PUBLISH_EPISODE_REQUEST)
                .setHeader(Exchange.HTTP_RESPONSE_CODE).constant(202)
                .setHeader("Content-Type").constant("application/json")
                .setBody().constant(
                        "{\"status\":\"accepted\",\"path\":\"camel-secure-admin-episode\"}")
                .end();
    }
}