package com.book.service;

import com.book.entity.Book;
import com.book.entity.Order;
import com.book.mapper.BookMapper;
import com.book.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BookMapper bookMapper;

    // 基于用户购买历史的推荐
    public List<Book> recommendByUserHistory(Integer userId, int limit) {
        List<Order> userOrders = orderMapper.selectByUserId(userId);
        if (userOrders == null || userOrders.isEmpty()) {
            return getHotBooks(limit);
        }

        Set<Integer> purchasedBookIds = userOrders.stream()
                .map(Order::getBookId)
                .collect(Collectors.toSet());

        Map<Integer, Integer> categoryCount = new HashMap<>();
        for (Order order : userOrders) {
            Book book = bookMapper.selectById(order.getBookId());
            if (book != null) {
                categoryCount.put(book.getCategoryId(), 
                    categoryCount.getOrDefault(book.getCategoryId(), 0) + 1);
            }
        }

        List<Book> allBooks = bookMapper.selectAll();
        List<Book> recommendedBooks = allBooks.stream()
                .filter(book -> !purchasedBookIds.contains(book.getId()))
                .sorted((b1, b2) -> {
                    int score1 = categoryCount.getOrDefault(b1.getCategoryId(), 0);
                    int score2 = categoryCount.getOrDefault(b2.getCategoryId(), 0);
                    return Integer.compare(score2, score1);
                })
                .limit(limit)
                .collect(Collectors.toList());

        if (recommendedBooks.isEmpty()) {
            return getHotBooks(limit);
        }

        return recommendedBooks;
    }

    // 基于协同过滤的推荐
    public List<Book> recommendByCollaborativeFiltering(Integer userId, int limit) {
        List<Order> allOrders = getAllOrders();
        Map<Integer, Set<Integer>> userBookMap = buildUserBookMap(allOrders);
        Set<Integer> targetUserBooks = userBookMap.getOrDefault(userId, new HashSet<>());

        if (targetUserBooks.isEmpty()) {
            return getHotBooks(limit);
        }

        Map<Integer, Double> userSimilarity = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : userBookMap.entrySet()) {
            Integer otherUserId = entry.getKey();
            if (!otherUserId.equals(userId)) {
                Set<Integer> otherUserBooks = entry.getValue();
                double similarity = calculateJaccardSimilarity(targetUserBooks, otherUserBooks);
                if (similarity > 0) {
                    userSimilarity.put(otherUserId, similarity);
                }
            }
        }

        Map<Integer, Double> bookScore = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : userSimilarity.entrySet()) {
            Integer otherUserId = entry.getKey();
            Double similarity = entry.getValue();
            Set<Integer> otherUserBooks = userBookMap.get(otherUserId);
            for (Integer bookId : otherUserBooks) {
                if (!targetUserBooks.contains(bookId)) {
                    bookScore.put(bookId, bookScore.getOrDefault(bookId, 0.0) + similarity);
                }
            }
        }

        List<Book> recommendedBooks = bookScore.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(entry -> bookMapper.selectById(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (recommendedBooks.isEmpty()) {
            return getHotBooks(limit);
        }

        return recommendedBooks;
    }

    // 热门推荐
    public List<Book> getHotBooks(int limit) {
        return bookMapper.selectHotBooks(limit);
    }

    // 混合推荐（结合多种推荐策略）
    public List<Book> getHybridRecommendations(Integer userId, int limit) {
        List<Book> historyRecommend = recommendByUserHistory(userId, limit / 2);
        List<Book> hotRecommend = getHotBooks(limit / 2);

        Set<Integer> seenIds = new HashSet<>();
        List<Book> result = new ArrayList<>();

        for (Book book : historyRecommend) {
            if (!seenIds.contains(book.getId())) {
                result.add(book);
                seenIds.add(book.getId());
            }
        }

        for (Book book : hotRecommend) {
            if (!seenIds.contains(book.getId())) {
                result.add(book);
                seenIds.add(book.getId());
            }
        }

        return result.stream().limit(limit).collect(Collectors.toList());
    }

    private List<Order> getAllOrders() {
        return orderMapper.selectAll();
    }

    private Map<Integer, Set<Integer>> buildUserBookMap(List<Order> orders) {
        Map<Integer, Set<Integer>> userBookMap = new HashMap<>();
        for (Order order : orders) {
            userBookMap.computeIfAbsent(order.getUserId(), k -> new HashSet<>())
                    .add(order.getBookId());
        }
        return userBookMap;
    }

    private double calculateJaccardSimilarity(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / union.size();
    }
}
