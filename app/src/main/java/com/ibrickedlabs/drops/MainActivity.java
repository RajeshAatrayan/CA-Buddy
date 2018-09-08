package com.ibrickedlabs.drops;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ibrickedlabs.drops.adapters.AdapterExam;
import com.ibrickedlabs.drops.adapters.AddListener;
import com.ibrickedlabs.drops.adapters.CompleteListner;
import com.ibrickedlabs.drops.adapters.Divider;
import com.ibrickedlabs.drops.adapters.Filter;
import com.ibrickedlabs.drops.adapters.MarkInterface;
import com.ibrickedlabs.drops.adapters.ResetListener;
import com.ibrickedlabs.drops.adapters.SimpleTouchCallBack;
import com.ibrickedlabs.drops.data.Exam;

import com.ibrickedlabs.drops.widgets.BucketRecycleView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    View mEmptyView;
    Toolbar toolbar;
    Button addDrop;
    public static PendingIntent pendingIntent;
    BucketRecycleView recyclerView;
    RealmResults<Exam> realmResults;
    Realm realm;
    private AdapterExam adapterDrops;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private AddListener addListener = new AddListener() {
        @Override
        public void add() {
            addDialogShow();
        }
    };
    private MarkInterface markInterface = new MarkInterface() {
        @Override
        public void onMark(int position) {
            showDialogMark(position);
        }
    };
    private CompleteListner completeListner = new CompleteListner() {
        @Override
        public void onComplete(int position) {
            adapterDrops.markComplete(position);
        }
    };
    private ResetListener resetListener = new ResetListener() {
        @Override
        public void onReset() {
            AppCABuddy.save(MainActivity.this, Filter.NONE);
            loadResults(Filter.NONE);

        }
    };

    private void showDialogMark(int position) {
        DialogMark dialogMark = new DialogMark();
        /**
         * The easiest way to pass teh data from here
         */

        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        dialogMark.setArguments(bundle);
        dialogMark.setCompleteListener(completeListner);
        dialogMark.show(getSupportFragmentManager(), "Mark");
    }


    @Override
    protected void onStart() {
        super.onStart();
        realmResults.addChangeListener(realmChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        realmResults.removeAllChangeListeners();
    }

    /**
     * for data ui updation we must set a listener to listen for the changes
     */
    private RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object o) {
            Log.i(LOG_TAG, "On change was called");
            /**
             * since realmChangeListener gets the latest data from the reslut
             *
             */
            adapterDrops.update(realmResults);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitle("CA Buddy");
        addDrop = (Button) findViewById(R.id.btn_add);
        mEmptyView = (View) findViewById(R.id.emptyDrops);

        /**RecyclerView setup**/
        recyclerView = (BucketRecycleView) findViewById(R.id.rv_drops);

        recyclerView.hideIfEmpty(toolbar);
        recyclerView.showIfEmpty(mEmptyView);
        /**
         * Stting layoutManager here
         */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        setSupportActionBar(toolbar);
        setBackgroundImage();
        /**
         * Instantiating the realm obj;
         */
        realm = Realm.getDefaultInstance();

        int filterOpt = AppCABuddy.load(this);
        loadResults(filterOpt);
        /**
         * RecyclerAdapter we created ie..custom adapter
         */
        adapterDrops = new AdapterExam(this, realmResults, addListener, realm, markInterface, resetListener);
        adapterDrops.setHasStableIds(true);
        recyclerView.setAdapter(adapterDrops);

        addDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialogShow();
            }
        });

        SimpleTouchCallBack simpleTouchCallBack = new SimpleTouchCallBack(adapterDrops);
        ItemTouchHelper helper = new ItemTouchHelper(simpleTouchCallBack);
        helper.attachToRecyclerView(recyclerView);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/ralway.ttf");
        addDrop.setTypeface(typeface);


    }


    private void addDialogShow() {
        DialogAdd dialogAdd = new DialogAdd();
        dialogAdd.show(getSupportFragmentManager(), "Add");
    }

    private void setBackgroundImage() {
        ImageView imageView = (ImageView) findViewById(R.id.iv_background);
        Glide.with(this).load(R.drawable.background).into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = true;
        int filterOption = Filter.NONE;
        switch (item.getItemId()) {
            case R.id.actiob_add:
                addDialogShow();
                break;
            case R.id.action_none:
                filterOption = Filter.NONE;
                break;
            case R.id.action_sort_ascending_date:

                filterOption = Filter.LEAST_TIME_LEFT;
                break;

            case R.id.action_show_complete:

                filterOption = Filter.COMPLETE;
                break;
            case R.id.action_sort_descending_date:

                filterOption = Filter.MOST_TIME_LEFT;
                break;

            case R.id.action_show_incomplete:

                filterOption = Filter.INCOMPLETE;
                break;
            default:
                handled = false;
                break;


        }
        AppCABuddy.save(this, filterOption);
        loadResults(filterOption);
        return handled;
    }

    private void loadResults(int filterOptions) {
        switch (filterOptions) {
            case Filter.NONE:
                realmResults = realm.where(Exam.class).findAllAsync();
                break;
            case Filter.LEAST_TIME_LEFT:
                realmResults = realm.where(Exam.class).sort("when").findAllAsync();
                break;
            case Filter.MOST_TIME_LEFT:
                realmResults = realm.where(Exam.class).sort("when", Sort.DESCENDING).findAllAsync();
                break;

            case Filter.COMPLETE:
                realmResults = realm.where(Exam.class).equalTo("completed", true).findAllAsync();
                break;
            case Filter.INCOMPLETE:
                realmResults = realm.where(Exam.class).equalTo("completed", false).findAllAsync();
                break;
        }
        realmResults.addChangeListener(realmChangeListener);
    }


}
