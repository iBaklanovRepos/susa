package com.example.susa.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.susa.MapActivity;
import com.example.susa.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements Filterable {
    private List<String> searchDataset;
    private List<String> searchDatasetFull;
    private Filter filter;

    public SearchAdapter(List<String> searchDataset) {
        this.searchDataset = searchDataset;
        searchDatasetFull = new ArrayList<>(searchDataset);
        filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<String> filteredList = new ArrayList<>();
                if(constraint == null || constraint.length()==0){
                    filteredList.addAll(searchDatasetFull);
                }else{
                    String filteredPattern = constraint.toString().toLowerCase().trim();
                    for (String s : searchDatasetFull){
                        if(s.toLowerCase().contains(filteredPattern)){
                            filteredList.add(s);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                searchDataset.clear();
                searchDataset.addAll((List)results.values);
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_element, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        String currentItem = searchDataset.get(position);

        holder.textView.setText(currentItem);
    }

    @Override
    public int getItemCount() {
        return searchDataset.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    class SearchViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.elementTV);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapActivity.fillSearchBar(textView.getText());
                    MapActivity.placeMarker(new LatLng(53.2327, 50.2481));
                }
            });
        }
    }
}
