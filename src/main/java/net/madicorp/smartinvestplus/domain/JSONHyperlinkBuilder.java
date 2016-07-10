package net.madicorp.smartinvestplus.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: sennen
 * Date: 10/07/2016
 * Time: 12:46
 */
public class JSONHyperlinkBuilder {
    private final String rel;
    private final String link;

    private JSONHyperlinkBuilder(String rel, String link) {
        this.rel = rel;
        this.link = link;
    }

    public static JSONHyperlinkBuilder init(String rel, String link) {
        return new JSONHyperlinkBuilder(rel, link);
    }

    public JSONObject build() throws JSONException {
        JSONObject hyperlink = new JSONObject();
        hyperlink.put("rel", rel);
        hyperlink.put("href", link);
        return hyperlink;
    }
}
