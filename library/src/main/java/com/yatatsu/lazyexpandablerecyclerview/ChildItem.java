package com.yatatsu.lazyexpandablerecyclerview;

public class ChildItem<P, C> {
  final C item;
  final P parent;

  public ChildItem(P parent, C item) {
    this.parent = parent;
    this.item = item;
  }

  public P getParent() {
    return parent;
  }

  public C getItem() {
    return item;
  }
}
