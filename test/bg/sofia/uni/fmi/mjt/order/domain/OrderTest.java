package bg.sofia.uni.fmi.mjt.order.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTest {

    @Test
    void testSuccessfulOrderOf() {
        String line = "ORD0001,14-03-25,Running Shoes,Footwear,60,3,180,Emma Clark,New York,Debit Card,Cancelled";

        Order order = Order.of(line);
        LocalDate orderDate = LocalDate.of(25,3,14);

        assertEquals("ORD0001", order.id(), "The id from the Order should be the same from the string");
        assertEquals(orderDate.format(DateTimeFormatter.ofPattern("dd-MM-yy")), order.date().format(DateTimeFormatter.ofPattern("dd-MM-yy")), "The date have to be the same");
        assertEquals("Running Shoes", order.product(), "The product have to be the same");
        assertEquals(Category.FOOTWEAR, order.category(), "The category has to be the same");
        assertEquals(60, order.price(), "The price should be the same");
        assertEquals(3, order.quantity(), "The quantity should be the same");
        assertEquals( 180, order.totalSales(), "The total sales should be the same");
        assertEquals("Emma Clark", order.customerName(), "The customer name should be the same");
        assertEquals("New York", order.customerLocation(), "The location should be the same");
        assertEquals(PaymentMethod.DEBIT_CARD, order.paymentMethod(), "The payment method should be the same");
        assertEquals(Status.CANCELLED, order.status(), "The status should be the same");
    }

    @Test
    void testOrdersEqualsTrue() {
        String line = "ORD0001,14-03-25,Running Shoes,Footwear,60,3,180,Emma Clark,New York,Debit Card,Cancelled";

        Order orderOne = Order.of(line);
        Order orderTwo = Order.of(line);

        assertEquals(orderTwo, orderOne, "The two orders should be equals");
    }

    @Test
    void testOrderSameInstanceTrue() {

        String line = "ORD0001,14-03-25,Running Shoes,Footwear,60,3,180,Emma Clark,New York,Debit Card,Cancelled";
        Order order = Order.of(line);
        Order orderRef = order;

        assertEquals(order, orderRef, "Two same instance should return true");

    }

    @Test
    void testOrderNullEqualsReturnFalse() {

        String line = "ORD0001,14-03-25,Running Shoes,Footwear,60,3,180,Emma Clark,New York,Debit Card,Cancelled";
        Order order = Order.of(line);

        Order orderRef = null;

        assertFalse(order.equals(orderRef), "When one of the argument is null, false shall be returned");
    }

    @Test
    void testEqualsOrderDifferentClass() {

        String line = "ORD0001,14-03-25,Running Shoes,Footwear,60,3,180,Emma Clark,New York,Debit Card,Cancelled";
        Order order = Order.of(line);

        String string = "This is a string";
        assertFalse(order.equals(string), "Two different classes shall return false");

    }

}
