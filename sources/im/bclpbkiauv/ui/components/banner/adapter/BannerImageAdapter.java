package im.bclpbkiauv.ui.components.banner.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import im.bclpbkiauv.ui.components.banner.holder.BannerImageHolder;
import java.util.List;

public abstract class BannerImageAdapter<T> extends BannerAdapter<T, BannerImageHolder> {
    public BannerImageAdapter(List<T> mData) {
        super(mData);
    }

    public BannerImageHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerImageHolder(imageView);
    }
}
