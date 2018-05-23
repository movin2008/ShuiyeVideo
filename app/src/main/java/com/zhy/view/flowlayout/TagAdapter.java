package com.zhy.view.flowlayout;

import java.util.List;

public abstract class TagAdapter<T> {
    private List<T> mTagDatas;
    private OnDataChangedListener mOnDataChangedListener;

    public TagAdapter(List<T> datas) {
        mTagDatas = datas;
    }

    interface OnDataChangedListener {
        void onChanged();
    }

    void setOnDataChangedListener(OnDataChangedListener listener) {
        mOnDataChangedListener = listener;
    }

    public int getCount() {
        return mTagDatas == null ? 0 : mTagDatas.size();
    }

    public void notifyDataChanged() {
        if (mOnDataChangedListener != null){
            mOnDataChangedListener.onChanged();
        }
    }

    public T getItem(int position) {
        return mTagDatas.get(position);
    }

    public abstract TagView getView(FlowLayout parent, int position, T t);

}
