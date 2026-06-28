package camelpodcast.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ResponseProcessor implements Processor {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        String routeId = exchange.getFromRouteId();
        String httpMethod = exchange.getMessage().getHeader(Exchange.HTTP_METHOD, String.class);
        Integer httpStatus = exchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        String endpoint = exchange.getProperty("endpoint", String.class);
        String rawResponse = exchange.getMessage().getBody(String.class);

        ObjectNode envelope = mapper.createObjectNode();
        envelope.put("capturedAt", LocalDateTime.now().toString());
        envelope.put("routeId", routeId);
        envelope.put("httpMethod", httpMethod);
        envelope.put("endpoint", endpoint);
        envelope.put("httpStatus", httpStatus);

        if (rawResponse != null && !rawResponse.isBlank()) {
            try {
                JsonNode responseJson = mapper.readTree(rawResponse);
                envelope.set("response", responseJson);
            } catch (Exception e) {
                envelope.put("response", rawResponse);
            }
        } else {
            envelope.put("response", "");
        }

        String preview = rawResponse == null ? "" : rawResponse.substring(0, Math.min(rawResponse.length(), 200));
        System.out.println("Route: " + routeId);
        System.out.println("HTTP method: " + httpMethod);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("HTTP status: " + httpStatus);
        System.out.println("Response preview: " + preview);

        exchange.getMessage().setBody(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(envelope));
    }
}