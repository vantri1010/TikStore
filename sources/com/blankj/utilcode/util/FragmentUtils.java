package com.blankj.utilcode.util;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class FragmentUtils {
    private static final String ARGS_ID = "args_id";
    private static final String ARGS_IS_ADD_STACK = "args_is_add_stack";
    private static final String ARGS_IS_HIDE = "args_is_hide";
    private static final String ARGS_TAG = "args_tag";
    private static final int TYPE_ADD_FRAGMENT = 1;
    private static final int TYPE_HIDE_FRAGMENT = 4;
    private static final int TYPE_REMOVE_FRAGMENT = 32;
    private static final int TYPE_REMOVE_TO_FRAGMENT = 64;
    private static final int TYPE_REPLACE_FRAGMENT = 16;
    private static final int TYPE_SHOW_FRAGMENT = 2;
    private static final int TYPE_SHOW_HIDE_FRAGMENT = 8;

    public interface OnBackClickListener {
        boolean onBackClick();
    }

    private FragmentUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void add(FragmentManager fm, Fragment add, int containerId) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, (String) null, false, false);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, boolean isHide) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, (String) null, isHide, false);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, boolean isHide, boolean isAddStack) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, (String) null, isHide, isAddStack);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, int enterAnim, int exitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, (String) null, false, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, boolean isAddStack, int enterAnim, int exitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, (String) null, isAddStack, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, (String) null, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 8, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, (String) null, isAddStack, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 8, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, View... sharedElements) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add == null) {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (sharedElements != null) {
            add(fm, add, containerId, (String) null, false, sharedElements);
        } else {
            throw new NullPointerException("Argument 'sharedElements' of type View[] (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, boolean isAddStack, View... sharedElements) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add == null) {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (sharedElements != null) {
            add(fm, add, containerId, (String) null, isAddStack, sharedElements);
        } else {
            throw new NullPointerException("Argument 'sharedElements' of type View[] (#4 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, List<Fragment> adds, int containerId, int showIndex) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (adds != null) {
            add(fm, (Fragment[]) adds.toArray(new Fragment[0]), containerId, (String[]) null, showIndex);
        } else {
            throw new NullPointerException("Argument 'adds' of type List<Fragment> (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment[] adds, int containerId, int showIndex) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (adds != null) {
            add(fm, adds, containerId, (String[]) null, showIndex);
        } else {
            throw new NullPointerException("Argument 'adds' of type Fragment[] (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, tag, false, false);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, boolean isHide) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, tag, isHide, false);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, boolean isHide, boolean isAddStack) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            putArgs(add, new Args(containerId, tag, isHide, isAddStack));
            operateNoAnim(fm, 1, (Fragment) null, add);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, int enterAnim, int exitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, tag, false, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, boolean isAddStack, int enterAnim, int exitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, tag, isAddStack, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 8, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            add(fm, add, containerId, tag, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 8, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 9, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add != null) {
            FragmentTransaction ft = fm.beginTransaction();
            putArgs(add, new Args(containerId, tag, false, isAddStack));
            addAnim(ft, enterAnim, exitAnim, popEnterAnim, popExitAnim);
            operate(1, fm, ft, (Fragment) null, add);
        } else {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 9, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, View... sharedElements) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add == null) {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (sharedElements != null) {
            add(fm, add, containerId, tag, false, sharedElements);
        } else {
            throw new NullPointerException("Argument 'sharedElements' of type View[] (#4 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, boolean isAddStack, View... sharedElements) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (add == null) {
            throw new NullPointerException("Argument 'add' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (sharedElements != null) {
            FragmentTransaction ft = fm.beginTransaction();
            putArgs(add, new Args(containerId, tag, false, isAddStack));
            addSharedElement(ft, sharedElements);
            operate(1, fm, ft, (Fragment) null, add);
        } else {
            throw new NullPointerException("Argument 'sharedElements' of type View[] (#5 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, List<Fragment> adds, int containerId, String[] tags, int showIndex) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (adds != null) {
            add(fm, (Fragment[]) adds.toArray(new Fragment[0]), containerId, tags, showIndex);
        } else {
            throw new NullPointerException("Argument 'adds' of type List<Fragment> (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void add(FragmentManager fm, Fragment[] adds, int containerId, String[] tags, int showIndex) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (adds != null) {
            if (tags == null) {
                int i = 0;
                int len = adds.length;
                while (i < len) {
                    putArgs(adds[i], new Args(containerId, (String) null, showIndex != i, false));
                    i++;
                }
            } else {
                int i2 = 0;
                int len2 = adds.length;
                while (i2 < len2) {
                    putArgs(adds[i2], new Args(containerId, tags[i2], showIndex != i2, false));
                    i2++;
                }
            }
            operateNoAnim(fm, 1, (Fragment) null, adds);
        } else {
            throw new NullPointerException("Argument 'adds' of type Fragment[] (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void show(Fragment show) {
        if (show != null) {
            putArgs(show, false);
            operateNoAnim(show.getFragmentManager(), 2, (Fragment) null, show);
            return;
        }
        throw new NullPointerException("Argument 'show' of type Fragment (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void show(FragmentManager fm) {
        if (fm != null) {
            List<Fragment> fragments = getFragments(fm);
            for (Fragment show : fragments) {
                putArgs(show, false);
            }
            operateNoAnim(fm, 2, (Fragment) null, (Fragment[]) fragments.toArray(new Fragment[0]));
            return;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void hide(Fragment hide) {
        if (hide != null) {
            putArgs(hide, true);
            operateNoAnim(hide.getFragmentManager(), 4, (Fragment) null, hide);
            return;
        }
        throw new NullPointerException("Argument 'hide' of type Fragment (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void hide(FragmentManager fm) {
        if (fm != null) {
            List<Fragment> fragments = getFragments(fm);
            for (Fragment hide : fragments) {
                putArgs(hide, true);
            }
            operateNoAnim(fm, 4, (Fragment) null, (Fragment[]) fragments.toArray(new Fragment[0]));
            return;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void showHide(int showIndex, List<Fragment> fragments) {
        if (fragments != null) {
            showHide(fragments.get(showIndex), fragments);
            return;
        }
        throw new NullPointerException("Argument 'fragments' of type List<Fragment> (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void showHide(Fragment show, List<Fragment> hide) {
        if (show == null) {
            throw new NullPointerException("Argument 'show' of type Fragment (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (hide != null) {
            Iterator<Fragment> it = hide.iterator();
            while (true) {
                boolean z = false;
                if (it.hasNext()) {
                    Fragment fragment = it.next();
                    if (fragment != show) {
                        z = true;
                    }
                    putArgs(fragment, z);
                } else {
                    operateNoAnim(show.getFragmentManager(), 8, show, (Fragment[]) hide.toArray(new Fragment[0]));
                    return;
                }
            }
        } else {
            throw new NullPointerException("Argument 'hide' of type List<Fragment> (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void showHide(int showIndex, Fragment... fragments) {
        if (fragments != null) {
            showHide(fragments[showIndex], fragments);
            return;
        }
        throw new NullPointerException("Argument 'fragments' of type Fragment[] (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void showHide(Fragment show, Fragment... hide) {
        if (show == null) {
            throw new NullPointerException("Argument 'show' of type Fragment (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (hide != null) {
            int length = hide.length;
            for (int i = 0; i < length; i++) {
                Fragment fragment = hide[i];
                putArgs(fragment, fragment != show);
            }
            operateNoAnim(show.getFragmentManager(), 8, show, hide);
        } else {
            throw new NullPointerException("Argument 'hide' of type Fragment[] (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void showHide(Fragment show, Fragment hide) {
        if (show == null) {
            throw new NullPointerException("Argument 'show' of type Fragment (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (hide != null) {
            putArgs(show, false);
            putArgs(hide, true);
            operateNoAnim(show.getFragmentManager(), 8, show, hide);
        } else {
            throw new NullPointerException("Argument 'hide' of type Fragment (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, (String) null, false);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, boolean isAddStack) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, (String) null, isAddStack);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, int enterAnim, int exitAnim) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, (String) null, false, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, boolean isAddStack, int enterAnim, int exitAnim) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, (String) null, isAddStack, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, (String) null, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, (String) null, isAddStack, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, View... sharedElements) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, (String) null, false, sharedElements);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, boolean isAddStack, View... sharedElements) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, (String) null, isAddStack, sharedElements);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, (String) null, false);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, boolean isAddStack) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, (String) null, isAddStack);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, int enterAnim, int exitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, (String) null, false, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, boolean isAddStack, int enterAnim, int exitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, (String) null, isAddStack, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, (String) null, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 8, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, (String) null, isAddStack, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 8, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, View... sharedElements) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, (String) null, false, sharedElements);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, boolean isAddStack, View... sharedElements) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, (String) null, isAddStack, sharedElements);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, destTag, false);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, boolean isAddStack) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            FragmentManager fm = srcFragment.getFragmentManager();
            if (fm != null) {
                replace(fm, destFragment, getArgs(srcFragment).id, destTag, isAddStack);
            }
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, int enterAnim, int exitAnim) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, destTag, false, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, boolean isAddStack, int enterAnim, int exitAnim) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, destTag, isAddStack, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, destTag, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 8, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            FragmentManager fm = srcFragment.getFragmentManager();
            if (fm != null) {
                replace(fm, destFragment, getArgs(srcFragment).id, destTag, isAddStack, enterAnim, exitAnim, popEnterAnim, popExitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 8, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, View... sharedElements) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            replace(srcFragment, destFragment, destTag, false, sharedElements);
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, boolean isAddStack, View... sharedElements) {
        if (srcFragment == null) {
            throw new NullPointerException("Argument 'srcFragment' of type Fragment (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (destFragment != null) {
            FragmentManager fm = srcFragment.getFragmentManager();
            if (fm != null) {
                replace(fm, destFragment, getArgs(srcFragment).id, destTag, isAddStack, sharedElements);
            }
        } else {
            throw new NullPointerException("Argument 'destFragment' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, destTag, false);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, boolean isAddStack) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            putArgs(fragment, new Args(containerId, destTag, false, isAddStack));
            operate(16, fm, ft, (Fragment) null, fragment);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, int enterAnim, int exitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, destTag, false, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, boolean isAddStack, int enterAnim, int exitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, destTag, isAddStack, enterAnim, exitAnim, 0, 0);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 8, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, destTag, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 8, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 9, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            putArgs(fragment, new Args(containerId, destTag, false, isAddStack));
            addAnim(ft, enterAnim, exitAnim, popEnterAnim, popExitAnim);
            operate(16, fm, ft, (Fragment) null, fragment);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 9, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, View... sharedElements) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            replace(fm, fragment, containerId, destTag, false, sharedElements);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, boolean isAddStack, View... sharedElements) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            putArgs(fragment, new Args(containerId, destTag, false, isAddStack));
            addSharedElement(ft, sharedElements);
            operate(16, fm, ft, (Fragment) null, fragment);
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void pop(FragmentManager fm) {
        if (fm != null) {
            pop(fm, true);
            return;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void pop(FragmentManager fm, boolean isImmediate) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isImmediate) {
            fm.popBackStackImmediate();
        } else {
            fm.popBackStack();
        }
    }

    public static void popTo(FragmentManager fm, Class<? extends Fragment> popClz, boolean isIncludeSelf) {
        if (fm != null) {
            popTo(fm, popClz, isIncludeSelf, true);
            return;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void popTo(FragmentManager fm, Class<? extends Fragment> popClz, boolean isIncludeSelf, boolean isImmediate) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isImmediate) {
            fm.popBackStackImmediate(popClz.getName(), (int) isIncludeSelf);
        } else {
            fm.popBackStack(popClz.getName(), (int) isIncludeSelf);
        }
    }

    public static void popAll(FragmentManager fm) {
        if (fm != null) {
            popAll(fm, true);
            return;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void popAll(FragmentManager fm, boolean isImmediate) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fm.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(0);
            if (isImmediate) {
                fm.popBackStackImmediate(entry.getId(), 1);
            } else {
                fm.popBackStack(entry.getId(), 1);
            }
        }
    }

    public static void remove(Fragment remove) {
        if (remove != null) {
            operateNoAnim(remove.getFragmentManager(), 32, (Fragment) null, remove);
            return;
        }
        throw new NullPointerException("Argument 'remove' of type Fragment (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void removeTo(Fragment removeTo, boolean isIncludeSelf) {
        if (removeTo != null) {
            operateNoAnim(removeTo.getFragmentManager(), 64, isIncludeSelf ? removeTo : null, removeTo);
            return;
        }
        throw new NullPointerException("Argument 'removeTo' of type Fragment (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void removeAll(FragmentManager fm) {
        if (fm != null) {
            operateNoAnim(fm, 32, (Fragment) null, (Fragment[]) getFragments(fm).toArray(new Fragment[0]));
            return;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static void putArgs(Fragment fragment, Args args) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        bundle.putInt(ARGS_ID, args.id);
        bundle.putBoolean(ARGS_IS_HIDE, args.isHide);
        bundle.putBoolean(ARGS_IS_ADD_STACK, args.isAddStack);
        bundle.putString(ARGS_TAG, args.tag);
    }

    private static void putArgs(Fragment fragment, boolean isHide) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        bundle.putBoolean(ARGS_IS_HIDE, isHide);
    }

    private static Args getArgs(Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = Bundle.EMPTY;
        }
        return new Args(bundle.getInt(ARGS_ID, fragment.getId()), bundle.getBoolean(ARGS_IS_HIDE), bundle.getBoolean(ARGS_IS_ADD_STACK));
    }

    private static void operateNoAnim(FragmentManager fm, int type, Fragment src, Fragment... dest) {
        if (fm != null) {
            operate(type, fm, fm.beginTransaction(), src, dest);
        }
    }

    private static void operate(int type, FragmentManager fm, FragmentTransaction ft, Fragment src, Fragment... dest) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (src == null || !src.isRemoving()) {
            int i = 0;
            if (type == 1) {
                int length = dest.length;
                while (i < length) {
                    Fragment fragment = dest[i];
                    Bundle args = fragment.getArguments();
                    if (args != null) {
                        String name = args.getString(ARGS_TAG, fragment.getClass().getName());
                        Fragment fragmentByTag = fm.findFragmentByTag(name);
                        if (fragmentByTag != null && fragmentByTag.isAdded()) {
                            ft.remove(fragmentByTag);
                        }
                        ft.add(args.getInt(ARGS_ID), fragment, name);
                        if (args.getBoolean(ARGS_IS_HIDE)) {
                            ft.hide(fragment);
                        }
                        if (args.getBoolean(ARGS_IS_ADD_STACK)) {
                            ft.addToBackStack(name);
                        }
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (type == 2) {
                int length2 = dest.length;
                while (i < length2) {
                    ft.show(dest[i]);
                    i++;
                }
            } else if (type == 4) {
                int length3 = dest.length;
                while (i < length3) {
                    ft.hide(dest[i]);
                    i++;
                }
            } else if (type == 8) {
                ft.show(src);
                int length4 = dest.length;
                while (i < length4) {
                    Fragment fragment2 = dest[i];
                    if (fragment2 != src) {
                        ft.hide(fragment2);
                    }
                    i++;
                }
            } else if (type == 16) {
                Bundle args2 = dest[0].getArguments();
                if (args2 != null) {
                    String name2 = args2.getString(ARGS_TAG, dest[0].getClass().getName());
                    ft.replace(args2.getInt(ARGS_ID), dest[0], name2);
                    if (args2.getBoolean(ARGS_IS_ADD_STACK)) {
                        ft.addToBackStack(name2);
                    }
                } else {
                    return;
                }
            } else if (type == 32) {
                int i2 = dest.length;
                while (i < i2) {
                    Fragment fragment3 = dest[i];
                    if (fragment3 != src) {
                        ft.remove(fragment3);
                    }
                    i++;
                }
            } else if (type == 64) {
                int i3 = dest.length - 1;
                while (true) {
                    if (i3 < 0) {
                        break;
                    }
                    Fragment fragment4 = dest[i3];
                    if (fragment4 != dest[0]) {
                        ft.remove(fragment4);
                        i3--;
                    } else if (src != null) {
                        ft.remove(fragment4);
                    }
                }
            }
            ft.commitAllowingStateLoss();
        } else {
            Log.e("FragmentUtils", src.getClass().getName() + " is isRemoving");
        }
    }

    private static void addAnim(FragmentTransaction ft, int enter, int exit, int popEnter, int popExit) {
        ft.setCustomAnimations(enter, exit, popEnter, popExit);
    }

    private static void addSharedElement(FragmentTransaction ft, View... views) {
        if (Build.VERSION.SDK_INT >= 21) {
            for (View view : views) {
                ft.addSharedElement(view, view.getTransitionName());
            }
        }
    }

    public static Fragment getTop(FragmentManager fm) {
        if (fm != null) {
            return getTopIsInStack(fm, (Fragment) null, false);
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Fragment getTopInStack(FragmentManager fm) {
        if (fm != null) {
            return getTopIsInStack(fm, (Fragment) null, true);
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static Fragment getTopIsInStack(FragmentManager fm, Fragment parentFragment, boolean isInStack) {
        if (fm != null) {
            List<Fragment> fragments = getFragments(fm);
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (fragment != null) {
                    if (!isInStack) {
                        return getTopIsInStack(fragment.getChildFragmentManager(), parentFragment, false);
                    }
                    Bundle args = fragment.getArguments();
                    if (args != null && args.getBoolean(ARGS_IS_ADD_STACK)) {
                        return getTopIsInStack(fragment.getChildFragmentManager(), parentFragment, true);
                    }
                }
            }
            return null;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Fragment getTopShow(FragmentManager fm) {
        if (fm != null) {
            return getTopShowIsInStack(fm, (Fragment) null, false);
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Fragment getTopShowInStack(FragmentManager fm) {
        if (fm != null) {
            return getTopShowIsInStack(fm, (Fragment) null, true);
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static Fragment getTopShowIsInStack(FragmentManager fm, Fragment parentFragment, boolean isInStack) {
        if (fm != null) {
            List<Fragment> fragments = getFragments(fm);
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (fragment != null && fragment.isResumed() && fragment.isVisible() && fragment.getUserVisibleHint()) {
                    if (!isInStack) {
                        return getTopShowIsInStack(fragment.getChildFragmentManager(), fragment, false);
                    }
                    Bundle args = fragment.getArguments();
                    if (args != null && args.getBoolean(ARGS_IS_ADD_STACK)) {
                        return getTopShowIsInStack(fragment.getChildFragmentManager(), fragment, true);
                    }
                }
            }
            return parentFragment;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static List<Fragment> getFragments(FragmentManager fm) {
        if (fm != null) {
            List<Fragment> fragments = fm.getFragments();
            if (fragments == null || fragments.isEmpty()) {
                return Collections.emptyList();
            }
            return fragments;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static List<Fragment> getFragmentsInStack(FragmentManager fm) {
        Bundle args;
        if (fm != null) {
            List<Fragment> fragments = getFragments(fm);
            List<Fragment> result = new ArrayList<>();
            for (Fragment fragment : fragments) {
                if (!(fragment == null || (args = fragment.getArguments()) == null || !args.getBoolean(ARGS_IS_ADD_STACK))) {
                    result.add(fragment);
                }
            }
            return result;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static List<FragmentNode> getAllFragments(FragmentManager fm) {
        if (fm != null) {
            return getAllFragments(fm, new ArrayList());
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static List<FragmentNode> getAllFragments(FragmentManager fm, List<FragmentNode> result) {
        if (fm != null) {
            List<Fragment> fragments = getFragments(fm);
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (fragment != null) {
                    result.add(new FragmentNode(fragment, getAllFragments(fragment.getChildFragmentManager(), new ArrayList())));
                }
            }
            return result;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static List<FragmentNode> getAllFragmentsInStack(FragmentManager fm) {
        if (fm != null) {
            return getAllFragmentsInStack(fm, new ArrayList());
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static List<FragmentNode> getAllFragmentsInStack(FragmentManager fm, List<FragmentNode> result) {
        Bundle args;
        if (fm != null) {
            List<Fragment> fragments = getFragments(fm);
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (!(fragment == null || (args = fragment.getArguments()) == null || !args.getBoolean(ARGS_IS_ADD_STACK))) {
                    result.add(new FragmentNode(fragment, getAllFragmentsInStack(fragment.getChildFragmentManager(), new ArrayList())));
                }
            }
            return result;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Fragment findFragment(FragmentManager fm, Class<? extends Fragment> findClz) {
        if (fm != null) {
            return fm.findFragmentByTag(findClz.getName());
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Fragment findFragment(FragmentManager fm, String tag) {
        if (fm == null) {
            throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (tag != null) {
            return fm.findFragmentByTag(tag);
        } else {
            throw new NullPointerException("Argument 'tag' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static boolean dispatchBackPress(Fragment fragment) {
        if (fragment != null) {
            return fragment.isResumed() && fragment.isVisible() && fragment.getUserVisibleHint() && (fragment instanceof OnBackClickListener) && ((OnBackClickListener) fragment).onBackClick();
        }
        throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean dispatchBackPress(FragmentManager fm) {
        if (fm != null) {
            List<Fragment> fragments = getFragments(fm);
            if (fragments == null || fragments.isEmpty()) {
                return false;
            }
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (fragment != null && fragment.isResumed() && fragment.isVisible() && fragment.getUserVisibleHint() && (fragment instanceof OnBackClickListener) && ((OnBackClickListener) fragment).onBackClick()) {
                    return true;
                }
            }
            return false;
        }
        throw new NullPointerException("Argument 'fm' of type FragmentManager (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setBackgroundColor(Fragment fragment, int color) {
        if (fragment != null) {
            View view = fragment.getView();
            if (view != null) {
                view.setBackgroundColor(color);
                return;
            }
            return;
        }
        throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setBackgroundResource(Fragment fragment, int resId) {
        if (fragment != null) {
            View view = fragment.getView();
            if (view != null) {
                view.setBackgroundResource(resId);
                return;
            }
            return;
        }
        throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setBackground(Fragment fragment, Drawable background) {
        if (fragment != null) {
            View view = fragment.getView();
            if (view != null) {
                if (Build.VERSION.SDK_INT >= 16) {
                    view.setBackground(background);
                } else {
                    view.setBackgroundDrawable(background);
                }
            }
        } else {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getSimpleName(Fragment fragment) {
        return fragment == null ? "null" : fragment.getClass().getSimpleName();
    }

    private static class Args {
        final int id;
        final boolean isAddStack;
        final boolean isHide;
        final String tag;

        Args(int id2, boolean isHide2, boolean isAddStack2) {
            this(id2, (String) null, isHide2, isAddStack2);
        }

        Args(int id2, String tag2, boolean isHide2, boolean isAddStack2) {
            this.id = id2;
            this.tag = tag2;
            this.isHide = isHide2;
            this.isAddStack = isAddStack2;
        }
    }

    public static class FragmentNode {
        final Fragment fragment;
        final List<FragmentNode> next;

        public FragmentNode(Fragment fragment2, List<FragmentNode> next2) {
            this.fragment = fragment2;
            this.next = next2;
        }

        public Fragment getFragment() {
            return this.fragment;
        }

        public List<FragmentNode> getNext() {
            return this.next;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.fragment.getClass().getSimpleName());
            sb.append("->");
            List<FragmentNode> list = this.next;
            sb.append((list == null || list.isEmpty()) ? "no child" : this.next.toString());
            return sb.toString();
        }
    }
}
