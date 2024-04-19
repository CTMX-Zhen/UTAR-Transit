package my.edu.utar.utartransit;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnnouncementViewHolder extends RecyclerView.ViewHolder {

    //ImageView imageView;
    TextView titleView,dateView;

    public AnnouncementViewHolder(@NonNull View itemView) {
        super(itemView);
        //imageView = itemView.findViewById(R.id.imageview);
        titleView = itemView.findViewById(R.id.a_title);
        dateView = itemView.findViewById(R.id.a_date);
    }

}
