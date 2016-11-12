package home.quote;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.List;

import home.quote.fragments.TableFragment;
import home.quote.helpers.DatabaseHelper;
import home.quote.helpers.NetworkHelper;
import home.quote.structs.BashQuotes;

public class MainActivity extends AppCompatActivity implements Handler.Callback {
    private final int M_DONE = 1;
    private final int M_FAIL = -1;

    private TableFragment tableFragment;
    private Handler handler;
    private ProgressDialog dialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        handler = new Handler(this);

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getString(R.string.wait));
        dialog.show();

        NetworkHelper.getInstance().getQuotesFromBash(this,
                new NetworkHelper.ListCallBack<BashQuotes>() {
                    @Override
                    public void onSuccess(List<BashQuotes> list) {
                        if (!list.isEmpty()) {
                            for (BashQuotes quote: list) {
                                DatabaseHelper.getInstance(getApplicationContext()).addQuote(quote);
                            }
                            handler.obtainMessage(M_DONE).sendToTarget();
                        } else {
                            Toast.makeText(MainActivity.this.getApplicationContext(),
                                    getString(R.string.empty_response), Toast.LENGTH_SHORT).show();
                            handler.obtainMessage(M_FAIL).sendToTarget();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(MainActivity.this,
                                error, Toast.LENGTH_SHORT).show();
                        handler.obtainMessage(M_FAIL).sendToTarget();
                    }
                });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case M_DONE:
                tableFragment = new TableFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentId, tableFragment)
                        .commit();
                dialog.dismiss();
                break;
            case M_FAIL:
                dialog.dismiss();
                finish();
                break;
        }
        return false;
    }


    private long lastPressedTime;
    private static final int PERIOD = 2000;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (event.getDownTime() - lastPressedTime < PERIOD) {
                        ExitActivity.exitApplication(getBaseContext());
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.confirm_exit),
                                Toast.LENGTH_SHORT).show();
                        lastPressedTime = event.getEventTime();
                    }
                    return true;
            }
        }
        return false;
    }

}
