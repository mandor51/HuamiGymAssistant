package com.mandor51.huamigymassistant;

import java.util.ArrayList;

public class SimpleRow {

    ArrayList<SimplePage> mPagesRow = new ArrayList<SimplePage>();

    public void addPages(SimplePage page) {
        mPagesRow.add(page);
    }

    public SimplePage getPages(int index) {
        return mPagesRow.get(index);
    }

    public int size(){
        return mPagesRow.size();
    }
}
