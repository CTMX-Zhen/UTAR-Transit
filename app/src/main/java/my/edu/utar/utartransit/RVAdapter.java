package my.edu.utar.utartransit;


import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RVAdapter extends RecyclerView.Adapter<AnnouncementViewHolder> {
    Context context;
    List<Item> items;

    public RVAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        return new AnnouncementViewHolder(LayoutInflater.from(context).inflate(R.layout.announcement_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull  AnnouncementViewHolder holder, int position) {
        holder.titleView.setText(items.get(position).getTitle());
        holder.dateView.setText(items.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
