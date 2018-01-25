package dao;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import model.Order;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diogo on 10/07/2017 14:04.
 */
public class OrderDAO extends Db<Order> {

    private final String objectName = "mOrders/demo1/";

    /**
     * Free method.
     *
     * @param node FireBase node
     * @return JSON Array
     */
    @Override
    public JSONArray getAll(String node) {
        HttpResponse<JsonNode> result = requestGet(node);
        return result != null ? result.getBody().getArray() : null;
    }

    @Override
    public Order get(String tableNumber, String key) {
        String node = objectName + "tables/" + tableNumber + "/orders/" + key;
        HttpResponse<JsonNode> result = requestGet(node);
        return gson.fromJson(result.getBody().getObject().toString(), Order.class);
    }

    /**
     * Add a order.
     *
     * @param tableNumber
     * @param obj
     * @return the node key
     */
    @Override
    public String add(String tableNumber, Order obj) {
        String node = objectName + "tables/" + tableNumber + "/orders";
        HttpResponse<JsonNode> response = requestPost(gson.toJson(obj), node);
        boolean result = response != null && response.getStatusText().equals("OK");
        return result ? response.getBody().toString() : null;
    }

    @Override
    public boolean update(String tableNumber, String key, Order obj) {
        String node = objectName + "tables/" + tableNumber + "/orders/" + key;
        HttpResponse<JsonNode> response = requestUpdate(gson.toJson(obj), node);
        return response != null && response.getStatusText().equals("OK");
    }

    @Override
    protected boolean update(String reference, String value) {
        System.out.println("update: " + reference + " - " + value);
        HttpResponse<JsonNode> response = requestUpdate(value, reference);
        return response != null && response.getStatusText().equals("OK");
    }

    //UTILS

    public boolean updateTableStatus(String tableNumber, String status) {
        String node = objectName + "tables/" + tableNumber;
        Map<String, String> map = new HashMap<>();
        map.put("status", status);
        HttpResponse<JsonNode> response = requestUpdate(gson.toJson(map), node);
        return response != null && response.getStatusText().equals("OK");
    }

    public boolean addOrUpdateTableProperty(String tableNumber, String label, String value) {
        String node = objectName + "tables/" + tableNumber;
        Map<String, String> map = new HashMap<>();
        map.put(label, value);
        HttpResponse<JsonNode> response = requestUpdate(gson.toJson(map), node);
        return response != null && response.getStatusText().equals("OK");
    }

    public boolean addOrUpdateOrderProperty(String tableNumber, String orderKey, String label, String value) {
        String node = objectName + "tables/" + tableNumber + "/orders/" + orderKey;
        Map<String, String> map = new HashMap<>();
        map.put(label, value);
        HttpResponse<JsonNode> response = requestUpdate(gson.toJson(map), node);
        return response != null && response.getStatusText().equals("OK");
    }

}
