package sg.edu.np.mad.pocketchef.base;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public class ViewHolder<V extends ViewBinding> extends RecyclerView.ViewHolder {
    protected V bind;
    public ViewHolder(V bind) {
        super(bind.getRoot());
        this.bind = bind;
    }
}
