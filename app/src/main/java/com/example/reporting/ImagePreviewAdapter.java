package com.example.reporting;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder> {
    private List<Uri> images;
    private Context context;
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
        View view = LayoutInflater.from(context).inflate(R.layout.image_preview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri imageUri = images.get(position);
        Glide.with(context)
            .load(imageUri)
            .centerCrop()
            .into(holder.imageView);

        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageRemove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton removeButton;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.previewImageView);
            removeButton = itemView.findViewById(R.id.removeImageButton);
        }
    }
}