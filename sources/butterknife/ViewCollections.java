package butterknife;

import android.util.Property;
import android.view.View;
import java.util.List;

public final class ViewCollections {
    @SafeVarargs
    public static <T extends View> void run(List<T> list, Action<? super T>... actions) {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            for (Action<? super T> action : actions) {
                action.apply((View) list.get(i), i);
            }
        }
    }

    @SafeVarargs
    public static <T extends View> void run(T[] array, Action<? super T>... actions) {
        int count = array.length;
        for (int i = 0; i < count; i++) {
            for (Action<? super T> action : actions) {
                action.apply(array[i], i);
            }
        }
    }

    public static <T extends View> void run(List<T> list, Action<? super T> action) {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            action.apply((View) list.get(i), i);
        }
    }

    public static <T extends View> void run(T[] array, Action<? super T> action) {
        int count = array.length;
        for (int i = 0; i < count; i++) {
            action.apply(array[i], i);
        }
    }

    @SafeVarargs
    public static <T extends View> void run(T view, Action<? super T>... actions) {
        for (Action<? super T> action : actions) {
            action.apply(view, 0);
        }
    }

    public static <T extends View> void run(T view, Action<? super T> action) {
        action.apply(view, 0);
    }

    public static <T extends View, V> void set(List<T> list, Setter<? super T, V> setter, V value) {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            setter.set((View) list.get(i), value, i);
        }
    }

    public static <T extends View, V> void set(T[] array, Setter<? super T, V> setter, V value) {
        int count = array.length;
        for (int i = 0; i < count; i++) {
            setter.set(array[i], value, i);
        }
    }

    public static <T extends View, V> void set(T view, Setter<? super T, V> setter, V value) {
        setter.set(view, value, 0);
    }

    public static <T extends View, V> void set(List<T> list, Property<? super T, V> setter, V value) {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            setter.set(list.get(i), value);
        }
    }

    public static <T extends View, V> void set(T[] array, Property<? super T, V> setter, V value) {
        for (T t : array) {
            setter.set(t, value);
        }
    }

    public static <T extends View, V> void set(T view, Property<? super T, V> setter, V value) {
        setter.set(view, value);
    }

    private ViewCollections() {
    }
}
