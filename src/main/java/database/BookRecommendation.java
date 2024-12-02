package database;

import java.util.Random;

import Handler.AuthorHandler;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class BookRecommendation {
    private String authors;
    private String title;
    private String publisher;
    private int numberOfPages;
    private String language;
    private String categories;
    private String format;
    private Boolean borrowed;

    // Constructor, getters, and setters
    public BookRecommendation(String authors, String title, String publisher, int numberOfPages,
                              String language, String categories, String format, Boolean borrowed) {
        this.authors = authors;
        this.title = title;
        this.publisher = publisher;
        this.numberOfPages = numberOfPages;
        this.language = language;
        this.categories = categories;
        this.format = format;
        this.borrowed = borrowed;
    }

    public String getAuthors() {
        return authors;
    }

    public String getTitle() {
        return title;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public String getCategories() {
        return categories;
    }

    public String getFormat() {
        return format;
    }

    public String getLanguage() {
        return language;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getBorrowed() {
        return borrowed;
    }

    public void setBorrowed(Boolean borrowed) {
        this.borrowed = borrowed;
    }

    // 1. Chuẩn bị dữ liệu từ danh sách sách và AuthorHandler
    public static Instances prepareData(List<BookRecommendation> bookRecommendationList, AuthorHandler authorHandler) {
        // 1.1. Khởi tạo danh sách các đặc trưng (attributes)
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("normalizedPages"));

        // Mã hóa định dạng (format) thành biến nhị phân
        List<String> formats = List.of("Hardcover", "Paperback", "Ebook", "Audiobook", "Newspaper");
        for (String format : formats) {
            attributes.add(new Attribute("is" + format));
        }

        // Mã hóa danh mục (categories) thành biến nhị phân
        List<String> categories = List.of("Fiction", "Science", "Comic", "Cyberspace", "Young Adult Fiction",
                "Biography & Autobiography", "Juvenile Nonfiction", "History", "Performing Arts", "True Crime",
                "Unknown", "Doraemon(Fictitious Character)");
        for (String category : categories) {
            attributes.add(new Attribute("is" + category.replaceAll("\\s+|\\(|\\)", ""))); // Xử lý tên biến hợp lệ
        }

        // Thêm các thuộc tính từ AuthorHandler (One-Hot Encoding)
        for (int i = 0; i < authorHandler.getNumAuthors(); i++) {
            attributes.add(new Attribute("author_" + i));
        }

        // Nhãn mục tiêu (borrowed) là nominal
        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("0"); // Chưa mượn
        classValues.add("1"); // Đã mượn
        attributes.add(new Attribute("borrowed", classValues)); // 'borrowed' là thuộc tính phân loại

        // 1.2. Khởi tạo đối tượng Instances
        Instances dataSet = new Instances("BooksDataset", attributes, 0);
        dataSet.setClassIndex(dataSet.numAttributes() - 1); // 'borrowed' là cột đích (class)

        // 1.3. Chuẩn bị và thêm dữ liệu vào Instances
        for (BookRecommendation book : bookRecommendationList) {
            double[] instanceValues = new double[dataSet.numAttributes()];
            int index = 0;

            // Chuẩn hóa numberOfPages
            double normalizedPages = book.getNumberOfPages() / 500.0; // Giả sử 500 là số trang tối đa
            instanceValues[index++] = normalizedPages;

            // Mã hóa format
            for (String format : formats) {
                instanceValues[index++] = book.getFormat().equalsIgnoreCase(format) ? 1.0 : 0.0;
            }

            // Mã hóa categories
            String bookCategory = book.getCategories() != null ? book.getCategories().trim() : "Unknown";
            for (String category : categories) {
                instanceValues[index++] = bookCategory.equalsIgnoreCase(category) ? 1.0 : 0.0;
            }

            // Mã hóa authors (One-Hot Encoding)
            double[] authorVector = authorHandler.encodeAuthors(book.getAuthors());
            for (double value : authorVector) {
                instanceValues[index++] = value;
            }

            // Nhãn mục tiêu: borrowed
            instanceValues[index] = book.getBorrowed() ? 1.0 : 0.0;

            // Thêm instance vào dataSet
            dataSet.add(new DenseInstance(1.0, instanceValues));
        }

        return dataSet;
    }

    // 2. Huấn luyện và đánh giá mô hình Logistic Regression
    public static Logistic trainAndEvaluateLogisticRegression(Instances dataSet) {
        Logistic logisticModel = new Logistic();
        try {
            // 2.1. Tách dữ liệu thành tập huấn luyện và kiểm tra
            dataSet.randomize(new Random(1)); // Trộn ngẫu nhiên dữ liệu
            int trainSize = (int) Math.round(dataSet.numInstances() * 0.8); // 80% dữ liệu để huấn luyện
            int testSize = dataSet.numInstances() - trainSize;

            Instances trainData = new Instances(dataSet, 0, trainSize);
            Instances testData = new Instances(dataSet, trainSize, testSize);

            // 2.2. Huấn luyện mô hình Logistic Regression
            logisticModel.buildClassifier(trainData);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return logisticModel;
    }

    // 3. Dự đoán xác suất mượn sách
    public static double predictBorrowedProbability(BookRecommendation book, AuthorHandler authorHandler, Logistic model) {
        try {
            // Tạo một instance duy nhất để dự đoán
            Instances dataSet = prepareData(List.of(book), authorHandler);
            return model.distributionForInstance(dataSet.firstInstance())[1]; // Xác suất 'borrowed' = 1
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0.0;
    }
}