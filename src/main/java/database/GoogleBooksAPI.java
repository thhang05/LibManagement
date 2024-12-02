package database;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.*;

public class GoogleBooksAPI {

    private static String API_KEY;
    private static String BASE_URL;
    private static GoogleBooksAPI googleBooksAPI = null;

    private GoogleBooksAPI() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Tải các cấu hình từ file config.properties vào đối tượng Properties
            Properties props = new Properties();
            props.load(input);
            API_KEY = props.getProperty("api.key"); // Lấy khóa API
            BASE_URL = props.getProperty("api.base.url"); // Lấy URL cơ bản của API
        } catch (Exception e) {
            System.out.println("Error loading config properties: " + e.getMessage());
        }
    }

    public JSONArray searchBooks(String query, String searchType) {
        int totalResults = 100;  // Tổng số kết quả tìm kiếm, có thể thay đổi dựa trên API
        JSONArray allResults = new JSONArray();  // Mảng chứa tất cả kết quả tìm kiếm
        int maxResults = 40;  // Số lượng kết quả tối đa lấy từ mỗi lần gọi API
        int startIndex = 0;  // Chỉ số bắt đầu tìm kiếm trong mỗi lần gọi API

        // Tạo một ExecutorService với một thread pool gồm 4 luồng
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<JSONArray>> futures = new ArrayList<>();  // Danh sách chứa các future để lưu kết quả từ các task

        try {
            // Mã hóa chuỗi tìm kiếm để đảm bảo an toàn trong URL
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // Xác định prefix cho việc tìm kiếm tùy thuộc vào loại tìm kiếm (tên sách, tác giả, chủ đề...)
            String queryPrefix = switch (searchType) {
                case "Author" -> "inauthor:";
                case "Subject" -> "subject:";
                default -> "intitle:";
            };

            // Vòng lặp này thực hiện tìm kiếm theo nhiều trang nếu cần thiết
            while (totalResults > 0) {
                // Xác định số lượng kết quả cần lấy trong mỗi batch
                int currentBatchSize = Math.min(maxResults, totalResults);

                // Tạo một task Callable để thực hiện tìm kiếm và lấy kết quả JSON
                Callable<JSONArray> task = getJsonArrayCallable(startIndex, queryPrefix, encodedQuery, currentBatchSize);

                // Gửi task vào thread pool và lưu Future trả về
                futures.add(executor.submit(task));

                // Giảm số lượng kết quả cần tìm kiếm và điều chỉnh startIndex
                totalResults -= currentBatchSize;
                startIndex += currentBatchSize;
            }

            // Lấy kết quả từ các task đã thực thi
            for (Future<JSONArray> future : futures) {
                try {
                    // Lấy kết quả JSON từ mỗi task và thêm vào mảng allResults
                    JSONArray items = future.get();
                    for (int i = 0; i < items.length(); i++) {
                        allResults.put(items.getJSONObject(i));
                    }
                } catch (Exception e) {
                    System.out.println("Error retrieving task result: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        } finally {
            // Đảm bảo tắt executor sau khi tất cả các task đã hoàn thành
            executor.shutdown();
        }

        // Trả về tất cả các kết quả tìm kiếm đã thu thập
        return allResults;
    }

    private static Callable<JSONArray> getJsonArrayCallable(int startIndex, String queryPrefix, String encodedQuery, int currentBatchSize) {
        return () -> {
            // Tạo URL API với các tham số tìm kiếm
            String url = BASE_URL + "?q=" +
                    queryPrefix + encodedQuery +
                    "&maxResults=" + currentBatchSize +
                    "&startIndex=" + startIndex +
                    "&key=" + API_KEY;

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // Gửi yêu cầu GET tới API
                HttpGet request = new HttpGet(url);
                CloseableHttpResponse response = httpClient.execute(request);

                // Đọc phản hồi từ API dưới dạng chuỗi JSON
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(jsonResponse);

                // Nếu phản hồi chứa "items", trả về mảng các kết quả tìm kiếm
                if (jsonObject.has("items")) {
                    return jsonObject.getJSONArray("items");
                }
            } catch (Exception e) {
                // Xử lý lỗi khi gọi API
                System.err.println(e.getMessage());
            }
            // Trả về mảng rỗng nếu có lỗi
            return new JSONArray();
        };
    }


    public static GoogleBooksAPI getInstance() {
        if (googleBooksAPI == null) {
            googleBooksAPI = new GoogleBooksAPI();
        }
        return googleBooksAPI;
    }
}