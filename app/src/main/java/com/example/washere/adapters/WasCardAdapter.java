package com.example.washere.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washere.R;
import com.example.washere.models.Was;

import java.util.ArrayList;
import java.util.List;

public class WasCardAdapter extends RecyclerView.Adapter<WasCardAdapter.WasCardHolder> {
    private List<Was> wasList = new ArrayList<>();
    private OnMarkerListener onMarkerListener;

    public WasCardAdapter(OnMarkerListener onMarkerListener) {
        this.onMarkerListener = onMarkerListener;
    }

    public interface OnMarkerListener {
        void onWasCardClick(int position);
    }

    @NonNull
    @Override
    public WasCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.was_item_view, parent, false);

        return new WasCardHolder(itemView,onMarkerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WasCardHolder holder, int position) {
        Was currentWas = wasList.get(position);
        //Bunlar hep geçici hehe

        //
        holder.textViewUploaderName.setText(currentWas.getUploaderName());
        holder.textViewUploadTime.setText(currentWas.getUploadTime());
        holder.textViewUploadDate.setText(currentWas.getUploadDate());

    }

    @Override
    public int getItemCount() {
        return wasList.size();
    }

    public void setWasList(List<Was> wasList) {
        this.wasList = wasList;
        notifyDataSetChanged();
    }

    class WasCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageViewUserAvatar,imageViewDeleteWas;
        private ProgressBar progressBarRating;
        private TextView textViewUploaderName, textViewUploadDate, textViewUploadTime;
        private CardView cardViewWasCard;
        private OnMarkerListener onMarkerListener;

        public WasCardHolder(@NonNull View itemView, OnMarkerListener onMarkerListener) {
            super(itemView);
            initViews();
            this.onMarkerListener = onMarkerListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMarkerListener.onWasCardClick(getAdapterPosition());
        }

        private void initViews() {
            imageViewDeleteWas = itemView.findViewById(R.id.imageViewDeleteWas);
            imageViewUserAvatar = itemView.findViewById(R.id.imageViewUserAvatar);
            progressBarRating = itemView.findViewById(R.id.progressBarRating);
            textViewUploadTime = itemView.findViewById(R.id.textViewUploadTime);
            textViewUploadDate = itemView.findViewById(R.id.textViewUploadDate);
            textViewUploaderName = itemView.findViewById(R.id.textViewUserName);
            cardViewWasCard = itemView.findViewById(R.id.cardViewWasCard);
        }


    }
}
