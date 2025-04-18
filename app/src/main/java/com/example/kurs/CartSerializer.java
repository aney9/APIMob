package com.example.kurs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class CartSerializer implements JsonSerializer<Cart> {
    @Override
    public JsonElement serialize(Cart src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("idCart", context.serialize(src.getIdCart()));
        jsonObject.add("quantity", context.serialize(src.getQuantity()));
        jsonObject.add("price", context.serialize(src.getPrice()));
        jsonObject.add("userId", context.serialize(src.getUserId()));
        jsonObject.add("catalogId", context.serialize(src.getCatalogId()));
        jsonObject.add("catalog", context.serialize(src.getCatalog()));
        return jsonObject;
    }
}