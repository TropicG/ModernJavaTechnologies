package bg.sofia.uni.fmi.mjt.order.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public record Order(String id, LocalDate date, String product, Category category, double price, int quantity,
                    double totalSales, String customerName, String customerLocation,
                    PaymentMethod paymentMethod, Status status) {

    private static final String STRING_SEPARATOR = ",";

    private static final int ID_INDEX = 0;
    private static final int DATE_INDEX = 1;
    private static final int PRODUCT_INDEX = 2;
    private static final int CATEGORY_INDEX = 3;
    private static final int PRICE_INDEX = 4;
    private static final int QUANTITY_INDEX = 5;
    private static final int TOTAL_SALES_INDEX = 6;
    private static final int CUSTOMER_NAME_INDEX = 7;
    private static final int CUSTOMER_LOC_INDEX = 8;
    private static final int PAYMENT_METHOD_INDEX = 9;
    private static final int STATUS_INDEX = 10;

    public static Order of(String line) {
        String[] arguments = line.split(STRING_SEPARATOR);

        arguments[CATEGORY_INDEX] = arguments[CATEGORY_INDEX].replace(" ", "_").toUpperCase();
        arguments[PAYMENT_METHOD_INDEX] =
                arguments[PAYMENT_METHOD_INDEX].replace(" ", "_").toUpperCase();
        arguments[STATUS_INDEX] = arguments[STATUS_INDEX].toUpperCase();

        return new Order(
                arguments[ID_INDEX],
                LocalDate.parse(arguments[DATE_INDEX], DateTimeFormatter.ofPattern("dd-MM-yy")),
                arguments[PRODUCT_INDEX],
                Category.valueOf(arguments[CATEGORY_INDEX]),
                Double.parseDouble(arguments[PRICE_INDEX]),
                Integer.parseInt(arguments[QUANTITY_INDEX]),
                Double.parseDouble(arguments[TOTAL_SALES_INDEX]),
                arguments[CUSTOMER_NAME_INDEX],
                arguments[CUSTOMER_LOC_INDEX],
                PaymentMethod.valueOf(arguments[PAYMENT_METHOD_INDEX]),
                Status.valueOf(arguments[STATUS_INDEX]));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }

        Order otherOrder = (Order) other;
        return Objects.equals(this.id, otherOrder.id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", product='" + product + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", quantity=" + quantity +
                ", totalSales=" + totalSales +
                ", customerName='" + customerName + '\'' +
                ", customerLocation='" + customerLocation + '\'' +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                '}';
    }
}
