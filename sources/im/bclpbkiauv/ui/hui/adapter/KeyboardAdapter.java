package im.bclpbkiauv.ui.hui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import java.util.List;

public class KeyboardAdapter extends BaseAdapter {
    private Context mContext;
    private List<Integer> mNumbers;

    public KeyboardAdapter(List<Integer> mNumbers2, Context mContext2) {
        this.mNumbers = mNumbers2;
        this.mContext = mContext2;
    }

    public int getCount() {
        List<Integer> list = this.mNumbers;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public Integer getItem(int position) {
        return this.mNumbers.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(this.mContext, R.layout.item_password_number, (ViewGroup) null);
            holder = new ViewHolder();
            holder.tvNumber = (TextView) convertView.findViewById(R.id.btn_number);
            holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position < 9 || position == 10) {
            holder.tvNumber.setText(String.valueOf(this.mNumbers.get(position)));
            holder.tvNumber.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        } else if (position == 9) {
            holder.tvNumber.setVisibility(4);
        } else if (position == 11) {
            holder.tvNumber.setVisibility(4);
            holder.ivDelete.setVisibility(0);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView ivDelete;
        TextView tvNumber;

        ViewHolder() {
        }
    }
}
