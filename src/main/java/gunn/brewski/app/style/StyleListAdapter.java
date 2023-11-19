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
        public final TextView styleDescriptionView;

        public ViewHolder(View view) {
            styleNameView = (TextView) view.findViewById(R.id.list_item_style_name_textview);
            styleDescriptionView = (TextView) view.findViewById(R.id.list_item_style_description_textview);
        }
    }

    public StyleListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
//        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (0) {
            case VIEW_TYPE_STYLE: {
                layoutId = R.layout.list_item_style;
                break;
            }
            case VIEW_TYPE_SELECTED_STYLE: {
                layoutId = R.layout.list_item_style_selected;
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

        String styleName = "";
        // Read date from cursor
        if(null != cursor.getString(StyleListFragment.COL_STYLE_NAME)) {
            styleName = cursor.getString(StyleListFragment.COL_STYLE_NAME);
        }

        if(null != cursor.getString(StyleListFragment.COL_STYLE_SHORT_NAME)) {
            styleName += " (" + cursor.getString(StyleListFragment.COL_STYLE_SHORT_NAME) + ")";
        }

        viewHolder.styleNameView.setText(styleName);

        String styleDescription = "";

        if(null != cursor.getString(StyleListFragment.COL_STYLE_DESCRIPTION)) {
            styleDescription = cursor.getString(StyleListFragment.COL_STYLE_DESCRIPTION);
        }

        if(styleDescription.length() > 100) {
            styleDescription = styleDescription.substring(0, 100) + "...";
        }

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
