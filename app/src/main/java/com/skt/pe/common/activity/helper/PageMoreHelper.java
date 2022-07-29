package com.skt.pe.common.activity.helper;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

public class PageMoreHelper {
    private int totalPage;
    private int page;
    private String end;
    private Activity activity;
    private ListView listView;
    //	private int layout_more_id;
    private int button_more_id;
    private boolean isSetFooter = false;
    View footer;

    public PageMoreHelper(Activity activity, ListView listView, int layout_more_id, int button_more_id, int totalPage, int page, String end) {
        this.init(activity, listView, layout_more_id, button_more_id, totalPage, page, end);
    }

    public PageMoreHelper(Activity activity, ListView listView, int layout_more_id, int button_more_id, int page, String end) {
        this.init(activity, listView, layout_more_id, button_more_id, -1, page, end);
    }

    private void init(Activity activity, ListView listView, int layout_more_id, int button_more_id, int totalPage, int page, String end) {
        this.activity = activity;
        this.listView = listView;
        this.button_more_id = button_more_id;
        this.totalPage = totalPage;
        this.page = page;
        this.end = end;
        footer = this.activity.getLayoutInflater().inflate(layout_more_id, null, true);
    }

    public void setPageInfo(int page, String end) {
        this.page = page;
        this.end = end;
    }

    public int nextPage() {
        if (this.isMorePage() == true) {
            this.page += 1;
        }
        return this.page;
    }

    public void putMoreButton() {
        if (isMorePage() == true) {
            if (isSetFooter == false) {
                setMoreButton(true);
//				this.listView.addFooterView(footer);
//				isSetFooter = true;
            }
        } else {
            setMoreButton(false);
//			this.listView.removeFooterView(footer);
//			isSetFooter = false;
        }
    }

    public void setMoreButton(boolean isSetMore) {
        if (isSetMore == true) {
            this.listView.addFooterView(footer);
        } else {
            this.listView.removeFooterView(footer);
        }

        isSetFooter = isSetMore;
    }

    public boolean isMorePageView(View view) {
        return view.getId() == this.button_more_id;
    }

    private boolean isMorePage() {
        return "N".equals(this.end);
    }
}