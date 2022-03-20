package com.example.covidtracker.ui.dashboard;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidtracker.R;
import com.example.covidtracker.databinding.ItemSymptomBinding;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class SymptomsAdapter extends RecyclerView.Adapter<SymptomsAdapter.ViewHolder> {
    private List<SymptomsModel> symptomsList;
    private Activity context;
    private LayoutInflater inflater;
    private RatingClickListener ratingClickListener;

    public SymptomsAdapter(Activity context, RatingClickListener ratingClickListener) {
        this.context = context;
        this.symptomsList = new ArrayList<>();
        this.ratingClickListener = ratingClickListener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSymptomBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_symptom, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return symptomsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemSymptomBinding binding;

        ViewHolder(ItemSymptomBinding bind) {
            super(bind.getRoot());
            this.binding = bind;
        }

        void bindData(final int position) {
            binding.txtName.setText(symptomsList.get(position).getName());
            binding.rating.setRating(symptomsList.get(position).getRating());
            binding.rating.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
                @Override
                public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                    ratingClickListener.onRatingClick(symptomsList.get(position), rating);
                }
            });
        }
    }

    public List<SymptomsModel> getList() {
        return this.symptomsList;
    }

    public void addAll(List<SymptomsModel> list) {
        this.symptomsList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearAll() {
        this.symptomsList.clear();
        notifyDataSetChanged();
    }

    public interface RatingClickListener {
        public void onRatingClick(SymptomsModel symptom, float rating);
    }
}