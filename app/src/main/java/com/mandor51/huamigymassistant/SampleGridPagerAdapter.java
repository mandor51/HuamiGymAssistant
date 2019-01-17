package com.mandor51.huamigymassistant;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.ArrayList;

public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    private ArrayList<SimpleRow> mPages;

    public SampleGridPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        initPages();
    }

    private void initPages() {
        mPages = new ArrayList<SimpleRow>();

        SimpleRow row1 = new SimpleRow();
        row1.addPages(new SimplePage("Title11", "Text1"));
        row1.addPages(new SimplePage("Title12", "Text2"));

        SimpleRow row2 = new SimpleRow();
        row2.addPages(new SimplePage("Title21", "Text3"));

        SimpleRow row3 = new SimpleRow();
        row3.addPages(new SimplePage("Title31", "Text4"));

        SimpleRow row4 = new SimpleRow();
        row4.addPages(new SimplePage("Title41", "Text5"));
        row4.addPages(new SimplePage("Title42", "Text6"));

        mPages.add(row1);
        mPages.add(row2);
        mPages.add(row3);
        mPages.add(row4);
    }

    @Override
    public Fragment getFragment(int row, int col) {
        SimplePage page = ((SimpleRow)mPages.get(row)).getPages(col);
        CardFragment fragment = CardFragment.create(page.mTitle, page.mText);
        return fragment;
    }

    /*@Override
    public ImageReference getBackground(int row, int col) {
        SimplePage page = ((SimpleRow)mPages.get(row)).getPages(col);
        return ImageReference.forDrawable(page.mBackgroundId);
    }*/

    @Override
    public int getRowCount() {
        return mPages.size();
    }

    @Override
    public int getColumnCount(int row) {
        return mPages.get(row).size();
    }
}
