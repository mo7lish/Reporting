package com.example.reporting;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import com.bumptech.glide.Glide;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder> {
    private Context context;
    private List<Uri> images;
    private OnImageRemoveListener listener;

    public interface OnImageRemoveListener {
        void onImageRemove(int position);
    }

    public ImagePreviewAdapter(Context context, List<Uri> images, OnImageRemoveListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri imageUri = images.get(position);
        try {
            Glide.with(context)
                .load(imageUri)
                .centerCrop()
                .into(holder.imagePreview);
        } catch (Exception e) {
            holder.imagePreview.setImageURI(imageUri);
        }
        
        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageRemove(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePreview;
        ImageButton removeButton;

        ViewHolder(View itemView) {
            super(itemView);
            imagePreview = itemView.findViewById(R.id.imagePreview);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}