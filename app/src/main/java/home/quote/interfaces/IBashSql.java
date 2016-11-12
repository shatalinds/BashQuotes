package home.quote.interfaces;

import java.util.List;

import home.quote.structs.BashQuotes;

/**
 * Created by Дмитрий on 11.11.2016.
 */

public interface IBashSql {
    /**
     * Добавить цитату в БД
     * @param quote
     */
    public void addQuote(final BashQuotes quote);

    /**
     * Удалить цитату по id
     * @param id
     */
    public void removeQuoteById(final int id);

    /**
     * Получить цитату по id
     * @param id
     * @return
     */
    public BashQuotes getQuoteById(final int id);

    /**
     * Удалить все цитаты
     */
    public void removeAll();

    /**
     * Старт режима выдачи данных "порциями"
     * @param partSize Сколько записей выдавать за раз
     */
    public void beginSelectPartOfQuotes(final int partSize);

    /**
     * Выдать "порцию" данных
     * @return Список цитат
     */
    public List<BashQuotes> selectNextPartQuotes();

    /**
     * Завершить режим выдачи данных порциями
     */
    public void endSelectPartOfQuotes();

    /**
     * Сколько всего цитат в БД
     * @return
     */
    public int getQuotesCount();
}
