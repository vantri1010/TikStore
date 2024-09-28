package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Looper;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.Bitmaps;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarPopupWindow;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.StickerMasksView;
import im.bclpbkiauv.ui.components.paint.Brush;
import im.bclpbkiauv.ui.components.paint.Painting;
import im.bclpbkiauv.ui.components.paint.PhotoFace;
import im.bclpbkiauv.ui.components.paint.RenderView;
import im.bclpbkiauv.ui.components.paint.Swatch;
import im.bclpbkiauv.ui.components.paint.UndoStore;
import im.bclpbkiauv.ui.components.paint.views.ColorPicker;
import im.bclpbkiauv.ui.components.paint.views.EditTextOutline;
import im.bclpbkiauv.ui.components.paint.views.EntitiesContainerView;
import im.bclpbkiauv.ui.components.paint.views.EntityView;
import im.bclpbkiauv.ui.components.paint.views.StickerView;
import im.bclpbkiauv.ui.components.paint.views.TextPaintView;
import java.util.ArrayList;

public class PhotoPaintView extends FrameLayout implements EntityView.EntityViewDelegate {
    private static final int gallery_menu_done = 1;
    private Bitmap bitmapToEdit;
    private Brush[] brushes = {new Brush.Radial(), new Brush.Elliptical(), new Brush.Neon()};
    private TextView cancelTextView;
    /* access modifiers changed from: private */
    public ColorPicker colorPicker;
    private Animator colorPickerAnimator;
    int currentBrush;
    /* access modifiers changed from: private */
    public EntityView currentEntityView;
    private FrameLayout curtainView;
    /* access modifiers changed from: private */
    public FrameLayout dimView;
    private TextView doneTextView;
    private Point editedTextPosition;
    private float editedTextRotation;
    private float editedTextScale;
    private boolean editingText;
    private EntitiesContainerView entitiesView;
    private ArrayList<PhotoFace> faces;
    private String initialText;
    private int orientation;
    private ImageView paintButton;
    private Size paintingSize;
    private boolean pickingSticker;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private DispatchQueue queue = new DispatchQueue("Paint");
    private RenderView renderView;
    private boolean selectedStroke = true;
    private FrameLayout selectionContainerView;
    /* access modifiers changed from: private */
    public StickerMasksView stickersView;
    /* access modifiers changed from: private */
    public FrameLayout textDimView;
    private FrameLayout toolsView;
    /* access modifiers changed from: private */
    public UndoStore undoStore;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PhotoPaintView(Context context, Bitmap bitmap, int rotation) {
        super(context);
        Context context2 = context;
        Bitmap bitmap2 = bitmap;
        this.bitmapToEdit = bitmap2;
        this.orientation = rotation;
        UndoStore undoStore2 = new UndoStore();
        this.undoStore = undoStore2;
        undoStore2.setDelegate(new UndoStore.UndoStoreDelegate() {
            public final void historyChanged() {
                PhotoPaintView.this.lambda$new$0$PhotoPaintView();
            }
        });
        FrameLayout frameLayout = new FrameLayout(context2);
        this.curtainView = frameLayout;
        frameLayout.setBackgroundColor(-16777216);
        this.curtainView.setVisibility(4);
        addView(this.curtainView);
        RenderView renderView2 = new RenderView(context2, new Painting(getPaintingSize()), bitmap2, this.orientation);
        this.renderView = renderView2;
        renderView2.setDelegate(new RenderView.RenderViewDelegate() {
            public void onBeganDrawing() {
                if (PhotoPaintView.this.currentEntityView != null) {
                    boolean unused = PhotoPaintView.this.selectEntity((EntityView) null);
                }
            }

            public void onFinishedDrawing(boolean moved) {
                PhotoPaintView.this.colorPicker.setUndoEnabled(PhotoPaintView.this.undoStore.canUndo());
            }

            public boolean shouldDraw() {
                boolean draw = PhotoPaintView.this.currentEntityView == null;
                if (!draw) {
                    boolean unused = PhotoPaintView.this.selectEntity((EntityView) null);
                }
                return draw;
            }
        });
        this.renderView.setUndoStore(this.undoStore);
        this.renderView.setQueue(this.queue);
        this.renderView.setVisibility(4);
        this.renderView.setBrush(this.brushes[0]);
        addView(this.renderView, LayoutHelper.createFrame(-1, -1, 51));
        EntitiesContainerView entitiesContainerView = new EntitiesContainerView(context2, new EntitiesContainerView.EntitiesContainerViewDelegate() {
            public boolean shouldReceiveTouches() {
                return PhotoPaintView.this.textDimView.getVisibility() != 0;
            }

            public EntityView onSelectedEntityRequest() {
                return PhotoPaintView.this.currentEntityView;
            }

            public void onEntityDeselect() {
                boolean unused = PhotoPaintView.this.selectEntity((EntityView) null);
            }
        });
        this.entitiesView = entitiesContainerView;
        entitiesContainerView.setPivotX(0.0f);
        this.entitiesView.setPivotY(0.0f);
        addView(this.entitiesView);
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.dimView = frameLayout2;
        frameLayout2.setAlpha(0.0f);
        this.dimView.setBackgroundColor(1711276032);
        this.dimView.setVisibility(8);
        addView(this.dimView);
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.textDimView = frameLayout3;
        frameLayout3.setAlpha(0.0f);
        this.textDimView.setBackgroundColor(1711276032);
        this.textDimView.setVisibility(8);
        this.textDimView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoPaintView.this.lambda$new$1$PhotoPaintView(view);
            }
        });
        AnonymousClass3 r4 = new FrameLayout(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                return false;
            }
        };
        this.selectionContainerView = r4;
        addView(r4);
        ColorPicker colorPicker2 = new ColorPicker(context2);
        this.colorPicker = colorPicker2;
        addView(colorPicker2);
        this.colorPicker.setDelegate(new ColorPicker.ColorPickerDelegate() {
            public void onBeganColorPicking() {
                if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView)) {
                    PhotoPaintView.this.setDimVisibility(true);
                }
            }

            public void onColorValueChanged() {
                PhotoPaintView photoPaintView = PhotoPaintView.this;
                photoPaintView.setCurrentSwatch(photoPaintView.colorPicker.getSwatch(), false);
            }

            public void onFinishedColorPicking() {
                PhotoPaintView photoPaintView = PhotoPaintView.this;
                photoPaintView.setCurrentSwatch(photoPaintView.colorPicker.getSwatch(), false);
                if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView)) {
                    PhotoPaintView.this.setDimVisibility(false);
                }
            }

            public void onSettingsPressed() {
                if (PhotoPaintView.this.currentEntityView == null) {
                    PhotoPaintView.this.showBrushSettings();
                } else if (PhotoPaintView.this.currentEntityView instanceof StickerView) {
                    PhotoPaintView.this.mirrorSticker();
                } else if (PhotoPaintView.this.currentEntityView instanceof TextPaintView) {
                    PhotoPaintView.this.showTextSettings();
                }
            }

            public void onUndoPressed() {
                PhotoPaintView.this.undoStore.undo();
            }
        });
        FrameLayout frameLayout4 = new FrameLayout(context2);
        this.toolsView = frameLayout4;
        frameLayout4.setBackgroundColor(-16777216);
        addView(this.toolsView, LayoutHelper.createFrame(-1, 48, 83));
        TextView textView = new TextView(context2);
        this.cancelTextView = textView;
        textView.setTextSize(1, 14.0f);
        this.cancelTextView.setTextColor(-1);
        this.cancelTextView.setGravity(17);
        this.cancelTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        this.cancelTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelTextView.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        this.cancelTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.toolsView.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
        TextView textView2 = new TextView(context2);
        this.doneTextView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.doneTextView.setTextColor(-11420173);
        this.doneTextView.setGravity(17);
        this.doneTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        this.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneTextView.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
        this.doneTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.toolsView.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
        ImageView imageView = new ImageView(context2);
        this.paintButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.paintButton.setImageResource(R.drawable.photo_paint);
        this.paintButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(this.paintButton, LayoutHelper.createFrame(54.0f, -1.0f, 17, 0.0f, 0.0f, 56.0f, 0.0f));
        this.paintButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoPaintView.this.lambda$new$2$PhotoPaintView(view);
            }
        });
        ImageView stickerButton = new ImageView(context2);
        stickerButton.setScaleType(ImageView.ScaleType.CENTER);
        stickerButton.setImageResource(R.drawable.photo_sticker);
        stickerButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(stickerButton, LayoutHelper.createFrame(54, -1, 17));
        stickerButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoPaintView.this.lambda$new$3$PhotoPaintView(view);
            }
        });
        ImageView textButton = new ImageView(context2);
        textButton.setScaleType(ImageView.ScaleType.CENTER);
        textButton.setImageResource(R.drawable.photo_paint_text);
        textButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(textButton, LayoutHelper.createFrame(54.0f, -1.0f, 17, 56.0f, 0.0f, 0.0f, 0.0f));
        textButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoPaintView.this.lambda$new$4$PhotoPaintView(view);
            }
        });
        this.colorPicker.setUndoEnabled(false);
        setCurrentSwatch(this.colorPicker.getSwatch(), false);
        updateSettingsButton();
    }

    public /* synthetic */ void lambda$new$0$PhotoPaintView() {
        this.colorPicker.setUndoEnabled(this.undoStore.canUndo());
    }

    public /* synthetic */ void lambda$new$1$PhotoPaintView(View v) {
        closeTextEnter(true);
    }

    public /* synthetic */ void lambda$new$2$PhotoPaintView(View v) {
        selectEntity((EntityView) null);
    }

    public /* synthetic */ void lambda$new$3$PhotoPaintView(View v) {
        openStickersView();
    }

    public /* synthetic */ void lambda$new$4$PhotoPaintView(View v) {
        createText();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.currentEntityView != null) {
            if (this.editingText) {
                closeTextEnter(true);
            } else {
                selectEntity((EntityView) null);
            }
        }
        return true;
    }

    private Size getPaintingSize() {
        Size size = this.paintingSize;
        if (size != null) {
            return size;
        }
        float width = (float) (isSidewardOrientation() ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
        float height = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        Size size2 = new Size(width, height);
        size2.width = 1280.0f;
        size2.height = (float) Math.floor((double) ((size2.width * height) / width));
        if (size2.height > 1280.0f) {
            size2.height = 1280.0f;
            size2.width = (float) Math.floor((double) ((size2.height * width) / height));
        }
        this.paintingSize = size2;
        return size2;
    }

    private boolean isSidewardOrientation() {
        int i = this.orientation;
        return i % 360 == 90 || i % 360 == 270;
    }

    private void updateSettingsButton() {
        int resource = R.drawable.photo_paint_brush;
        EntityView entityView = this.currentEntityView;
        if (entityView != null) {
            if (entityView instanceof StickerView) {
                resource = R.drawable.photo_flip;
            } else if (entityView instanceof TextPaintView) {
                resource = R.drawable.photo_outline;
            }
            this.paintButton.setImageResource(R.drawable.photo_paint);
            this.paintButton.setColorFilter((ColorFilter) null);
        } else {
            this.paintButton.setColorFilter(new PorterDuffColorFilter(-11420173, PorterDuff.Mode.MULTIPLY));
            this.paintButton.setImageResource(R.drawable.photo_paint);
        }
        this.colorPicker.setSettingsButtonImage(resource);
    }

    public void init() {
        this.renderView.setVisibility(0);
        detectFaces();
    }

    public void shutdown() {
        this.renderView.shutdown();
        this.entitiesView.setVisibility(8);
        this.selectionContainerView.setVisibility(8);
        this.queue.postRunnable($$Lambda$PhotoPaintView$zKLn9x1OVZgVjPHmLfTiEXlqAJs.INSTANCE);
    }

    static /* synthetic */ void lambda$shutdown$5() {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            looper.quit();
        }
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public TextView getDoneTextView() {
        return this.doneTextView;
    }

    public TextView getCancelTextView() {
        return this.cancelTextView;
    }

    public ColorPicker getColorPicker() {
        return this.colorPicker;
    }

    private boolean hasChanges() {
        return this.undoStore.canUndo() || this.entitiesView.entitiesCount() > 0;
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = this.renderView.getResultBitmap();
        if (bitmap != null && this.entitiesView.entitiesCount() > 0) {
            Canvas canvas = new Canvas(bitmap);
            for (int i = 0; i < this.entitiesView.getChildCount(); i++) {
                View v = this.entitiesView.getChildAt(i);
                canvas.save();
                if (v instanceof EntityView) {
                    EntityView entity = (EntityView) v;
                    canvas.translate(entity.getPosition().x, entity.getPosition().y);
                    canvas.scale(v.getScaleX(), v.getScaleY());
                    canvas.rotate(v.getRotation());
                    canvas.translate((float) ((-entity.getWidth()) / 2), (float) ((-entity.getHeight()) / 2));
                    if (v instanceof TextPaintView) {
                        Bitmap b = Bitmaps.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(b);
                        v.draw(c);
                        canvas.drawBitmap(b, (Rect) null, new Rect(0, 0, b.getWidth(), b.getHeight()), (Paint) null);
                        try {
                            c.setBitmap((Bitmap) null);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        b.recycle();
                    } else {
                        v.draw(canvas);
                    }
                }
                canvas.restore();
            }
        }
        return bitmap;
    }

    public void maybeShowDismissalAlert(PhotoViewer photoViewer, Activity parentActivity, Runnable okRunnable) {
        if (this.editingText) {
            closeTextEnter(false);
        } else if (this.pickingSticker) {
            closeStickersView();
        } else if (!hasChanges()) {
            okRunnable.run();
        } else if (parentActivity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
            builder.setMessage(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(okRunnable) {
                private final /* synthetic */ Runnable f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    this.f$0.run();
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            photoViewer.showAlertDialog(builder);
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentSwatch(Swatch swatch, boolean updateInterface) {
        this.renderView.setColor(swatch.color);
        this.renderView.setBrushSize(swatch.brushWeight);
        if (updateInterface) {
            this.colorPicker.setSwatch(swatch);
        }
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof TextPaintView) {
            ((TextPaintView) entityView).setSwatch(swatch);
        }
    }

    /* access modifiers changed from: private */
    public void setDimVisibility(final boolean visible) {
        Animator animator;
        if (visible) {
            this.dimView.setVisibility(0);
            animator = ObjectAnimator.ofFloat(this.dimView, "alpha", new float[]{0.0f, 1.0f});
        } else {
            animator = ObjectAnimator.ofFloat(this.dimView, "alpha", new float[]{1.0f, 0.0f});
        }
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    PhotoPaintView.this.dimView.setVisibility(8);
                }
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    private void setTextDimVisibility(final boolean visible, EntityView view) {
        Animator animator;
        if (visible && view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (this.textDimView.getParent() != null) {
                ((EntitiesContainerView) this.textDimView.getParent()).removeView(this.textDimView);
            }
            parent.addView(this.textDimView, parent.indexOfChild(view));
        }
        view.setSelectionVisibility(!visible);
        if (visible) {
            this.textDimView.setVisibility(0);
            animator = ObjectAnimator.ofFloat(this.textDimView, "alpha", new float[]{0.0f, 1.0f});
        } else {
            animator = ObjectAnimator.ofFloat(this.textDimView, "alpha", new float[]{1.0f, 0.0f});
        }
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    PhotoPaintView.this.textDimView.setVisibility(8);
                    if (PhotoPaintView.this.textDimView.getParent() != null) {
                        ((EntitiesContainerView) PhotoPaintView.this.textDimView.getParent()).removeView(PhotoPaintView.this.textDimView);
                    }
                }
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float bitmapH;
        float bitmapW;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        int maxHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f);
        if (this.bitmapToEdit != null) {
            bitmapW = (float) (isSidewardOrientation() ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
            bitmapH = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        } else {
            bitmapW = (float) width;
            bitmapH = (float) ((height - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f));
        }
        float renderWidth = (float) width;
        float renderHeight = (float) Math.floor((double) ((renderWidth * bitmapH) / bitmapW));
        if (renderHeight > ((float) maxHeight)) {
            renderHeight = (float) maxHeight;
            renderWidth = (float) Math.floor((double) ((renderHeight * bitmapW) / bitmapH));
        }
        this.renderView.measure(View.MeasureSpec.makeMeasureSpec((int) renderWidth, 1073741824), View.MeasureSpec.makeMeasureSpec((int) renderHeight, 1073741824));
        this.entitiesView.measure(View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.width, 1073741824), View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.height, 1073741824));
        this.dimView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(maxHeight, Integer.MIN_VALUE));
        this.selectionContainerView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(maxHeight, 1073741824));
        this.colorPicker.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(maxHeight, 1073741824));
        this.toolsView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView != null) {
            stickerMasksView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, 1073741824));
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        float bitmapH;
        float bitmapW;
        int width = right - left;
        int height = bottom - top;
        int status = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
        int actionBarHeight = ActionBar.getCurrentActionBarHeight();
        int actionBarHeight2 = ActionBar.getCurrentActionBarHeight() + status;
        int maxHeight = (AndroidUtilities.displaySize.y - actionBarHeight) - AndroidUtilities.dp(48.0f);
        if (this.bitmapToEdit != null) {
            bitmapW = (float) (isSidewardOrientation() ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
            bitmapH = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        } else {
            bitmapW = (float) width;
            bitmapH = (float) ((height - actionBarHeight) - AndroidUtilities.dp(48.0f));
        }
        float renderWidth = (float) width;
        if (((float) Math.floor((double) ((renderWidth * bitmapH) / bitmapW))) > ((float) maxHeight)) {
            renderWidth = (float) Math.floor((double) ((((float) maxHeight) * bitmapW) / bitmapH));
        }
        int x = (int) Math.ceil((double) ((width - this.renderView.getMeasuredWidth()) / 2));
        int y = ((((((height - actionBarHeight2) - AndroidUtilities.dp(48.0f)) - this.renderView.getMeasuredHeight()) / 2) + actionBarHeight2) - ActionBar.getCurrentActionBarHeight()) + AndroidUtilities.dp(8.0f);
        RenderView renderView2 = this.renderView;
        renderView2.layout(x, y, renderView2.getMeasuredWidth() + x, this.renderView.getMeasuredHeight() + y);
        float scale = renderWidth / this.paintingSize.width;
        this.entitiesView.setScaleX(scale);
        this.entitiesView.setScaleY(scale);
        EntitiesContainerView entitiesContainerView = this.entitiesView;
        float f = scale;
        entitiesContainerView.layout(x, y, entitiesContainerView.getMeasuredWidth() + x, this.entitiesView.getMeasuredHeight() + y);
        FrameLayout frameLayout = this.dimView;
        int i = actionBarHeight;
        frameLayout.layout(0, status, frameLayout.getMeasuredWidth(), this.dimView.getMeasuredHeight() + status);
        FrameLayout frameLayout2 = this.selectionContainerView;
        frameLayout2.layout(0, status, frameLayout2.getMeasuredWidth(), this.selectionContainerView.getMeasuredHeight() + status);
        ColorPicker colorPicker2 = this.colorPicker;
        colorPicker2.layout(0, actionBarHeight2, colorPicker2.getMeasuredWidth(), this.colorPicker.getMeasuredHeight() + actionBarHeight2);
        FrameLayout frameLayout3 = this.toolsView;
        frameLayout3.layout(0, height - frameLayout3.getMeasuredHeight(), this.toolsView.getMeasuredWidth(), height);
        this.curtainView.layout(0, 0, width, maxHeight);
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView != null) {
            stickerMasksView.layout(0, status, stickerMasksView.getMeasuredWidth(), this.stickersView.getMeasuredHeight() + status);
        }
        EntityView entityView = this.currentEntityView;
        if (entityView != null) {
            entityView.updateSelectionView();
            this.currentEntityView.setOffset(this.entitiesView.getLeft() - this.selectionContainerView.getLeft(), this.entitiesView.getTop() - this.selectionContainerView.getTop());
        }
    }

    public boolean onEntitySelected(EntityView entityView) {
        return selectEntity(entityView);
    }

    public boolean onEntityLongClicked(EntityView entityView) {
        showMenuForEntity(entityView);
        return true;
    }

    public boolean allowInteraction(EntityView entityView) {
        return !this.editingText;
    }

    private Point centerPositionForEntity() {
        Size paintingSize2 = getPaintingSize();
        return new Point(paintingSize2.width / 2.0f, paintingSize2.height / 2.0f);
    }

    private Point startPositionRelativeToEntity(EntityView entityView) {
        if (entityView != null) {
            Point position = entityView.getPosition();
            return new Point(position.x + 200.0f, position.y + 200.0f);
        }
        Point position2 = centerPositionForEntity();
        while (true) {
            boolean occupied = false;
            for (int index = 0; index < this.entitiesView.getChildCount(); index++) {
                View view = this.entitiesView.getChildAt(index);
                if (view instanceof EntityView) {
                    Point location = ((EntityView) view).getPosition();
                    if (((float) Math.sqrt(Math.pow((double) (location.x - position2.x), 2.0d) + Math.pow((double) (location.y - position2.y), 2.0d))) < 100.0f) {
                        occupied = true;
                    }
                }
            }
            if (!occupied) {
                return position2;
            }
            position2 = new Point(position2.x + 200.0f, position2.y + 200.0f);
        }
    }

    public ArrayList<TLRPC.InputDocument> getMasks() {
        ArrayList<TLRPC.InputDocument> result = null;
        int count = this.entitiesView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.entitiesView.getChildAt(a);
            if (child instanceof StickerView) {
                TLRPC.Document document = ((StickerView) child).getSticker();
                if (result == null) {
                    result = new ArrayList<>();
                }
                TLRPC.TL_inputDocument inputDocument = new TLRPC.TL_inputDocument();
                inputDocument.id = document.id;
                inputDocument.access_hash = document.access_hash;
                inputDocument.file_reference = document.file_reference;
                if (inputDocument.file_reference == null) {
                    inputDocument.file_reference = new byte[0];
                }
                result.add(inputDocument);
            }
        }
        return result;
    }

    /* access modifiers changed from: private */
    public boolean selectEntity(EntityView entityView) {
        boolean changed = false;
        EntityView entityView2 = this.currentEntityView;
        if (entityView2 != null) {
            if (entityView2 == entityView) {
                if (!this.editingText) {
                    showMenuForEntity(entityView2);
                }
                return true;
            }
            entityView2.deselect();
            changed = true;
        }
        this.currentEntityView = entityView;
        if (entityView != null) {
            entityView.select(this.selectionContainerView);
            this.entitiesView.bringViewToFront(this.currentEntityView);
            EntityView entityView3 = this.currentEntityView;
            if (entityView3 instanceof TextPaintView) {
                setCurrentSwatch(((TextPaintView) entityView3).getSwatch(), true);
            }
            changed = true;
        }
        updateSettingsButton();
        return changed;
    }

    /* access modifiers changed from: private */
    /* renamed from: removeEntity */
    public void lambda$registerRemovalUndo$7$PhotoPaintView(EntityView entityView) {
        EntityView entityView2 = this.currentEntityView;
        if (entityView == entityView2) {
            entityView2.deselect();
            if (this.editingText) {
                closeTextEnter(false);
            }
            this.currentEntityView = null;
            updateSettingsButton();
        }
        this.entitiesView.removeView(entityView);
        this.undoStore.unregisterUndo(entityView.getUUID());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: im.bclpbkiauv.ui.components.paint.views.TextPaintView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: im.bclpbkiauv.ui.components.paint.views.TextPaintView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: im.bclpbkiauv.ui.components.paint.views.TextPaintView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: im.bclpbkiauv.ui.components.paint.views.StickerView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: im.bclpbkiauv.ui.components.paint.views.TextPaintView} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void duplicateSelectedEntity() {
        /*
            r6 = this;
            im.bclpbkiauv.ui.components.paint.views.EntityView r0 = r6.currentEntityView
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            r1 = 0
            im.bclpbkiauv.ui.components.Point r0 = r6.startPositionRelativeToEntity(r0)
            im.bclpbkiauv.ui.components.paint.views.EntityView r2 = r6.currentEntityView
            boolean r3 = r2 instanceof im.bclpbkiauv.ui.components.paint.views.StickerView
            if (r3 == 0) goto L_0x0027
            im.bclpbkiauv.ui.components.paint.views.StickerView r2 = new im.bclpbkiauv.ui.components.paint.views.StickerView
            android.content.Context r3 = r6.getContext()
            im.bclpbkiauv.ui.components.paint.views.EntityView r4 = r6.currentEntityView
            im.bclpbkiauv.ui.components.paint.views.StickerView r4 = (im.bclpbkiauv.ui.components.paint.views.StickerView) r4
            r2.<init>(r3, r4, r0)
            r2.setDelegate(r6)
            im.bclpbkiauv.ui.components.paint.views.EntitiesContainerView r3 = r6.entitiesView
            r3.addView(r2)
            r1 = r2
            goto L_0x0056
        L_0x0027:
            boolean r2 = r2 instanceof im.bclpbkiauv.ui.components.paint.views.TextPaintView
            if (r2 == 0) goto L_0x0056
            im.bclpbkiauv.ui.components.paint.views.TextPaintView r2 = new im.bclpbkiauv.ui.components.paint.views.TextPaintView
            android.content.Context r3 = r6.getContext()
            im.bclpbkiauv.ui.components.paint.views.EntityView r4 = r6.currentEntityView
            im.bclpbkiauv.ui.components.paint.views.TextPaintView r4 = (im.bclpbkiauv.ui.components.paint.views.TextPaintView) r4
            r2.<init>(r3, r4, r0)
            r2.setDelegate(r6)
            im.bclpbkiauv.ui.components.Size r3 = r6.getPaintingSize()
            float r3 = r3.width
            r4 = 1101004800(0x41a00000, float:20.0)
            float r3 = r3 - r4
            int r3 = (int) r3
            r2.setMaxWidth(r3)
            im.bclpbkiauv.ui.components.paint.views.EntitiesContainerView r3 = r6.entitiesView
            r4 = -2
            r5 = -1073741824(0xffffffffc0000000, float:-2.0)
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r4, r5)
            r3.addView(r2, r4)
            r1 = r2
            goto L_0x0057
        L_0x0056:
        L_0x0057:
            r6.registerRemovalUndo(r1)
            r6.selectEntity(r1)
            r6.updateSettingsButton()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.PhotoPaintView.duplicateSelectedEntity():void");
    }

    private void openStickersView() {
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView == null || stickerMasksView.getVisibility() != 0) {
            this.pickingSticker = true;
            if (this.stickersView == null) {
                StickerMasksView stickerMasksView2 = new StickerMasksView(getContext());
                this.stickersView = stickerMasksView2;
                stickerMasksView2.setListener(new StickerMasksView.Listener() {
                    public void onStickerSelected(Object parentObject, TLRPC.Document sticker) {
                        PhotoPaintView.this.closeStickersView();
                        PhotoPaintView.this.createSticker(parentObject, sticker);
                    }

                    public void onTypeChanged() {
                    }
                });
                addView(this.stickersView, LayoutHelper.createFrame(-1, -1, 51));
            }
            this.stickersView.setVisibility(0);
            Animator a = ObjectAnimator.ofFloat(this.stickersView, "alpha", new float[]{0.0f, 1.0f});
            a.setDuration(200);
            a.start();
        }
    }

    /* access modifiers changed from: private */
    public void closeStickersView() {
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView != null && stickerMasksView.getVisibility() == 0) {
            this.pickingSticker = false;
            Animator a = ObjectAnimator.ofFloat(this.stickersView, "alpha", new float[]{1.0f, 0.0f});
            a.setDuration(200);
            a.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PhotoPaintView.this.stickersView.setVisibility(8);
                }
            });
            a.start();
        }
    }

    private Size baseStickerSize() {
        float side = (float) Math.floor(((double) getPaintingSize().width) * 0.5d);
        return new Size(side, side);
    }

    private void registerRemovalUndo(EntityView entityView) {
        this.undoStore.registerUndo(entityView.getUUID(), new Runnable(entityView) {
            private final /* synthetic */ EntityView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PhotoPaintView.this.lambda$registerRemovalUndo$7$PhotoPaintView(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    public void createSticker(Object parentObject, TLRPC.Document sticker) {
        StickerPosition position = calculateStickerPosition(sticker);
        StickerView view = new StickerView(getContext(), position.position, position.angle, position.scale, baseStickerSize(), sticker, parentObject);
        view.setDelegate(this);
        this.entitiesView.addView(view);
        registerRemovalUndo(view);
        selectEntity(view);
    }

    /* access modifiers changed from: private */
    public void mirrorSticker() {
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof StickerView) {
            ((StickerView) entityView).mirror();
        }
    }

    private int baseFontSize() {
        return (int) (getPaintingSize().width / 9.0f);
    }

    private void createText() {
        Swatch currentSwatch = this.colorPicker.getSwatch();
        setCurrentSwatch(this.selectedStroke ? new Swatch(-16777216, 0.85f, currentSwatch.brushWeight) : new Swatch(-1, 1.0f, currentSwatch.brushWeight), true);
        TextPaintView textPaintView = new TextPaintView(getContext(), startPositionRelativeToEntity((EntityView) null), baseFontSize(), "", this.colorPicker.getSwatch(), this.selectedStroke);
        textPaintView.setDelegate(this);
        textPaintView.setMaxWidth((int) (getPaintingSize().width - 20.0f));
        this.entitiesView.addView(textPaintView, LayoutHelper.createFrame(-2, -2.0f));
        registerRemovalUndo(textPaintView);
        selectEntity(textPaintView);
        editSelectedTextEntity();
    }

    private void editSelectedTextEntity() {
        if ((this.currentEntityView instanceof TextPaintView) && !this.editingText) {
            this.curtainView.setVisibility(0);
            TextPaintView textPaintView = (TextPaintView) this.currentEntityView;
            this.initialText = textPaintView.getText();
            this.editingText = true;
            this.editedTextPosition = textPaintView.getPosition();
            this.editedTextRotation = textPaintView.getRotation();
            this.editedTextScale = textPaintView.getScale();
            textPaintView.setPosition(centerPositionForEntity());
            textPaintView.setRotation(0.0f);
            textPaintView.setScale(1.0f);
            this.toolsView.setVisibility(8);
            setTextDimVisibility(true, textPaintView);
            textPaintView.beginEditing();
            ((InputMethodManager) ApplicationLoader.applicationContext.getSystemService("input_method")).toggleSoftInputFromWindow(textPaintView.getFocusedView().getWindowToken(), 2, 0);
        }
    }

    public void closeTextEnter(boolean apply) {
        if (this.editingText) {
            EntityView entityView = this.currentEntityView;
            if (entityView instanceof TextPaintView) {
                TextPaintView textPaintView = (TextPaintView) entityView;
                this.toolsView.setVisibility(0);
                AndroidUtilities.hideKeyboard(textPaintView.getFocusedView());
                textPaintView.getFocusedView().clearFocus();
                textPaintView.endEditing();
                if (!apply) {
                    textPaintView.setText(this.initialText);
                }
                if (textPaintView.getText().trim().length() == 0) {
                    this.entitiesView.removeView(textPaintView);
                    selectEntity((EntityView) null);
                } else {
                    textPaintView.setPosition(this.editedTextPosition);
                    textPaintView.setRotation(this.editedTextRotation);
                    textPaintView.setScale(this.editedTextScale);
                    this.editedTextPosition = null;
                    this.editedTextRotation = 0.0f;
                    this.editedTextScale = 0.0f;
                }
                setTextDimVisibility(false, textPaintView);
                this.editingText = false;
                this.initialText = null;
                this.curtainView.setVisibility(8);
            }
        }
    }

    private void setBrush(int brush) {
        RenderView renderView2 = this.renderView;
        Brush[] brushArr = this.brushes;
        this.currentBrush = brush;
        renderView2.setBrush(brushArr[brush]);
    }

    private void setStroke(boolean stroke) {
        this.selectedStroke = stroke;
        if (this.currentEntityView instanceof TextPaintView) {
            Swatch currentSwatch = this.colorPicker.getSwatch();
            if (stroke && currentSwatch.color == -1) {
                setCurrentSwatch(new Swatch(-16777216, 0.85f, currentSwatch.brushWeight), true);
            } else if (!stroke && currentSwatch.color == -16777216) {
                setCurrentSwatch(new Swatch(-1, 1.0f, currentSwatch.brushWeight), true);
            }
            ((TextPaintView) this.currentEntityView).setStroke(stroke);
        }
    }

    private void showMenuForEntity(EntityView entityView) {
        showPopup(new Runnable(entityView) {
            private final /* synthetic */ EntityView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PhotoPaintView.this.lambda$showMenuForEntity$11$PhotoPaintView(this.f$1);
            }
        }, entityView, 17, (int) ((entityView.getPosition().x - ((float) (this.entitiesView.getWidth() / 2))) * this.entitiesView.getScaleX()), ((int) (((entityView.getPosition().y - ((((float) entityView.getHeight()) * entityView.getScale()) / 2.0f)) - ((float) (this.entitiesView.getHeight() / 2))) * this.entitiesView.getScaleY())) - AndroidUtilities.dp(32.0f));
    }

    public /* synthetic */ void lambda$showMenuForEntity$11$PhotoPaintView(EntityView entityView) {
        LinearLayout parent = new LinearLayout(getContext());
        parent.setOrientation(0);
        TextView deleteView = new TextView(getContext());
        deleteView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        deleteView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        deleteView.setGravity(16);
        deleteView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(14.0f), 0);
        deleteView.setTextSize(1, 18.0f);
        deleteView.setTag(0);
        deleteView.setText(LocaleController.getString("PaintDelete", R.string.PaintDelete));
        deleteView.setOnClickListener(new View.OnClickListener(entityView) {
            private final /* synthetic */ EntityView f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                PhotoPaintView.this.lambda$null$8$PhotoPaintView(this.f$1, view);
            }
        });
        parent.addView(deleteView, LayoutHelper.createLinear(-2, 48));
        if (entityView instanceof TextPaintView) {
            TextView editView = new TextView(getContext());
            editView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
            editView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            editView.setGravity(16);
            editView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            editView.setTextSize(1, 18.0f);
            editView.setTag(1);
            editView.setText(LocaleController.getString("PaintEdit", R.string.PaintEdit));
            editView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoPaintView.this.lambda$null$9$PhotoPaintView(view);
                }
            });
            parent.addView(editView, LayoutHelper.createLinear(-2, 48));
        }
        TextView duplicateView = new TextView(getContext());
        duplicateView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        duplicateView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        duplicateView.setGravity(16);
        duplicateView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
        duplicateView.setTextSize(1, 18.0f);
        duplicateView.setTag(2);
        duplicateView.setText(LocaleController.getString("PaintDuplicate", R.string.PaintDuplicate));
        duplicateView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoPaintView.this.lambda$null$10$PhotoPaintView(view);
            }
        });
        parent.addView(duplicateView, LayoutHelper.createLinear(-2, 48));
        this.popupLayout.addView(parent);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
        params.width = -2;
        params.height = -2;
        parent.setLayoutParams(params);
    }

    public /* synthetic */ void lambda$null$8$PhotoPaintView(EntityView entityView, View v) {
        lambda$registerRemovalUndo$7$PhotoPaintView(entityView);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    public /* synthetic */ void lambda$null$9$PhotoPaintView(View v) {
        editSelectedTextEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    public /* synthetic */ void lambda$null$10$PhotoPaintView(View v) {
        duplicateSelectedEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    private FrameLayout buttonForBrush(int brush, int resource, boolean selected) {
        FrameLayout button = new FrameLayout(getContext());
        button.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        button.setOnClickListener(new View.OnClickListener(brush) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                PhotoPaintView.this.lambda$buttonForBrush$12$PhotoPaintView(this.f$1, view);
            }
        });
        ImageView preview = new ImageView(getContext());
        preview.setImageResource(resource);
        button.addView(preview, LayoutHelper.createFrame(165.0f, 44.0f, 19, 46.0f, 0.0f, 8.0f, 0.0f));
        if (selected) {
            ImageView check = new ImageView(getContext());
            check.setImageResource(R.drawable.ic_ab_done);
            check.setScaleType(ImageView.ScaleType.CENTER);
            check.setColorFilter(new PorterDuffColorFilter(-13660983, PorterDuff.Mode.MULTIPLY));
            button.addView(check, LayoutHelper.createFrame(50, -1.0f));
        }
        return button;
    }

    public /* synthetic */ void lambda$buttonForBrush$12$PhotoPaintView(int brush, View v) {
        setBrush(brush);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    public void showBrushSettings() {
        showPopup(new Runnable() {
            public final void run() {
                PhotoPaintView.this.lambda$showBrushSettings$13$PhotoPaintView();
            }
        }, this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    public /* synthetic */ void lambda$showBrushSettings$13$PhotoPaintView() {
        boolean z = false;
        View radial = buttonForBrush(0, R.drawable.paint_radial_preview, this.currentBrush == 0);
        this.popupLayout.addView(radial);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) radial.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(52.0f);
        radial.setLayoutParams(layoutParams);
        View elliptical = buttonForBrush(1, R.drawable.paint_elliptical_preview, this.currentBrush == 1);
        this.popupLayout.addView(elliptical);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) elliptical.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = AndroidUtilities.dp(52.0f);
        elliptical.setLayoutParams(layoutParams2);
        if (this.currentBrush == 2) {
            z = true;
        }
        View neon = buttonForBrush(2, R.drawable.paint_neon_preview, z);
        this.popupLayout.addView(neon);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) neon.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = AndroidUtilities.dp(52.0f);
        neon.setLayoutParams(layoutParams3);
    }

    private FrameLayout buttonForText(boolean stroke, String text, boolean selected) {
        FrameLayout button = new FrameLayout(getContext()) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return true;
            }
        };
        button.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        button.setOnClickListener(new View.OnClickListener(stroke) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                PhotoPaintView.this.lambda$buttonForText$14$PhotoPaintView(this.f$1, view);
            }
        });
        EditTextOutline textView = new EditTextOutline(getContext());
        textView.setBackgroundColor(0);
        textView.setEnabled(false);
        textView.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        int i = -16777216;
        textView.setTextColor(stroke ? -1 : -16777216);
        if (!stroke) {
            i = 0;
        }
        textView.setStrokeColor(i);
        textView.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f), 0);
        textView.setTextSize(1, 18.0f);
        textView.setTypeface((Typeface) null, 1);
        textView.setTag(Boolean.valueOf(stroke));
        textView.setText(text);
        button.addView(textView, LayoutHelper.createFrame(-2.0f, -2.0f, 19, 46.0f, 0.0f, 16.0f, 0.0f));
        if (selected) {
            ImageView check = new ImageView(getContext());
            check.setImageResource(R.drawable.ic_ab_done);
            check.setScaleType(ImageView.ScaleType.CENTER);
            check.setColorFilter(new PorterDuffColorFilter(-13660983, PorterDuff.Mode.MULTIPLY));
            button.addView(check, LayoutHelper.createFrame(50, -1.0f));
        }
        return button;
    }

    public /* synthetic */ void lambda$buttonForText$14$PhotoPaintView(boolean stroke, View v) {
        setStroke(stroke);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    public void showTextSettings() {
        showPopup(new Runnable() {
            public final void run() {
                PhotoPaintView.this.lambda$showTextSettings$15$PhotoPaintView();
            }
        }, this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    public /* synthetic */ void lambda$showTextSettings$15$PhotoPaintView() {
        View outline = buttonForText(true, LocaleController.getString("PaintOutlined", R.string.PaintOutlined), this.selectedStroke);
        this.popupLayout.addView(outline);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) outline.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        outline.setLayoutParams(layoutParams);
        View regular = buttonForText(false, LocaleController.getString("PaintRegular", R.string.PaintRegular), true ^ this.selectedStroke);
        this.popupLayout.addView(regular);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) regular.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = AndroidUtilities.dp(48.0f);
        regular.setLayoutParams(layoutParams2);
    }

    private void showPopup(Runnable setupRunnable, View parent, int gravity, int x, int y) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
                this.popupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new View.OnTouchListener() {
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        return PhotoPaintView.this.lambda$showPopup$16$PhotoPaintView(view, motionEvent);
                    }
                });
                this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        PhotoPaintView.this.lambda$showPopup$17$PhotoPaintView(keyEvent);
                    }
                });
                this.popupLayout.setShowedFromBotton(true);
            }
            this.popupLayout.removeInnerViews();
            setupRunnable.run();
            if (this.popupWindow == null) {
                ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow = actionBarPopupWindow2;
                actionBarPopupWindow2.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                this.popupWindow.getContentView().setFocusableInTouchMode(true);
                this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    public final void onDismiss() {
                        PhotoPaintView.this.lambda$showPopup$18$PhotoPaintView();
                    }
                });
            }
            this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(parent, gravity, x, y);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    public /* synthetic */ boolean lambda$showPopup$16$PhotoPaintView(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        v.getHitRect(this.popupRect);
        if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    public /* synthetic */ void lambda$showPopup$17$PhotoPaintView(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$showPopup$18$PhotoPaintView() {
        this.popupLayout.removeInnerViews();
    }

    private int getFrameRotation() {
        int i = this.orientation;
        if (i == 90) {
            return 1;
        }
        if (i == 180) {
            return 2;
        }
        if (i != 270) {
            return 0;
        }
        return 3;
    }

    private void detectFaces() {
        this.queue.postRunnable(new Runnable() {
            public final void run() {
                PhotoPaintView.this.lambda$detectFaces$19$PhotoPaintView();
            }
        });
    }

    public /* synthetic */ void lambda$detectFaces$19$PhotoPaintView() {
        FaceDetector faceDetector = null;
        try {
            faceDetector = new FaceDetector.Builder(getContext()).setMode(1).setLandmarkType(1).setTrackingEnabled(false).build();
            if (!faceDetector.isOperational()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("face detection is not operational");
                }
                if (faceDetector != null) {
                    faceDetector.release();
                    return;
                }
                return;
            }
            try {
                SparseArray<Face> faces2 = faceDetector.detect(new Frame.Builder().setBitmap(this.bitmapToEdit).setRotation(getFrameRotation()).build());
                ArrayList<PhotoFace> result = new ArrayList<>();
                Size targetSize = getPaintingSize();
                for (int i = 0; i < faces2.size(); i++) {
                    PhotoFace face = new PhotoFace(faces2.get(faces2.keyAt(i)), this.bitmapToEdit, targetSize, isSidewardOrientation());
                    if (face.isSufficient()) {
                        result.add(face);
                    }
                }
                this.faces = result;
                if (faceDetector == null) {
                    return;
                }
                faceDetector.release();
            } catch (Throwable e) {
                FileLog.e(e);
                if (faceDetector != null) {
                    faceDetector.release();
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            if (faceDetector == null) {
            }
        } catch (Throwable th) {
            if (faceDetector != null) {
                faceDetector.release();
            }
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003a, code lost:
        r4 = r2.n;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private im.bclpbkiauv.ui.components.PhotoPaintView.StickerPosition calculateStickerPosition(im.bclpbkiauv.tgnet.TLRPC.Document r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = 0
            r3 = 0
        L_0x0006:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r4 = r1.attributes
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0020
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r4 = r1.attributes
            java.lang.Object r4 = r4.get(r3)
            im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute r4 = (im.bclpbkiauv.tgnet.TLRPC.DocumentAttribute) r4
            boolean r5 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeSticker
            if (r5 == 0) goto L_0x001d
            im.bclpbkiauv.tgnet.TLRPC$TL_maskCoords r2 = r4.mask_coords
            goto L_0x0020
        L_0x001d:
            int r3 = r3 + 1
            goto L_0x0006
        L_0x0020:
            im.bclpbkiauv.ui.components.PhotoPaintView$StickerPosition r3 = new im.bclpbkiauv.ui.components.PhotoPaintView$StickerPosition
            im.bclpbkiauv.ui.components.Point r4 = r19.centerPositionForEntity()
            r5 = 1061158912(0x3f400000, float:0.75)
            r6 = 0
            r3.<init>(r4, r5, r6)
            if (r2 == 0) goto L_0x00c6
            java.util.ArrayList<im.bclpbkiauv.ui.components.paint.PhotoFace> r4 = r0.faces
            if (r4 == 0) goto L_0x00c6
            int r4 = r4.size()
            if (r4 != 0) goto L_0x003a
            goto L_0x00c6
        L_0x003a:
            int r4 = r2.n
            long r5 = r1.id
            im.bclpbkiauv.ui.components.paint.PhotoFace r5 = r0.getRandomFaceWithVacantAnchor(r4, r5, r2)
            if (r5 != 0) goto L_0x0045
            return r3
        L_0x0045:
            im.bclpbkiauv.ui.components.Point r6 = r5.getPointForAnchor(r4)
            float r7 = r5.getWidthForAnchor(r4)
            float r8 = r5.getAngle()
            im.bclpbkiauv.ui.components.Size r9 = r19.baseStickerSize()
            float r10 = r9.width
            float r10 = r7 / r10
            double r10 = (double) r10
            double r12 = r2.zoom
            double r10 = r10 * r12
            float r10 = (float) r10
            double r11 = (double) r8
            double r11 = java.lang.Math.toRadians(r11)
            float r11 = (float) r11
            double r12 = (double) r11
            r14 = 4609753056924675352(0x3ff921fb54442d18, double:1.5707963267948966)
            double r12 = r14 - r12
            double r12 = java.lang.Math.sin(r12)
            double r14 = (double) r7
            double r12 = r12 * r14
            double r14 = r2.x
            double r12 = r12 * r14
            float r12 = (float) r12
            double r13 = (double) r11
            r15 = 4609753056924675352(0x3ff921fb54442d18, double:1.5707963267948966)
            double r13 = r15 - r13
            double r13 = java.lang.Math.cos(r13)
            r17 = r4
            r18 = r5
            double r4 = (double) r7
            double r13 = r13 * r4
            double r4 = r2.x
            double r13 = r13 * r4
            float r4 = (float) r13
            double r13 = (double) r11
            double r13 = r13 + r15
            double r13 = java.lang.Math.cos(r13)
            double r0 = (double) r7
            double r13 = r13 * r0
            double r0 = r2.y
            double r13 = r13 * r0
            float r0 = (float) r13
            double r13 = (double) r11
            double r13 = r13 + r15
            double r13 = java.lang.Math.sin(r13)
            r1 = r8
            r5 = r9
            double r8 = (double) r7
            double r13 = r13 * r8
            double r8 = r2.y
            double r13 = r13 * r8
            float r8 = (float) r13
            float r9 = r6.x
            float r9 = r9 + r12
            float r9 = r9 + r0
            float r13 = r6.y
            float r13 = r13 + r4
            float r13 = r13 + r8
            im.bclpbkiauv.ui.components.PhotoPaintView$StickerPosition r14 = new im.bclpbkiauv.ui.components.PhotoPaintView$StickerPosition
            im.bclpbkiauv.ui.components.Point r15 = new im.bclpbkiauv.ui.components.Point
            r15.<init>(r9, r13)
            r16 = r0
            r0 = r19
            r14.<init>(r15, r10, r1)
            return r14
        L_0x00c6:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.PhotoPaintView.calculateStickerPosition(im.bclpbkiauv.tgnet.TLRPC$Document):im.bclpbkiauv.ui.components.PhotoPaintView$StickerPosition");
    }

    private PhotoFace getRandomFaceWithVacantAnchor(int anchor, long documentId, TLRPC.TL_maskCoords maskCoords) {
        int i = anchor;
        if (i < 0 || i > 3 || this.faces.isEmpty()) {
            return null;
        }
        int count = this.faces.size();
        int i2 = Utilities.random.nextInt(count);
        for (int remaining = count; remaining > 0; remaining--) {
            PhotoFace face = this.faces.get(i2);
            if (!isFaceAnchorOccupied(face, anchor, documentId, maskCoords)) {
                return face;
            }
            i2 = (i2 + 1) % count;
        }
        return null;
    }

    private boolean isFaceAnchorOccupied(PhotoFace face, int anchor, long documentId, TLRPC.TL_maskCoords maskCoords) {
        Point anchorPoint = face.getPointForAnchor(anchor);
        if (anchorPoint == null) {
            return true;
        }
        float minDistance = face.getWidthForAnchor(0) * 1.1f;
        for (int index = 0; index < this.entitiesView.getChildCount(); index++) {
            View view = this.entitiesView.getChildAt(index);
            if (!(view instanceof StickerView)) {
                int i = anchor;
            } else {
                StickerView stickerView = (StickerView) view;
                if (stickerView.getAnchor() != anchor) {
                    continue;
                } else {
                    Point location = stickerView.getPosition();
                    float distance = (float) Math.hypot((double) (location.x - anchorPoint.x), (double) (location.y - anchorPoint.y));
                    if ((documentId == stickerView.getSticker().id || this.faces.size() > 1) && distance < minDistance) {
                        return true;
                    }
                }
            }
        }
        int i2 = anchor;
        return false;
    }

    private class StickerPosition {
        /* access modifiers changed from: private */
        public float angle;
        /* access modifiers changed from: private */
        public Point position;
        /* access modifiers changed from: private */
        public float scale;

        StickerPosition(Point position2, float scale2, float angle2) {
            this.position = position2;
            this.scale = scale2;
            this.angle = angle2;
        }
    }
}
