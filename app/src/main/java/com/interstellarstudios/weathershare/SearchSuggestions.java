package com.interstellarstudios.weathershare;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SearchSuggestions {

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("city_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static ArrayList<String> getSearchSuggestions(Context context) {

        ArrayList<String> mLocations = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(context));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject arrayObject = jsonArray.getJSONObject(i);
                String city = arrayObject.getString("name");
                String country = arrayObject.getString("country");
                String location = city + ", " + country;
                mLocations.add(location);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mLocations;
    }
}
