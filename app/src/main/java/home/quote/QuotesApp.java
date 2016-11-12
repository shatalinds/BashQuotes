package home.quote;

import android.app.Application;

import home.quote.helpers.DatabaseHelper;

/**
 * Created by Дмитрий on 11.11.2016.
 */

public class QuotesApp extends Application {
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate(){
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                //Место для реакции на неперехваченные исключения
                e.printStackTrace();
            }
        });

        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        dbHelper.getWritableDatabase();
        dbHelper.removeAll();//Нам не нужны повторения
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        dbHelper.close();
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        dbHelper.close();
    }
}
