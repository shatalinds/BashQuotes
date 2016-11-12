package home.quote.adapters;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import home.quote.R;
import home.quote.structs.BashQuotes;

/**
 * Created by dave on 11.11.2016.
 */
public class QuoteAdapter extends BaseAdapter {
    public static final String TAG = QuoteAdapter.class.getSimpleName();

    private static final String urlApi = "http://www.umori.li";

    private List<BashQuotes> quotesList;
    private LayoutInflater inflater;

    public QuoteAdapter(final Activity activity, List<BashQuotes> list) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.quotesList = new ArrayList<>(list);
    }

    public void appendItems(List<BashQuotes> list) {
        if (quotesList != null) {
            this.quotesList.addAll(list);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (quotesList != null) {
            return quotesList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (quotesList != null) {
            if (quotesList.size() >= position) {
                return quotesList.get(position);
            }
        }
        return null;
    }

    /**
     * Держатель ячеек
     */
    private static class ViewHolder {
        private TextView tvLink;
        private TextView tvHtml;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SoftReference<ViewHolder> softViewHolder;

        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.quote_item, null, true);

            softViewHolder = new SoftReference<ViewHolder>(new ViewHolder());
            ViewHolder holder = softViewHolder.get();
            if (holder != null) {
                holder.tvLink = (TextView) rowView.findViewById(R.id.tvLink);
                holder.tvHtml = (TextView) rowView.findViewById(R.id.tvHtml);
                rowView.setTag(softViewHolder);
            }
        } else {
            softViewHolder = (SoftReference<ViewHolder>) rowView.getTag();
        }

        final ViewHolder holder = softViewHolder.get();
        if (holder != null) {
            final BashQuotes quote = quotesList.get(position);
            try {
                holder.tvLink.setText(new StringBuilder().append(urlApi)
                        .append(URLDecoder.decode(quote.getLink(), "UTF-8")).toString());
                holder.tvHtml.setText(Html.fromHtml(quote.getElementPureHtml()));
                holder.tvHtml.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectAnimator animation;
                        if (holder.tvHtml.getMaxLines() == 1) {
                            int lineCount = holder.tvHtml.getLineCount();
                            if (lineCount < 1) {
                                lineCount = 1;
                            }
                            animation = ObjectAnimator.ofInt(holder.tvHtml, "maxLines",
                                    lineCount);
                        } else {
                            animation = ObjectAnimator.ofInt(holder.tvHtml, "maxLines", 1);
                        }
                        animation.setDuration(150).start();
                        holder.tvHtml.invalidate();
                    }
                });
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }

        return rowView;
    }
}
