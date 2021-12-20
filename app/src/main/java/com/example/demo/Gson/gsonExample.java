package com.example.demo.Gson;

import com.example.demo.bean.Contact;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class gsonExample {
    static String TAG="gson";

    public static String toGson(Contact contact) {
        Gson gson=new GsonBuilder().create();
        String json=gson.toJson(contact);
        return json;
    }

    public static Contact fromGson(String json) {
        Gson gson=new GsonBuilder().create();
        try {
            Contact contact=gson.fromJson(json,Contact.class);
            return contact;
        }catch (Exception e){
            return null;
        }
    }
}
