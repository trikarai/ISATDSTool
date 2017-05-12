package com.barapraja.isatds;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.barapraja.isatds.adapter.DrawerAdapter;
import com.barapraja.isatds.config.AppPref;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;

    //Menu List
    //private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    //private RecyclerView.LayoutManager mLayoutManager;

    //Navigation Drawer
    RecyclerView mRecyclerViewDrawer;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapterDrawer;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManagerDrawer;
    DrawerLayout Drawer;
    ActionBarDrawerToggle mDrawerToggle;

    String TITLES[] = {"Home","Hotspot","Check In","Sales","Product","Training","Logout"};
    int ICONS[] = {R.mipmap.ic_launcher_round,R.mipmap.ic_launcher_round,R.mipmap.ic_launcher_round,R.mipmap.ic_launcher_round,R.mipmap.ic_launcher_round,R.mipmap.ic_launcher_round,R.mipmap.ic_launcher_round};

    int PROFILE = R.mipmap.ic_launcher_round;

    //end Navigation Drawer

    //SHARED LOGIN
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor,editor2;
    String userId;
    String name;
    String email;
    //END SHARED LOGIN

    //SHARED RESOURCE
    SharedPreferences resourcePreferences;
    //END SHARED RESOURCE

    ProgressDialog progressDialog;

    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.empty_view) ImageView _emptyview;

    private Boolean isFabOpen = false;
    @BindView(R.id.fab) FloatingActionButton _fab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    private FloatingActionButton fab1,fab2;
    @BindView(R.id.fabtext1) TextView _fabtext1;
    @BindView(R.id.fabtext2) TextView _fabtext2;


    @BindView(R.id.scrollViewMenu)
    View menuScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActivityHelper.initialize(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);

        // Get the app's shared preferences
        sharedPreferences = getApplicationContext().getSharedPreferences(AppPref.LOGIN_PREF, Context.MODE_PRIVATE);
        resourcePreferences = getApplicationContext().getSharedPreferences(AppPref.RESOURCE_PREF, Context.MODE_PRIVATE);

        userId = sharedPreferences.getString("userId", null);
        name = sharedPreferences.getString("name", null);
        email = sharedPreferences.getString("email",null);

        String isLogged = sharedPreferences.getString("logged",null);

        //Toast.makeText(this, "is Logged ? "+ userId +"-"+ name + " - "+ isLogged ,Toast.LENGTH_LONG).show();

        if (!Objects.equals(isLogged, "true")){
            logoutUser();
        }

        menuScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                //int scrollX = menuScroll.getScrollX(); //for horizontalScrollView
                int scrollY = menuScroll.getScrollY(); //for verticalScrollView

                if (scrollY > 0 || scrollY < 0 && _fab.isShown()){
                    _fab.hide();
                }else{
                    _fab.show();
                }
            }
        });

        //RecyclerView for menu list
        /*
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        */
        //end RecyclerView menu list


        //toobar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //swipe container
        //Pull to Refresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //Toast.makeText(getApplicationContext(),"Swipe",Toast.LENGTH_SHORT).show();
                //getCorporateResourceList(userId);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        //RecycleDrawer
        mRecyclerViewDrawer = (RecyclerView) findViewById(R.id.RecyclerViewDrawer);                   // Assigning the RecyclerView Object to the xml View
        mRecyclerViewDrawer.setHasFixedSize(true);                                                    // Letting the system know that the list objects are of fixed size
        mAdapterDrawer = new DrawerAdapter(TITLES,ICONS,name,email,PROFILE);       // Creating the Adapter of  class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerViewDrawer.setAdapter(mAdapterDrawer);                                                        // Setting the adapter to RecyclerView

        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        mRecyclerViewDrawer.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
                if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                    Drawer.closeDrawers();
                    //Toast.makeText(FlowPage.this,"The Item Clicked is: "+recyclerView.getChildAdapterPosition(child),Toast.LENGTH_SHORT).show();

                    switch(recyclerView.getChildAdapterPosition(child)){
                        case 0:
                            //Toast.makeText(HomeActivity.this,"PROFILE",Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
                            //startActivity(new Intent(HomeActivity.this,DebiturListActvity.class));
                            break;
                        case 1:
                            //Toast.makeText(HomeActivity.this,"OPPORTUNITY",Toast.LENGTH_SHORT).show();

                            break;
                        case 2:
                            //Toast.makeText(HomeActivity.this,"OPPORTUNITY",Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(HomeActivity.this, OpportunityPhaseListActivity.class));
                            break;
                        case 3:
                            //Toast.makeText(HomeActivity.this,"APPOINTMENT",Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            //Toast.makeText(HomeActivity.this,"PROSPECT",Toast.LENGTH_SHORT).show();
                            break;
                        case 5:
                            //Toast.makeText(HomeActivity.this,"Retention",Toast.LENGTH_SHORT).show();
                            break;
                        case 6:
                            //Toast.makeText(HomeActivity.this,"Simulation",Toast.LENGTH_SHORT).show();
                            break;
                        case 7:
                            //Toast.makeText(HomeActivity.this,"Log Out",Toast.LENGTH_SHORT).show();
                            logoutUser();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
                return false;
            }
            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        mLayoutManagerDrawer = new LinearLayoutManager(this);       // Creating a layout Manager
        mRecyclerViewDrawer.setLayoutManager(mLayoutManagerDrawer); // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);    // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer, toolbar,R.string.navigation_drawer_open ,R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I don't want anything happened whe drawer is
                // open I am not going to put anything here)
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }
        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        //end navigation drawer

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        //floating action button
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeActivity.this, "Would you like a coffee?", Toast.LENGTH_SHORT).show();
                animateFAB();
                //Intent intent = new Intent(HomeActivity.this,NewProspectActivity.class);
                //startActivity(intent);
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //end floating button


        //logic get corporate Resource List
        //getCorporateResourceList(userId);


    }

    private void animateFAB() {
        if(isFabOpen){
            menuScroll.setClickable(false);
            _fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);

            _fabtext1.startAnimation(fab_close);
            _fabtext2.startAnimation(fab_close);

            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {
            menuScroll.setClickable(true);
            _fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);

            _fabtext1.startAnimation(fab_open);
            _fabtext2.startAnimation(fab_open);

            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }


    private void showDialog() {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    private void hideDialog(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @SuppressLint("CommitPrefEdits")
    private void logoutUser() {
        editor = sharedPreferences.edit();
        editor2 = resourcePreferences.edit();
        editor2.clear();
        editor.clear();
        editor2.commit();
        editor.commit();

        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }


    //menu onClick


    @Override
    public void onRestart(){
        super.onRestart();
        //getDashboardData(userId);
        //Toast.makeText(this,"harusnya nge refresh data",Toast.LENGTH_SHORT).show();
    }

}