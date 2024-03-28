package ch.hslu.swda.services;

import ch.hslu.swda.entities.Order;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.micro.OrderListRequestedReceiver;
import ch.hslu.swda.mongo.MongoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class GetOrderListService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderListRequestedReceiver.class);
    private final MongoService mongoService;
    private final LogService logService;

    public GetOrderListService(final MongoService mongoService, final LogService logService) {
        this.mongoService = mongoService;
        this.logService = logService;

    }

    public String getJsonOrderList() {
        ArrayList<Order> orderList = this.getOrderList();

        return this.convertOrdersToJson(orderList);
    }

    private ArrayList<Order> getOrderList() {
        return this.mongoService.findAllOrders();
    }

    private String convertOrdersToJson(ArrayList<Order> orderList) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        ObjectId.class,
                        (JsonSerializer<ObjectId>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toHexString())
                )
                .create();

        return gson.toJson(
                orderList,
                new TypeToken<ArrayList<Order>>() {}.getType()
        );
    }
}
