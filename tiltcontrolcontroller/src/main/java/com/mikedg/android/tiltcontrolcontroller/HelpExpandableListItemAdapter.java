package com.mikedg.android.tiltcontrolcontroller;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mikedg.android.tiltcontrolcontroller.data.HelpData;
import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;

/**
 * Created by jmann on 11/4/14.
 */
public class HelpExpandableListItemAdapter extends ExpandableListItemAdapter<Integer> {

    private final Context mContext;

    protected HelpExpandableListItemAdapter(Context context) {
        super(context, R.layout.expandable_list_item_card, R.id.activity_expandablelistitem_card_title, R.id.activity_expandablelistitem_card_content);

        mContext = context;

        for (int i = 0; i < HelpData.getQuestions().length; i++)
            add(i);
    }


    @Override
    public View getTitleView(int position, View convertView, ViewGroup parent) {

        TextView tv = (TextView)convertView;

        if (tv == null) {
            tv = new TextView(mContext);
            tv.setTypeface(null, Typeface.BOLD);
        }

        tv.setText(HelpData.getQuestions()[position]);

        return tv;
    }

    @Override
    public View getContentView(int position, View convertView, ViewGroup parent) {

        TextView tv = (TextView)convertView;
        if (tv == null) {
            tv = new TextView(mContext);
        }
        tv.setText(HelpData.getAnswers()[position]);

        return tv;
    }
}
