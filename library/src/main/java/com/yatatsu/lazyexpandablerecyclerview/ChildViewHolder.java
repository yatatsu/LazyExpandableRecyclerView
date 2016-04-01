package com.yatatsu.lazyexpandablerecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ChildViewHolder<P, C> extends RecyclerView.ViewHolder {
  private ChildItem<P, C> childItem;

  public ChildViewHolder(View itemView) {
    super(itemView);
  }

  public void setChildItem(ChildItem<P, C> childItem) {
    this.childItem = childItem;
  }

  public ChildItem<P, C> getChildItem() {
    return childItem;
  }
}
