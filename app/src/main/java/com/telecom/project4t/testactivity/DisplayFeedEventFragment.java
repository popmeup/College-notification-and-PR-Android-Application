package com.telecom.project4t.testactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.button.MaterialButton;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class DisplayFeedEventFragment extends Fragment {

    Bundle args;
    ViewPager viewPager;
    TabLayout tabs;
    Toolbar toolbar;
    ImageView backdrop;
    ImageView profileImagePreview;
    CollapsingToolbarLayout collapsingToolbarLayout;
    MaterialButton floatingActionButton;
    Menu menu;
    Snackbar createEventSnackbar;
    Bitmap bitmapProfile;
    RoundedBitmapDrawable roundDrawable;
    BitmapDrawable drawableProfile;
    TabsAdapter tabsAdapter;

    AppBarLayout appbarLayout;
    CoordinatorLayout appbarandtabs;
    CoordinatorLayout.LayoutParams createEventSnackbarParams,
            fabParams,
            profileImagePreviewParams;
    ConstraintLayout profilePreviewLayout,
            profilePreviewBar;
    ConstraintSet set;

    UserProfile userProfile;
    String userStatus = "user";
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    DatabaseReference myProfileDatabaseReference;
    StorageReference storageReference;

    Bitmap bitmap, largeBitmap;
    Uri imagePath;
    ByteArrayOutputStream imageBytesSteam, largeImageBytesSteam;
    String stringData = " ";

    boolean accountTabsFlags;
    int tabsCount = 4;
    int[] tabsIcons = {
            R.drawable.ic_sort_48px,
            R.drawable.ic_dns_48px,
            R.drawable.ic_stars_48px,
            R.drawable.ic_account_circle_48px
    };
    int[] tabsTitle = {
            R.string.event_tab,
            R.string.category_tab,
            R.string.starred_tab,
            R.string.account_tab
    };
    int searchMenuID = 101;
    int statMenuID = 102;
    int settingMenuID = 103;
    int logoutMenuID = 104;

    int snackbarMargin, tabHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Log.d("lifechk disFedEvn","DisplayFeedEventFragment onCreate");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        args = getArguments();
        if (args != null) {
            stringData = args.getString("passYeah");
            this.getArguments().remove("passYeah");
        }
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        myProfileDatabaseReference = firebaseDatabase.getReference().child("Profile").child(firebaseAuth.getUid());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appbar_tabs, container, false);

        snackbarMargin = ((NavigationHost) getActivity()).dptopx(8);
        tabHeight = ((NavigationHost) getActivity()).dptopx(48);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabs = (TabLayout) view.findViewById(R.id.tabs);
        toolbar = view.findViewById(R.id.toolbar);
        backdrop = (ImageView) view.findViewById(R.id.toolbarBackdrop);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolCollapsing);
        appbarandtabs = (CoordinatorLayout) view.findViewById(R.id.appbarandtabs);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbarLayout);
        profilePreviewLayout = (ConstraintLayout) view.findViewById(R.id.profilePreviewLayout);
        profilePreviewBar = (ConstraintLayout) view.findViewById(R.id.profilePreviewBar);
        set = new ConstraintSet();
        floatingActionButton = new MaterialButton(getActivity());
        profileImagePreview = new ImageView(getActivity());
        profileImagePreview.setId(View.generateViewId());
        profileImagePreviewParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fabParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setBackdrop();
        setProfileImage();
        setEventFAB();
        receiveUserProfile();
        setupViewPager(viewPager);
        setupTabs();
        setupToolbar();
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black, getContext().getTheme()));
        collapsingToolbarLayout.setTitle(getString(R.string.event_tab));
        collapsingToolbarLayout.setExpandedTitleTextColor(getResources().getColorStateList(R.color.colorAccent, getContext().getTheme()));

        if (stringData == "createEvent") {
            showSnackbar("Event Created");
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tabs) {
                collapsingToolbarLayout.setTitle(setTabsTitle(tabs.getPosition()));
                //collapsingToolbarLayout.setExpandedTitleTextColor(R.color.white);
                //backdrop.setImageDrawable(setToolbarBackdrop(tabs.getPosition()));
                backdrop.setBackgroundColor(getResources().getColor(R.color.superTransparent, getContext().getTheme()));

                if (tabs.getPosition() == 3) {
                    if (userStatus.equals("admin")) {
                        appbarandtabs.removeView(floatingActionButton);
                    }
                    accountTabsFlags = true;
                    appbarandtabs.addView(profileImagePreview, profileImagePreviewParams);
                }
                if (tabs.getPosition() != 3 && accountTabsFlags == true) {
                    if (userStatus.equals("admin")) {
                        appbarandtabs.addView(floatingActionButton, fabParams);
                    }
                    accountTabsFlags = false;
                    appbarandtabs.removeView(profileImagePreview);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tabs) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tabs) {

            }
        });

        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    //  Collapsed
                    appbarandtabs.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight, getContext().getTheme()));
                    appbarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight, getContext().getTheme()));
                    tabs.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight, getContext().getTheme()));
                    /*if (tabs.getSelectedTabPosition() == 3) {
                        profilePreviewBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary, getContext().getTheme()));
                    }*/
                }
                else {
                    //Expanded
                    appbarandtabs.setBackground(getResources().getDrawable(R.drawable.app_background_gradient_yellow, getContext().getTheme()));
                    appbarLayout.setBackgroundColor(getResources().getColor(R.color.superTransparent, getContext().getTheme()));
                    tabs.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight, getContext().getTheme()));
                    /*if (tabs.getSelectedTabPosition() == 3) {
                        profilePreviewBar.setBackgroundColor(getResources().getColor(R.color.superTransparent, getContext().getTheme()));
                    }*/
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationHost) getActivity()).navigateTo(new CreateEventFragment(), true);
            }
        });

        Log.d("lifechk disFedEvn","DisplayFeedEventFragment onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("lifechk disFedEvn","DisplayFeedEventFragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifechk disFedEvn","DisplayFeedEventFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("lifechk disFedEvn","DisplayFeedEventFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("lifechk disFedEvn","DisplayFeedEventFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifechk disFedEvn","DisplayFeedEventFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("lifechk disFedEvn","DisplayFeedEventFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("lifechk disFedEvn","DisplayFeedEventFragment onDestroy");
    }

    public void setBackdrop() {
        //backdrop.setAlpha(0.75f);
        backdrop.setBackgroundColor(getResources().getColor(R.color.superTransparent, getContext().getTheme()));
        //backdrop.setImageDrawable(getResources().getDrawable(R.drawable.home, getContext().getTheme()));
    }

    public void setProfileImage() {
        profileImagePreviewParams.anchorGravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        profileImagePreviewParams.setAnchorId(R.id.toolCollapsing);
        profileImagePreviewParams.width = ((NavigationHost) getActivity()).dptopx(120);
        profileImagePreviewParams.height = ((NavigationHost) getActivity()).dptopx(120);
        profileImagePreview.setElevation(((NavigationHost) getActivity()).dptopx(8));
        profileImagePreview.setLayoutParams(profileImagePreviewParams);
        profileImagePreview.setImageDrawable(getResources().getDrawable(R.drawable.reg_profile_image, getContext().getTheme()));
    }

    public void setEventFAB() {
        fabParams.setAnchorId(R.id.toolCollapsing);
        fabParams.anchorGravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        fabParams.height = (((NavigationHost) getActivity()).dptopx(56));
        floatingActionButton.setText("+");
        floatingActionButton.setTextSize(28);
        floatingActionButton.setElevation(((NavigationHost) getActivity()).dptopx(8));
        floatingActionButton.setCornerRadius(((NavigationHost) getActivity()).dptopx(28));
        floatingActionButton.setLayoutParams(fabParams);

        /*floatingActionButton = new FloatingActionButton(getActivity());
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp, getContext().getTheme()));
        floatingActionButton.setElevation(8f);
        fabParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fabParams.setAnchorId(R.id.appbarLayout);
        fabParams.anchorGravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        floatingActionButton.setLayoutParams(fabParams);*/
    }

    public void receiveUserProfile() {
        try {
            final File profileImage = File.createTempFile("Images", "bmp");
            largeImageBytesSteam = new ByteArrayOutputStream();
            imageBytesSteam = new ByteArrayOutputStream();
            storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getFile(profileImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    largeBitmap = BitmapFactory.decodeFile(profileImage.getAbsolutePath());
                    largeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, largeImageBytesSteam);
                    bitmap = ((NavigationHost) getActivity()).decodeSampledBitmapFromResource(largeImageBytesSteam.toByteArray(), 480, 480);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, imageBytesSteam);
                    roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    roundDrawable.setCircular(true);
                    profileImagePreview.setImageDrawable(roundDrawable);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        myProfileDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);
                if (String.valueOf(userProfile.getuserStatus()).equals("admin")) {
                    userStatus = "admin";
                    if (tabs.getSelectedTabPosition() != 3) {
                        appbarandtabs.removeView(floatingActionButton);
                        appbarandtabs.addView(floatingActionButton, fabParams);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        tabsAdapter = new TabsAdapter(getChildFragmentManager());
        tabsAdapter.addFragment(new EventFragment());
        tabsAdapter.addFragment(new CategoryFragment());
        tabsAdapter.addFragment(new StarredFragment());
        tabsAdapter.addFragment(new AccountFragment());
        viewPager.setAdapter(tabsAdapter);
    }

    public void setupTabs() {
        tabs.setupWithViewPager(viewPager);
        tabs.setTabMode(tabs.MODE_FIXED);
        tabs.setTabGravity(tabs.GRAVITY_FILL);
        for (int i = 0; i < tabsCount; i++) {
            tabs.newTab();
            tabs.getTabAt(i).setIcon(tabsIcons[i]);
        }
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
    }

    public String setTabsTitle(int position) {
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.home_tab);
        for (int i = 0; i < tabsCount; i++) {
            if (position == i) {
                return getString(tabsTitle[i]);
            }
        }

        return null;
    }

    public Drawable setToolbarBackdrop (int position) {
        if (position == 0) {
            return getResources().getDrawable(R.drawable.home, getContext().getTheme());
        }
        if (position == 1) {
            return getResources().getDrawable(R.drawable.category, getContext().getTheme());
        }
        if (position == 2) {
            return getResources().getDrawable(R.drawable.star, getContext().getTheme());
        }
        if (position == 3) {
            return getResources().getDrawable(R.drawable.account, getContext().getTheme());
        }

        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch(menu.getItemId()) {
            case 101:

                break;
            case 102: // Stat Bottom Sheet
                BeaconStatBottomSheet beaconStatBottomSheet = new BeaconStatBottomSheet();

                beaconStatBottomSheet.show(getActivity().getSupportFragmentManager(), "beaconStatBottomSheet");
                break;
            case 103:

                break;
            case 104:
                logout();
                break;
        }
        return super.onOptionsItemSelected(menu);
    }

    private void logout(){
        firebaseAuth.signOut();
        showSnackbar("Signed Out");

        boolean isChecked=false;
        saveLoginState(isChecked);

        ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false);
    }

    public void saveLoginState(Boolean isChecked) {
        SharedPreferences aSharedPreferenes = getActivity().getSharedPreferences(
                "LoginState", Context.MODE_PRIVATE);
        SharedPreferences.Editor aSharedPreferenesEdit = aSharedPreferenes
                .edit();
        aSharedPreferenesEdit.putBoolean("State" ,isChecked);
        aSharedPreferenesEdit.commit();
    }

    public void showSnackbar(String showString) {
        createEventSnackbar = Snackbar.make(appbarandtabs, showString, Snackbar.LENGTH_SHORT);
        createEventSnackbarParams = (CoordinatorLayout.LayoutParams) createEventSnackbar.getView().getLayoutParams();
        createEventSnackbarParams.bottomMargin = (tabHeight + snackbarMargin);
        Log.d("SnackbarMonitoring", "Created Event Snackbar : " + tabHeight);
        createEventSnackbarParams.leftMargin = (snackbarMargin);
        createEventSnackbarParams.rightMargin = (snackbarMargin);
        createEventSnackbar.getView().setLayoutParams(createEventSnackbarParams);
        createEventSnackbar.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.menu_bar, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

        menu.add(Menu.NONE,searchMenuID,1,R.string.search_menu).setIcon(R.drawable.ic_search_48px)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE,statMenuID,2,R.string.stat_menu).setIcon(R.drawable.ic_filter_list_48px)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE,settingMenuID,3,R.string.setting_menu).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(Menu.NONE,logoutMenuID,4,R.string.logout_menu).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

}
