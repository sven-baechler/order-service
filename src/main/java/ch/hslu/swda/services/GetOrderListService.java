package ch.hslu.swda.services;

import ch.hslu.swda.entities.Order;
import ch.hslu.swda.services.mongo.MongoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;


public class GetOrderListService {
    private final MongoService mongoService;

    public GetOrderListService(final MongoService mongoService) {
        this.mongoService = mongoService;
    }

    public String getJsonOrderList() {
        List<Order> orderList = this.getOrderList();

        return this.convertOrdersToJson(orderList);
    }

    private List<Order> getOrderList() {
        return this.mongoService.findAllOrders();
    }

    private String convertOrdersToJson(List<Order> orderList) {
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
