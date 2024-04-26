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

/**
 * Holds all constants for message routes.
 */
public final class Routes {

    public static final String ORDER_RECEIVED = "order.received";

    public static final String ORDER_CREATED = "order.created";

    public static final String ORDER_ENTRY_UPDATED = "order_entry.updated";

    public static final String ORDERS_ASSORTMENT_UPDATED = "orders.assortment_updated";

    public static final String ARTICLE_STOCK_UPDATE_REQUIRED = "article.stock_update_required";

    public static final String LOG_OCCURRED = "log.occurred";

    public static final String ORDER_LIST_REQUESTED = "order.list_requested";


    /**
     * No instance allowed.
     */
    private Routes() {
    }
}
