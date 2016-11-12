package home.quote.structs;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dave on 11.11.2016.
 */

public class BashQuotes implements Serializable {
    private static final String TAG = BashQuotes.class.getSimpleName();

    public static final String SITE = "site";
    public static final String NAME = "name";
    public static final String DESC = "desc";
    public static final String LINK = "link";
    public static final String ELPH = "elementPureHtml";

    private String site;
    private String name;
    private String desc;
    private String link;
    private String elementPureHtml;

    /**
     * Наше собственное исключение
     */
    public class BashQuotesException extends JSONException {
        public BashQuotesException(final String mess) {
            super(mess);
            Log.e(TAG, mess);
            //...
        }
    }

    /**
     * "Запаковываем данные в json объект"
     * @return
     * @throws BashQuotesException
     */
    public JSONObject toJSON() throws BashQuotesException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SITE, site);
            jsonObject.put(NAME, name);
            jsonObject.put(DESC, desc);
            jsonObject.put(LINK, link);
            jsonObject.put(ELPH, elementPureHtml);

        } catch (JSONException ex) {
            ex.printStackTrace();
            new BashQuotesException(ex.getMessage());
        }
        return jsonObject;
    }

    /**
     * "Распаковываем" данные из json объекта
     * @param jsonObject
     * @throws BashQuotesException
     */
    public void fromJSON(JSONObject jsonObject) throws BashQuotesException {
        try {
            if (jsonObject.has(SITE)) {
                this.site = jsonObject.get(SITE).toString();
            }
            if (jsonObject.has(NAME)) {
                this.name = jsonObject.get(NAME).toString();
            }
            if (jsonObject.has(DESC)) {
                this.desc = jsonObject.get(DESC).toString();
            }
            if (jsonObject.has(LINK)) {
                this.link = jsonObject.get(LINK).toString();
            }
            if (jsonObject.has(ELPH)) {
                this.elementPureHtml = jsonObject.get(ELPH).toString();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            new BashQuotesException(ex.getMessage());
        }
    }

    /**
     * Конструктор с параметрами
     * @param site
     * @param name
     * @param desc
     * @param link
     * @param html
     */
    public BashQuotes(String name, String site, String desc, String link, String html) {
        this.site = site;
        this.name = name;
        this.desc = desc;
        this.link = link;
        this.elementPureHtml = html;
    }

    public BashQuotes() {
    }

    /**
     * Set/Get Методы
     */

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getElementPureHtml() {
        return elementPureHtml;
    }

    public void setElementPureHtml(String elementPureHtml) {
        this.elementPureHtml = elementPureHtml;
    }
}
