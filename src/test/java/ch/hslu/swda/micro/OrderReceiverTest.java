package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.receivers.OrderReceiver;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Mockito.verify;

public class OrderReceiverTest {

    // TODO
    /*
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
        MockitoAnnotations.openMocks(this);
        orderReceiver = new OrderReceiver(exchangeName, bus);

        String route = "testRoute";
        String replyTo = "testReplyTo";
        String corrId = "testCorrId";
        String message = "{\"order\": {\"branchOfficeId\": {\"$oid\": \"000000000000000000000000\"}, \"sellerId\": {\"$oid\": \"000000000000000000000000\"}, \"customer\": {\"id\": {\"$oid\": \"000000000000000000000000\"}, \"name\": \"\", \"preName\": \"\", \"street\": \"\", \"zip\": \"\", \"city\": \"\"}, \"datetime\": null, \"status\": null, \"entries\": [{\"articleId\": {\"$oid\": \"000000000000000000000000\"}, \"articleName\": \"\", \"amount\": 0, \"pricePerUnit\": 0}]}}";

        String asdf = "{\"order\": {\"branchOfficeId\": 0, \"sellerId\": 0, \"customer\": {\"id\": 0, \"name\": \"\", \"preName\": \"\", \"street\": \"\", \"zip\": \"\", \"city\": \"\"}, \"entries\": [{\"articleId\": 0, \"articleName\": \"\", \"amount\": 0, \"pricePerUnit\": 0}]}}";
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

        orderReceiver.onMessageReceived(route, replyTo, corrId, message);

        // Verify that debug message is logged correctly
        verify(logService).debug("received message of type: {}", route);

        // NOTE: wird niemals funktionieren, wenn der MongoClient innerhalb der Methode erstellt wird
        // Der Client müsste gemockt und in die Methode mitgegeben werden können, um zu testen
    }
    */
}
