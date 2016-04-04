package com.yatatsu.example;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import com.yatatsu.example.databinding.ActivityCheckBinding;

public class CheckListActivity extends AppCompatActivity {

  private ActivityCheckBinding binding;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_check);
    binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
  }


}
