package com.ibrickedlabs.drops.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ibrickedlabs.drops.AlarmReciever;
import com.ibrickedlabs.drops.AppCABuddy;
import com.ibrickedlabs.drops.R;
import com.ibrickedlabs.drops.data.Exam;

import io.realm.Realm;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

/**
 * Created by RajeshAatrayan on 28-08-2018.
 */

public class AdapterExam extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeListener {
    public static final int COUNT_FOOTER = 1;
    public static final int COUNT_NO_ITEMS = 1;
    public static final int ITEM = 0;
    public static final int NO_ITEM = 1;
    public static final int FOOTER = 2;
    private final ResetListener mResetListener;
    LayoutInflater layoutInflater;
    //we dont have any method available here to instantiate it into a empty  list
    RealmResults<Exam> realmResults;
    Realm realm;
    private int mFilterOptions;
    private AddListener mAddListener;
    private MarkInterface markInterface;
    private Context cont;

    public AdapterExam(Context c, RealmResults<Exam> realmResults, AddListener listener, Realm realm, MarkInterface markInterface, ResetListener resetListener) {
        cont = c;
        layoutInflater = LayoutInflater.from(c);
        update(realmResults);
        mAddListener = listener;
        this.realm = realm;
        this.markInterface = markInterface;
        mResetListener = resetListener;


    }

    /*
    this method by default says there is not special view therefore it returs 0
    but here we are making some changes to embed our footer into it
     */
    @Override
    public int getItemViewType(int position) {
        if (!realmResults.isEmpty()) {
            if (position < realmResults.size()) {
                return ITEM;
            } else {
                return FOOTER;
            }
        } else {
            if (mFilterOptions == Filter.COMPLETE || mFilterOptions == Filter.INCOMPLETE) {
                if (position == 0) {
                    return NO_ITEM;
                } else {
                    return FOOTER;
                }
            } else {
                return ITEM;
            }
        }

    }

    @NonNull
    @Override//WE ARE GETTING int viewType from the above  method so we need to perform if here
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FOOTER) {

            View view = layoutInflater.inflate(R.layout.footer, parent, false);
            FooterHolder footerHolder = new FooterHolder(view);
            return footerHolder;
        } else if (viewType == NO_ITEM) {
            View view = layoutInflater.inflate(R.layout.no_item, parent, false);
            NoItemsHolder noItemsHolder = new NoItemsHolder(view);
            return noItemsHolder;

        } else {
            View view = layoutInflater.inflate(R.layout.row_drop, parent, false);
            DropHolder dropHolder = new DropHolder(view);
            return dropHolder;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*
        Only when u define the views in ViewHolder ie..DropHolder we are able to use the views here to set the text or something so be there to first created the Views in the inner class
         */
        if (holder instanceof DropHolder) {
            DropHolder dropHolder = (DropHolder) holder;
            Exam item = realmResults.get(position);
            dropHolder.setWhat(item.getWhat());
            dropHolder.whenView.setText(DateUtils.getRelativeTimeSpanString(item.getWhen(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));
            dropHolder.setBackground(item.isCompleted());

        }


    }

    @Override
    public int getItemCount() {
        if (!realmResults.isEmpty()) {
            return realmResults.size() + COUNT_FOOTER;
        } else {

            if (mFilterOptions == Filter.LEAST_TIME_LEFT || mFilterOptions == Filter.MOST_TIME_LEFT || mFilterOptions == Filter.NONE) {
                return 0;
            } else {
                return COUNT_NO_ITEMS + COUNT_FOOTER;
            }

        }


    }

    @Override
    public long getItemId(int position) {
        if (position < realmResults.size()) {
            return realmResults.get(position).getAdded();
        }
        return RecyclerView.NO_ID;
    }

    public void update(RealmResults<Exam> realmResults) {
        this.realmResults = realmResults;
        mFilterOptions = AppCABuddy.load(cont);
        /**
         * it will ask adapter to request for the view generation again for the new set of data
         */
        notifyDataSetChanged();
    }

    @Override
    public void onSwipe(int position) {
        if (position < realmResults.size()) {
Exam examObj=realmResults.get(position);
int primarId=(int)examObj.getAdded();
removeAlarm(primarId);
            realm.beginTransaction();
            realmResults.get(position).deleteFromRealm();
            realm.commitTransaction();
            notifyItemRemoved(position);
        }
        resetFilterIfEmpty();

    }

    private void removeAlarm(int primarId) {
        Log.i("AdapterExam","Alarm cancelled");
        AlarmManager alarmManager = (AlarmManager) cont.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), AlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), primarId, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    private void resetFilterIfEmpty() {
        if (realmResults.isEmpty() && (mFilterOptions == Filter.COMPLETE || mFilterOptions == Filter.INCOMPLETE)) {
            mResetListener.onReset();
        }
    }

    public void markComplete(int position) {
        if (position < realmResults.size()) {
            realm.beginTransaction();
            realmResults.get(position).setCompleted(true);
            realm.commitTransaction();
            notifyItemChanged(position);
        }

    }

    public class DropHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView whatView;
        private TextView whenView;
        Context c;
        View itemV;

        public DropHolder(View itemView) {
            super(itemView);
            itemV = itemView;
            c = itemView.getContext();
            whatView = (TextView) itemView.findViewById(R.id.whatView);
            whenView = (TextView) itemView.findViewById(R.id.whenView);
            Typeface typeface = Typeface.createFromAsset(cont.getAssets(), "fonts/ral.ttf");
            whatView.setTypeface(typeface);
            whenView.setTypeface(typeface);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            markInterface.onMark(getAdapterPosition());

        }

        public void setWhat(String what) {
            whatView.setText(what);
        }

        public void setBackground(boolean completed) {
            Drawable drawable;
            if (completed) {
                drawable = ContextCompat.getDrawable(c, R.color.bg_drop_complete);
            } else {
                drawable = ContextCompat.getDrawable(c, R.drawable.bg_row_drop);
            }
            itemV.setBackground(drawable);


        }
    }


    private static class NoItemsHolder extends RecyclerView.ViewHolder {
        public NoItemsHolder(View viewItem) {
            super(viewItem);
        }
    }

    //TO INFALTE FOOTER WE NEED ANYOTHER BUTTON SO WE ARE DOING IT
    public class FooterHolder extends RecyclerView.ViewHolder {
        private Button addDrop;

        public FooterHolder(View itemView) {
            super(itemView);
            addDrop = (Button) itemView.findViewById(R.id.btn_footer);
            addDrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAddListener.add();
                }
            });

        }
    }

}
