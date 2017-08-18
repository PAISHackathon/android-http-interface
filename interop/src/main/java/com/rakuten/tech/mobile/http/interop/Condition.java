package com.rakuten.tech.mobile.http.interop;

import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;

class Condition {

    static org.assertj.core.api.Condition<JSONObject> equalJsonTo(final JSONObject expected) {
        return new org.assertj.core.api.Condition<JSONObject>() {
            @Override public boolean matches(JSONObject actual) {
                if(actual == null && expected == null) return true;
                if(actual == null ^ expected == null) return false;

                try {
                    return JSONCompare.compareJSON(expected, actual, JSONCompareMode.STRICT).passed();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }
}
