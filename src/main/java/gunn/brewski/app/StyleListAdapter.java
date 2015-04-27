package gunn.brewski.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by SESA300553 on 4/26/2015.
 */
public class StyleListAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_STYLE = 0;
    private static final int VIEW_TYPE_SELECTED_STYLE = 1;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView styleNameView;
        public final TextView styleShortNameView;
        public final TextView styleDescriptionView;

        public ViewHolder(View view) {
            styleNameView = (TextView) view.findViewById(R.id.list_item_date_textview);
            styleShortNameView = (TextView) view.findViewById(R.id.list_item_high_textview);
            styleDescriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        }
    }

    public StyleListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_STYLE: {
                layoutId = R.layout.list_item_brewery;
                break;
            }
            case VIEW_TYPE_SELECTED_STYLE: {
                layoutId = R.layout.list_item_brewery_selected;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read date from cursor
        String styleName = cursor.getString(StyleListFragment.COL_STYLE_NAME);
        viewHolder.styleNameView.setText(styleName);

        // Read high temperature from cursor
        String styleShortName = cursor.getString(StyleListFragment.COL_STYLE_SHORT_NAME);
        viewHolder.styleShortNameView.setText(styleShortName);

        // Read weather forecast from cursor
        String styleDescription = cursor.getString(StyleListFragment.COL_STYLE_DESCRIPTION);
        viewHolder.styleDescriptionView.setText(styleDescription);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_STYLE : VIEW_TYPE_SELECTED_STYLE;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
