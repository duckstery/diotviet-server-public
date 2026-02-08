package diotviet.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import diotviet.server.structures.Tuple;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Other utility
 */
@Component
public class StorageUtils {

    // ****************************
    // Constants
    // ****************************

    /**
     * ImgBB account username
     */
    private static final String USERNAME = "ducdao";
    /**
     * ImgBB account password
     */
    private static final String PASSWORD = "z/PpH*J*7jigEiK";

    /**
     * ImgBB Login URL
     */
    private static final String IMG_BB_LOGIN_URL = "https://imgbb.com/login";
    /**
     * ImgBB Action URL
     */
    private static final String IMG_BB_ACTION_URL = "https://imgbb.com/json";
    /**
     * ImgBB Upload API
     */
    private static final String IMG_BB_UPLOAD_API = "https://api.imgbb.com/1/upload?key=5ae2315a8c7e1debf761ed1282f7c933";


    /**
     * Auth token form's key
     */
    private static final String TOKEN_FORM_KEY = "auth_token";
    /**
     * Username form's key
     */
    private static final String USERNAME_FORM_KEY = "login-subject";
    /**
     * Password form's key
     */
    private static final String PASSWORD_FORM_KEY = "password";
    /**
     * Session cookie's name
     */
    private static final String SESSION_COOKIE_KEY = "PHPSESSID";
    /**
     * LID cookie's name
     */
    private static final String LID_COOKIE_KEY = "LID";
    /**
     * Auth token value find regex
     */
    private static final Pattern TOKEN_VALUE_REGEX = Pattern.compile("(?<=auth_token=\")[^\"]*");

    // ****************************
    // Properties
    // ****************************

    /**
     * PHP Session (Cookie)
     */
    private String phpSession = null;
    /**
     * Really don't know what is this
     */
    private String lid = null;
    /**
     * Authentication token
     */
    private String authToken = null;

    // ****************************
    // Public API
    // ****************************

    /**
     * Save file to ImgBB
     *
     * @param file
     * @return
     */
    public JsonNode upload(MultipartFile file) throws IOException {
        // Convert Multipart file to bytes[]
        return upload(file.getBytes());
    }

    /**
     * Save file as bytes to ImgBB
     * @param bytes
     * @return
     * @throws IOException
     */
    public JsonNode upload(byte[] bytes) throws IOException {
        // Create HttpEntity
        HttpEntity<?> request = new HttpEntity<>(
                RestUtils.craftFormData(Tuple.of("image", Base64.getEncoder().encodeToString(bytes))),
                RestUtils.generateHeaders(true)
        );

        // Send file to ImgBB Upload API to save file
        return RestUtils.requestForBody(IMG_BB_UPLOAD_API, HttpMethod.POST, request);
    }

