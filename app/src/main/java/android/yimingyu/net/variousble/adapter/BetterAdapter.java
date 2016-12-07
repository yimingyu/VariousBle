package android.yimingyu.net.variousble.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.yimingyu.net.blesrv.util.LogUtil;

/**
 * Author：Mingyu Yi on 2016/6/13 17:13
 * Email：461072496@qq.com
 */
public abstract class BetterAdapter extends BaseAdapter{
    public Context context;

    public BetterAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            ViewHolder viewHolder= getViewHolder();
            view = View.inflate(context, viewHolder.getHolderLayout(), null);
            viewHolder.findView(view);
            //view虽然目前看起和viewHolder一模一样，但是view有还可能用Inflate填充其他view
            view.setTag(viewHolder);
        }
        //这里改变的是viewHolder，不是view，所以没效果
        renderView(position,view);
        return view;
    }
    public abstract ViewHolder getViewHolder();
    public abstract void renderView(int index, View view);
}
