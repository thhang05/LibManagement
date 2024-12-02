package Handler;

import database.BookRecommendation;

import java.util.*;

public class AuthorHandler {
    private final Map<String, Integer> authorIndex; // Bảng ánh xạ tác giả -> chỉ số
    private final int numAuthors; // Số lượng tác giả duy nhất

    public AuthorHandler(List<BookRecommendation> books) {
        Set<String> uniqueAuthors = new HashSet<>();

        // Lấy danh sách tác giả duy nhất từ danh sách sách
        for (BookRecommendation book : books) {
            String authorsString = book.getAuthors();
            if (authorsString != null && !authorsString.isEmpty()) {
                String[] authors = authorsString.split(",\\s*");
                Collections.addAll(uniqueAuthors, authors);
            }
        }

        // Gắn chỉ số cho từng tác giả
        authorIndex = new HashMap<>();
        int index = 0;
        for (String author : uniqueAuthors) {
            authorIndex.put(author, index++);
        }

        this.numAuthors = uniqueAuthors.size();
    }

    // Trả về tổng số tác giả
    public int getNumAuthors() {
        return numAuthors;
    }

    // Mã hóa One-Hot Encoding cho cột authors
    public double[] encodeAuthors(String authorsString) {
        double[] authorVector = new double[numAuthors];
        Arrays.fill(authorVector, 0); // Khởi tạo vector toàn 0

        if (authorsString != null && !authorsString.isEmpty()) {
            String[] authors = authorsString.split(",\\s*");
            for (String author : authors) {
                Integer index = authorIndex.get(author.trim());
                if (index != null) {
                    authorVector[index] = 1.0;
                }
            }
        }
        return authorVector;
    }

    // Lấy bảng ánh xạ tác giả -> chỉ số
    public Map<String, Integer> getAuthorIndex() {
        return authorIndex;
    }
}