    /**
     * Delete file
     *
     * @param uIds
     */
    public void delete(List<String> uIds) {
        try {
            // Try to log in
            tryToLoginToImgBB();

            // Create HttpEntity with form data
            HttpEntity<?> request = createRequestEntity(ArrayUtils.addAll(
                    uIds.stream().map(uid -> Tuple.of("deleting[ids][]", uid)).toArray(Tuple[]::new),
                    Tuple.of(TOKEN_FORM_KEY, authToken),
                    Tuple.of("pathname", "/"),
                    Tuple.of("action", "delete"),
                    Tuple.of("from", "list"),
                    Tuple.of("delete", "images"),
                    Tuple.of("multiple", "true")
            ));

            // Request to delete and check if fail because of 403
            try {
                RestUtils.requestForBody(IMG_BB_ACTION_URL, HttpMethod.POST, request);
            } catch (HttpClientErrorException e) {
                // Get response code
                int code = e.getResponseBodyAs(JsonNode.class).get("error").get("code").asInt();
                if (code == 403) {
                    // Force login
                    forceLoginToImgBB();
                    // Retry
                    RestUtils.request(IMG_BB_ACTION_URL, HttpMethod.POST, request);
                } else if (code != 100) {
                    throw e;
                }
            }
        } catch (Throwable ignored) {
            ignored.printStackTrace();
            // Really don't care if deleted or not since ImgBB space is unlimited
            System.out.println("Failed to delete " + uIds);
        }
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Create HttpEntity
     *
     * @param tuples
     * @return
     */
    @SafeVarargs
    private HttpEntity<?> createRequestEntity(Tuple<String, String>... tuples) {
        // Generate headers
        HttpHeaders headers = RestUtils.generateHeaders(false);
        // Cookie header
        List<String> cookieStrings = new ArrayList<>();

        // Set PHP Session
        if (StringUtils.isNotBlank(phpSession)) {
            cookieStrings.add(String.format("%s=%s", SESSION_COOKIE_KEY, phpSession));
        }
        // Set lid
        if (StringUtils.isNotBlank(lid)) {
            cookieStrings.add(String.format("%s=%s", LID_COOKIE_KEY, lid));
        }

        // Set Cookie header
        headers.set(HttpHeaders.COOKIE, String.join(";", cookieStrings));

        return new HttpEntity<>(RestUtils.craftFormData(tuples), headers);
    }

    /**
     * Try to log in to ImgBB server
     */
    private void tryToLoginToImgBB() {
        // Only login if no credential information is stored
        if (StringUtils.isBlank(authToken) || StringUtils.isBlank(phpSession) || StringUtils.isBlank(lid)) {
            // Get PHP Session
            getPHPSessionAndAuthToken();

            // Create HttpEntity with username and password
            HttpEntity<?> request = createRequestEntity(
                    Tuple.of(TOKEN_FORM_KEY, authToken),
                    Tuple.of(USERNAME_FORM_KEY, USERNAME),
                    Tuple.of(PASSWORD_FORM_KEY, PASSWORD)
            );

            // Send login request to ImgBB and get response
            ResponseEntity<String> response = RestUtils.request(IMG_BB_LOGIN_URL, HttpMethod.POST, request);
            // Get Cookies in Set-Cookie header
            String cookiesString = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
            if (Objects.isNull(cookiesString)) {
                throw new RuntimeException("LID not found");
            }
            // Parse LID cookie
            HttpCookie cookie = HttpCookie.parse(cookiesString).get(0);
            if (!cookie.getName().equalsIgnoreCase("lid")) {
                throw new RuntimeException("LID not found");
            }

            // Set LID
            lid = cookie.getValue();
        }
    }

    /**
     * Force login
     */
    private void forceLoginToImgBB() {
        // Clear all credential
        phpSession = null;
        lid = null;
        authToken = null;

        // Login
        tryToLoginToImgBB();
    }

    /**
     * Get PHP Session of ImgBB
     */
    private void getPHPSessionAndAuthToken() {
        // Create HttpEntity
        HttpEntity<?> request = createRequestEntity();
        // Send request to ImgBB to get response
        ResponseEntity<String> response = RestUtils.request(IMG_BB_LOGIN_URL, HttpMethod.GET, request);

        // Get Cookies in Set-Cookie header
        String cookiesString = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        if (Objects.isNull(cookiesString)) {
            throw new RuntimeException("PHP Session not found");
        }
        // Parse cookies
        List<HttpCookie> cookies = HttpCookie.parse(cookiesString);
        // Iterate through each cookie to find the PHP Session cookie
        for (HttpCookie cookie : cookies) {
            if (cookie.getName().equals(SESSION_COOKIE_KEY)) {
                // Save cookie value
                phpSession = cookie.getValue();
                // Save auth token
                Matcher matcher = TOKEN_VALUE_REGEX.matcher(OtherUtils.get(response.getBody(), ""));
                if (matcher.find()) {
                    authToken = matcher.group();
                } else {
                    throw new RuntimeException("Auth token not found");
                }
                return;
            }
        }

        throw new RuntimeException("PHP Session not found");
    }
}
