package gunn.brewski.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by SESA300553 on 4/2/2015.
 */
public class BeerListAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_BEER = 0;
    private static final int VIEW_TYPE_SELECTED_BEER = 1;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView largeLabelView;
        public final TextView nameView;
        public final TextView descriptionView;

        public ViewHolder(View view) {
            largeLabelView = (ImageView) view.findViewById(R.id.list_item_beer_label);
            nameView = (TextView) view.findViewById(R.id.list_item_beer_name_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_beer_description_textview);
        }
    }

    public BeerListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (0) {
            case VIEW_TYPE_BEER: {
                layoutId = R.layout.list_item_beer;
                break;
            }
            case VIEW_TYPE_SELECTED_BEER: {
                layoutId = R.layout.list_item_beer_selected;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        try {
            final ViewHolder viewHolder = (ViewHolder) view.getTag();

            if (null != cursor.getString(BeerListFragment.COL_LABEL_MEDIUM)) {
                ImageLoader.getInstance().displayImage(cursor.getString(BeerListFragment.COL_LABEL_MEDIUM), viewHolder.largeLabelView);
            }
            else {
                viewHolder.largeLabelView.setImageResource(R.drawable.brewski_default);
            }

            // Read date from cursor
            String beerName = cursor.getString(BeerListFragment.COL_BEER_NAME);
            // Find TextView and set formatted date on it
            viewHolder.nameView.setText(beerName);

            // Read weather forecast from cursor
            String beerDescription = cursor.getString(BeerListFragment.COL_BEER_DESCRIPTION);
            // Find TextView and set weather forecast on it

            if(beerDescription.length() > 100) {
                beerDescription = beerDescription.substring(0, 100) + "...";
            }

            viewHolder.descriptionView.setText(beerDescription);

            // For accessibility, add a content description to the icon field
            viewHolder.largeLabelView.setContentDescription(beerDescription);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_BEER : VIEW_TYPE_SELECTED_BEER;
    }


    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
