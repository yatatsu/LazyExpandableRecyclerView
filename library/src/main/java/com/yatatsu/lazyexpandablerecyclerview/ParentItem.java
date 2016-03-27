package com.yatatsu.lazyexpandablerecyclerview;

public class ParentItem<P> {
  final P item;
  boolean expanded;

  public ParentItem(P item) {
    this.item = item;
  }

  public P getItem() {
    return item;
  }

  public boolean isExpanded() {
    return expanded;
  }

  public void setExpanded(boolean expanded) {
    this.expanded = expanded;
  }
}
