package com.yatatsu.example;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yatatsu.example.databinding.ActivityMainBinding;
import com.yatatsu.example.databinding.ListItemSimpleBinding;
import com.yatatsu.lazyexpandablerecyclerview.ChildViewHolder;
import com.yatatsu.lazyexpandablerecyclerview.LazyExpandableRecyclerAdapter;
import com.yatatsu.lazyexpandablerecyclerview.ParentViewHolder;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements LazyExpandableRecyclerAdapter.ExpandableDataListener<String,String> {

  private ActivityMainBinding binding;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    final ExpandableAdapter adapter = new ExpandableAdapter(this, this);
    List<String> parents = Arrays.asList("Sample", "Todo");
    adapter.addAll(parents);
    adapter.setOnItemClickListener(new OnItemClickListener() {
      @Override public void onItemClick(int parentPosition, int childPosition, String name) {
        if (name.equals("Check list")) {
          startActivity(new Intent(MainActivity.this, CheckListActivity.class));
        }
      }
    });
    binding.recyclerView.setAdapter(adapter);
  }

  @Override public int getChildItemCount(int parentPosition, String parent) {
    return 1;
  }

  @Override public String getChildItem(int parentPosition, int childPosition, String parent) {
    if (parentPosition == 0) {
      return "Check list";
    }
    return "Todo";
  }

  @Override public boolean initiallyExpanded(int parentPosition, String parent) {
    return parentPosition == 0;
  }

  static class AlphabetViewHolder extends ParentViewHolder<String> {
    ListItemSimpleBinding binding;
    public AlphabetViewHolder(Context context, ViewGroup parent, int resId) {
      super(LayoutInflater.from(context).inflate(resId, parent, false));
      binding = DataBindingUtil.bind(itemView);
    }
  }

  static class WordViewHolder extends ChildViewHolder<String, String> {
    ListItemSimpleBinding binding;
    public WordViewHolder(Context context, ViewGroup parent, int resId) {
      super(LayoutInflater.from(context).inflate(resId, parent, false));
      binding = DataBindingUtil.bind(itemView);
    }
  }

  interface OnItemClickListener {
    void onItemClick(int parentPosition, int childPosition, String name);
  }

  private class ExpandableAdapter
      extends LazyExpandableRecyclerAdapter<String, String, AlphabetViewHolder, WordViewHolder> {

    private final Context context;
    OnItemClickListener onItemClickListener;

    public ExpandableAdapter(Context context,
        @NonNull ExpandableDataListener<String, String> expandableDataListener) {
      super(expandableDataListener);
      this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
      this.onItemClickListener = onItemClickListener;
    }

    @Override public AlphabetViewHolder onCreateParentViewHolder(ViewGroup parent) {
      return new AlphabetViewHolder(context, parent, R.layout.list_item_simple);
    }

    @Override public WordViewHolder onCreateChildViewHolder(ViewGroup parent) {
      return new WordViewHolder(context, parent, R.layout.list_item_simple);
    }

    @Override
    public void onBindParentViewHolder(AlphabetViewHolder holder, int position, String item) {
      holder.binding.name.setText(item);
    }

    @Override public void onBindChildViewHolder(WordViewHolder holder, final int position, String parent,
        final String item) {
      holder.binding.name.setText(item);
      holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (onItemClickListener != null) {
            onItemClickListener.onItemClick(getParentPosition(position), getChildPosition(position), item);
          }
        }
      });
    }
  }
}
