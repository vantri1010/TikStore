package im.bclpbkiauv.ui.components.banner.holder;

import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

public class BannerImageHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;

    public BannerImageHolder(View view) {
        super(view);
        this.imageView = (ImageView) view;
    }
}
