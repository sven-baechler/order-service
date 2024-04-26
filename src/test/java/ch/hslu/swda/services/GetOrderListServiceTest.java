package ch.hslu.swda.services;

import ch.hslu.swda.entities.Customer;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.services.mongo.MongoService;
import ch.hslu.swda.services.logging.LogService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GetOrderListServiceTest {

    @Mock
    private MongoService mongoService;

    @Mock
    private LogService logService;

    @InjectMocks
    private GetOrderListService getOrderListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetJsonOrderList() {
        // arrange
        ArrayList<Order> orderList = new ArrayList<>();
        Order order = new Order();
        order.setId(new ObjectId());
        order.setBranchOfficeId(new ObjectId());
        order.setSellerId(new ObjectId());
        Customer customer = new Customer();
        customer.setName("Mustermann");
        customer.setPreName("Max");
        customer.setZip("6000");
        customer.setStreet("Musterstrasse 1");
        customer.setCity("Luzern");
        order.setCustomer(customer);
        order.setStatus(OrderStatus.ORDERED);
        order.setDatetime(null);
        OrderEntry entry1 = new OrderEntry();
        entry1.setArticleId(new ObjectId());
        entry1.setAmount(5);
        entry1.setPricePerUnit(10.0);
        entry1.setStatus(OrderStatus.ORDERED);

        OrderEntry entry2 = new OrderEntry();
        entry2.setArticleId(new ObjectId());
        entry2.setAmount(10);
        entry2.setPricePerUnit(15.0);
        entry2.setStatus(OrderStatus.ORDERED);
        order.setEntries(Arrays.asList(entry1, entry2));
        orderList.add(order);

        Mockito.when(mongoService.findAllOrders()).thenReturn(orderList);

        // act
        String result = getOrderListService.getJsonOrderList();

        // assert
        assertEquals("[{\"id\":\"" + order.getId().toHexString() + "\",\"branchOfficeId\":\"" + order.getBranchOfficeId().toHexString() +
                        "\",\"sellerId\":\"" + order.getSellerId().toHexString() +
                        "\",\"customer\":{\"name\":\"" + customer.getName() + "\",\"preName\":\"" + customer.getPreName() +
                        "\",\"zip\":\"" + customer.getZip() + "\",\"street\":\"" + customer.getStreet() + "\",\"city\":\"" +
                        customer.getCity() + "\"},\"status\":\"ORDERED\",\"entries\":[{\"articleId\":\"" +
                        entry1.getArticleId().toHexString() + "\",\"amount\":5,\"pricePerUnit\":10.0,\"status\":\"ORDERED\"},{\"articleId\":\"" +
                        entry2.getArticleId().toHexString() + "\",\"amount\":10,\"pricePerUnit\":15.0,\"status\":\"ORDERED\"}]}]",
                result);
    }

    @Test
    void testGetJsonOrderListOfEmptyList() {
        // arrange
        ArrayList<Order> orderList = new ArrayList<>();
        Mockito.when(mongoService.findAllOrders()).thenReturn(orderList);

        // act
        String result = getOrderListService.getJsonOrderList();

        // assert
        assertEquals("[]", result);

    }

}