package com.yatatsu.lazyexpandablerecyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LazyExpandableRecyclerAdapter<P, C, PVH extends ParentViewHolder, CVH extends ChildViewHolder>
    extends RecyclerView.Adapter<ViewHolder>
    implements ParentViewHolder.ParentItemExpandCollapseListener {

  protected List<P> parentItems;
  protected List<Object> allItems;
  @NonNull protected ExpandableDataListener<P, C> expandableDataListener;
  protected ExpandCollapseListener expandCollapseListener;

  protected static final int VIEW_TYPE_PARENT = 0;
  protected static final int VIEW_TYPE_CHILD = 1;

  public interface ExpandableDataListener<P, C> {
    int getChildItemCount(int parentPosition, P parent);

    C getChildItem(int parentPosition, int childPosition, P parent);

    boolean initiallyExpanded(int parentPosition, P parent);
  }

  public interface ExpandCollapseListener {
    void onItemExpanded(int position);

    void onItemCollapsed(int position);
  }

  public LazyExpandableRecyclerAdapter(
      @NonNull ExpandableDataListener<P, C> expandableDataListener) {
    parentItems = new ArrayList<>();
    allItems = new ArrayList<>();
    this.expandableDataListener = expandableDataListener;
  }

  public void clear() {
    parentItems.clear();
    allItems.clear();
  }

  public void addAll(Collection<P> items) {
    parentItems.addAll(items);
    generateAllItemList();
  }

  public void setExpandCollapseListener(ExpandCollapseListener expandCollapseListener) {
    this.expandCollapseListener = expandCollapseListener;
  }

  @Override public int getItemViewType(int position) {
    Object o = allItems.get(position);
    if (o instanceof ParentItem) {
      return VIEW_TYPE_PARENT;
    } else if (o instanceof ChildItem) {
      return VIEW_TYPE_CHILD;
    } else {
      throw new IllegalArgumentException("can't detect view type for position " + position);
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case VIEW_TYPE_PARENT:
        PVH parentViewHolder = onCreateParentViewHolder(parent);
        parentViewHolder.setExpandCollapseListener(this);
        return parentViewHolder;
      case VIEW_TYPE_CHILD:
        return onCreateChildViewHolder(parent);
    }
    throw new IllegalArgumentException("unexpected viewType " + viewType);
  }

  public abstract PVH onCreateParentViewHolder(ViewGroup parent);

  public abstract CVH onCreateChildViewHolder(ViewGroup parent);

  @SuppressWarnings("unchecked") @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    if (holder instanceof ParentViewHolder) {
      ParentItem<P> parentItem = (ParentItem<P>) allItems.get(position);
      P parent = parentItem.getItem();
      PVH parentViewHolder = (PVH) holder;
      parentViewHolder.setExpanded(parentItem.isExpanded());
      onBindParentViewHolder(parentViewHolder, position, parent);
    } else if (holder instanceof ChildViewHolder) {
      ChildItem<P, C> childItem = (ChildItem<P, C>) allItems.get(position);
      P parent = childItem.getParent();
      C child = childItem.getItem();
      onBindChildViewHolder((CVH) holder, position, parent, child);
    }
  }

  public abstract void onBindParentViewHolder(PVH holder, int position, P item);

  public abstract void onBindChildViewHolder(CVH holder, int position, P parent, C item);

  @Override public int getItemCount() {
    return allItems.size();
  }

  @SuppressWarnings("unchecked") @Override public void onExpanded(int position) {
    Object item = allItems.get(position);
    if (item instanceof ParentItem) {
      expandParentItem((ParentItem<P>) item, position);
    }
  }

  @SuppressWarnings("unchecked") @Override public void onCollapsed(int position) {
    Object item = allItems.get(position);
    if (item instanceof ParentItem) {
      collapseParentItem((ParentItem<P>) item, position);
    }
  }

  protected void expandParentItem(ParentItem<P> item, int position) {
    if (!item.isExpanded()) {
      item.setExpanded(true);
      P parent = item.getItem();
      int parentPosition = parentItems.indexOf(parent);
      int childCount = expandableDataListener.getChildItemCount(parentPosition, parent);
      if (childCount > 0) {
        for (int i = 0; i < childCount; i++) {
          C child = expandableDataListener.getChildItem(parentPosition, i, parent);
          allItems.add(position + i + 1, new ChildItem<>(parent, child));
        }
        notifyItemRangeInserted(position + 1, childCount);
      }

      // TODO
    }
  }

  protected void collapseParentItem(ParentItem<P> item, int position) {
    if (item.isExpanded()) {
      item.setExpanded(false);
      P parent = item.getItem();
      int parentPosition = parentItems.indexOf(parent);
      int childCount = expandableDataListener.getChildItemCount(parentPosition, parent);
      if (childCount > 0) {
        for (int i = childCount; i > 0; i--) {
          allItems.remove(position + i + 1);
        }
        notifyItemRangeRemoved(position + 1, childCount);
      }

      // TODO
    }
  }

  private int getActualParentPosition(int parentPosition) {
    if (parentPosition == 0) {
      return 0;
    } else if (parentPosition == parentItems.size() - 1) {
      return allItems.size();
    } else {
      P beforeItem = parentItems.get(parentPosition - 1);
      int beforeParentIndex = allItems.indexOf(new ParentItem<>(beforeItem));
      return beforeParentIndex + expandableDataListener.getChildItemCount(parentPosition - 1,
          beforeItem);
    }
  }

  private int addParentItem(int position, int parentPosition, P parent) {
    int sizeChanged = 1;
    ParentItem<P> parentItem = new ParentItem<>(parent);
    allItems.add(position, parentItem);
    if (expandableDataListener.initiallyExpanded(parentPosition, parent)) {
      parentItem.setExpanded(true);
      int count = expandableDataListener.getChildItemCount(parentPosition, parent);
      for (int i = 0; i < count; i++) {
        sizeChanged += i;
        C child = expandableDataListener.getChildItem(parentPosition, i, parent);
        allItems.add(sizeChanged, new ChildItem<>(parent, child));
      }
    }
    return sizeChanged;
  }

  public void notifyParentItemInserted(int parentPosition) {
    P parent = parentItems.get(parentPosition);
    int parentIndex = getActualParentPosition(parentPosition);
    int sizeChanged = addParentItem(parentIndex, parentPosition, parent);
    notifyItemRangeInserted(parentIndex, sizeChanged);
  }

  public void notifyParentItemRangeInserted(int parentPositionStart, int itemCount) {
    P parent = parentItems.get(parentPositionStart);
    int initialParentIndex = getActualParentPosition(parentPositionStart);

    int sizeChanged = 0;
    int indexChanged;
    int parentIndex = initialParentIndex;
    int parentPositionEnd = parentPositionStart + itemCount;
    for (int i = parentPositionStart; i < parentPositionEnd; i++) {
      P parentForIndex = parentItems.get(i);
      indexChanged = addParentItem(parentIndex, i, parentForIndex);
      parentIndex += indexChanged;
      sizeChanged += indexChanged;
    }
    notifyItemRangeInserted(initialParentIndex, sizeChanged);
  }

  public void notifyParentItemRemoved(int parentPosition) {
    // TODO
  }

  public void notifyParentItemRangeRemoved(int parentPositionStart, int itemCount) {
    // TODO
  }

  public void notifyParentItemChanged(int parentPosition) {
    // TODO
  }

  public void notifyParentItemRangeChanged(int parentPositionStart, int itemCount) {
    // TODO
  }

  public void notifyParentItemMoved(int fromParentPosition, int toParentPosition) {
    // TODO
  }

  public void notifyChildItemInserted(int parentPosition, int childPosition) {
    // TODO
  }

  public void notifyChildItemRangeInserted(int parentPosition, int childPositionStart,
      int itemCount) {
    // TODO
  }

  public void notifyChildItemRemoved(int parentPosition, int childPosition) {
    // TODO
  }

  public void notifyChildItemRangeRemoved(int parentPosition, int childPositionStart,
      int itemCount) {
    // TODO
  }

  public void notifyChildItemChanged(int parentPosition, int childPosition) {
    // TODO
  }

  public void notifyChildItemRangeChanged(int parentPosition, int childPositionStart,
      int itemCount) {
    // TODO
  }

  public void notifyChildItemMoved(int parentPosition, int fromChildPosition, int toChildPosition) {
    // TODO
  }

  protected void generateAllItemList() {
    allItems.clear();
    for (int i = 0, count = parentItems.size(); i < count; i++) {
      P parent = parentItems.get(i);
      ParentItem<P> parentItem = new ParentItem<>(parent);
      boolean isExpandedInitially = expandableDataListener.initiallyExpanded(i, parent);
      parentItem.setExpanded(isExpandedInitially);
      allItems.add(parentItem);
      if (isExpandedInitially) {
        int childCount = expandableDataListener.getChildItemCount(i, parent);
        for (int j = 0; j < childCount; j++) {
          C child = expandableDataListener.getChildItem(i, j, parent);
          allItems.add(new ChildItem<>(child, parent));
        }
      }
    }
  }
}
