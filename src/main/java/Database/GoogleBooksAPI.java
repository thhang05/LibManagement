package database;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleBooksAPI {

    private static String API_KEY;
    private static String BASE_URL;

    public GoogleBooksAPI() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            Properties props = new Properties();
            props.load(input);
            API_KEY = props.getProperty("api.key");
            BASE_URL = props.getProperty("api.base.url");
        } catch (Exception e) {
            System.out.println("Error loading config properties: " + e.getMessage());
        }
    }

    public JSONArray searchBooks(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = BASE_URL + "?q=" + encodedQuery + "&key=" + API_KEY;

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                CloseableHttpResponse response = httpClient.execute(request);
                String jsonResponse = EntityUtils.toString(response.getEntity());

                JSONObject jsonObject = new JSONObject(jsonResponse);
                return jsonObject.getJSONArray("items");
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error while searching books: " + e.getMessage());
        }
        return null;
    }
}
