package im.bclpbkiauv.ui.components;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.VideoEditedInfo;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.PhotoAlbumPickerActivity;
import im.bclpbkiauv.ui.PhotoCropActivity;
import im.bclpbkiauv.ui.PhotoPickerActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageUpdater implements NotificationCenter.NotificationCenterDelegate, PhotoCropActivity.PhotoEditActivityDelegate {
    private TLRPC.PhotoSize bigPhoto;
    private boolean clearAfterUpdate;
    private int currentAccount = UserConfig.selectedAccount;
    public String currentPicturePath;
    public ImageUpdaterDelegate delegate;
    private String finalPath;
    private ImageReceiver imageReceiver = new ImageReceiver((View) null);
    public BaseFragment parentFragment;
    private File picturePath = null;
    private boolean searchAvailable = true;
    private TLRPC.PhotoSize smallPhoto;
    private boolean uploadAfterSelect = true;
    public String uploadingImage;

    public interface ImageUpdaterDelegate {
        void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i);

        void didUploadPhoto(TLRPC.InputFile inputFile, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2);

        String getInitialSearchString();

        /* renamed from: im.bclpbkiauv.ui.components.ImageUpdater$ImageUpdaterDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didSelectPhotos(ImageUpdaterDelegate _this, ArrayList arrayList, boolean notify, int scheduleDate) {
            }

            public static String $default$getInitialSearchString(ImageUpdaterDelegate _this) {
                return null;
            }
        }
    }

    public void clear() {
        if (this.uploadingImage != null) {
            this.clearAfterUpdate = true;
            return;
        }
        this.parentFragment = null;
        this.delegate = null;
    }

    public void openMenu(boolean hasAvatar, Runnable onDeleteAvatar) {
        int[] icons;
        CharSequence[] items;
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null) {
            Runnable runnable = onDeleteAvatar;
        } else if (baseFragment.getParentActivity() == null) {
            Runnable runnable2 = onDeleteAvatar;
        } else {
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto));
            int i = 3;
            if (this.searchAvailable) {
                if (hasAvatar) {
                    items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("ChooseFromGallery", R.string.ChooseFromGallery), LocaleController.getString("ChooseFromSearch", R.string.ChooseFromSearch), LocaleController.getString("DeletePhoto", R.string.DeletePhoto)};
                    icons = new int[]{R.drawable.menu_camera, R.drawable.profile_photos, R.drawable.menu_search, R.drawable.chats_delete};
                } else {
                    items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("ChooseFromGallery", R.string.ChooseFromGallery), LocaleController.getString("ChooseFromSearch", R.string.ChooseFromSearch)};
                    icons = new int[]{R.drawable.menu_camera, R.drawable.profile_photos, R.drawable.menu_search};
                }
            } else if (hasAvatar) {
                items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("ChooseFromGallery", R.string.ChooseFromGallery), LocaleController.getString("DeletePhoto", R.string.DeletePhoto)};
                icons = new int[]{R.drawable.menu_camera, R.drawable.profile_photos, R.drawable.chats_delete};
            } else {
                items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("ChooseFromGallery", R.string.ChooseFromGallery)};
                icons = new int[]{R.drawable.menu_camera, R.drawable.profile_photos};
            }
            builder.setItems(items, icons, new DialogInterface.OnClickListener(onDeleteAvatar) {
                private final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ImageUpdater.this.lambda$openMenu$0$ImageUpdater(this.f$1, dialogInterface, i);
                }
            });
            BottomSheet sheet = builder.create();
            this.parentFragment.showDialog(sheet);
            TextView titleView = sheet.getTitleView();
            if (titleView != null) {
                titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                titleView.setTextSize(1, 18.0f);
                titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            }
            if (!this.searchAvailable) {
                i = 2;
            }
            sheet.setItemColor(i, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
        }
    }

    public /* synthetic */ void lambda$openMenu$0$ImageUpdater(Runnable onDeleteAvatar, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            openCamera();
        } else if (i == 1) {
            openGallery();
        } else if (this.searchAvailable && i == 2) {
            openSearch();
        } else if ((this.searchAvailable && i == 3) || i == 2) {
            onDeleteAvatar.run();
        }
    }

    public void setSearchAvailable(boolean value) {
        this.searchAvailable = value;
    }

    public void setUploadAfterSelect(boolean value) {
        this.uploadAfterSelect = value;
    }

    public void openSearch() {
        if (this.parentFragment != null) {
            final HashMap<Object, Object> photos = new HashMap<>();
            final ArrayList<Object> order = new ArrayList<>();
            PhotoPickerActivity fragment = new PhotoPickerActivity(0, (MediaController.AlbumEntry) null, photos, order, new ArrayList(), 1, false, (ChatActivity) null);
            fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                private boolean sendPressed;

                public void selectedPhotosChanged() {
                }

                private void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, boolean notify, int scheduleDate) {
                }

                public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate, boolean blnOriginalImg) {
                    if (!photos.isEmpty() && ImageUpdater.this.delegate != null && !this.sendPressed && !canceled) {
                        this.sendPressed = true;
                        ArrayList<SendMessagesHelper.SendingMediaInfo> media = new ArrayList<>();
                        for (int a = 0; a < order.size(); a++) {
                            Object object = photos.get(order.get(a));
                            SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                            media.add(info);
                            if (object instanceof MediaController.SearchImage) {
                                MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                                if (searchImage.imagePath != null) {
                                    info.path = searchImage.imagePath;
                                } else {
                                    info.searchImage = searchImage;
                                }
                                ArrayList<TLRPC.InputDocument> arrayList = null;
                                info.caption = searchImage.caption != null ? searchImage.caption.toString() : null;
                                info.entities = searchImage.entities;
                                if (!searchImage.stickers.isEmpty()) {
                                    arrayList = new ArrayList<>(searchImage.stickers);
                                }
                                info.masks = arrayList;
                                info.ttl = searchImage.ttl;
                            }
                        }
                        ImageUpdater.this.didSelectPhotos(media);
                    }
                }

                public void onCaptionChanged(CharSequence caption) {
                }
            });
            fragment.setInitialSearchString(this.delegate.getInitialSearchString());
            this.parentFragment.presentFragment(fragment);
        }
    }

    /* access modifiers changed from: private */
    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos) {
        if (!photos.isEmpty()) {
            SendMessagesHelper.SendingMediaInfo info = photos.get(0);
            Bitmap bitmap = null;
            if (info.path != null) {
                bitmap = ImageLoader.loadBitmap(info.path, (Uri) null, 800.0f, 800.0f, true);
            } else if (info.searchImage != null) {
                if (info.searchImage.photo != null) {
                    TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(info.searchImage.photo.sizes, AndroidUtilities.getPhotoSize());
                    if (photoSize != null) {
                        File path = FileLoader.getPathToAttach(photoSize, true);
                        this.finalPath = path.getAbsolutePath();
                        if (!path.exists()) {
                            path = FileLoader.getPathToAttach(photoSize, false);
                            if (!path.exists()) {
                                path = null;
                            }
                        }
                        if (path != null) {
                            bitmap = ImageLoader.loadBitmap(path.getAbsolutePath(), (Uri) null, 800.0f, 800.0f, true);
                        } else {
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
                            this.uploadingImage = FileLoader.getAttachFileName(photoSize.location);
                            this.imageReceiver.setImage(ImageLocation.getForPhoto(photoSize, info.searchImage.photo), (String) null, (Drawable) null, "jpg", (Object) null, 1);
                        }
                    }
                } else if (info.searchImage.imageUrl != null) {
                    File cacheFile = new File(FileLoader.getDirectory(4), Utilities.MD5(info.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(info.searchImage.imageUrl, "jpg"));
                    this.finalPath = cacheFile.getAbsolutePath();
                    if (!cacheFile.exists() || cacheFile.length() == 0) {
                        this.uploadingImage = info.searchImage.imageUrl;
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
                        this.imageReceiver.setImage(info.searchImage.imageUrl, (String) null, (Drawable) null, "jpg", 1);
                    } else {
                        bitmap = ImageLoader.loadBitmap(cacheFile.getAbsolutePath(), (Uri) null, 800.0f, 800.0f, true);
                    }
                } else {
                    bitmap = null;
                }
            }
            processBitmap(bitmap);
            return;
        }
        ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList = photos;
    }

    public void openCamera() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            try {
                if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File image = AndroidUtilities.generatePicturePath();
                    if (image != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            takePictureIntent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "im.bclpbkiauv.messenger.provider", image));
                            takePictureIntent.addFlags(2);
                            takePictureIntent.addFlags(1);
                        } else {
                            takePictureIntent.putExtra("output", Uri.fromFile(image));
                        }
                        this.currentPicturePath = image.getAbsolutePath();
                    }
                    this.parentFragment.startActivityForResult(takePictureIntent, 13);
                    return;
                }
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void openGallery() {
        BaseFragment baseFragment;
        if (this.parentFragment != null) {
            if (Build.VERSION.SDK_INT < 23 || (baseFragment = this.parentFragment) == null || baseFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) == 0) {
                PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(1, false, false, (ChatActivity) null);
                fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate, boolean blnOriginalImg) {
                        ImageUpdater.this.didSelectPhotos(photos);
                        if (ImageUpdater.this.delegate != null) {
                            ImageUpdater.this.delegate.didSelectPhotos(photos, notify, scheduleDate);
                        }
                    }

                    public void startPhotoSelectActivity() {
                        try {
                            Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                            photoPickerIntent.setType("image/*");
                            ImageUpdater.this.parentFragment.startActivityForResult(photoPickerIntent, 14);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                this.parentFragment.presentFragment(fragment);
                return;
            }
            this.parentFragment.getParentActivity().requestPermissions(new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, 4);
        }
    }

    private void startCrop(String path, Uri uri) {
        try {
            LaunchActivity activity = (LaunchActivity) this.parentFragment.getParentActivity();
            if (activity != null) {
                Bundle args = new Bundle();
                if (path != null) {
                    args.putString("photoPath", path);
                } else if (uri != null) {
                    args.putParcelable("photoUri", uri);
                }
                PhotoCropActivity photoCropActivity = new PhotoCropActivity(args);
                photoCropActivity.setDelegate(this);
                activity.lambda$runLinkRequest$28$LaunchActivity(photoCropActivity);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            processBitmap(ImageLoader.loadBitmap(path, uri, 800.0f, 800.0f, true));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 13) {
            PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
            int orientation = 0;
            try {
                int exif = new ExifInterface(this.currentPicturePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                if (exif == 3) {
                    orientation = 180;
                } else if (exif == 6) {
                    orientation = 90;
                } else if (exif == 8) {
                    orientation = 270;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            final ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(new MediaController.PhotoEntry(0, 0, 0, this.currentPicturePath, orientation, false));
            PhotoViewer.getInstance().setIsFcCrop(false);
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, new PhotoViewer.EmptyPhotoViewerProvider() {
                public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate) {
                    String path = null;
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(0);
                    if (photoEntry.imagePath != null) {
                        path = photoEntry.imagePath;
                    } else if (photoEntry.path != null) {
                        path = photoEntry.path;
                    }
                    ImageUpdater.this.processBitmap(ImageLoader.loadBitmap(path, (Uri) null, 800.0f, 800.0f, true));
                }

                public boolean allowCaption() {
                    return false;
                }

                public boolean canScrollAway() {
                    return false;
                }
            }, (ChatActivity) null);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        } else if (requestCode == 14 && data != null && data.getData() != null) {
            startCrop((String) null, data.getData());
        }
    }

    /* access modifiers changed from: private */
    public void processBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
            TLRPC.PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
            this.smallPhoto = scaleAndSaveImage;
            if (scaleAndSaveImage != null) {
                try {
                    Bitmap b = BitmapFactory.decodeFile(FileLoader.getPathToAttach(scaleAndSaveImage, true).getAbsolutePath());
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(b), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50");
                } catch (Throwable th) {
                }
            }
            bitmap.recycle();
            if (this.bigPhoto != null) {
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                this.uploadingImage = FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
                if (this.uploadAfterSelect) {
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
                    FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingImage, false, true, 16777216);
                }
                ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                if (imageUpdaterDelegate != null) {
                    imageUpdaterDelegate.didUploadPhoto((TLRPC.InputFile) null, this.bigPhoto, this.smallPhoto);
                }
            }
        }
    }

    public void didFinishEdit(Bitmap bitmap) {
        processBitmap(bitmap);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.FileDidUpload) {
            if (args[0].equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                if (imageUpdaterDelegate != null) {
                    imageUpdaterDelegate.didUploadPhoto(args[1], this.bigPhoto, this.smallPhoto);
                }
                this.uploadingImage = null;
                if (this.clearAfterUpdate) {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            if (args[0].equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                this.uploadingImage = null;
                if (this.clearAfterUpdate) {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if ((id == NotificationCenter.fileDidLoad || id == NotificationCenter.fileDidFailToLoad || id == NotificationCenter.httpFileDidLoad || id == NotificationCenter.httpFileDidFailedLoad) && args[0].equals(this.uploadingImage)) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidFailedLoad);
            this.uploadingImage = null;
            if (id == NotificationCenter.fileDidLoad || id == NotificationCenter.httpFileDidLoad) {
                processBitmap(ImageLoader.loadBitmap(this.finalPath, (Uri) null, 800.0f, 800.0f, true));
            } else {
                this.imageReceiver.setImageBitmap((Drawable) null);
            }
        }
    }
}
