package org.protocoder.gui.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.FileIO;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by biquillo on 12/09/16.
 */
public class NewUserPreferences {

    Map<String, Object> pref = null;

    private static NewUserPreferences instance;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static NewUserPreferences getInstance() {
        if (instance == null) instance = new NewUserPreferences();
        return instance;
    }

    public void load() {
        String json = FileIO.loadCodeFromFile(ProtocoderSettings.getPrefUrl());
        if (json != null) {
            Type stringStringMap = new TypeToken<Map<String, Object>>() {}.getType();
            pref = gson.fromJson(json, stringStringMap);
        } else {
            pref = new HashMap<String, Object>();
        }

        AndroidUtils.prettyPrintMap(pref);

        // fill with default properties
        resetIfEmpty("device_id", "12345");
        resetIfEmpty("screen_always_on", false);
        resetIfEmpty("servers_enabled_on_start", true);
        resetIfEmpty("notify_new_version", true);
        resetIfEmpty("send_usage_log", true);
        resetIfEmpty("webide_mode", false);
        resetIfEmpty("launch_on_device_boot", false);
        resetIfEmpty("launch_script_on_app_launch", "");
        resetIfEmpty("apps_in_list_mode", true);

        resetIfEmpty("background_color", new String[]{"#0000bb", "#00bb00"});
        resetIfEmpty("background_image", null);


        /*
        resetIfEmpty("http_username", "qq");
        resetIfEmpty("http_password", "qq");
        resetIfEmpty("http_port", 8585);
        resetIfEmpty("websockets_port", 8587);

        resetIfEmpty("ftp_enabled", false);
        resetIfEmpty("ftp_port", 8589);
        resetIfEmpty("ftp_username", "qq");
        resetIfEmpty("ftp_password", "12345678");
        */
    }

    public Object get(String key) {
        if (pref == null) load();
        return pref.get(key);
    }

    private void resetIfEmpty(String key, Object obj) {
        if (!pref.containsKey(key)) pref.put(key, obj);

    }

    public void save() {
        String p = gson.toJson(pref).toString();
        FileIO.saveCodeToFile(p, ProtocoderSettings.getPrefUrl());
    }

    public NewUserPreferences set(String key, Object value) {
        if (pref == null) load();
        pref.put(key, value);
        return this;
    }
}
