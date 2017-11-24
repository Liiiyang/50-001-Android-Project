package com.example.liyang.androidtouristapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.checked;

/**
 * Created by Li Yang on 24/11/2017.
 */

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // A menu item view type.
    private static final int ATTRACTION_VIEW_TYPE = 0;

    // The Native Express ad view type.
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;

    // An Activity's Context.
    private final Context mContext;

    // The list of Attractions
    private final ArrayList<Pojo> mRecyclerViewItems;


    public RecyclerViewAdapter(Context context, ArrayList<Pojo> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;
    }

    /**
     * The {@link AttractionViewHolder} class.
     * Provides a reference to each view in the menu item view.
     */
    public class AttractionViewHolder extends RecyclerView.ViewHolder {
        private TextView attractionName;
        private TextView attractionaddress;
        private ImageView attractionImage;
        CheckBox checkBox;

        AttractionViewHolder(View view) {
            super(view);
            attractionImage = (ImageView) view.findViewById(R.id.imageview_attraction);
            attractionName = (TextView) view.findViewById(R.id.attraction_name);
            attractionaddress = (TextView) view.findViewById(R.id.attraction_address);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    /**
     * Determines the view type for the given position.
     */
    /**
     * Creates a new view for a menu item view or a Native Express ad view
     * based on the viewType. This method is invoked by the layout manager.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ATTRACTION_VIEW_TYPE:

            default:
                View attractionLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.card, viewGroup, false);
                return new AttractionViewHolder(attractionLayoutView);
        }

    }

    /**
     *  Replaces the content in the views that make up the menu item view and the
     *  Native Express ad view. This method is invoked by the layout manager.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ATTRACTION_VIEW_TYPE:
            default:
                final AttractionViewHolder attractionHolder = (AttractionViewHolder) holder;
                Pojo attractionitem = (Pojo) mRecyclerViewItems.get(position);

                // Get the attraction item image resource ID.
                String imageName = attractionitem.getImageName();
                int imageResID = mContext.getResources().getIdentifier(imageName, "drawable",
                        mContext.getPackageName());


                // Add the Attraction details to the card.
                attractionHolder.attractionImage.setImageResource(imageResID);
                attractionHolder.attractionName.setText(attractionitem.getName());
                attractionHolder.attractionaddress.setText(attractionitem.getAddress());

                //Implementing CheckBox to create Nodes for Algorithm
                attractionHolder.checkBox.setOnCheckedChangeListener(null);
                //if true, your checkbox will be selected, else unselected
                attractionHolder.checkBox.setChecked(attractionitem.isSelected());

                attractionHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mRecyclerViewItems.get(attractionHolder.getAdapterPosition()).setSelected(isChecked);
                    }
                });
        }
    }

}