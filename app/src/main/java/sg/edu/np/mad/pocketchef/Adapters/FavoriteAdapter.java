package sg.edu.np.mad.pocketchef.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class FavoriteAdapter<V extends ViewBinding,T> extends RecyclerView.Adapter<FavoriteViewHolder<V>> {

    private List<T> data;

    // abstract method to get the type of view
    protected abstract int getType(int position);

    // override method to get item view type
    @Override
    public int getItemViewType(int position) {
        return getType(position);
    }

    public FavoriteAdapter(List<T> data) {
        this.data = data;
    }

    // override method to create view holder
    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteViewHolder(getViewBinding(parent));
    }

    // method to get viewbinding
    private V getViewBinding(ViewGroup parent) {
        V bind = null;
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Class<V> clazz = (Class<V>) type.getActualTypeArguments()[0];
        try {
            Method inflate = clazz.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            bind = (V) inflate.invoke(null, LayoutInflater.from(parent.getContext()), parent, false);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return bind;
    }

    // override method to bind view holder
    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder<V> holder, int position) {
        show(holder.bind, position, data.get(position));
    }

    // abstract method to bind data to view holder
    protected abstract void show(V holder, int position, T t);

    // method to set new data to the adapter
    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    // overide method to get item count
    @Override
    public int getItemCount() {
        return data.size();
    }

    // method to set text to a TextView
    public FavoriteAdapter setText(TextView textView, String data){
        textView.setText(data);
        return this;
    }
}

// class for view holder
class FavoriteViewHolder<V extends ViewBinding> extends RecyclerView.ViewHolder {
    public V bind;
    public FavoriteViewHolder(V bind) {
        super(bind.getRoot());
        this.bind = bind;
    }
}
