package ws;

import com.google.gson.JsonSyntaxException;
import dao.OrderDAO;
import model.Order;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.MyWS;
import util.OrderStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Iterator;

/**
 * Created by Diogo on 10/07/2017 15:10.
 */
@Path("/v1/order")
public class OrderRest extends MyWS {

    private OrderDAO orderDAO;
    private final String project = "demo1";
    private final int tablesCount = 21;

    public OrderRest() {
        orderDAO = new OrderDAO();
    }

    @POST
    @Path("/new")
    @Consumes("application/json")
    @Produces("application/json")
    public Response newOrder(String data) {
        System.out.println("data: " + data);

        try {
            JSONObject object = new JSONObject(data);

            String token = object.getString("token");
            String model = object.getString("order");

            Order order = gson.fromJson(model, Order.class);
            order.setCreated(System.currentTimeMillis());
            order.setUpdated(System.currentTimeMillis());
            String key = orderDAO.add(order.getTableNumber(), order);

            object = new JSONObject();

            if (key != null) {
                JSONObject keyObj = new JSONObject(key);
                key = keyObj.getString("name");
                object.put("status", "OK");
                object.put("orderKey", key);
                order.setKey(key);

                //info table updated
                orderDAO.updateTableStatus(order.getTableNumber(), "OCCUPY");
                orderDAO.addOrUpdateTableProperty(order.getTableNumber(), "currentOrder", key);
                orderDAO.addOrUpdateOrderProperty(order.getTableNumber(), key, "key", key);

            } else {
                object.put("status", "FAIL");
                object.put("message", "fail to add order");
            }

            return Response.status(200).entity(object.toString()).build();
        } catch (NullPointerException e) {
            return Response.status(400).entity("please, review the fields").build();
        } catch (JsonSyntaxException e) {
            return Response.status(400).entity("please, review the order json").build();
        } catch (JSONException e) {
            return Response.status(400).entity("please, review json").build();
        }
    }


    @POST
    @Path("/update")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateOrder(String data) {
        System.out.println("order update: data: " + data);

        try {
            JSONObject object = new JSONObject(data);

            String token = object.getString("token");
            String model = object.getString("order");

            Order order = gson.fromJson(model, Order.class);
            order.setUpdated(System.currentTimeMillis());
            boolean result = orderDAO.update(order.getTableNumber(), order.getKey(), order);
            object = new JSONObject();

            if (result) {

                object.put("status", "OK");
                object.put("orderKey", order.getKey());
            } else {
                object.put("status", "FAIL");
                object.put("message", "fail to update order");
            }

            return Response.status(200).entity(object.toString()).build();
        } catch (NullPointerException e) {
            return Response.status(400).entity("please, review the fields").build();
        } catch (JsonSyntaxException e) {
            return Response.status(400).entity("please, review the order json").build();
        }
    }


    @GET
    @Path("/mobile/currentOrder/{tableNumber}")
    @Produces("application/json")
    public Response getCurrentOrder(@PathParam("tableNumber") String tableNumber) {

        try {
            JSONArray obj = orderDAO.getAll("mOrders/" + project + "/tables/" + tableNumber.trim());
            JSONObject theTable = obj.getJSONObject(0);
            String currentOrderKey = theTable.getString("currentOrder");
            JSONObject orders = theTable.getJSONObject("orders");
            Order order = gson.fromJson(orders.getJSONObject(currentOrderKey).toString(), Order.class);

            return Response.status(200).entity(gson.toJson(order)).build();
        } catch (NullPointerException e) {
            return Response.status(400).entity("please, review the fields").build();
        } catch (JsonSyntaxException e) {
            return Response.status(400).entity("please, review the order json").build();
        }
    }

    @GET
    @Path("/tables")
    @Produces("application/json")
    public Response getTablesStatus() {

        try {
            JSONArray obj = orderDAO.getAll("mOrders/" + project + "/tables");
            JSONObject theTables = obj.getJSONObject(0);
            JSONArray tables = new JSONArray();

            for (int i = 1; i < tablesCount; i++) {
                JSONObject table = new JSONObject();
                try {
                    String status = theTables.getJSONObject(String.valueOf(i)).getString("status");
                    table.put("tableNumber", i);
                    table.put("status", status);
                } catch (JSONException e) {
                    table.put("tableNumber", i);
                    table.put("status", "FREE");
                }

                tables.put(table);
            }

            return Response.status(200).entity(tables.toString()).build();
        } catch (NullPointerException e) {
            return Response.status(400).entity("please, review the fields").build();
        } catch (JsonSyntaxException e) {
            return Response.status(400).entity("please, review the order json").build();
        }
    }


    @POST
    @Path("/mobile/orderStatus")
    @Consumes("application/json")
    @Produces("application/json")
    public Response getOrderStatus(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String tableNumber = jsonObject.getString("tableNumber");
            String orderId = jsonObject.getString("orderId");
            System.out.println("getOrderStatus: orderId: " + orderId + " tableNumber: " + tableNumber);
            Order order = orderDAO.get(tableNumber, orderId);
            JSONObject object = new JSONObject();
            object.put("status", order.getStatus());
            return Response.status(200).entity(object.toString()).build();
        } catch (NullPointerException e) {
            return Response.status(200).entity(gson.toJson("Invalid request")).build();
        } catch (JSONException e) {
            return Response.status(200).entity(gson.toJson("verify json request")).build();
        }
    }


    @GET
    @Path("/mobile/me/{id}")
    @Produces("application/json")
    public Response getMyOrders(@PathParam("id") String id) {
        System.out.println("getMyOrders: id: " + id);
        //TODO: create this method in order to get all orders placed by this waiter
        return Response.status(200).entity(gson.toJson("TODO")).build();
    }

    @POST
    @Path("/closeTable")
    @Consumes("application/json")
    @Produces("application/json")
    public Response closeTable(String data) {
        System.out.println("closeTable: data: " + data);

        try {
            JSONObject object = new JSONObject(data);

            String token = object.getString("token");
            String status = "FREE";
            String tableNumber = object.getString("tableNumber");

            JSONArray obj = orderDAO.getAll("mOrders/" + project + "/tables/" + tableNumber);
            JSONObject theTable = obj.getJSONObject(0);
            JSONObject orders = theTable.getJSONObject("orders");

            //clean orders
            Iterator iterator = orders.keys();
            while (iterator.hasNext()) {
                String key = String.valueOf(iterator.next());
                JSONObject order = orders.getJSONObject(key);
                if (order.getString("status").equals("Pending")) {
                    System.out.println(order.getString("key"));
                    String orderKey = order.getString("key");
                    orderDAO.addOrUpdateOrderProperty(tableNumber, orderKey, "status", OrderStatus.DONE.name());
                }
            }


            //clean table status
            boolean result1 = orderDAO.updateTableStatus(tableNumber, status);
            object = new JSONObject();

            if (result1) {
                //clean current order
                orderDAO.addOrUpdateTableProperty(tableNumber, "currentOrder", "");
                object.put("status", "OK");
            } else {
                object.put("status", "FAIL");
                object.put("message", "fail to close table " + tableNumber);
            }

            return Response.status(200).entity(object.toString()).build();
        } catch (NullPointerException e) {
            return Response.status(400).entity("please, review the fields").build();
        } catch (JsonSyntaxException e) {
            return Response.status(400).entity("please, review the order json").build();
        }
    }


}
