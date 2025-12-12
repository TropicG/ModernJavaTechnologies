package bg.sofia.uni.fmi.mjt.order.analyzer;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.loader.OrderLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderAnalyzerTest {

    String orders;
    static OrderAnalyzerImpl orderAnalyzer;

    @BeforeAll
    static void setUpOrders() {
        String orders =
                "\nORD0001,14-03-25,Running Shoes,Footwear,60,3,180,Emma Clark,New York,Debit Card,Cancelled\n" +
                        "ORD0002,20-03-25,Headphones,Electronics,100,4,400,Emily Johnson,San Francisco,Debit Card,Pending\n" +
                        "ORD0003,15-02-25,Running Shoes,Footwear,60,2,120,John Doe,Denver,Amazon Pay,Cancelled\n" +
                        "ORD0004,19-02-25,Running Shoes,Footwear,60,3,180,Olivia Wilson,Dallas,Credit Card,Pending\n" +
                        "ORD0005,10-03-25,Smartwatch,Electronics,150,3,450,Emma Clark,New York,Debit Card,Pending\n" +
                        "ORD0006,14-03-25,T-Shirt,Clothing,20,1,20,John Doe,Dallas,Credit Card,Pending\n" +
                        "ORD0007,18-03-25,Smartwatch,Electronics,150,4,600,Emma Clark,Houston,PayPal,Completed\n" +
                        "ORD0008,02-03-25,Smartphone,Electronics,500,1,500,Sophia Miller,Miami,PayPal,Completed\n" +
                        "ORD0009,08-03-25,T-Shirt,Clothing,20,3,60,Sophia Miller,Boston,PayPal,Completed\n" +
                        "ORD0010,12-03-25,Smartphone,Electronics,500,1,500,Emily Johnson,San Francisco,Credit Card,Cancelled\n" +
                        "ORD0011,17-02-25,Book,Books,15,2,30,David Lee,Boston,Amazon Pay,Pending\n" +
                        "ORD0012,13-03-25,Jeans,Clothing,40,4,160,Michael Brown,Dallas,Credit Card,Completed\n" +
                        "ORD0013,01-03-25,Laptop,Electronics,800,2,1600,Daniel Harris,San Francisco,Gift Card,Pending\n" +
                        "ORD0014,04-03-25,Washing Machine,Home Appliances,600,3,1800,Michael Brown,Miami,Credit Card,Cancelled\n" +
                        "ORD0015,20-02-25,Smartwatch,Electronics,150,4,600,John Doe,Seattle,Credit Card,Completed\n" +
                        "ORD0016,26-02-25,Refrigerator,Home Appliances,1200,1,1200,John Doe,Boston,Credit Card,Cancelled\n" +
                        "ORD0017,01-04-25,T-Shirt,Clothing,20,1,20,Emma Clark,New York,Amazon Pay,Completed\n" +
                        "ORD0018,10-02-25,Smartphone,Electronics,500,2,1000,Michael Brown,Los Angeles,Amazon Pay,Completed\n" +
                        "ORD0019,22-03-25,Running Shoes,Footwear,60,3,180,Olivia Wilson,Houston,Credit Card,Completed\n" +
                        "ORD0020,07-03-25,Headphones,Electronics,100,4,400,Olivia Wilson,Seattle,Debit Card,Pending\n" +
                        "ORD0021,05-02-25,Headphones,Electronics,100,3,300,Chris White,Miami,Debit Card,Cancelled\n" +
                        "ORD0022,07-03-25,Refrigerator,Home Appliances,1200,4,4800,Olivia Wilson,Houston,Credit Card,Pending\n" +
                        "ORD0023,23-02-25,Book,Books,15,1,15,Emma Clark,Houston,Credit Card,Pending\n" +
                        "ORD0024,24-03-25,Refrigerator,Home Appliances,1200,3,3600,Chris White,Dallas,Credit Card,Cancelled\n" +
                        "ORD0025,02-03-25,Book,Books,15,5,75,Sophia Miller,Seattle,Amazon Pay,Completed\n" +
                        "ORD0026,14-02-25,Washing Machine,Home Appliances,600,1,600,Olivia Wilson,Boston,Debit Card,Cancelled\n" +
                        "ORD0027,07-02-25,T-Shirt,Clothing,20,1,20,Daniel Harris,New York,Amazon Pay,Pending\n" +
                        "ORD0028,03-02-25,Headphones,Electronics,100,1,100,Jane Smith,Chicago,Amazon Pay,Completed\n" +
                        "ORD0029,12-02-25,Smartphone,Electronics,500,1,500,Sophia Miller,Denver,Credit Card,Cancelled\n" +
                        "ORD0030,10-02-25,Washing Machine,Home Appliances,600,3,1800,Emily Johnson,Dallas,Gift Card,Cancelled\n" +
                        "ORD0031,24-03-25,Smartphone,Electronics,500,1,500,John Doe,Houston,Gift Card,Pending\n" +
                        "ORD0032,10-03-25,Smartphone,Electronics,500,4,2000,Michael Brown,Seattle,PayPal,Pending\n" +
                        "ORD0033,04-02-25,Book,Books,15,1,15,Olivia Wilson,New York,Debit Card,Cancelled\n" +
                        "ORD0034,02-04-25,T-Shirt,Clothing,20,5,100,Jane Smith,New York,Credit Card,Pending\n" +
                        "ORD0035,02-04-25,Laptop,Electronics,800,3,2400,Emma Clark,Denver,Amazon Pay,Completed\n" +
                        "ORD0036,09-02-25,Refrigerator,Home Appliances,1200,2,2400,Sophia Miller,Boston,PayPal,Cancelled\n" +
                        "ORD0037,16-02-25,Headphones,Electronics,100,3,300,Michael Brown,New York,Debit Card,Cancelled\n" +
                        "ORD0038,26-03-25,Laptop,Electronics,800,3,2400,Olivia Wilson,Chicago,Amazon Pay,Completed\n" +
                        "ORD0039,02-03-25,Laptop,Electronics,800,2,1600,Olivia Wilson,San Francisco,PayPal,Completed\n" +
                        "ORD0040,26-03-25,Smartwatch,Electronics,150,1,150,Emily Johnson,Seattle,Gift Card,Pending\n" +
                        "ORD0041,20-02-25,Book,Books,15,1,15,Jane Smith,Miami,Credit Card,Cancelled\n" +
                        "ORD0042,17-03-25,Headphones,Electronics,100,3,300,Jane Smith,Chicago,Amazon Pay,Cancelled\n" +
                        "ORD0043,08-02-25,T-Shirt,Clothing,20,4,80,Jane Smith,Denver,Credit Card,Pending\n" +
                        "ORD0044,24-03-25,Smartwatch,Electronics,150,1,150,Chris White,Houston,Debit Card,Pending\n" +
                        "ORD0045,15-03-25,Refrigerator,Home Appliances,1200,3,3600,Daniel Harris,New York,Credit Card,Pending\n" +
                        "ORD0046,06-03-25,Running Shoes,Footwear,60,2,120,David Lee,Houston,Debit Card,Cancelled\n" +
                        "ORD0047,26-03-25,T-Shirt,Clothing,20,2,40,Chris White,Miami,Gift Card,Cancelled\n" +
                        "ORD0048,11-02-25,T-Shirt,Clothing,20,5,100,Jane Smith,Denver,Amazon Pay,Completed\n" +
                        "ORD0049,18-02-25,Smartphone,Electronics,500,4,2000,Emma Clark,Chicago,Debit Card,Completed\n" +
                        "ORD0050,14-03-25,Jeans,Clothing,40,3,120,John Doe,Boston,Gift Card,Completed\n" +
                        "ORD0051,19-02-25,Book,Books,15,1,15,Emma Clark,Houston,Gift Card,Cancelled\n" +
                        "ORD0052,24-02-25,Laptop,Electronics,800,3,2400,John Doe,San Francisco,Credit Card,Pending\n" +
                        "ORD0053,24-03-25,Running Shoes,Footwear,60,4,240,Emily Johnson,Los Angeles,PayPal,Completed\n" +
                        "ORD0054,15-03-25,Smartwatch,Electronics,150,3,450,David Lee,Boston,Gift Card,Pending\n" +
                        "ORD0055,15-03-25,Jeans,Clothing,40,2,80,Sophia Miller,Dallas,PayPal,Completed\n" +
                        "ORD0056,19-03-25,Smartwatch,Electronics,150,2,300,Emma Clark,Dallas,Credit Card,Completed\n" +
                        "ORD0057,15-03-25,Smartphone,Electronics,500,1,500,Jane Smith,Los Angeles,Debit Card,Cancelled\n" +
                        "ORD0058,13-03-25,Smartphone,Electronics,500,1,500,Jane Smith,Chicago,PayPal,Cancelled\n" +
                        "ORD0059,01-04-25,Smartwatch,Electronics,150,2,300,Daniel Harris,Dallas,Credit Card,Cancelled\n" +
                        "ORD0060,12-03-25,Book,Books,15,5,75,Jane Smith,Dallas,Credit Card,Pending\n" +
                        "ORD0061,11-03-25,Refrigerator,Home Appliances,1200,1,1200,Jane Smith,New York,PayPal,Cancelled\n" +
                        "ORD0062,10-02-25,Laptop,Electronics,800,5,4000,Olivia Wilson,San Francisco,PayPal,Completed\n" +
                        "ORD0063,30-03-25,Smartphone,Electronics,500,5,2500,Emma Clark,Miami,Gift Card,Completed\n" +
                        "ORD0064,13-02-25,Refrigerator,Home Appliances,1200,4,4800,Emily Johnson,Denver,PayPal,Pending\n" +
                        "ORD0065,16-03-25,Book,Books,15,3,45,Emma Clark,San Francisco,Amazon Pay,Pending\n" +
                        "ORD0066,14-03-25,Smartwatch,Electronics,150,2,300,Michael Brown,Denver,PayPal,Pending\n" +
                        "ORD0067,31-03-25,Headphones,Electronics,100,3,300,Chris White,New York,Debit Card,Pending\n" +
                        "ORD0068,23-02-25,Headphones,Electronics,100,1,100,David Lee,Houston,Debit Card,Cancelled\n" +
                        "ORD0069,25-02-25,Refrigerator,Home Appliances,1200,4,4800,David Lee,Boston,Gift Card,Pending\n" +
                        "ORD0070,10-03-25,Book,Books,15,1,15,Emily Johnson,Boston,Credit Card,Completed\n" +
                        "ORD0071,16-03-25,Smartwatch,Electronics,150,5,750,John Doe,Denver,Credit Card,Pending\n" +
                        "ORD0072,07-03-25,Laptop,Electronics,800,3,2400,Daniel Harris,Houston,Credit Card,Pending\n" +
                        "ORD0073,20-02-25,Smartphone,Electronics,500,5,2500,Emily Johnson,Miami,Credit Card,Cancelled\n" +
                        "ORD0074,25-03-25,Refrigerator,Home Appliances,1200,4,4800,Jane Smith,Dallas,Gift Card,Cancelled\n" +
                        "ORD0075,26-02-25,Headphones,Electronics,100,2,200,Daniel Harris,Boston,PayPal,Pending\n" +
                        "ORD0076,24-02-25,Washing Machine,Home Appliances,600,1,600,Jane Smith,Dallas,Amazon Pay,Completed\n" +
                        "ORD0077,20-03-25,Headphones,Electronics,100,2,200,Daniel Harris,Houston,Credit Card,Completed\n" +
                        "ORD0078,18-03-25,Smartwatch,Electronics,150,2,300,Emma Clark,Los Angeles,Gift Card,Cancelled\n" +
                        "ORD0079,09-03-25,Running Shoes,Footwear,60,2,120,Emily Johnson,Denver,Gift Card,Cancelled\n" +
                        "ORD0080,23-02-25,Running Shoes,Footwear,60,4,240,Sophia Miller,San Francisco,Debit Card,Pending\n" +
                        "ORD0081,26-02-25,Headphones,Electronics,100,3,300,Michael Brown,Chicago,PayPal,Cancelled\n" +
                        "ORD0082,24-02-25,Smartphone,Electronics,500,3,1500,Jane Smith,Seattle,Debit Card,Cancelled\n" +
                        "ORD0083,28-02-25,Washing Machine,Home Appliances,600,4,2400,Emma Clark,Houston,Gift Card,Cancelled\n" +
                        "ORD0084,14-02-25,T-Shirt,Clothing,20,5,100,Olivia Wilson,Boston,PayPal,Completed\n" +
                        "ORD0085,06-02-25,Smartphone,Electronics,500,5,2500,Michael Brown,Houston,PayPal,Completed\n" +
                        "ORD0086,25-02-25,Smartwatch,Electronics,150,5,750,Jane Smith,Dallas,PayPal,Cancelled\n" +
                        "ORD0087,13-03-25,Running Shoes,Footwear,60,5,300,Emma Clark,Miami,Debit Card,Completed\n" +
                        "ORD0088,06-02-25,Refrigerator,Home Appliances,1200,2,2400,Chris White,Seattle,Debit Card,Pending\n" +
                        "ORD0089,26-03-25,Running Shoes,Footwear,60,5,300,Emma Clark,Los Angeles,Credit Card,Cancelled\n" +
                        "ORD0090,24-03-25,Smartwatch,Electronics,150,5,750,Emily Johnson,Houston,Amazon Pay,Completed\n" +
                        "ORD0091,03-02-25,Laptop,Electronics,800,4,3200,Daniel Harris,Houston,Gift Card,Pending\n" +
                        "ORD0092,15-03-25,Smartphone,Electronics,500,2,1000,Olivia Wilson,Boston,PayPal,Cancelled\n" +
                        "ORD0093,06-03-25,Refrigerator,Home Appliances,1200,5,6000,David Lee,Denver,PayPal,Cancelled\n" +
                        "ORD0094,25-03-25,Jeans,Clothing,40,5,200,Daniel Harris,Seattle,Credit Card,Cancelled\n" +
                        "ORD0095,17-02-25,Smartwatch,Electronics,150,4,600,Chris White,New York,Debit Card,Cancelled\n" +
                        "ORD0096,30-03-25,Smartwatch,Electronics,150,3,450,Jane Smith,New York,Amazon Pay,Completed\n" +
                        "ORD0097,25-03-25,Book,Books,15,5,75,Olivia Wilson,Chicago,Amazon Pay,Pending\n" +
                        "ORD0098,14-02-25,Smartwatch,Electronics,150,2,300,Chris White,Miami,PayPal,Pending\n" +
                        "ORD0099,18-02-25,Washing Machine,Home Appliances,600,5,3000,Michael Brown,Seattle,Debit Card,Completed\n" +
                        "ORD0100,13-02-25,Running Shoes,Footwear,60,1,60,Jane Smith,Houston,Gift Card,Cancelled\n" +
                        "ORD0101,20-02-25,Book,Books,15,5,75,John Doe,Denver,PayPal,Pending\n" +
                        "ORD0102,28-02-25,Smartphone,Electronics,500,2,1000,David Lee,Boston,PayPal,Pending\n" +
                        "ORD0103,14-03-25,T-Shirt,Clothing,20,2,40,Michael Brown,Chicago,PayPal,Pending\n" +
                        "ORD0104,22-02-25,Jeans,Clothing,40,5,200,Jane Smith,Dallas,Debit Card,Pending\n" +
                        "ORD0105,16-03-25,Jeans,Clothing,40,1,40,Jane Smith,Boston,Debit Card,Pending\n" +
                        "ORD0106,15-03-25,T-Shirt,Clothing,20,3,60,Olivia Wilson,Chicago,PayPal,Pending\n" +
                        "ORD0107,19-03-25,Jeans,Clothing,40,3,120,Olivia Wilson,Dallas,Credit Card,Cancelled\n" +
                        "ORD0108,16-03-25,Headphones,Electronics,100,2,200,Daniel Harris,Miami,PayPal,Pending\n" +
                        "ORD0109,17-03-25,Jeans,Clothing,40,5,200,Michael Brown,New York,PayPal,Completed\n" +
                        "ORD0110,18-02-25,Smartphone,Electronics,500,5,2500,Olivia Wilson,Dallas,Amazon Pay,Pending\n" +
                        "ORD0111,31-03-25,Laptop,Electronics,800,4,3200,Emma Clark,Los Angeles,Credit Card,Completed\n" +
                        "ORD0112,06-03-25,Washing Machine,Home Appliances,600,2,1200,David Lee,Dallas,Gift Card,Cancelled\n" +
                        "ORD0113,19-03-25,Book,Books,15,5,75,David Lee,San Francisco,Debit Card,Pending\n" +
                        "ORD0114,23-02-25,Running Shoes,Footwear,60,1,60,Emma Clark,Houston,Credit Card,Pending\n" +
                        "ORD0115,21-03-25,Running Shoes,Footwear,60,3,180,Olivia Wilson,Miami,Amazon Pay,Completed\n" +
                        "ORD0116,19-03-25,Laptop,Electronics,800,4,3200,Emma Clark,Los Angeles,Amazon Pay,Completed\n" +
                        "ORD0117,27-02-25,T-Shirt,Clothing,20,1,20,Daniel Harris,New York,Credit Card,Completed\n" +
                        "ORD0118,10-02-25,Headphones,Electronics,100,5,500,John Doe,Houston,Amazon Pay,Completed\n" +
                        "ORD0119,16-03-25,Smartphone,Electronics,500,2,1000,Chris White,Chicago,PayPal,Pending\n" +
                        "ORD0120,16-02-25,Laptop,Electronics,800,5,4000,Emily Johnson,Denver,PayPal,Completed\n" +
                        "ORD0121,18-02-25,Smartphone,Electronics,500,1,500,John Doe,Dallas,Gift Card,Pending\n" +
                        "ORD0122,28-03-25,Laptop,Electronics,800,3,2400,John Doe,Miami,Credit Card,Cancelled\n" +
                        "ORD0123,23-02-25,Book,Books,15,3,45,Chris White,Boston,Gift Card,Cancelled\n" +
                        "ORD0124,10-02-25,Book,Books,15,1,15,Emma Clark,Seattle,Gift Card,Pending\n" +
                        "ORD0125,23-02-25,Smartphone,Electronics,500,1,500,Emily Johnson,Denver,Amazon Pay,Completed\n" +
                        "ORD0126,04-02-25,Refrigerator,Home Appliances,1200,5,6000,Olivia Wilson,Chicago,Gift Card,Pending\n" +
                        "ORD0127,18-02-25,T-Shirt,Clothing,20,3,60,Emma Clark,Dallas,Debit Card,Completed\n" +
                        "ORD0128,23-03-25,Smartwatch,Electronics,150,2,300,Emily Johnson,Dallas,PayPal,Cancelled\n" +
                        "ORD0129,26-02-25,Jeans,Clothing,40,5,200,Emily Johnson,New York,Debit Card,Pending\n" +
                        "ORD0130,10-02-25,Smartwatch,Electronics,150,5,750,David Lee,Boston,PayPal,Completed\n" +
                        "ORD0131,26-02-25,Jeans,Clothing,40,2,80,Daniel Harris,Chicago,Credit Card,Pending\n" +
                        "ORD0132,07-03-25,Smartphone,Electronics,500,2,1000,Olivia Wilson,Chicago,Debit Card,Pending\n" +
                        "ORD0133,23-02-25,Laptop,Electronics,800,1,800,Emily Johnson,New York,Amazon Pay,Pending\n" +
                        "ORD0134,07-02-25,Smartphone,Electronics,500,5,2500,David Lee,Chicago,Gift Card,Pending\n" +
                        "ORD0135,10-02-25,T-Shirt,Clothing,20,1,20,Olivia Wilson,Miami,Credit Card,Pending\n" +
                        "ORD0136,11-02-25,Refrigerator,Home Appliances,1200,2,2400,Daniel Harris,San Francisco,PayPal,Cancelled\n" +
                        "ORD0137,20-03-25,Smartwatch,Electronics,150,3,450,Sophia Miller,San Francisco,Debit Card,Completed\n" +
                        "ORD0138,11-02-25,Smartwatch,Electronics,150,5,750,John Doe,Los Angeles,Credit Card,Cancelled\n" +
                        "ORD0139,18-03-25,Laptop,Electronics,800,2,1600,Michael Brown,Denver,PayPal,Cancelled\n" +
                        "ORD0140,19-03-25,Smartwatch,Electronics,150,2,300,Olivia Wilson,Miami,Debit Card,Completed\n" +
                        "ORD0141,21-03-25,Smartphone,Electronics,500,2,1000,John Doe,Miami,PayPal,Completed\n" +
                        "ORD0142,15-03-25,Smartphone,Electronics,500,3,1500,Sophia Miller,Miami,Gift Card,Completed\n" +
                        "ORD0143,31-03-25,Refrigerator,Home Appliances,1200,2,2400,Jane Smith,Dallas,PayPal,Cancelled\n" +
                        "ORD0144,27-02-25,Jeans,Clothing,40,2,80,Michael Brown,Los Angeles,Gift Card,Cancelled\n" +
                        "ORD0145,08-03-25,Smartwatch,Electronics,150,2,300,Michael Brown,Seattle,Debit Card,Cancelled\n" +
                        "ORD0146,25-03-25,Smartwatch,Electronics,150,2,300,Emma Clark,Boston,Debit Card,Completed\n" +
                        "ORD0147,05-02-25,Running Shoes,Footwear,60,5,300,Jane Smith,Dallas,PayPal,Pending\n" +
                        "ORD0148,06-02-25,Headphones,Electronics,100,4,400,Emily Johnson,Los Angeles,Amazon Pay,Cancelled\n" +
                        "ORD0149,20-03-25,Running Shoes,Footwear,60,3,180,Michael Brown,New York,Gift Card,Pending\n" +
                        "ORD0150,08-02-25,Book,Books,15,4,60,Daniel Harris,Chicago,Gift Card,Cancelled\n" +
                        "ORD0151,29-03-25,Washing Machine,Home Appliances,600,4,2400,Jane Smith,Los Angeles,Amazon Pay,Pending\n" +
                        "ORD0152,28-02-25,Smartwatch,Electronics,150,1,150,John Doe,Seattle,Gift Card,Completed\n" +
                        "ORD0153,23-02-25,Smartwatch,Electronics,150,5,750,Sophia Miller,San Francisco,PayPal,Pending\n" +
                        "ORD0154,01-03-25,Headphones,Electronics,100,2,200,John Doe,Denver,Debit Card,Completed\n" +
                        "ORD0155,05-02-25,Refrigerator,Home Appliances,1200,4,4800,Sophia Miller,Seattle,Credit Card,Pending\n" +
                        "ORD0156,16-03-25,Smartwatch,Electronics,150,4,600,Olivia Wilson,Miami,PayPal,Cancelled\n" +
                        "ORD0157,08-02-25,Smartphone,Electronics,500,3,1500,Sophia Miller,Denver,Credit Card,Pending\n" +
                        "ORD0158,24-02-25,Laptop,Electronics,800,3,2400,Chris White,Miami,Debit Card,Pending\n" +
                        "ORD0159,16-02-25,T-Shirt,Clothing,20,2,40,David Lee,Dallas,Gift Card,Cancelled\n" +
                        "ORD0160,06-02-25,Washing Machine,Home Appliances,600,3,1800,Olivia Wilson,Denver,Gift Card,Cancelled\n" +
                        "ORD0161,22-02-25,Headphones,Electronics,100,1,100,Emily Johnson,Los Angeles,Credit Card,Completed\n" +
                        "ORD0162,09-02-25,Smartphone,Electronics,500,2,1000,Olivia Wilson,Chicago,Credit Card,Completed\n" +
                        "ORD0163,20-02-25,Running Shoes,Footwear,60,2,120,Chris White,Houston,PayPal,Completed\n" +
                        "ORD0164,25-02-25,Smartwatch,Electronics,150,5,750,Michael Brown,Boston,Debit Card,Pending\n" +
                        "ORD0165,14-03-25,Book,Books,15,1,15,David Lee,New York,Amazon Pay,Pending\n" +
                        "ORD0166,30-03-25,Washing Machine,Home Appliances,600,4,2400,Chris White,Houston,Credit Card,Pending\n" +
                        "ORD0167,20-03-25,Refrigerator,Home Appliances,1200,2,2400,Olivia Wilson,Seattle,Credit Card,Completed\n" +
                        "ORD0168,24-03-25,Laptop,Electronics,800,5,4000,Michael Brown,Miami,Debit Card,Pending\n" +
                        "ORD0169,06-03-25,Refrigerator,Home Appliances,1200,2,2400,John Doe,Denver,PayPal,Completed\n" +
                        "ORD0170,28-02-25,Laptop,Electronics,800,1,800,Emma Clark,San Francisco,Gift Card,Pending\n" +
                        "ORD0171,28-02-25,Smartwatch,Electronics,150,2,300,Daniel Harris,Houston,PayPal,Completed\n" +
                        "ORD0172,12-03-25,Book,Books,15,1,15,David Lee,Miami,Debit Card,Cancelled\n" +
                        "ORD0173,28-02-25,Refrigerator,Home Appliances,1200,1,1200,Olivia Wilson,Seattle,PayPal,Completed\n" +
                        "ORD0174,13-03-25,Smartwatch,Electronics,150,2,300,David Lee,Chicago,Debit Card,Pending\n" +
                        "ORD0175,24-03-25,Jeans,Clothing,40,5,200,David Lee,New York,Debit Card,Cancelled\n" +
                        "ORD0176,27-03-25,Book,Books,15,1,15,Michael Brown,Boston,Amazon Pay,Completed\n" +
                        "ORD0177,14-03-25,Book,Books,15,5,75,David Lee,San Francisco,Credit Card,Pending\n" +
                        "ORD0178,06-02-25,Smartphone,Electronics,500,3,1500,Emily Johnson,Chicago,PayPal,Completed\n" +
                        "ORD0179,25-03-25,Jeans,Clothing,40,1,40,Olivia Wilson,Seattle,PayPal,Cancelled\n" +
                        "ORD0180,04-03-25,Refrigerator,Home Appliances,1200,3,3600,David Lee,Boston,Gift Card,Completed\n" +
                        "ORD0181,03-03-25,Running Shoes,Footwear,60,2,120,David Lee,Los Angeles,Debit Card,Cancelled\n" +
                        "ORD0182,02-04-25,T-Shirt,Clothing,20,5,100,Emma Clark,Denver,PayPal,Completed\n" +
                        "ORD0183,25-03-25,Washing Machine,Home Appliances,600,1,600,Emma Clark,Seattle,Debit Card,Pending\n" +
                        "ORD0184,17-02-25,Book,Books,15,5,75,Daniel Harris,Miami,Debit Card,Pending\n" +
                        "ORD0185,22-03-25,T-Shirt,Clothing,20,1,20,Chris White,Dallas,Debit Card,Completed\n" +
                        "ORD0186,02-03-25,Washing Machine,Home Appliances,600,4,2400,Michael Brown,New York,PayPal,Completed\n" +
                        "ORD0187,13-03-25,Laptop,Electronics,800,3,2400,John Doe,New York,PayPal,Pending\n" +
                        "ORD0188,14-02-25,Book,Books,15,1,15,John Doe,Boston,Amazon Pay,Completed\n" +
                        "ORD0189,10-02-25,Laptop,Electronics,800,3,2400,Jane Smith,Houston,Amazon Pay,Completed\n" +
                        "ORD0190,23-02-25,Running Shoes,Footwear,60,1,60,Sophia Miller,Houston,PayPal,Completed\n" +
                        "ORD0191,28-02-25,Jeans,Clothing,40,4,160,John Doe,Seattle,Amazon Pay,Pending\n" +
                        "ORD0192,31-03-25,Headphones,Electronics,100,3,300,Sophia Miller,Dallas,Gift Card,Pending\n" +
                        "ORD0193,30-03-25,Book,Books,15,5,75,David Lee,Chicago,Amazon Pay,Pending\n" +
                        "ORD0194,20-02-25,Jeans,Clothing,40,4,160,John Doe,San Francisco,Debit Card,Cancelled\n" +
                        "ORD0195,10-03-25,Smartphone,Electronics,500,3,1500,Olivia Wilson,Denver,Gift Card,Cancelled\n" +
                        "ORD0196,04-03-25,Headphones,Electronics,100,5,500,Daniel Harris,Seattle,Debit Card,Pending\n" +
                        "ORD0197,12-03-25,Running Shoes,Footwear,60,2,120,Michael Brown,Denver,Gift Card,Completed\n" +
                        "ORD0198,20-02-25,Laptop,Electronics,800,4,3200,Jane Smith,Seattle,Amazon Pay,Pending\n" +
                        "ORD0199,22-02-25,Smartphone,Electronics,500,3,1500,Chris White,Dallas,Credit Card,Completed\n" +
                        "ORD0200,10-02-25,Book,Books,15,2,30,Chris White,Los Angeles,Debit Card,Completed\n" +
                        "ORD0201,03-02-25,Book,Books,15,4,60,Michael Brown,San Francisco,Credit Card,Completed\n" +
                        "ORD0202,21-02-25,Smartphone,Electronics,500,2,1000,Daniel Harris,Miami,Credit Card,Cancelled\n" +
                        "ORD0203,21-02-25,Refrigerator,Home Appliances,1200,3,3600,John Doe,Dallas,Debit Card,Completed\n" +
                        "ORD0204,08-03-25,Running Shoes,Footwear,60,5,300,Jane Smith,Miami,Gift Card,Pending\n" +
                        "ORD0205,27-02-25,Headphones,Electronics,100,2,200,David Lee,Houston,PayPal,Completed\n" +
                        "ORD0206,12-02-25,Washing Machine,Home Appliances,600,5,3000,Emma Clark,Miami,Amazon Pay,Pending\n" +
                        "ORD0207,21-03-25,Washing Machine,Home Appliances,600,1,600,John Doe,Miami,Credit Card,Completed\n" +
                        "ORD0208,02-02-25,Refrigerator,Home Appliances,1200,3,3600,John Doe,Miami,PayPal,Completed\n" +
                        "ORD0209,29-03-25,Jeans,Clothing,40,5,200,Daniel Harris,Dallas,PayPal,Completed\n" +
                        "ORD0210,23-03-25,Laptop,Electronics,800,3,2400,Jane Smith,New York,PayPal,Completed\n" +
                        "ORD0211,13-03-25,Smartphone,Electronics,500,1,500,David Lee,Miami,Gift Card,Pending\n" +
                        "ORD0212,09-03-25,T-Shirt,Clothing,20,4,80,Emma Clark,Houston,Debit Card,Completed\n" +
                        "ORD0213,06-02-25,Laptop,Electronics,800,1,800,Jane Smith,Los Angeles,Amazon Pay,Cancelled\n" +
                        "ORD0214,18-02-25,Smartwatch,Electronics,150,5,750,Emily Johnson,Houston,Debit Card,Completed\n" +
                        "ORD0215,07-03-25,Running Shoes,Footwear,60,1,60,Emma Clark,Houston,PayPal,Completed\n" +
                        "ORD0216,26-03-25,Jeans,Clothing,40,2,80,Chris White,Chicago,Credit Card,Completed\n" +
                        "ORD0217,19-03-25,Running Shoes,Footwear,60,2,120,Emma Clark,Boston,Gift Card,Pending\n" +
                        "ORD0218,15-02-25,Headphones,Electronics,100,4,400,Daniel Harris,New York,Gift Card,Cancelled\n" +
                        "ORD0219,17-02-25,Headphones,Electronics,100,3,300,David Lee,Houston,PayPal,Cancelled\n" +
                        "ORD0220,10-02-25,Smartwatch,Electronics,150,3,450,Michael Brown,Denver,Debit Card,Completed\n" +
                        "ORD0221,28-02-25,Washing Machine,Home Appliances,600,2,1200,Emily Johnson,Chicago,Gift Card,Cancelled\n" +
                        "ORD0222,16-02-25,Washing Machine,Home Appliances,600,2,1200,Chris White,Seattle,PayPal,Cancelled\n" +
                        "ORD0223,24-03-25,Jeans,Clothing,40,2,80,David Lee,Denver,Amazon Pay,Cancelled\n" +
                        "ORD0224,18-03-25,Refrigerator,Home Appliances,1200,1,1200,Jane Smith,Miami,PayPal,Completed\n" +
                        "ORD0225,11-03-25,Jeans,Clothing,40,1,40,Sophia Miller,New York,Gift Card,Cancelled\n" +
                        "ORD0226,05-03-25,Running Shoes,Footwear,60,4,240,Chris White,Chicago,Amazon Pay,Completed\n" +
                        "ORD0227,02-04-25,Headphones,Electronics,100,5,500,Emma Clark,Miami,Amazon Pay,Cancelled\n" +
                        "ORD0228,12-02-25,Running Shoes,Footwear,60,1,60,David Lee,San Francisco,Credit Card,Pending\n" +
                        "ORD0229,21-03-25,Running Shoes,Footwear,60,3,180,Emma Clark,San Francisco,Credit Card,Pending\n" +
                        "ORD0230,31-03-25,Headphones,Electronics,100,4,400,Jane Smith,San Francisco,Amazon Pay,Pending\n" +
                        "ORD0231,16-02-25,Laptop,Electronics,800,5,4000,Jane Smith,Boston,Credit Card,Cancelled\n" +
                        "ORD0232,14-03-25,Refrigerator,Home Appliances,1200,3,3600,Emma Clark,Los Angeles,Credit Card,Completed\n" +
                        "ORD0233,20-02-25,Running Shoes,Footwear,60,1,60,David Lee,Boston,PayPal,Cancelled\n" +
                        "ORD0234,08-03-25,Book,Books,15,1,15,Jane Smith,Miami,Amazon Pay,Cancelled\n" +
                        "ORD0235,23-03-25,Refrigerator,Home Appliances,1200,1,1200,Daniel Harris,Dallas,PayPal,Completed\n" +
                        "ORD0236,05-03-25,Smartphone,Electronics,500,5,2500,John Doe,Boston,PayPal,Completed\n" +
                        "ORD0237,11-02-25,Headphones,Electronics,100,3,300,Michael Brown,Houston,Debit Card,Cancelled\n" +
                        "ORD0238,16-03-25,Headphones,Electronics,100,1,100,Olivia Wilson,Denver,PayPal,Completed\n" +
                        "ORD0239,22-03-25,Smartwatch,Electronics,150,5,750,Daniel Harris,Houston,Amazon Pay,Completed\n" +
                        "ORD0240,09-02-25,Smartwatch,Electronics,150,1,150,John Doe,Denver,Gift Card,Pending\n" +
                        "ORD0241,30-03-25,Smartphone,Electronics,500,4,2000,Olivia Wilson,New York,Credit Card,Pending\n" +
                        "ORD0242,08-03-25,Smartphone,Electronics,500,4,2000,Chris White,Boston,Gift Card,Pending\n" +
                        "ORD0243,05-03-25,Running Shoes,Footwear,60,2,120,Chris White,Houston,Amazon Pay,Completed\n" +
                        "ORD0244,06-02-25,Smartphone,Electronics,500,4,2000,Emma Clark,Houston,Credit Card,Completed\n" +
                        "ORD0245,04-02-25,Laptop,Electronics,800,1,800,Michael Brown,Los Angeles,Credit Card,Cancelled\n" +
                        "ORD0246,17-03-25,T-Shirt,Clothing,20,2,40,Daniel Harris,Miami,Debit Card,Cancelled\n" +
                        "ORD0247,30-03-25,Jeans,Clothing,40,1,40,Sophia Miller,Dallas,Debit Card,Cancelled\n" +
                        "ORD0248,05-03-25,T-Shirt,Clothing,20,2,40,Chris White,Denver,Debit Card,Cancelled\n" +
                        "ORD0249,08-03-25,Smartwatch,Electronics,150,3,450,Emily Johnson,New York,Debit Card,Cancelled\n" +
                        "ORD0250,19-02-25,Smartphone,Electronics,500,4,2000,Emily Johnson,Seattle,Amazon Pay,Completed";

        orderAnalyzer = new OrderAnalyzerImpl(OrderLoader.load(new StringReader(orders)));
    }

    // public OrderAnalyzerImpl(List<Order> orders)
    @Test
    void testOrderAnalyzerConstructorNullArgsEmptyList(){
        OrderAnalyzerImpl orderAnalyzerImplTemp = new OrderAnalyzerImpl(null);
        assertEquals(0, orderAnalyzerImplTemp.allOrders().size(), "When null is passed to the argument" +
                "of OrderAnalyzer the list shall be empty" );
    }

    // public List<Order> ordersByCustomer(String customer)
    @Test
    void testOrdersByCustomerSuccessfully() {
        List<Order> orderByCustomer = orderAnalyzer.ordersByCustomer("Emma Clark");
        assertEquals(32, orderByCustomer.size(), "There are 3 orders from Emma Clark");
    }

    @Test
    void testOrdersByNullCustomerThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> orderAnalyzer.ordersByCustomer(null), "When customer is null" +
                "Ivalid argument exception shall be thrown");
    }

    @Test
    void testOrdersByBlankArgThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> orderAnalyzer.ordersByCustomer("  "), "When customer is null" +
                "Ivalid argument exception shall be thrown");
    }

    // public Map.Entry<LocalDate, Long> dateWithMostOrders()
    @Test
    void testDateWithMostOrdersSuccessfully() {
        LocalDate dateWithMostOrders = LocalDate.of(25,02,10);

        LocalDate dateWithMostOrdersFromAnalyzer = orderAnalyzer.dateWithMostOrders().getKey();

        assertEquals(dateWithMostOrders.format(DateTimeFormatter.ofPattern("dd-MM-yy")),
                dateWithMostOrdersFromAnalyzer.format(DateTimeFormatter.ofPattern("dd-MM-yy")),
                "The date with most orders is 10-02-25");
    }

    @Test
    void tesDateWithMostOrdersReturnsNull() {
        OrderAnalyzerImpl orderAnalyzerTemp = new OrderAnalyzerImpl(null);
        assertNull(orderAnalyzerTemp.dateWithMostOrders(), "When there are no orders null shall be returned");
    }

    // public List<String> topNMostOrderedProducts(int n)
    @Test
    void testNMostOrderProductsThrowsExceptionWhenNisZero() {
        assertThrows(IllegalArgumentException.class, () -> orderAnalyzer.topNMostOrderedProducts(-1),
                "When negative number is given to TopNMostOrderedProducts exception shall be thrown");
    }

    @Test
    void testNMostOrderedProductsReturnEmptyList() {
        assertTrue(orderAnalyzer.topNMostOrderedProducts(0).isEmpty(),
                "When 0 is passed to TopNOrderedProducts shall return empty list");
    }

    @Test
    void testTop3MostOrderedProducts() {

        List<String> top3MostOrderedProducts = List.of("Smartphone", "Smartwatch", "Running Shoes");

        List<String> topNMostOrderedProducts = orderAnalyzer.topNMostOrderedProducts(3);

        assertEquals(top3MostOrderedProducts.get(0), topNMostOrderedProducts.get(0),
                "The first top ordered products is Smartphone");
        assertEquals(top3MostOrderedProducts.get(1), topNMostOrderedProducts.get(1),
                "The second top ordered product is Smartwatch");
        assertEquals(top3MostOrderedProducts.get(2), topNMostOrderedProducts.get(2),
                "The third top rated product is RunningShoes");
    }

    // public Map<Category, Double> revenueByCategory()
    @Test
    void testSuccessfullyRevenueByCategory() {

        Map<Category, Double> categoryDoubleMap = new HashMap<>();
        categoryDoubleMap.put(Category.FOOTWEAR, 4320.0);
        categoryDoubleMap.put(Category.BOOKS, 1035.0);
        categoryDoubleMap.put(Category.CLOTHING, 3540.0);
        categoryDoubleMap.put(Category.ELECTRONICS, 129950.0);
        categoryDoubleMap.put(Category.HOME_APPLIANCES, 105000.0);

        Map<Category, Double> orderAnalyzerMap = orderAnalyzer.revenueByCategory();

        assertEquals(categoryDoubleMap.get(Category.FOOTWEAR), orderAnalyzerMap.get(Category.FOOTWEAR),
                "The total value from Footwear category is 4320");

        assertEquals(categoryDoubleMap.get(Category.BOOKS), orderAnalyzerMap.get(Category.BOOKS),
                "The total value from Footwear category is 1034");

        assertEquals(categoryDoubleMap.get(Category.CLOTHING), orderAnalyzerMap.get(Category.CLOTHING),
                "The total value from Footwear category is 3540");

        assertEquals(categoryDoubleMap.get(Category.ELECTRONICS), orderAnalyzerMap.get(Category.ELECTRONICS),
                "The total value from Footwear category is 129950");

        assertEquals(categoryDoubleMap.get(Category.HOME_APPLIANCES), orderAnalyzerMap.get(Category.HOME_APPLIANCES),
                "The total value from Footwear category is 105000");
    }

    // public Set<String> suspiciousCustomers()
    @Test
    void testSuccessfulSuspiciousCustomer() {
        Set<String> suspiciousCustomerByAnalyzer = orderAnalyzer.suspiciousCustomers();
        assertEquals("David Lee", orderAnalyzer.suspiciousCustomers().iterator().next(),
                "David Lee is the only suspicious customer");
    }

    // public Map<Category, PaymentMethod> mostUsedPaymentMethodForCategory()
    @Test
    void testSuccessfulMostUsedPaymentMethodForCategory() {

        Map<Category, PaymentMethod> categoryPaymentMethodMap = new HashMap<>();
        categoryPaymentMethodMap.put(Category.BOOKS, PaymentMethod.PAYPAL);
        categoryPaymentMethodMap.put(Category.ELECTRONICS, PaymentMethod.GIFT_CARD);
        categoryPaymentMethodMap.put(Category.FOOTWEAR, PaymentMethod.AMAZON_PAY);
        categoryPaymentMethodMap.put(Category.HOME_APPLIANCES, PaymentMethod.AMAZON_PAY);
        categoryPaymentMethodMap.put(Category.CLOTHING, PaymentMethod.AMAZON_PAY);

        Map<Category, PaymentMethod> categoryPaymentMethodAnalyzer = orderAnalyzer.mostUsedPaymentMethodForCategory();

        assertEquals(categoryPaymentMethodMap.get(Category.BOOKS), categoryPaymentMethodAnalyzer.get(Category.BOOKS),
                "The most used payment method is PayPal");

        assertEquals(categoryPaymentMethodMap.get(Category.ELECTRONICS), categoryPaymentMethodAnalyzer.get(Category.ELECTRONICS),
                "The most used payment method is GIFT_CARD");

        assertEquals(categoryPaymentMethodMap.get(Category.FOOTWEAR), categoryPaymentMethodAnalyzer.get(Category.FOOTWEAR),
                "The most used payment method is AmazonPay");

        assertEquals(categoryPaymentMethodMap.get(Category.HOME_APPLIANCES), categoryPaymentMethodAnalyzer.get(Category.HOME_APPLIANCES),
                "The most used payment method is AmazonPay");

        assertEquals(categoryPaymentMethodMap.get(Category.CLOTHING), categoryPaymentMethodAnalyzer.get(Category.CLOTHING),
                "The most used payment method is AmazonPay");
    }

    // public String locationWithMostOrders()
    @Test
    void testLocationWithMostOrdersSuccess() {
        String mostUsedLocation = "Houston";
        assertEquals("Houston", orderAnalyzer.locationWithMostOrders(),
                "The location with most orders is Houston");
    }

    //
}
