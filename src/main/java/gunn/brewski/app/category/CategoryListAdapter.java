package gunn.brewski.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by SESA300553 on 4/2/2015.
 */
public class CategoryListAdapter extends android.support.v4.widget.CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_CATEGORY = 0;
    private static final int VIEW_TYPE_CATEGORY_SELECTED = 1;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView categoryNameView;

        public ViewHolder(View view) {
            categoryNameView = (TextView) view.findViewById(R.id.list_item_date_textview);
        }
    }

    public CategoryListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
//        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = 0;

        switch (0) {
            case VIEW_TYPE_CATEGORY: {
                layoutId = R.layout.list_item_category;
                break;
            }
            case VIEW_TYPE_CATEGORY_SELECTED: {
                layoutId = R.layout.list_item_category_selected;
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
        String categoryName = cursor.getString(CategoryListFragment.COL_CATEGORY_NAME);
        // Find TextView and set formatted date on it
        viewHolder.categoryNameView.setText(categoryName);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_CATEGORY : VIEW_TYPE_CATEGORY_SELECTED;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
