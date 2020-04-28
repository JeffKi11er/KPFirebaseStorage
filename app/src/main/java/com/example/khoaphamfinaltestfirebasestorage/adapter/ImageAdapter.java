package com.example.khoaphamfinaltestfirebasestorage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khoaphamfinaltestfirebasestorage.R;
import com.example.khoaphamfinaltestfirebasestorage.item.ItemImage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyHolder> {
    private Context context;
    private List<ItemImage>itemImages;

    public ImageAdapter(Context context, List<ItemImage> itemImages) {
        this.context = context;
        this.itemImages = itemImages;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_image,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.tvName.setText(itemImages.get(position).getImageName());
        Picasso.get().load(itemImages.get(position).getLinkName()).into(holder.imgRes);
    }

    @Override
    public int getItemCount() {
        return itemImages.size();
    }

    public static final class MyHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private ImageView imgRes;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            imgRes = (ImageView) itemView.findViewById(R.id.img_item);
        }
    }
}
