package com.example.plantnetapp.back.api;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ReturnType {
    public final int status;
    public final String type;
    public final JsonObject values;

    public ReturnType(int status, String type, JsonObject values) {
        this.status = status;
        this.type = type;
        this.values = values;
    }
    public String parseJsonElement(JsonElement element, int indentLevel) {
        StringBuilder sb = new StringBuilder();
        String indent = "  ".repeat(indentLevel); // 2 spaces per indent level
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (String key : obj.keySet()) {
                JsonElement value = obj.get(key);
                sb.append(indent).append(key).append(": ");
                if (value.isJsonPrimitive()) {
                    sb.append(value.getAsJsonPrimitive().toString()).append("\n");
                } else {
                    sb.append("\n").append(parseJsonElement(value, indentLevel + 1));
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            int index = 0;
            for (JsonElement item : arr) {
                sb.append(indent).append("- [").append(index).append("]:");
                sb.append(parseJsonElement(item, indentLevel + 1));
                index++;
            }
        } else if (element.isJsonPrimitive()) {
            sb.append(indent).append(element.getAsString()).append("\n");
        } else if (element.isJsonNull()) {
            sb.append(indent).append("null\n");
        }

        return sb.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return "ReturnType{" +
                "status=" + status +
                ", type='" + type + '\'' +
                this.parseJsonElement(values,0)+
                '}';
    }
}
