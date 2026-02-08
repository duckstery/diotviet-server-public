package diotviet.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import diotviet.server.structures.Tuple;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Rest utility
 */
public class RestUtils {

    // ****************************
    // Public API
    // ****************************

    /**
     * Craft form-data
     *
     * @param tuples
     * @return
     */
    @SafeVarargs
    public static LinkedMultiValueMap<String, String> craftFormData(Tuple<String, String>... tuples) {
        // Create body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        for (Tuple<String, String> tuple : tuples) {
            body.add(tuple.getLeft(), tuple.getRight());
        }

        return body;
    }

    /**
     * Generate headers
     *
     * @return
     */
    public static HttpHeaders generateHeaders(boolean jsonOnly) {
        // Create header
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, jsonOnly ? MediaType.APPLICATION_JSON_VALUE : MediaType.ALL_VALUE);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setCacheControl(CacheControl.noStore());

        return headers;
    }

    /**
     * Send request
     *
     * @param url
     * @param request
     * @return
     */
    public static ResponseEntity<String> request(String url, HttpMethod method, HttpEntity<?> request) {
        return (new RestTemplate()).exchange(url, method, request, String.class);
    }

    /**
     * Send request and parse body to JsonNode
     *
     * @param url
     * @param method
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    public static JsonNode requestForBody(String url, HttpMethod method, HttpEntity<?> request) throws JsonProcessingException {
        return (new ObjectMapper()).readTree(request(url, method, request).getBody());
    }
}
