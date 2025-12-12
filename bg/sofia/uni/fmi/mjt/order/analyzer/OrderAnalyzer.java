package bg.sofia.uni.fmi.mjt.order.analyzer;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides analytical operations over a collection of {@link Order} objects.
 * <p>
 * This interface exposes various utility methods for querying, grouping,
 * and extracting insights from order data.
 */
public interface OrderAnalyzer {

    /**
     * Returns an immutable copy of all orders.
     *
     * @return the list of all orders
     */
    List<Order> allOrders();

    /**
     * Returns an immutable list of all orders placed by the given customer.
     *
     * @param customer the customer name (case-sensitive) to filter by
     * @return a list of orders made by the specified customer,
     * or an empty list if none exist
     * @throws IllegalArgumentException if customer is null or blank
     */
    List<Order> ordersByCustomer(String customer);

    /**
     * Returns the date on which the most orders were placed and their count.
     * In case of a tie, return the earliest of the dates with equal number of orders.
     *
     * @return the date with the highest number of orders and the count of the orders,
     * or null if there are no orders
     */
    Map.Entry<LocalDate, Long> dateWithMostOrders();

    /**
     * Returns the top N most frequently ordered products, where frequency is
     * the number of orders containing the product (not the sum of ordered quantities).
     * If two products have the same number of orders, sort them alphabetically.
     *
     * @param n the number of products to return. If n is 0, returns an empty list.
     *          If n exceeds the number of distinct products, returns all of them.
     * @return a list of product names ordered by frequency
     * @throws IllegalArgumentException if n < 0
     */
    List<String> topNMostOrderedProducts(int n);

    /**
     * Computes the total revenue for each {@link Category} present in the dataset.
     * Revenue is defined as the sum of {@link Order#totalSales()} for all orders
     * in that category.
     *
     * @return a map from category to total revenue
     */
    Map<Category, Double> revenueByCategory();

    /**
     * Identifies customers whose ordering behavior is suspicious.
     * A customer is suspicious if they have more than 3 orders that are both:
     * <ul>
     *   <li>with status {@link Status#CANCELLED}</li>
     *   <li>with total sales value < 100.0</li>
     * </ul>
     *
     * @return a set of suspicious customer names
     */
    Set<String> suspiciousCustomers();

    /**
     * Determines the most frequently used {@link PaymentMethod}
     * for each product category in the dataset. In case the dataset is empty,
     * returns an empty Map.
     * In case of a tie for a category, return the {@link PaymentMethod}
     * whose name comes first alphabetically.
     *
     * @return a map from category to its most frequently used payment method
     */
    Map<Category, PaymentMethod> mostUsedPaymentMethodForCategory();

    /**
     * Determines the customer location that has the most orders.
     * If multiple locations tie, return the one that is alphabetically smallest.
     *
     * @return the location with the most orders, or null if there are no orders
     */
    String locationWithMostOrders();

    /**
     * Groups orders first by {@link Category}, and within each category
     * by {@link Status}, counting how many orders fall into each group.
     * In case the dataset is empty, returns an empty Map.
     *
     * @return a map where each category maps to another map
     * from status to the count of orders with that status
     */
    Map<Category, Map<Status, Long>> groupByCategoryAndStatus();
}