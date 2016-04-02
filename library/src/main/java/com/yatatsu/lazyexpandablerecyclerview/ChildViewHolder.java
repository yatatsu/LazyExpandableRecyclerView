package com.yatatsu.lazyexpandablerecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ChildViewHolder<P, C> extends RecyclerView.ViewHolder {
  private ChildItem<P, C> childItem;
  private LazyExpandableRecyclerAdapter adapter;

  public ChildViewHolder(View itemView) {
    super(itemView);
  }

  void setChildItem(ChildItem<P, C> childItem) {
    this.childItem = childItem;
  }

  public ChildItem<P, C> getChildItem() {
    return childItem;
  }

  void setAdapter(LazyExpandableRecyclerAdapter adapter) {
    this.adapter = adapter;
  }

  public int getParentAdapterPosition() {
    int adapterPosition = getAdapterPosition();
    if (adapterPosition == RecyclerView.NO_POSITION) {
      return adapterPosition;
    }
    return adapter.getParentPosition(adapterPosition);
  }

  public int getChildAdapterPosition() {
    int adapterPosition = getAdapterPosition();
    if (adapterPosition == RecyclerView.NO_POSITION) {
      return adapterPosition;
    }
    return adapter.getChildPosition(adapterPosition);
  }
}
