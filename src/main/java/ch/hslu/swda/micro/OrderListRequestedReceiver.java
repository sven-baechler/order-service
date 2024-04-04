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
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.mongo.MongoService;
import ch.hslu.swda.services.GetOrderListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class OrderListRequestedReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(OrderListRequestedReceiver.class);

    private final String exchangeName;
    private final BusConnector bus;
    private final LogService logService;
    private final MongoService mongoService;
    private final GetOrderListService orderListService;

    public OrderListRequestedReceiver(final String exchangeName, final BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.logService = new LogService(exchangeName, bus, LOG);
        this.mongoService = new MongoService(logService);
        this.orderListService = new GetOrderListService(mongoService);
    }

    /**
     * @see MessageReceiver#onMessageReceived(String, String, String, String)
     */
    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {
        LOG.debug("received request for log list  [{}]: [{}]", replyTo, message);
        String json = orderListService.getJsonOrderList();
        LOG.debug("Replying with requested list of orders: [{}]", json);

        try {
            bus.reply(exchangeName, replyTo, corrId, json);
        } catch (IOException e) {
            LOG.error("Could not reply to order list request: {}", e.getMessage());
        }
    }


}