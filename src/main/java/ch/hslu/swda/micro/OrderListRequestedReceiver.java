/*
 * Copyright 2023 Roland Christen, HSLU Informatik, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.mongo.MongoConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public final class OrderListRequestedReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(OrderListRequestedReceiver.class);

    private final String exchangeName;
    private final BusConnector bus;

    public OrderListRequestedReceiver(final String exchangeName, final BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
    }

    /**
     * @see MessageReceiver#onMessageReceived(String, String, String, String)
     */
    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {
        LOG.debug("received request for log list  [{}]: [{}]", replyTo, message);

        // TODO filtering using the content of message?

        ArrayList<Order> orderList = this.getOrderList();

        String json = this.convertOrdersToJson(orderList);

        LOG.debug("Replying with requested list of orders: [{}]", json);

        try {
            bus.reply(exchangeName, replyTo, corrId, json);
        } catch (IOException e) {
            LOG.error("Could not reply to order list request: {}", e.getMessage());
        }
    }

    private ArrayList<Order> getOrderList() {
        MongoConfig config = new MongoConfig();
        String connectionString = config.getConnectionString();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        try (MongoClient client = MongoClients.create(connectionString)) {
            MongoDatabase database = client.getDatabase(config.getDatabaseName()).withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Order> collection = database.getCollection(config.getOrderCollectionName(), Order.class);

            return collection.find().into(new ArrayList<>());
        } catch (Exception e) {
            LOG.error("Could not get list of orders: [{}]", e.getMessage());
            return new ArrayList<>();
        }
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