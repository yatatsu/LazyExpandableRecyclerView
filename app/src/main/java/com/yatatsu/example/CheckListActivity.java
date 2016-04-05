package com.yatatsu.example;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yatatsu.example.databinding.ActivityCheckBinding;
import com.yatatsu.example.databinding.ListItemCheckableBinding;
import com.yatatsu.lazyexpandablerecyclerview.ChildViewHolder;
import com.yatatsu.lazyexpandablerecyclerview.LazyExpandableRecyclerAdapter;
import com.yatatsu.lazyexpandablerecyclerview.ParentViewHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckListActivity extends AppCompatActivity {

  private ActivityCheckBinding binding;
  private CheckListAdapter adapter;

  private List<CategoryItem> checkedList = new ArrayList<>();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_check);
    binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    adapter = new CheckListAdapter(this,
        new LazyExpandableRecyclerAdapter.ExpandableDataListener<Category, CategoryItem>() {
          @Override public int getChildItemCount(int parentPosition, Category parent) {
            return parent.items.size();
          }

          @Override
          public CategoryItem getChildItem(int parentPosition, int childPosition, Category parent) {
            return parent.items.get(childPosition);
          }

          @Override public boolean initiallyExpanded(int parentPosition, Category parent) {
            return parentPosition == 0;
          }
        });
    adapter.addAll(loadCategory());
    binding.recyclerView.setAdapter(adapter);
  }

  private List<Category> loadCategory() {
    return Arrays.asList(new Category("Book", new CategoryItem("eBook"), new CategoryItem("Comic"),
            new CategoryItem("Magazine")),
        new Category("Food", new CategoryItem("Drink"), new CategoryItem("Coffee"),
            new CategoryItem("Noodle")),
        new Category("Sport", new CategoryItem("Bike"), new CategoryItem("Training"),
            new CategoryItem("Running")));
  }

  public static class Category {
    final String name;
    final List<CategoryItem> items = new ArrayList<>();

    public Category(String name, @NonNull CategoryItem... items) {
      this.name = name;
      this.items.addAll(Arrays.asList(items));
    }

    @Override public boolean equals(Object o) {
      return o == this || (o instanceof Category && ((Category) o).name.equals(name));
    }
  }

  public static class CategoryItem {
    final String name;

    public CategoryItem(String name) {
      this.name = name;
    }

    @Override public boolean equals(Object o) {
      return o == this || (o instanceof CategoryItem && ((CategoryItem) o).name.equals(name));
    }
  }

  private class CheckListAdapter extends
      LazyExpandableRecyclerAdapter<Category, CategoryItem, CategoryViewHolder, CategoryItemViewHolder> {

    private final Context context;

    public CheckListAdapter(Context context,
        @NonNull ExpandableDataListener<Category, CategoryItem> expandableDataListener) {
      super(expandableDataListener);
      this.context = context;
    }

    @Override public CategoryViewHolder onCreateParentViewHolder(ViewGroup parent) {
      return new CategoryViewHolder(context, parent);
    }

    @Override public CategoryItemViewHolder onCreateChildViewHolder(ViewGroup parent) {
      return new CategoryItemViewHolder(context, parent);
    }

    @Override
    public void onBindParentViewHolder(CategoryViewHolder holder, int position, Category item) {
      holder.binding.name.setText(item.name);
      boolean highlighted = false;
      for (CategoryItem ci : item.items) {
        if (checkedList.contains(ci)) {
          highlighted = true;
        }
      }
      holder.binding.setHighlighted(highlighted);
    }

    @Override public void onBindChildViewHolder(final CategoryItemViewHolder holder, int position,
        Category parent, final CategoryItem item) {
      holder.binding.name.setText(item.name);
      boolean isCheckedItem = checkedList.contains(item);
      holder.binding.setChecked(isCheckedItem);
      holder.binding.setHighlighted(isCheckedItem);
      holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (checkedList.contains(item)) {
            checkedList.remove(item);
          } else {
            checkedList.add(item);
          }
          int parentPosition = holder.getParentAdapterPosition();
          // notify child
          adapter.notifyChildItemChanged(parentPosition, holder.getChildAdapterPosition());
          // notify parent
          adapter.notifyParentItemChanged(parentPosition, false);
        }
      });
    }
  }

  static class CategoryViewHolder extends ParentViewHolder<Category> {
    ListItemCheckableBinding binding;

    public CategoryViewHolder(Context context, ViewGroup parent) {
      super(LayoutInflater.from(context).inflate(R.layout.list_item_checkable, parent, false));
      binding = DataBindingUtil.bind(itemView);
      binding.checkbox.setVisibility(View.GONE);
    }
  }

  static class CategoryItemViewHolder extends ChildViewHolder<Category, CategoryItem> {
    ListItemCheckableBinding binding;

    public CategoryItemViewHolder(Context context, ViewGroup parent) {
      super(LayoutInflater.from(context).inflate(R.layout.list_item_checkable, parent, false));
      binding = DataBindingUtil.bind(itemView);
      binding.toggleButton.setVisibility(View.GONE);
    }
  }
}
