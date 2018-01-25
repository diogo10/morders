package dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import junit.framework.TestCase;
import model.Order;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.util.Iterator;

/**
 * Created by Diogo on 10/07/2017 14:17.
 */
public class OrderDAOTest extends TestCase {

    private OrderDAO orderDAO;
    private String project = "demo1";

    public void setUp() throws Exception {
        orderDAO = new OrderDAO();
        super.setUp();
    }

    public void tearDown() throws Exception {
        orderDAO = null;
    }

    public void testAdd() throws Exception {
        System.out.println("OrderDAOTest: testAdd");
//        Order order = new Order();
//        String tableNumber = "2";
//
//        order.setId(orderDAO.generateUID());
//        order.setStatus("Pending");
//        order.setCreated(System.currentTimeMillis());
//        order.setUpdated(System.currentTimeMillis());
//        order.setTableNumber(tableNumber);
//        order.setTotalCost(10.90);
//        order.setWaiterName("Diogo");
//        order.setWaiterId("12");
//
//        order.setItems(new ArrayList<>());
//        order.getItems().add(new Item("Pizza G","jdhfjdhf3",1));
//        order.getItems().add(new Item("Beer G","3829832hfjdf",2));
//
//
//        boolean result = orderDAO.add(tableNumber,order);
//        assertTrue(result);

    }

    public void testUpdate() {
        System.out.println("OrderDAOTest.testUpdate");
//        String key = "-KogiZKKZLKWcWegtoKM";
//        String tableNumber = "2";
//        Order order = orderDAO.get(tableNumber,key);
//        assertNotNull(order);
//
//        order.setUpdated(System.currentTimeMillis());
//        order.setTotalCost(20.0);
//        boolean result = orderDAO.update(tableNumber,key,order);
//        assertTrue(result);
    }

    public void testGetAll() {
        System.out.println("OrderDAOTest.testGetAll");

        JSONArray obj = orderDAO.getAll("mOrders/" + project + "/tables");
        JSONObject theTables = obj.getJSONObject(0);
        JSONArray tables = new JSONArray();

        int tablesCount = 21;
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


    }

    public void testUpdateTableStatus() throws Exception {
        System.out.println("testUpdateTableStatus");
        String tableNumber = "16";
        //boolean aa = orderDAO.updateTableStatus("19","FREE");
        //assertTrue(aa);

        JSONArray obj = orderDAO.getAll("mOrders/" + project + "/tables/" + tableNumber);
        JSONObject theTable = obj.getJSONObject(0);
        JSONObject orders = theTable.getJSONObject("orders");

        Iterator iterator = orders.keys();
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            JSONObject order = orders.getJSONObject(key);
            if (order.getString("status").equals("Pending"))
                System.out.println(order.getString("key"));

        }
    }

    public void testAddOrUpdateTableProperty() {
        System.out.print("testAddOrUpdateTableProperty");
        //orderDAO.addOrUpdateTableProperty("8","currentOrder","BB");
    }

}