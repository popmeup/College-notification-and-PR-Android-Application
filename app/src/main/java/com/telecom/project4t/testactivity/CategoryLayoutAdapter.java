package com.telecom.project4t.testactivity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class CategoryLayoutAdapter extends RecyclerView.Adapter<CategoryLayoutViewHolder> {

    View categoryLayoutView;

    Context context;
    Activity activity;

    List<UserProfile> categoryList;
    List<String> categoryListPath;

    public CategoryLayoutAdapter(List<UserProfile> categoryList, List<String> categoryListPath, Context context, Activity activity) {
        this.categoryList = categoryList;
        this.categoryListPath = categoryListPath;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public CategoryLayoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        categoryLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card, parent, false);

        return new CategoryLayoutViewHolder(categoryLayoutView);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onBindViewHolder(CategoryLayoutViewHolder viewHolder, int position) {
        viewHolder.setData(categoryList, categoryListPath, position, context, activity);
    }

}
