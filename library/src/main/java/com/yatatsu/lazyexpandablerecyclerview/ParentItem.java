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

  @Override public int hashCode() {
    return item.hashCode();
  }

  @Override public boolean equals(Object o) {
    return o == this ||
        (o instanceof ParentItem && ((ParentItem) o).item.equals(item));
  }
}
