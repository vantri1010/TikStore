package im.bclpbkiauv.ui.actionbar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.ActionBarPopupWindow;
import im.bclpbkiauv.ui.components.CloseProgressDrawable2;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class ActionBarMenuItem extends FrameLayout {
    private int additionalXOffset;
    private int additionalYOffset;
    private boolean allowCloseAnimation;
    /* access modifiers changed from: private */
    public boolean animateClear;
    private boolean animationEnabled;
    /* access modifiers changed from: private */
    public ImageView clearButton;
    private ActionBarMenuItemDelegate delegate;
    protected ImageView iconView;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    private boolean isSearchField;
    private boolean layoutInScreen;
    /* access modifiers changed from: private */
    public ActionBarMenuItemSearchListener listener;
    private int[] location;
    private boolean longClickEnabled;
    protected boolean overrideMenuClick;
    private ActionBarMenu parentMenu;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private ActionBarPopupWindow popupWindow;
    private boolean processedPopupClick;
    private CloseProgressDrawable2 progressDrawable;
    private Rect rect;
    private FrameLayout searchContainer;
    /* access modifiers changed from: private */
    public EditTextBoldCursor searchField;
    /* access modifiers changed from: private */
    public TextView searchFieldCaption;
    private View selectedMenuView;
    private Runnable showMenuRunnable;
    private int subMenuOpenSide;
    protected TextView textView;
    private int yOffset;

    public interface ActionBarMenuItemDelegate {
        void onItemClick(int i);
    }

    public static class ActionBarMenuItemSearchListener {
        public void onSearchExpand() {
        }

        public boolean canCollapseSearch() {
            return true;
        }

        public void onSearchCollapse() {
        }

        public void onTextChanged(EditText editText) {
        }

        public void onSearchPressed(EditText editText) {
        }

        public void onCaptionCleared() {
        }

        public boolean forceShowClear() {
            return false;
        }
    }

    public ActionBarMenuItem(Context context, ActionBarMenu menu, int backgroundColor, int iconColor) {
        this(context, menu, backgroundColor, iconColor, false);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu menu, int backgroundColor, int iconColor, boolean text) {
        super(context);
        this.allowCloseAnimation = true;
        this.animationEnabled = true;
        this.longClickEnabled = true;
        this.animateClear = true;
        if (backgroundColor != 0) {
            setBackgroundDrawable(Theme.createSelectorDrawable(backgroundColor, text ? 5 : 1));
        }
        this.parentMenu = menu;
        if (text) {
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setGravity(17);
            this.textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            if (iconColor != 0) {
                this.textView.setTextColor(iconColor);
            }
            addView(this.textView, LayoutHelper.createFrame(-2, -1.0f));
            return;
        }
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.iconView, LayoutHelper.createFrame(-1, -1.0f));
        if (iconColor != 0) {
            this.iconView.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setLongClickEnabled(boolean value) {
        this.longClickEnabled = value;
    }

    public boolean onTouchEvent(MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        ActionBarPopupWindow actionBarPopupWindow2;
        if (event.getActionMasked() == 0) {
            if (this.longClickEnabled && hasSubMenu() && ((actionBarPopupWindow2 = this.popupWindow) == null || (actionBarPopupWindow2 != null && !actionBarPopupWindow2.isShowing()))) {
                $$Lambda$ActionBarMenuItem$uY9Dm7GQwA1UenRdzdwLiGJ3aE r0 = new Runnable() {
                    public final void run() {
                        ActionBarMenuItem.this.lambda$onTouchEvent$0$ActionBarMenuItem();
                    }
                };
                this.showMenuRunnable = r0;
                AndroidUtilities.runOnUIThread(r0, 200);
            }
        } else if (event.getActionMasked() != 2) {
            ActionBarPopupWindow actionBarPopupWindow3 = this.popupWindow;
            if (actionBarPopupWindow3 == null || !actionBarPopupWindow3.isShowing() || event.getActionMasked() != 1) {
                View view = this.selectedMenuView;
                if (view != null) {
                    view.setSelected(false);
                    this.selectedMenuView = null;
                }
            } else {
                View view2 = this.selectedMenuView;
                if (view2 != null) {
                    view2.setSelected(false);
                    ActionBarMenu actionBarMenu = this.parentMenu;
                    if (actionBarMenu != null) {
                        actionBarMenu.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                    } else {
                        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
                        if (actionBarMenuItemDelegate != null) {
                            actionBarMenuItemDelegate.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                        }
                    }
                    this.popupWindow.dismiss(this.allowCloseAnimation);
                } else {
                    this.popupWindow.dismiss();
                }
            }
        } else if (!hasSubMenu() || ((actionBarPopupWindow = this.popupWindow) != null && (actionBarPopupWindow == null || actionBarPopupWindow.isShowing()))) {
            ActionBarPopupWindow actionBarPopupWindow4 = this.popupWindow;
            if (actionBarPopupWindow4 != null && actionBarPopupWindow4.isShowing()) {
                getLocationOnScreen(this.location);
                float x = event.getX() + ((float) this.location[0]);
                float y = event.getY();
                int[] iArr = this.location;
                float y2 = y + ((float) iArr[1]);
                this.popupLayout.getLocationOnScreen(iArr);
                int[] iArr2 = this.location;
                float x2 = x - ((float) iArr2[0]);
                float y3 = y2 - ((float) iArr2[1]);
                this.selectedMenuView = null;
                for (int a = 0; a < this.popupLayout.getItemsCount(); a++) {
                    View child = this.popupLayout.getItemAt(a);
                    child.getHitRect(this.rect);
                    if (((Integer) child.getTag()).intValue() < 100) {
                        if (!this.rect.contains((int) x2, (int) y3)) {
                            child.setPressed(false);
                            child.setSelected(false);
                            if (Build.VERSION.SDK_INT == 21) {
                                child.getBackground().setVisible(false, false);
                            }
                        } else {
                            child.setPressed(true);
                            child.setSelected(true);
                            if (Build.VERSION.SDK_INT >= 21) {
                                if (Build.VERSION.SDK_INT == 21) {
                                    child.getBackground().setVisible(true, false);
                                }
                                child.drawableHotspotChanged(x2, y3 - ((float) child.getTop()));
                            }
                            this.selectedMenuView = child;
                        }
                    }
                }
            }
        } else if (event.getY() > ((float) getHeight())) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            toggleSubMenu();
            return true;
        }
        return super.onTouchEvent(event);
    }

    public /* synthetic */ void lambda$onTouchEvent$0$ActionBarMenuItem() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        toggleSubMenu();
    }

    public void setDelegate(ActionBarMenuItemDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setIconColor(int color) {
        ImageView imageView = this.iconView;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
        TextView textView2 = this.textView;
        if (textView2 != null) {
            textView2.setTextColor(color);
        }
        ImageView imageView2 = this.clearButton;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setSubMenuOpenSide(int side) {
        this.subMenuOpenSide = side;
    }

    public void setLayoutInScreen(boolean value) {
        this.layoutInScreen = value;
    }

    private void createPopupLayout() {
        if (this.popupLayout == null) {
            this.rect = new Rect();
            this.location = new int[2];
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
            this.popupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setPadding(0, 0, 0, 0);
            this.popupLayout.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return ActionBarMenuItem.this.lambda$createPopupLayout$1$ActionBarMenuItem(view, motionEvent);
                }
            });
            this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    ActionBarMenuItem.this.lambda$createPopupLayout$2$ActionBarMenuItem(keyEvent);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$createPopupLayout$1$ActionBarMenuItem(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        v.getHitRect(this.rect);
        if (this.rect.contains((int) event.getX(), (int) event.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    public /* synthetic */ void lambda$createPopupLayout$2$ActionBarMenuItem(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public void removeAllSubItems() {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.removeInnerViews();
        }
    }

    public void addSubItem(View view, int width, int height) {
        createPopupLayout();
        this.popupLayout.addView(view, new LinearLayout.LayoutParams(width, height));
    }

    public void addSubItem(int id, View view, int width, int height) {
        createPopupLayout();
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        this.popupLayout.addView(view);
        view.setTag(Integer.valueOf(id));
        view.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ActionBarMenuItem.this.lambda$addSubItem$3$ActionBarMenuItem(view);
            }
        });
        view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    }

    public /* synthetic */ void lambda$addSubItem$3$ActionBarMenuItem(View view1) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (!this.processedPopupClick) {
                this.processedPopupClick = true;
                this.popupWindow.dismiss(this.allowCloseAnimation);
            } else {
                return;
            }
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view1.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view1.getTag()).intValue());
        }
    }

    public TextView addSubItem(int id, CharSequence text) {
        createPopupLayout();
        TextView textView2 = new TextView(getContext());
        textView2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        textView2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (!LocaleController.isRTL) {
            textView2.setGravity(16);
        } else {
            textView2.setGravity(21);
        }
        textView2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        textView2.setTextSize(1, 15.0f);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setTag(Integer.valueOf(id));
        textView2.setText(text);
        this.popupLayout.addView(textView2);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView2.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        textView2.setLayoutParams(layoutParams);
        textView2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ActionBarMenuItem.this.lambda$addSubItem$4$ActionBarMenuItem(view);
            }
        });
        return textView2;
    }

    public /* synthetic */ void lambda$addSubItem$4$ActionBarMenuItem(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (!this.processedPopupClick) {
                this.processedPopupClick = true;
                this.popupWindow.dismiss(this.allowCloseAnimation);
            } else {
                return;
            }
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public ActionBarMenuSubItem addSubItem(int id, int icon, CharSequence text) {
        createPopupLayout();
        ActionBarMenuSubItem cell = new ActionBarMenuSubItem(getContext());
        cell.setTextAndIcon(text, icon);
        cell.setTag(Integer.valueOf(id));
        this.popupLayout.addView(cell);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        cell.setLayoutParams(layoutParams);
        cell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ActionBarMenuItem.this.lambda$addSubItem$5$ActionBarMenuItem(view);
            }
        });
        return cell;
    }

    public /* synthetic */ void lambda$addSubItem$5$ActionBarMenuItem(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (!this.processedPopupClick) {
                this.processedPopupClick = true;
                this.popupWindow.dismiss(this.allowCloseAnimation);
            } else {
                return;
            }
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public void redrawPopup(int color) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.backgroundDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            this.popupLayout.invalidate();
        }
    }

    public void setPopupItemsColor(int color, boolean icon) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            int count = actionBarPopupWindowLayout.linearLayout.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.popupLayout.linearLayout.getChildAt(a);
                if (child instanceof TextView) {
                    ((TextView) child).setTextColor(color);
                } else if (child instanceof ActionBarMenuSubItem) {
                    if (icon) {
                        ((ActionBarMenuSubItem) child).setIconColor(color);
                    } else {
                        ((ActionBarMenuSubItem) child).setTextColor(color);
                    }
                }
            }
        }
    }

    public boolean hasSubMenu() {
        return this.popupLayout != null;
    }

    public void setMenuYOffset(int offset) {
        this.yOffset = offset;
    }

    public void toggleSubMenu() {
        if (this.popupLayout != null) {
            ActionBarMenu actionBarMenu = this.parentMenu;
            if (actionBarMenu == null || !actionBarMenu.isActionMode || this.parentMenu.parentActionBar == null || this.parentMenu.parentActionBar.isActionModeShowed()) {
                Runnable runnable = this.showMenuRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.showMenuRunnable = null;
                }
                ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
                if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
                    if (this.popupWindow == null) {
                        this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                        if (!this.animationEnabled || Build.VERSION.SDK_INT < 19) {
                            this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
                        } else {
                            this.popupWindow.setAnimationStyle(0);
                        }
                        boolean z = this.animationEnabled;
                        if (!z) {
                            this.popupWindow.setAnimationEnabled(z);
                        }
                        this.popupWindow.setOutsideTouchable(true);
                        this.popupWindow.setClippingEnabled(true);
                        if (this.layoutInScreen) {
                            this.popupWindow.setLayoutInScreen(true);
                        }
                        this.popupWindow.setInputMethodMode(2);
                        this.popupWindow.setSoftInputMode(0);
                        this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                        this.popupWindow.getContentView().setFocusableInTouchMode(true);
                        this.popupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
                            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                                return ActionBarMenuItem.this.lambda$toggleSubMenu$6$ActionBarMenuItem(view, i, keyEvent);
                            }
                        });
                    }
                    this.processedPopupClick = false;
                    this.popupWindow.setFocusable(true);
                    if (this.popupLayout.getMeasuredWidth() == 0) {
                        updateOrShowPopup(true, true);
                    } else {
                        updateOrShowPopup(true, false);
                    }
                    this.popupWindow.startAnimation();
                    return;
                }
                this.popupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ boolean lambda$toggleSubMenu$6$ActionBarMenuItem(View v, int keyCode, KeyEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyCode != 82 || event.getRepeatCount() != 0 || event.getAction() != 1 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        this.popupWindow.dismiss();
        return true;
    }

    public void openSearch(boolean openKeyboard) {
        ActionBarMenu actionBarMenu;
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null && frameLayout.getVisibility() != 0 && (actionBarMenu = this.parentMenu) != null) {
            actionBarMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(openKeyboard));
        }
    }

    public boolean toggleSearch(boolean openKeyboard) {
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout == null) {
            return false;
        }
        if (frameLayout.getVisibility() == 0) {
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
            if (actionBarMenuItemSearchListener == null || (actionBarMenuItemSearchListener != null && actionBarMenuItemSearchListener.canCollapseSearch())) {
                if (openKeyboard) {
                    AndroidUtilities.hideKeyboard(this.searchField);
                }
                this.searchField.setText("");
                this.searchContainer.setVisibility(8);
                this.searchField.clearFocus();
                setVisibility(0);
                ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
                if (actionBarMenuItemSearchListener2 != null) {
                    actionBarMenuItemSearchListener2.onSearchCollapse();
                }
            }
            return false;
        }
        this.searchContainer.setVisibility(0);
        setVisibility(8);
        this.searchField.setText("");
        this.searchField.requestFocus();
        if (openKeyboard) {
            AndroidUtilities.showKeyboard(this.searchField);
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener3 = this.listener;
        if (actionBarMenuItemSearchListener3 == null) {
            return true;
        }
        actionBarMenuItemSearchListener3.onSearchExpand();
        return true;
    }

    public void closeSubMenu() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public void setIcon(Drawable drawable) {
        ImageView imageView = this.iconView;
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    public void setIcon(int resId) {
        ImageView imageView = this.iconView;
        if (imageView != null) {
            imageView.setImageResource(resId);
        }
    }

    public void setText(CharSequence text) {
        TextView textView2 = this.textView;
        if (textView2 != null) {
            textView2.setText(text);
        }
    }

    public View getContentView() {
        ImageView imageView = this.iconView;
        return imageView != null ? imageView : this.textView;
    }

    public void setSearchFieldHint(CharSequence hint) {
        if (this.searchFieldCaption != null) {
            this.searchField.setHint(hint);
            setContentDescription(hint);
        }
    }

    public void setSearchFieldText(CharSequence text, boolean animated) {
        if (this.searchFieldCaption != null) {
            this.animateClear = animated;
            this.searchField.setText(text);
            if (!TextUtils.isEmpty(text)) {
                this.searchField.setSelection(text.length());
            }
        }
    }

    public void onSearchPressed() {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
        }
    }

    public EditTextBoldCursor getSearchField() {
        return this.searchField;
    }

    public ActionBarMenuItem setOverrideMenuClick(boolean value) {
        this.overrideMenuClick = value;
        return this;
    }

    public ActionBarMenuItem setIsSearchField(boolean value) {
        if (this.parentMenu == null) {
            return this;
        }
        if (value && this.searchContainer == null) {
            AnonymousClass1 r0 = new FrameLayout(getContext()) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width;
                    measureChildWithMargins(ActionBarMenuItem.this.clearButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, widthMeasureSpec, View.MeasureSpec.getSize(widthMeasureSpec) / 2, heightMeasureSpec, 0);
                        width = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                    } else {
                        width = 0;
                    }
                    measureChildWithMargins(ActionBarMenuItem.this.searchField, widthMeasureSpec, width, heightMeasureSpec, 0);
                    int size = View.MeasureSpec.getSize(widthMeasureSpec);
                    int size2 = View.MeasureSpec.getSize(heightMeasureSpec);
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int x;
                    super.onLayout(changed, left, top, right, bottom);
                    if (LocaleController.isRTL) {
                        x = 0;
                    } else if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        x = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                    } else {
                        x = 0;
                    }
                    ActionBarMenuItem.this.searchField.layout(x, ActionBarMenuItem.this.searchField.getTop(), ActionBarMenuItem.this.searchField.getMeasuredWidth() + x, ActionBarMenuItem.this.searchField.getBottom());
                }
            };
            this.searchContainer = r0;
            this.parentMenu.addView(r0, 0, LayoutHelper.createLinear(0, -1, 1.0f, 6, 0, 0, 0));
            this.searchContainer.setVisibility(8);
            TextView textView2 = new TextView(getContext());
            this.searchFieldCaption = textView2;
            textView2.setTextSize(1, 18.0f);
            this.searchFieldCaption.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSearch));
            this.searchFieldCaption.setSingleLine(true);
            this.searchFieldCaption.setEllipsize(TextUtils.TruncateAt.END);
            this.searchFieldCaption.setVisibility(8);
            this.searchFieldCaption.setGravity(LocaleController.isRTL ? 5 : 3);
            AnonymousClass2 r02 = new EditTextBoldCursor(getContext()) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {
                    if (keyCode != 67 || ActionBarMenuItem.this.searchField.length() != 0 || ActionBarMenuItem.this.searchFieldCaption.getVisibility() != 0 || ActionBarMenuItem.this.searchFieldCaption.length() <= 0) {
                        return super.onKeyDown(keyCode, event);
                    }
                    ActionBarMenuItem.this.clearButton.callOnClick();
                    return true;
                }

                public boolean onTouchEvent(MotionEvent event) {
                    if (event.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return super.onTouchEvent(event);
                }
            };
            this.searchField = r02;
            r02.setCursorWidth(1.5f);
            this.searchField.setCursorColor(Theme.getColor(Theme.key_actionBarDefaultSearch));
            this.searchField.setTextSize(1, 18.0f);
            this.searchField.setHintTextColor(Theme.getColor(Theme.key_actionBarDefaultSearchPlaceholder));
            this.searchField.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSearch));
            this.searchField.setSingleLine(true);
            this.searchField.setBackgroundResource(0);
            this.searchField.setPadding(0, 0, 0, 0);
            this.searchField.setInputType(this.searchField.getInputType() | 524288);
            if (Build.VERSION.SDK_INT < 23) {
                this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode mode) {
                    }

                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }
                });
            }
            this.searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ActionBarMenuItem.this.lambda$setIsSearchField$7$ActionBarMenuItem(textView, i, keyEvent);
                }
            });
            this.searchField.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (ActionBarMenuItem.this.ignoreOnTextChange) {
                        boolean unused = ActionBarMenuItem.this.ignoreOnTextChange = false;
                        return;
                    }
                    if (ActionBarMenuItem.this.listener != null) {
                        ActionBarMenuItem.this.listener.onTextChanged(ActionBarMenuItem.this.searchField);
                    }
                    if (ActionBarMenuItem.this.clearButton == null) {
                        return;
                    }
                    if (!TextUtils.isEmpty(s) || ((ActionBarMenuItem.this.listener != null && ActionBarMenuItem.this.listener.forceShowClear()) || (ActionBarMenuItem.this.searchFieldCaption != null && ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0))) {
                        if (ActionBarMenuItem.this.clearButton.getTag() == null) {
                            ActionBarMenuItem.this.clearButton.setTag(1);
                            ActionBarMenuItem.this.clearButton.clearAnimation();
                            ActionBarMenuItem.this.clearButton.setVisibility(0);
                            if (ActionBarMenuItem.this.animateClear) {
                                ActionBarMenuItem.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(1.0f).setDuration(180).scaleY(1.0f).scaleX(1.0f).rotation(0.0f).start();
                                return;
                            }
                            ActionBarMenuItem.this.clearButton.setAlpha(1.0f);
                            ActionBarMenuItem.this.clearButton.setRotation(0.0f);
                            ActionBarMenuItem.this.clearButton.setScaleX(1.0f);
                            ActionBarMenuItem.this.clearButton.setScaleY(1.0f);
                            boolean unused2 = ActionBarMenuItem.this.animateClear = true;
                        }
                    } else if (ActionBarMenuItem.this.clearButton.getTag() != null) {
                        ActionBarMenuItem.this.clearButton.setTag((Object) null);
                        ActionBarMenuItem.this.clearButton.clearAnimation();
                        if (ActionBarMenuItem.this.animateClear) {
                            ActionBarMenuItem.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(0.0f).setDuration(180).scaleY(0.0f).scaleX(0.0f).rotation(45.0f).withEndAction(new Runnable() {
                                public final void run() {
                                    ActionBarMenuItem.AnonymousClass4.this.lambda$onTextChanged$0$ActionBarMenuItem$4();
                                }
                            }).start();
                            return;
                        }
                        ActionBarMenuItem.this.clearButton.setAlpha(0.0f);
                        ActionBarMenuItem.this.clearButton.setRotation(45.0f);
                        ActionBarMenuItem.this.clearButton.setScaleX(0.0f);
                        ActionBarMenuItem.this.clearButton.setScaleY(0.0f);
                        ActionBarMenuItem.this.clearButton.setVisibility(4);
                        boolean unused3 = ActionBarMenuItem.this.animateClear = true;
                    }
                }

                public /* synthetic */ void lambda$onTextChanged$0$ActionBarMenuItem$4() {
                    ActionBarMenuItem.this.clearButton.setVisibility(4);
                }

                public void afterTextChanged(Editable s) {
                }
            });
            this.searchField.setImeOptions(33554435);
            this.searchField.setTextIsSelectable(false);
            if (!LocaleController.isRTL) {
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2.0f, 36.0f, 19, 0.0f, 5.5f, 0.0f, 0.0f));
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1.0f, 36.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
            } else {
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1.0f, 36.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2.0f, 36.0f, 21, 0.0f, 5.5f, 48.0f, 0.0f));
            }
            AnonymousClass5 r1 = new ImageView(getContext()) {
                /* access modifiers changed from: protected */
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    clearAnimation();
                    if (getTag() == null) {
                        ActionBarMenuItem.this.clearButton.setVisibility(4);
                        ActionBarMenuItem.this.clearButton.setAlpha(0.0f);
                        ActionBarMenuItem.this.clearButton.setRotation(45.0f);
                        ActionBarMenuItem.this.clearButton.setScaleX(0.0f);
                        ActionBarMenuItem.this.clearButton.setScaleY(0.0f);
                        return;
                    }
                    ActionBarMenuItem.this.clearButton.setAlpha(1.0f);
                    ActionBarMenuItem.this.clearButton.setRotation(0.0f);
                    ActionBarMenuItem.this.clearButton.setScaleX(1.0f);
                    ActionBarMenuItem.this.clearButton.setScaleY(1.0f);
                }
            };
            this.clearButton = r1;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            r1.setImageDrawable(closeProgressDrawable2);
            this.clearButton.setColorFilter(new PorterDuffColorFilter(this.parentMenu.parentActionBar.itemsColor, PorterDuff.Mode.MULTIPLY));
            this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
            this.clearButton.setAlpha(0.0f);
            this.clearButton.setRotation(45.0f);
            this.clearButton.setScaleX(0.0f);
            this.clearButton.setScaleY(0.0f);
            this.clearButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ActionBarMenuItem.this.lambda$setIsSearchField$8$ActionBarMenuItem(view);
                }
            });
            this.clearButton.setContentDescription(LocaleController.getString("ClearButton", R.string.ClearButton));
            this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
        }
        this.isSearchField = value;
        return this;
    }

    public /* synthetic */ boolean lambda$setIsSearchField$7$ActionBarMenuItem(TextView v, int actionId, KeyEvent event) {
        if (event == null) {
            return false;
        }
        if ((event.getAction() != 1 || event.getKeyCode() != 84) && (event.getAction() != 0 || event.getKeyCode() != 66)) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.searchField);
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener == null) {
            return false;
        }
        actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
        return false;
    }

    public /* synthetic */ void lambda$setIsSearchField$8$ActionBarMenuItem(View v) {
        if (this.searchField.length() != 0) {
            this.searchField.setText("");
        } else {
            TextView textView2 = this.searchFieldCaption;
            if (textView2 != null && textView2.getVisibility() == 0) {
                this.searchFieldCaption.setVisibility(8);
                ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
                if (actionBarMenuItemSearchListener != null) {
                    actionBarMenuItemSearchListener.onCaptionCleared();
                }
            }
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    public void setShowSearchProgress(boolean show) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 != null) {
            if (show) {
                closeProgressDrawable2.startAnimation();
            } else {
                closeProgressDrawable2.stopAnimation();
            }
        }
    }

    public void setSearchFieldCaption(CharSequence caption) {
        if (this.searchFieldCaption != null) {
            if (TextUtils.isEmpty(caption)) {
                this.searchFieldCaption.setVisibility(8);
                return;
            }
            this.searchFieldCaption.setVisibility(0);
            this.searchFieldCaption.setText(caption);
        }
    }

    public void setIgnoreOnTextChange() {
        this.ignoreOnTextChange = true;
    }

    public boolean isSearchField() {
        return this.isSearchField;
    }

    public void clearSearchText() {
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.setText("");
        }
    }

    public ActionBarMenuItem setActionBarMenuItemSearchListener(ActionBarMenuItemSearchListener listener2) {
        this.listener = listener2;
        return this;
    }

    public ActionBarMenuItem setAllowCloseAnimation(boolean value) {
        this.allowCloseAnimation = value;
        return this;
    }

    public void setPopupAnimationEnabled(boolean value) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.setAnimationEnabled(value);
        }
        this.animationEnabled = value;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            updateOrShowPopup(false, true);
        }
    }

    public void setAdditionalYOffset(int value) {
        this.additionalYOffset = value;
    }

    public void setAdditionalXOffset(int value) {
        this.additionalXOffset = value;
    }

    private void updateOrShowPopup(boolean show, boolean update) {
        int offsetY = 0;
        if (this.parentMenu == null) {
            float scaleY = getScaleY();
            offsetY = (-((int) ((((float) getMeasuredHeight()) * scaleY) - ((this.subMenuOpenSide != 2 ? getTranslationY() : 0.0f) / scaleY)))) + this.additionalYOffset;
        }
        int offsetY2 = offsetY + this.yOffset;
        if (show) {
            this.popupLayout.scrollToTop();
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            ActionBar actionBar = actionBarMenu.parentActionBar;
            if (this.subMenuOpenSide == 0) {
                if (show) {
                    this.popupWindow.showAsDropDown(actionBar, (((getLeft() + this.parentMenu.getLeft()) + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth()) + ((int) getTranslationX()), offsetY2);
                }
                if (update) {
                    this.popupWindow.update(actionBar, ((int) getTranslationX()) + (((getLeft() + this.parentMenu.getLeft()) + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth()), offsetY2, -1, -1);
                }
            } else {
                if (show) {
                    this.popupWindow.showAsDropDown(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), offsetY2);
                }
                if (update) {
                    this.popupWindow.update(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), offsetY2, -1, -1);
                }
            }
            this.popupWindow.dimBehind();
            return;
        }
        int i = this.subMenuOpenSide;
        if (i == 0) {
            if (getParent() != null) {
                View parent = (View) getParent();
                if (show) {
                    this.popupWindow.showAsDropDown(parent, ((getLeft() + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth()) + this.additionalXOffset, offsetY2);
                }
                if (update) {
                    this.popupWindow.update(parent, this.additionalXOffset + ((getLeft() + getMeasuredWidth()) - this.popupLayout.getMeasuredWidth()), offsetY2, -1, -1);
                }
            }
        } else if (i == 1) {
            if (show) {
                this.popupWindow.showAsDropDown(this, (-AndroidUtilities.dp(8.0f)) + this.additionalXOffset, offsetY2);
            }
            if (update) {
                this.popupWindow.update(this, (-AndroidUtilities.dp(8.0f)) + this.additionalXOffset, offsetY2, -1, -1);
            }
        } else {
            if (show) {
                this.popupWindow.showAsDropDown(this, (getMeasuredWidth() - this.popupLayout.getMeasuredWidth()) + this.additionalXOffset, offsetY2);
            }
            if (update) {
                this.popupWindow.update(this, (getMeasuredWidth() - this.popupLayout.getMeasuredWidth()) + this.additionalXOffset, offsetY2, -1, -1);
            }
        }
    }

    public void hideSubItem(int id) {
        View view;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && (view = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(id))) != null && view.getVisibility() != 8) {
            view.setVisibility(8);
        }
    }

    public boolean isSubItemVisible(int id) {
        View view;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null || (view = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(id))) == null || view.getVisibility() != 0) {
            return false;
        }
        return true;
    }

    public void showSubItem(int id) {
        View view;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && (view = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(id))) != null && view.getVisibility() != 0) {
            view.setVisibility(0);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.ImageButton");
    }
}
