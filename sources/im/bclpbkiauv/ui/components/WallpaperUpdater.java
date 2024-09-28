package im.bclpbkiauv.ui.components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.PhotoAlbumPickerActivity;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class WallpaperUpdater {
    private String currentPicturePath;
    private File currentWallpaperPath;
    private WallpaperUpdaterDelegate delegate;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    private BaseFragment parentFragment;
    private File picturePath = null;

    public interface WallpaperUpdaterDelegate {
        void didSelectWallpaper(File file, Bitmap bitmap, boolean z);

        void needOpenColorPicker();
    }

    public WallpaperUpdater(Activity activity, BaseFragment fragment, WallpaperUpdaterDelegate wallpaperUpdaterDelegate) {
        this.parentActivity = activity;
        this.parentFragment = fragment;
        this.delegate = wallpaperUpdaterDelegate;
    }

    public void showAlert(boolean fromTheme) {
        int[] icons;
        CharSequence[] items;
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
        builder.setTitle(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto));
        if (fromTheme) {
            items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("SelectFromGallery", R.string.SelectFromGallery), LocaleController.getString("SelectColor", R.string.SelectColor), LocaleController.getString("Default", R.string.Default)};
            icons = null;
        } else {
            items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("SelectFromGallery", R.string.SelectFromGallery)};
            icons = new int[]{R.drawable.menu_camera, R.drawable.profile_photos};
        }
        builder.setItems(items, icons, new DialogInterface.OnClickListener(fromTheme) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                WallpaperUpdater.this.lambda$showAlert$0$WallpaperUpdater(this.f$1, dialogInterface, i);
            }
        });
        builder.show();
    }

    public /* synthetic */ void lambda$showAlert$0$WallpaperUpdater(boolean fromTheme, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                File image = AndroidUtilities.generatePicturePath();
                if (image != null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        takePictureIntent.putExtra("output", FileProvider.getUriForFile(this.parentActivity, "im.bclpbkiauv.messenger.provider", image));
                        takePictureIntent.addFlags(2);
                        takePictureIntent.addFlags(1);
                    } else {
                        takePictureIntent.putExtra("output", Uri.fromFile(image));
                    }
                    this.currentPicturePath = image.getAbsolutePath();
                }
                this.parentActivity.startActivityForResult(takePictureIntent, 10);
            } catch (Exception e) {
                try {
                    FileLog.e((Throwable) e);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
        } else if (i == 1) {
            openGallery();
        } else if (!fromTheme) {
        } else {
            if (i == 2) {
                this.delegate.needOpenColorPicker();
            } else if (i == 3) {
                this.delegate.didSelectWallpaper((File) null, (Bitmap) null, false);
            }
        }
    }

    public void openGallery() {
        if (this.parentFragment == null) {
            Intent photoPickerIntent = new Intent("android.intent.action.PICK");
            photoPickerIntent.setType("image/*");
            this.parentActivity.startActivityForResult(photoPickerIntent, 11);
        } else if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) == 0) {
            PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(2, false, false, (ChatActivity) null);
            fragment.setAllowSearchImages(false);
            fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
                public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate, boolean blnOriginalImg) {
                    WallpaperUpdater.this.didSelectPhotos(photos);
                }

                public void startPhotoSelectActivity() {
                    try {
                        Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                        photoPickerIntent.setType("image/*");
                        WallpaperUpdater.this.parentActivity.startActivityForResult(photoPickerIntent, 11);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            });
            this.parentFragment.presentFragment(fragment);
        } else {
            this.parentFragment.getParentActivity().requestPermissions(new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, 4);
        }
    }

    /* access modifiers changed from: private */
    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos) {
        try {
            if (!photos.isEmpty()) {
                SendMessagesHelper.SendingMediaInfo info = photos.get(0);
                if (info.path != null) {
                    File directory = FileLoader.getDirectory(4);
                    this.currentWallpaperPath = new File(directory, Utilities.random.nextInt() + ".jpg");
                    Point screenSize = AndroidUtilities.getRealScreenSize();
                    Bitmap bitmap = ImageLoader.loadBitmap(info.path, (Uri) null, (float) screenSize.x, (float) screenSize.y, true);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
                    this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap, true);
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void cleanup() {
    }

    public String getCurrentPicturePath() {
        return this.currentPicturePath;
    }

    public void setCurrentPicturePath(String value) {
        this.currentPicturePath = value;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 10) {
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            FileOutputStream stream = null;
            try {
                File directory = FileLoader.getDirectory(4);
                this.currentWallpaperPath = new File(directory, Utilities.random.nextInt() + ".jpg");
                Point screenSize = AndroidUtilities.getRealScreenSize();
                Bitmap bitmap = ImageLoader.loadBitmap(this.currentPicturePath, (Uri) null, (float) screenSize.x, (float) screenSize.y, true);
                stream = new FileOutputStream(this.currentWallpaperPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap, false);
                try {
                    stream.close();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
                if (stream != null) {
                    stream.close();
                }
            } catch (Throwable th) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                throw th;
            }
            this.currentPicturePath = null;
        } else if (requestCode == 11 && data != null && data.getData() != null) {
            try {
                File directory2 = FileLoader.getDirectory(4);
                this.currentWallpaperPath = new File(directory2, Utilities.random.nextInt() + ".jpg");
                Point screenSize2 = AndroidUtilities.getRealScreenSize();
                Bitmap bitmap2 = ImageLoader.loadBitmap((String) null, data.getData(), (float) screenSize2.x, (float) screenSize2.y, true);
                bitmap2.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
                this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap2, false);
            } catch (Exception e4) {
                FileLog.e((Throwable) e4);
            }
        }
    }
}
