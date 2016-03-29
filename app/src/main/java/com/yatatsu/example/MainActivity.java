package com.yatatsu.example;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
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
    ExpandableAdapter adapter = new ExpandableAdapter(this, this);
    List<String> parents = Arrays.asList("a", "b", "c");
    adapter.addAll(parents);
    binding.recyclerView.setAdapter(adapter);
  }

  @Override public int getChildItemCount(int parentPosition, String parent) {
    return 3;
  }

  @Override public String getChildItem(int parentPosition, int childPosition, String parent) {
    return parent + " child " + childPosition;
  }

  @Override public boolean initiallyExpanded(int parentPosition, String parent) {
    return parentPosition == 0;
  }

  static class AlphabetViewHolder extends ParentViewHolder {
    ListItemSimpleBinding binding;
    public AlphabetViewHolder(Context context, ViewGroup parent, int resId) {
      super(LayoutInflater.from(context).inflate(resId, parent, false));
      binding = DataBindingUtil.bind(itemView);
    }
  }

  static class WordViewHolder extends ChildViewHolder {
    ListItemSimpleBinding binding;
    public WordViewHolder(Context context, ViewGroup parent, int resId) {
      super(LayoutInflater.from(context).inflate(resId, parent, false));
      binding = DataBindingUtil.bind(itemView);
    }
  }

  private class ExpandableAdapter
      extends LazyExpandableRecyclerAdapter<String, String, AlphabetViewHolder, WordViewHolder> {

    private final Context context;

    public ExpandableAdapter(Context context,
        @NonNull ExpandableDataListener<String, String> expandableDataListener) {
      super(expandableDataListener);
      this.context = context;
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

    @Override public void onBindChildViewHolder(WordViewHolder holder, int position, String parent,
        String item) {
      holder.binding.name.setText(item);
    }
  }
}
