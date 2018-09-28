package com.hellapinot.thomassmith.metricelltask;


import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LogDataAdapter extends RecyclerView.Adapter<LogDataAdapter.ViewHolder>{

    /*
    Adapter for recycler view. Relays the five most recent entries from database to the database as mCallback is called.
     */

    private static final String TAG = "LogDataAdapter";
    private Context context;
    DataBaseHelper dbh;

    public LogDataAdapter(Context context) {
        this.context = context;
        dbh = DataBaseHelper.getInstance(context);
    }

    @NonNull
    @Override
    public LogDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.log_entry, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LogDataAdapter.ViewHolder holder, int position) {
        Cursor data = dbh.getLogData(dbh.getLastRowLogData()-position);

        if( data != null && data.moveToFirst() ){
            holder.signalStrength.setText(data.getString(1));
            holder.serviceState.setText(data.getString(2));
            holder.location.setText(data.getString(3));
        }
        data.close();
    }

    @Override
    public int getItemCount() {
        if(dbh.getLastRowLogData() > 5){
            return 5;
        } else {
            return dbh.getLastRowLogData();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView signalStrength;
        TextView serviceState;
        TextView location;

        public ViewHolder(View itemView) {
            super(itemView);
            signalStrength = itemView.findViewById(R.id.signal_strength_entry);
            serviceState = itemView.findViewById(R.id.service_state_entry);
            location = itemView.findViewById(R.id.location_entry);
        }
    }
}

