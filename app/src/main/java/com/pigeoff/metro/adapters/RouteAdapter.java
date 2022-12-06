package com.pigeoff.metro.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pigeoff.metro.R;
import com.pigeoff.metro.data.Station;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Station> route;

    public RouteAdapter(Context context, ArrayList<Station> route) {
        this.context = context;
        this.route = route;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RouteViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_route, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RouteViewHolder routeHolder = (RouteViewHolder) holder;
        Station station = route.get(position);
        routeHolder.textStationName.setText(station.name);
        routeHolder.textStationLine.setText(station.line);
        routeHolder.textStationLine.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(station.color)));
        routeHolder.textStationLine.setTextColor(Color.parseColor(station.textColor));
    }

    @Override
    public int getItemCount() {
        return route.size();
    }

    private class RouteViewHolder extends RecyclerView.ViewHolder {
        public TextView textStationName;
        public TextView textStationLine;
        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            textStationName = itemView.findViewById(R.id.textStationName);
            textStationLine = itemView.findViewById(R.id.textStationLine);
        }
    }
}
