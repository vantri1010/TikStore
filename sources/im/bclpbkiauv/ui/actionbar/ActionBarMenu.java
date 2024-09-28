package im.bclpbkiauv.ui.actionbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class ActionBarMenu extends LinearLayout {
    protected boolean isActionMode;
    protected ActionBar parentActionBar;

    public ActionBarMenu(Context context, ActionBar layer) {
        super(context);
        setOrientation(0);
        this.parentActionBar = layer;
    }

    public ActionBarMenu(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void updateItemsBackgroundColor() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                view.setBackgroundDrawable(Theme.createSelectorDrawable(this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateItemsColor() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) view).setIconColor(this.isActionMode ? this.parentActionBar.itemsActionModeColor : this.parentActionBar.itemsColor);
            }
        }
    }

    public ActionBarMenuItem addItem(int id, Drawable drawable) {
        return addItem(id, 0, (CharSequence) null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, drawable, AndroidUtilities.dp(48.0f), (CharSequence) null, 0, 0);
    }

    public ActionBarMenuItem addItem(int id, int icon) {
        return addItem(id, icon, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor);
    }

    public ActionBarMenuItem addItem(int id, CharSequence text) {
        return addItem(id, 0, text, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, (Drawable) null, 0, text, 0, 0);
    }

    public ActionBarMenuItem addItem(int id, int icon, int backgroundColor) {
        return addItem(id, icon, (CharSequence) null, backgroundColor, (Drawable) null, AndroidUtilities.dp(48.0f), (CharSequence) null, 0, 0);
    }

    public ActionBarMenuItem addItemWithWidth(int id, int icon, int width) {
        return addItem(id, icon, (CharSequence) null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, (Drawable) null, width, (CharSequence) null, 0, 0);
    }

    public ActionBarMenuItem addItemWithWidth(int id, int icon, int width, int marginStart, int marginEnd) {
        return addItem(id, icon, (CharSequence) null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, (Drawable) null, width, (CharSequence) null, marginStart, marginEnd);
    }

    public ActionBarMenuItem addItemWithWidth(int id, int icon, int width, CharSequence title) {
        return addItem(id, icon, (CharSequence) null, this.isActionMode ? this.parentActionBar.itemsActionModeBackgroundColor : this.parentActionBar.itemsBackgroundColor, (Drawable) null, width, title, 0, 0);
    }

    public ActionBarMenuItem addItem(int id, int icon, CharSequence text, int backgroundColor, Drawable drawable, int width, CharSequence title, int marginStart, int marginEnd) {
        int i = icon;
        CharSequence charSequence = text;
        Drawable drawable2 = drawable;
        int i2 = width;
        CharSequence charSequence2 = title;
        int i3 = marginStart;
        int i4 = marginEnd;
        ActionBarMenuItem menuItem = new ActionBarMenuItem(getContext(), this, backgroundColor, this.isActionMode ? this.parentActionBar.itemsActionModeColor : this.parentActionBar.itemsColor, charSequence != null);
        menuItem.setTag(Integer.valueOf(id));
        if (charSequence != null) {
            menuItem.textView.setText(charSequence);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i2 != 0 ? i2 : -2, -1);
            int dp = AndroidUtilities.dp(14.0f);
            layoutParams.rightMargin = dp;
            layoutParams.leftMargin = dp;
            addView(menuItem, layoutParams);
        } else {
            if (drawable2 != null) {
                menuItem.iconView.setImageDrawable(drawable2);
            } else if (i != 0) {
                menuItem.iconView.setImageResource(i);
            }
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(i2, -1);
            if (i3 != 0) {
                layoutParams2.leftMargin = AndroidUtilities.dp((float) i3);
            }
            if (i4 != 0) {
                layoutParams2.rightMargin = AndroidUtilities.dp((float) i4);
            }
            addView(menuItem, layoutParams2);
        }
        menuItem.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ActionBarMenu.this.lambda$addItem$0$ActionBarMenu(view);
            }
        });
        if (charSequence2 != null) {
            menuItem.setContentDescription(charSequence2);
        }
        return menuItem;
    }

    public /* synthetic */ void lambda$addItem$0$ActionBarMenu(View view) {
        ActionBarMenuItem item = (ActionBarMenuItem) view;
        if (item.hasSubMenu()) {
            if (this.parentActionBar.actionBarMenuOnItemClick.canOpenMenu()) {
                item.toggleSubMenu();
            }
        } else if (item.isSearchField()) {
            this.parentActionBar.onSearchFieldVisibilityChanged(item.toggleSearch(true));
        } else {
            onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public View addItemView(int id, View view) {
        view.setTag(Integer.valueOf(id));
        addView(view, LayoutHelper.createLinear(-2, -1, 0.0f, 0.0f, 15.0f, 0.0f));
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ActionBarMenu.this.onItemClick(((Integer) view.getTag()).intValue());
            }
        });
        return view;
    }

    public View addItemView(int id, View view, ViewGroup.LayoutParams layoutParams) {
        view.setTag(Integer.valueOf(id));
        addView(view, layoutParams);
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ActionBarMenu.this.onItemClick(((Integer) view.getTag()).intValue());
            }
        });
        return view;
    }

    public View addRightItemView(int id, CharSequence value) {
        TextView view = new TextView(getContext());
        view.setTextSize((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 13.0f : 12.0f);
        view.setText(value);
        view.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        view.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        view.setTag(Integer.valueOf(id));
        view.setGravity(16);
        addView(view, LayoutHelper.createLinear(-2, -1, 0.0f, 0.0f, 15.0f, 0.0f));
        view.setBackground(Theme.getSelectorDrawable(false));
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ActionBarMenu.this.onItemClick(((Integer) view.getTag()).intValue());
            }
        });
        return view;
    }

    public void hideAllPopupMenus() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) view).closeSubMenu();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setPopupItemsColor(int color, boolean icon) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) view).setPopupItemsColor(color, icon);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void redrawPopup(int color) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ((ActionBarMenuItem) view).redrawPopup(color);
            }
        }
    }

    public void onItemClick(int id) {
        if (this.parentActionBar.actionBarMenuOnItemClick != null) {
            this.parentActionBar.actionBarMenuOnItemClick.onItemClick(id);
        }
    }

    public void clearItems() {
        removeAllViews();
    }

    public void onMenuButtonPressed() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.getVisibility() != 0) {
                    continue;
                } else if (item.hasSubMenu()) {
                    item.toggleSubMenu();
                    return;
                } else if (item.overrideMenuClick) {
                    onItemClick(((Integer) item.getTag()).intValue());
                    return;
                }
            }
        }
    }

    public void closeSearchField(boolean closeKeyboard) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField()) {
                    this.parentActionBar.onSearchFieldVisibilityChanged(false);
                    item.toggleSearch(closeKeyboard);
                    return;
                }
            }
        }
    }

    public void setSearchTextColor(int color, boolean placeholder) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField()) {
                    if (placeholder) {
                        item.getSearchField().setHintTextColor(color);
                        return;
                    } else {
                        item.getSearchField().setTextColor(color);
                        return;
                    }
                }
            }
        }
    }

    public void setSearchFieldText(String text) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField()) {
                    item.setSearchFieldText(text, false);
                    item.getSearchField().setSelection(text.length());
                    return;
                }
            }
        }
    }

    public void onSearchPressed() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField()) {
                    item.onSearchPressed();
                    return;
                }
            }
        }
    }

    public void openSearchField(boolean toggle, String text, boolean animated) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View view = getChildAt(a);
            if (view instanceof ActionBarMenuItem) {
                ActionBarMenuItem item = (ActionBarMenuItem) view;
                if (item.isSearchField()) {
                    if (toggle) {
                        this.parentActionBar.onSearchFieldVisibilityChanged(item.toggleSearch(true));
                    }
                    item.setSearchFieldText(text, animated);
                    item.getSearchField().setSelection(text.length());
                    return;
                }
            }
        }
    }

    public ActionBarMenuItem getItem(int id) {
        View v = findViewWithTag(Integer.valueOf(id));
        if (v instanceof ActionBarMenuItem) {
            return (ActionBarMenuItem) v;
        }
        return null;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).setEnabled(enabled);
        }
    }
}
