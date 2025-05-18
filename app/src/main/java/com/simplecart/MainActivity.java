package com.simplecart;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.database.Cursor;

import com.simplecart.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {
    private static final android.R.attr R = ;
    TextView tvGreeting;
    EditText etProduct, etQuantity;
    Button btnAdd;
    ListView lvProducts;
    DatabaseHelper db;
    int userId;
    ArrayList<HashMap<String, String>> productList;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        tvGreeting = findViewById(R.id.tvGreeting);
        String username = db.getUsernameById(userId);
        if (tvGreeting != null) {
            tvGreeting.setText("Hello, " + username + "!");
        }

        etProduct = findViewById(R.id.etProduct);
        etQuantity = findViewById(R.id.etQuantity);
        btnAdd = findViewById(R.id.btnAdd);
        lvProducts = findViewById(R.id.lvProducts);

        btnAdd.setOnClickListener(v -> {
            String name = etProduct.getText().toString().trim();
            String qtyStr = etQuantity.getText().toString().trim();
            if (name.isEmpty() || qtyStr.isEmpty()) {
                Toast.makeText(this, "Enter product and quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.addProduct(userId, name, qty)) {
                etProduct.setText("");
                etQuantity.setText("");
                loadProducts();
            } else {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
            }
        });

        lvProducts.setOnItemLongClickListener((parent, view, position, id) -> {
            int productId = Integer.parseInt(productList.get(position).get("id"));
            db.removeProduct(productId);
            loadProducts();
            return true;
        });

        loadProducts();
    }

    private void loadProducts() {
        productList = new ArrayList<>();
        Cursor cursor = db.getProductsForUser(userId);
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")));
            map.put("name", cursor.getString(cursor.getColumnIndexOrThrow("name")));
            map.put("quantity", "Qty: " + cursor.getString(cursor.getColumnIndexOrThrow("quantity")));
            productList.add(map);
        }
        cursor.close();
        adapter = new SimpleAdapter(this, productList, android.R.layout.simple_list_item_2,
                new String[]{"name", "quantity"},
                new int[]{android.R.id.text1, android.R.id.text2});
        lvProducts.setAdapter(adapter);
    }
}
