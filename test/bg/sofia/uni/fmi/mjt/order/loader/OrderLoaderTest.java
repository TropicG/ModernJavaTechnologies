package bg.sofia.uni.fmi.mjt.order.loader;

import bg.sofia.uni.fmi.mjt.order.domain.Order;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderLoaderTest {


    @Test
    void testLoadingNullArgShallThrowException() {
        assertThrows(IllegalArgumentException.class, () -> OrderLoader.load(null), "When trying to load" +
                "with null argumen, IllegalArgumentExceptionShallBeThrown");
    }

    @Test
    void testSuccessfulLoading() {
        String orderData = "\nORD0001,14-03-25,Running Shoes,Footwear,60,3,180,Emma Clark,New York,Debit Card,Cancelled";
        Reader stringReader = new StringReader(orderData);

        List<Order> orderList = OrderLoader.load(stringReader);

        assertNotNull(orderList, "When loading the returned value should not be null");
        assertEquals(1, orderList.size(), "The returned list should have only one Order");

        assertEquals("ORD0001", orderList.getFirst().id(), "After loading the order should have the same id");
    }

}
