package bg.sofia.uni.fmi.mjt.order.analyzer;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OrderAnalyzerImpl implements OrderAnalyzer {

    private final List<Order> orders;

    private static final int LIMIT_SUSPICIOUS = 100;
    private static final int MAX_SUSPICIOUS_ORDERS = 3;

    public OrderAnalyzerImpl(List<Order> orders) {
        if (orders == null) {
            this.orders = new ArrayList<>();
        } else {
            this.orders = orders;
        }
    }

    @Override
    public List<Order> allOrders() {
        return Collections.unmodifiableList(orders);
    }

    @Override
    public List<Order> ordersByCustomer(String customer) {
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Null cannot be passed to check orders by customer");
        }

        // stream that will filter all orders that aren't from customer, returning the result as a string
        return orders.stream()
                .filter(order -> order.customerName().equals(customer))
                .toList();
    }

    @Override
    public Map.Entry<LocalDate, Long> dateWithMostOrders() {

        // returns a map that contains for each date how many orders were made
        Map<LocalDate, Long> datesWithOrders = orders.stream()
                .collect(Collectors.groupingBy(Order::date, Collectors.counting()));

        // from all the dates and how many orders were made on them, the date with max num is gotten
        // comparison is based on the values of the map and in case of a tie, the check is made on the keys
        return datesWithOrders.entrySet().stream()
                .max(Map.Entry.<LocalDate, Long>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                .orElse(null);
    }

    @Override
    public List<String> topNMostOrderedProducts(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Most ordered products expects a >=0 parameter");
        }

        if (n == 0) {
            return new ArrayList<>();
        }

        return orders.stream()
                // creates a Map<String, Long> that contains how many times an order was ordered
                .collect(Collectors.groupingBy(Order::product, Collectors.counting()))
                .entrySet().stream()
                // compares products based on their frequency, in case of tie compare the order names alphabetically
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                // gets the top n
                .limit(n)
                // gets the names of the products and returns it as list
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public Map<Category, Double> revenueByCategory() {
        return orders.stream()
                // collects for every category how many (sum) totalsales were made from the orders
                .collect(Collectors.groupingBy(Order::category, Collectors.summingDouble(Order::totalSales)));
    }

    @Override
    public Set<String> suspiciousCustomers() {

        // condition for suspicious order for potential suspicious candidate
        Predicate<Order> suspiciousCustomer =
                order -> order.status() == Status.CANCELLED && order.totalSales() < LIMIT_SUSPICIOUS;

        // getting all the orders, filtering those customers that have at least one suspicious order
        // the returned map has a key of the customer name and how many suspicious order does he have
        Map<String, Long> suspiciousCustomers = orders.stream()
                .filter(suspiciousCustomer)
                .collect(Collectors.groupingBy(Order::customerName, Collectors.counting()));

        // filtering those customer that has less than 3 suspicious orders and returning set
        return suspiciousCustomers.entrySet().stream()
                .filter(entry -> entry.getValue() > MAX_SUSPICIOUS_ORDERS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Map<Category, PaymentMethod> mostUsedPaymentMethodForCategory() {
        // groups for each category a map of each payment issued with the number of times
        Map<Category, Map<PaymentMethod, Long>> paymentMethodsPerCategory = orders.stream()
                .collect(Collectors.groupingBy(
                        Order::category,
                        Collectors.groupingBy(Order::paymentMethod, Collectors.counting())
                ));

        return paymentMethodsPerCategory.entrySet().stream()
                // for each category, get the most used payment method
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        // get for each specific category a stream containing information about the payment method
                        entry -> entry.getValue().entrySet().stream()
                                // gets the most used payment method, in case of a tie compare them in alphabetic order
                                .max(Map.Entry.<PaymentMethod, Long>comparingByValue(Comparator.reverseOrder())
                                        .thenComparing(e -> e.getKey().toString(), Comparator.reverseOrder()))
                                .map(Map.Entry::getKey)
                                .orElse(null)
                ));
    }

    @Override
    public String locationWithMostOrders() {
        return orders.stream()
                // map that has a key for the customer location and value as how many orders from this loc
                .collect(Collectors.groupingBy(Order::customerLocation, Collectors.counting()))
                .entrySet().stream()
                // sorts the entries, first based on their count, in case of a time loc are compared alphabetically
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                // gets all the keys (customer locations)
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    @Override
    public Map<Category, Map<Status, Long>> groupByCategoryAndStatus() {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::category,
                        Collectors.groupingBy(
                                Order::status,
                                Collectors.counting())));
    }

}
