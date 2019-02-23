package com.telecom.project4t.testactivity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryLayoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TabsAdapter tabsAdapter;
    FragmentManager fragmentManager;
    ImageView categoryOrgImage;
    TextView categoryOrgName;
    TextView categoryOrgParent;
    UserProfile categoryListSet;

    Context context;
    Activity activity;

    String categoryPath;
    List<UserProfile> categoryList;
    List<String> categoryListPath;

    public CategoryLayoutViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        categoryOrgImage = (ImageView) itemView.findViewById(R.id.categoryOrgImage);
        categoryOrgName = (TextView) itemView.findViewById(R.id.categoryOrgName);
        categoryOrgParent = (TextView) itemView.findViewById(R.id.categoryOrgParent);
    }

    public void setData(List<UserProfile> categoryList, List<String> categoryListPath, int position, Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.categoryList = categoryList;
        this.categoryListPath = categoryListPath;
        this.categoryListSet = categoryList.get(position);

        categoryOrgImage.setImageResource(R.drawable.reg_profile_image);
        Picasso.get().load(categoryListSet.userImageURL).into(categoryOrgImage);
        categoryOrgName.setText(categoryListSet.getuserOrganization());
        categoryOrgParent.setText(categoryListSet.getuserParentOrganization());
    }

    @Override
    public void onClick(View view) {
        categoryPath = categoryListSet.getuserOrganization();
        /*tabsAdapter = new TabsAdapter(fragmentManager);
        tabsAdapter.replaceFragment(new DisplayCategoryFragment(), true);*/
        ((NavigationHost) activity).passStringTo(new DisplayCategoryFragment(), categoryPath, true);
        Log.d("catpath",categoryPath);
    }


}
