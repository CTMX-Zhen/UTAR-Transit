package my.edu.utar.utartransit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;

import java.util.ArrayList;

import my.edu.utar.utartransit.Domains.itemBuildings;
import my.edu.utar.utartransit.R;

public class BuildingsAdapter extends RecyclerView.Adapter<BuildingsAdapter.ViewHolder> {

    ArrayList<itemBuildings> items;

    public BuildingsAdapter(ArrayList<itemBuildings> items){
        this.items = items;
    }
    @NonNull
    @Override
    public BuildingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_buildings, parent, false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingsAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());


        String imageName = items.get(position).getPic();

        if (imageName != null && !imageName.isEmpty()) {
            int drawableResourceId = holder.itemView.getResources().getIdentifier(
                    imageName, "drawable", holder.itemView.getContext().getPackageName());

            Glide.with(holder.itemView.getContext())
                    .load(drawableResourceId)
                    .transform(new CenterCrop(), new GranularRoundedCorners(40, 40, 40, 40))
                    .into(holder.pic);
        } else {
            // Handle cases where there's no image name (e.g., set a placeholder image)
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTxt;
        ImageView pic;//pic2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.titleTxt);

            pic=itemView.findViewById(R.id.BuildImg);
            //pic2=itemView.findViewById(R.id.LocImg);
        }
    }
}
