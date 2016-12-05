package com.example.lei.refreshlistview;

import android.content.Context;
import java.util.List;

/**
 * Created by lei on 2016/12/5.
 */
public class ListViewAdapter extends CommonAdapter<String> {

    public ListViewAdapter(Context context, List<String> datas, int mItemLayout) {
        super(context, datas, mItemLayout);
    }

    @Override
    public void convert(ViewHolder viewHolder, String item, int position) {

        viewHolder.setText(R.id.tv,item);

    }
}
