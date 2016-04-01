package com.yatatsu.lazyexpandablerecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ParentViewHolder<P> extends RecyclerView.ViewHolder {
  private boolean expanded;
  private ParentItemExpandCollapseListener expandCollapseListener;
  private ParentItem<P> parentItem;

  public interface ParentItemExpandCollapseListener {
    void onExpanded(int position);
    void onCollapsed(int position);
  }

  public ParentViewHolder(View itemView) {
    super(itemView);
    expanded = false;
    if (shouldItemViewClickToggleExpansion()) {
      setMainItemClickToExpand();
    }
  }

  public void setExpandCollapseListener(ParentItemExpandCollapseListener expandCollapseListener) {
    this.expandCollapseListener = expandCollapseListener;
  }

  public ParentItemExpandCollapseListener getExpandCollapseListener() {
    return expandCollapseListener;
  }

  public boolean shouldItemViewClickToggleExpansion() {
    return true;
  }

  public void setMainItemClickToExpand() {
    itemView.setOnClickListener(onClickForToggle);
  }

  public boolean isExpanded() {
    return expanded;
  }

  public void setExpanded(boolean expanded) {
    this.expanded = expanded;
  }

  public void setParentItem(ParentItem<P> item) {
    this.parentItem = item;
  }

  public ParentItem<P> getParentItem() {
    return parentItem;
  }

  protected void expandView() {
    setExpanded(true);
    onExpansionToggled(false);

    if (expandCollapseListener != null) {
      expandCollapseListener.onExpanded(getAdapterPosition());
    }
  }

  protected void collapseView() {
    setExpanded(false);
    onExpansionToggled(true);

    if (expandCollapseListener != null) {
      expandCollapseListener.onCollapsed(getAdapterPosition());
    }
  }

  protected void onExpansionToggled(boolean expanded) {

  }

  protected View.OnClickListener onClickForToggle = new View.OnClickListener() {
    @Override public void onClick(View v) {
      if (isExpanded()) {
        collapseView();
      } else {
        expandView();
      }
    }
  };
}
