package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.messages.OrderCreatedMessage;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class OrderReceiverTest {
    @Mock
    private Logger logService;

    @Mock
    private BusConnector bus;

    private OrderReceiver orderReceiver;
    private String exchangeName = "swda";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orderReceiver = new OrderReceiver(exchangeName, bus);
    }

    @Test
    public void testOnMessageReceived() {
        String route = "testRoute";
        String replyTo = "testReplyTo";
        String corrId = "testCorrId";
        String message = "{\"order\": {\"id\": \"testId\", \"entries\": [{\"articleId\": \"1\", \"amount\": 2}]}}";
        String test = "{\n" +
                "  \"branchOfficeId\": 0,\n" +
                "  \"sellerId\": 0,\n" +
                "  \"customer\": {\n" +
                "    \"id\": 0,\n" +
                "    \"name\": \"\",\n" +
                "    \"preName\": \"\",\n" +
                "    \"street\": \"\",\n" +
                "    \"zip\": \"\",\n" +
                "    \"city\": \"\"\n" +
                "  },\n" +
                "  \"entries\": [\n" +
                "    {\n" +
                "      \"articleId\": 0,\n" +
                "      \"articleName\": \"\",\n" +
                "      \"amount\": 0,\n" +
                "      \"pricePerUnit\": 0\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";

        orderReceiver.onMessageReceived(route, replyTo, corrId, test);

        // Verify that debug message is logged correctly
        verify(logService).debug("received message of type: {}", route);

        /*
        // Verify that the order is processed and inserted into the database
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(databaseMock).getCollection(eq("orderCollectionName"), eq(Order.class));
        verify(collectionMock).insertOne(orderCaptor.capture());
        Order insertedOrder = orderCaptor.getValue();
        assertEquals("testId", insertedOrder.getId());

        // Verify that log service is called with correct message
        verify(logService).info(eq("Order created with id %s"), eq("testId"));

        // Verify that bus is called with correct parameters
        verify(bus).talkAsync(eq("exchangeName"), eq(Routes.ORDER_CREATED), anyString());

        // Verify that the correct message is sent through the bus
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(bus).talkAsync(anyString(), anyString(), messageCaptor.capture());
        Gson gson = new Gson();
        OrderCreatedMessage orderCreatedMessage = gson.fromJson(messageCaptor.getValue(), OrderCreatedMessage.class);
        assertEquals("testId", orderCreatedMessage.getOrderId());
        assertEquals(1, orderCreatedMessage.getEntries().size());
        assertEquals("1", orderCreatedMessage.getEntries().get(0).getArticleId());
        assertEquals(2, orderCreatedMessage.getEntries().get(0).getAmount());

        // Optionally, verify that the confirmation email is sent (mock the email service for this)
        // verify(emailService).sendConfirmationEmail(anyString(), anyString());
         */
    }
}
