package home.quote.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import home.quote.R;
import home.quote.adapters.QuoteAdapter;
import home.quote.helpers.DatabaseHelper;
import home.quote.structs.BashQuotes;

public class TableFragment extends Fragment implements AbsListView.OnScrollListener {
    private static final String TAG = TableFragment.class.getSimpleName();

    private ListView lvQuotes;
    private QuoteAdapter adapter;
    private List<BashQuotes> bashQuotes;

    public TableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_table, container, false);
        lvQuotes = (ListView) view.findViewById(R.id.lvOuotes);
        lvQuotes.setOnScrollListener(this);

        DatabaseHelper.getInstance(getContext()).beginSelectPartOfQuotes(20);
        bashQuotes = DatabaseHelper.getInstance(getContext()).selectNextPartQuotes();
        if (bashQuotes != null) {
            adapter = new QuoteAdapter(getActivity(), bashQuotes);
            lvQuotes.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(),
                    getString(R.string.empty_response), Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && (lvQuotes.getLastVisiblePosition() - lvQuotes.getHeaderViewsCount() -
                lvQuotes.getFooterViewsCount()) >= (adapter.getCount() - 1)) {
            bashQuotes = DatabaseHelper.getInstance(getContext()).selectNextPartQuotes();
            if (bashQuotes != null) {
                adapter.appendItems(bashQuotes);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private StringBuilder logBuilder;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        logBuilder = new StringBuilder().append(" TotalItemCount:").append(totalItemCount);
        Log.d(TAG, logBuilder.toString());
    }
}
