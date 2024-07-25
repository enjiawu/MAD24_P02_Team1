package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.InstructionsResponse;
import sg.edu.np.mad.pocketchef.Models.Notification;
import sg.edu.np.mad.pocketchef.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationsViewHolder> {
    final Context context;
    final List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }
    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_community_notifications, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        Notification notification = notifications.get(position); // Get the notification from the list

        // Setting up values
        holder.title.setText(notification.getTitle());
        holder.date.setText(notification.formatDate());
        holder.description.setText(notification.getMessage());

        // Implementing notifications
        holder.notifRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        NotificationAdapter adapter = new NotificationAdapter(context, notifications);
        holder.notifRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}

class NotificationsViewHolder extends RecyclerView.ViewHolder {
    TextView title, date, description;
    RecyclerView notifRecyclerView;

    public NotificationsViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.notification_title);
        date = itemView.findViewById(R.id.notification_date);
        description = itemView.findViewById(R.id.notification_description);
        notifRecyclerView = itemView.findViewById(R.id.notification_recycler_view);
    }
}
