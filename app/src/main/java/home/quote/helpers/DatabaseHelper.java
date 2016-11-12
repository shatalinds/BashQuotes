package home.quote.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import home.quote.interfaces.IBashSql;
import home.quote.structs.BashQuotes;

/**
 * Created by Дмитрий on 11.11.2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper implements IBashSql {
    private static final int DB_VER = 1;
    private static final String DB_NAME = "BashQuotes";

    private static final String TBL_QUOTES = "Ouotes";
    private static final String K_ID = "id";
    private static final String K_SITE = "site";
    private static final String K_NAME = "name";
    private static final String K_DESC = "desc";
    private static final String K_LINK = "link";
    private static final String K_ELPH = "elph";

    private static final String CREATE_TBL_QUOTES = new StringBuilder()
            .append("CREATE TABLE ").append(TBL_QUOTES).append("(")
            .append(K_ID).append(" INTEGER PRIMARY KEY NOT NULL,")
            .append(K_SITE).append(" TEXT,")
            .append(K_NAME).append(" TEXT,")
            .append(K_DESC).append(" TEXT,")
            .append(K_LINK).append(" TEXT,")
            .append(K_ELPH).append(" TEXT)").toString();

    private static final String DROP_TBL_QUOTES = new StringBuilder()
            .append("DROP TABLE IF EXISTS ").append(TBL_QUOTES).toString();

    private static final String SELECT_ALL = new StringBuilder()
            .append("SELECT * FROM ").append(TBL_QUOTES).toString();

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    private static DatabaseHelper instance = null;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
            db.execSQL(CREATE_TBL_QUOTES);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
            db.execSQL(DROP_TBL_QUOTES);
        db.setTransactionSuccessful();
        db.endTransaction();

        onCreate(db);
    }

    /**
     * Добавить цитату
     * @param quote
     */
    @Override
    public void addQuote(final BashQuotes quote) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(K_NAME, quote.getName());
        values.put(K_SITE, quote.getSite());
        values.put(K_DESC, quote.getDesc());
        values.put(K_LINK, quote.getLink());
        values.put(K_ELPH, quote.getElementPureHtml());

        db.insert(TBL_QUOTES, null, values);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        this.size++;
    }

    /**
     * Удалить цитату по id
     * @param id
     */
    @Override
    public void removeQuoteById(final int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.delete(TBL_QUOTES,
                new StringBuilder().append(K_ID).append(" = ?").toString(),
                new String[] { String.valueOf(id) });
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        this.size--;
    }

    /**
     * Удалить все цитаты
     */
    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.delete(TBL_QUOTES, null, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        this.size = 0;
    }


    /**
     * Получить цитату по id
     * @param id
     * @return
     */
    public BashQuotes getQuoteById(final int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        final String keyId = new StringBuilder()
                .append(K_ID).append(" = ? ").toString();

        Cursor cursor = db.query(TBL_QUOTES, new String[] { K_ID,
                        K_NAME, K_SITE, K_DESC, K_LINK, K_ELPH }, keyId,
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        BashQuotes quote = new BashQuotes(cursor.getString(cursor.getColumnIndex(K_NAME)),
                cursor.getString(cursor.getColumnIndex(K_SITE)),
                cursor.getString(cursor.getColumnIndex(K_DESC)),
                cursor.getString(cursor.getColumnIndex(K_LINK)),
                cursor.getString(cursor.getColumnIndex(K_ELPH)));
        return quote;

    }


    private static int size = 0; //всего цитат
    private static int partSize = -1; //размер очередной "порции" цитат
    private static int nextId = -1; //следующий id

    /**
     * Старт режима выдачи данных "порциями"
     * @param partSize Сколько записей выдавать за раз
     */
    @Override
    public void beginSelectPartOfQuotes(final int partSize) {
        this.partSize = partSize;
        this.nextId = 0;
        this.size = getQuotesCount();
    }

    /**
     * Выдать "порцию" данных
     * @return Список цитат или null
     */
    @Override
    public List<BashQuotes> selectNextPartQuotes() {
        if (partSize == -1 || nextId == -1 || nextId > size) {
            return null;
        }

        List<BashQuotes> quotesList = new ArrayList<>();
        final String selectQuotes = new StringBuilder().append("SELECT * FROM ")
                .append(TBL_QUOTES).append(" WHERE ")
                .append(K_ID).append(" >= ").append(nextId).append(" AND ")
                .append(K_ID).append(" < ").append(nextId + partSize).toString();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuotes, null);

        if (cursor.moveToFirst()) {
            do {
                BashQuotes quotes = new BashQuotes();

                quotes.setName(cursor.getString(cursor.getColumnIndex(K_NAME)));
                quotes.setDesc(cursor.getString(cursor.getColumnIndex(K_DESC)));
                quotes.setLink(cursor.getString(cursor.getColumnIndex(K_LINK)));
                quotes.setSite(cursor.getString(cursor.getColumnIndex(K_SITE)));
                quotes.setElementPureHtml(cursor.getString(cursor.getColumnIndex(K_ELPH)));

                quotesList.add(quotes);
            } while (cursor.moveToNext());
        }
        nextId += partSize;
        return quotesList;
    }

    /**
     * Завершить режим выдачи данных порциями
     */
    @Override
    public void endSelectPartOfQuotes() {
        this.partSize = -1;
        this.nextId = -1;
    }

    /**
     * Сколько всего цитат в БД
     * @return
     */
    public int getQuotesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ALL, null);
        return cursor.getCount();
    }
}
