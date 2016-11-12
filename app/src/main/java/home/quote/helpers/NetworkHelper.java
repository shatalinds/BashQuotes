package home.quote.helpers;

import android.app.Activity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import home.quote.structs.BashQuotes;

/**
 * Created by dave on 11.11.2016.
 */

public class NetworkHelper {
    public static final String TAG = NetworkHelper.class.getSimpleName();

    private static final String AN = "&";
    private static final String WT = "?";
    private static final String EQ = "=";

    private static final String PREFIX = "http://";
    private static final String DOMAIN = "www.umori.li";
    private static final String PATH   = "/";

    private static final String apiGet = "api/get";

    private static final String P_SITE = "site";
    private static final String V_SITE = "bash.im";
    private static final String P_NAME = "name";
    private static final String V_NAME = "bash";
    private static final String P_NUM = "num";
    private static final String V_NUM = "100";


    private final static String urlBase;
    private static NetworkHelper instance;

    static {
        instance = null;
        urlBase = new StringBuilder().append(PREFIX).append(DOMAIN).append(PATH).toString();
    }

    private NetworkHelper() {
    }

    /**
     * Синглтон с двойной проверкой https://ru.wikipedia.org/wiki/Double_checked_locking
     * @return
     */
    public static NetworkHelper getInstance() {
        if (instance == null) {
            synchronized (NetworkHelper.class) {
                if (instance == null) {
                    instance = new NetworkHelper();
                }
            }
        }
        return instance;
    }

    /**
     * Формирует url из параметров
     * @param operationQuery
     * @param params
     * @return
     */
    private static String getUrl(final String operationQuery, final Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder().append(urlBase).append(operationQuery);
        if (params != null) {
            boolean bFirst = true;
            for (Map.Entry<String, String> entry: params.entrySet()) {
                if (bFirst) {
                    urlBuilder.append(WT).append(entry.getKey()).append(EQ).append(entry.getValue());
                    bFirst = false;
                } else {
                    urlBuilder.append(AN).append(entry.getKey()).append(EQ).append(entry.getValue());
                }
            }
        }
        return urlBuilder.toString();
    }

    /**
     * Наши callback
     */
    public interface ErrorCallBack {
        public void onError(final String error);
    }

    public interface ListCallBack<T> extends ErrorCallBack {
        public void onSuccess(final List<T> list);
    }


    /**
     * Получить цитаты с баша
     * @param activity
     * @param callBack
     */
    public void getQuotesFromBash(final Activity activity, final ListCallBack<BashQuotes> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put(P_SITE, V_SITE);
        params.put(P_NAME, V_NAME);
        params.put(P_NUM, V_NUM);

        final String url = getUrl(apiGet, params);

        Request<JSONArray> request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject object;
                BashQuotes bashQuotes;
                List<BashQuotes> listQuotes = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        bashQuotes = new BashQuotes();
                        object = response.getJSONObject(i);
                        bashQuotes.fromJSON(object);
                        listQuotes.add(bashQuotes);
                    }
                    callBack.onSuccess(listQuotes);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    callBack.onError(ex.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callBack.onError(getErrorResponseMessage(error));
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(activity, null);
        requestQueue.add(request);
    }

    /**
     * "Расшифровка" ошибки, смотрим поле response.data
     * @param error
     * @return
     */
    private String getErrorResponseMessage(VolleyError error) {
        String json = error.toString();
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
            json = new String(response.data);
        }
        return json;
    }
}
