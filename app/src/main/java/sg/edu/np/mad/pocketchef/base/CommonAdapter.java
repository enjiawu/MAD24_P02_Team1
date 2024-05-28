package sg.edu.np.mad.pocketchef.base;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class CommonAdapter<V extends ViewBinding,T> extends RecyclerView.Adapter<ViewHolder<V>> {

    private List<T> data;
    protected abstract int getType(int position);
    @Override
    public int getItemViewType(int position) {
        return getType(position);
    }

    public CommonAdapter(List<T> data) {
        this.data = data;
    }
    @NonNull
    @Override
    public ViewHolder<V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getViewBinding(parent));
    }

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


    @Override
    public void onBindViewHolder(@NonNull ViewHolder<V> holder, int position) {
        show(holder.bind, position, data.get(position));
    }

    protected abstract void show(V holder, int position, T t);


    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public CommonAdapter setText(TextView textView, String data){
        textView.setText(data);
        return this;
    }
}
