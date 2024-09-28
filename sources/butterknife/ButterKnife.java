package butterknife;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ButterKnife {
    static final Map<Class<?>, Constructor<? extends Unbinder>> BINDINGS = new LinkedHashMap();
    private static final String TAG = "ButterKnife";
    private static boolean debug = false;

    private ButterKnife() {
        throw new AssertionError("No instances.");
    }

    public static void setDebug(boolean debug2) {
        debug = debug2;
    }

    public static Unbinder bind(Activity target) {
        return bind((Object) target, target.getWindow().getDecorView());
    }

    public static Unbinder bind(View target) {
        return bind((Object) target, target);
    }

    public static Unbinder bind(Dialog target) {
        return bind((Object) target, target.getWindow().getDecorView());
    }

    public static Unbinder bind(Object target, Activity source) {
        return bind(target, source.getWindow().getDecorView());
    }

    public static Unbinder bind(Object target, Dialog source) {
        return bind(target, source.getWindow().getDecorView());
    }

    public static Unbinder bind(Object target, View source) {
        Class<?> targetClass = target.getClass();
        if (debug) {
            Log.d(TAG, "Looking up binding for " + targetClass.getName());
        }
        Constructor<? extends Unbinder> constructor = findBindingConstructorForClass(targetClass);
        if (constructor == null) {
            return Unbinder.EMPTY;
        }
        try {
            return (Unbinder) constructor.newInstance(new Object[]{target, source});
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e2) {
            throw new RuntimeException("Unable to invoke " + constructor, e2);
        } catch (InvocationTargetException e3) {
            Throwable cause = e3.getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            } else if (cause instanceof Error) {
                throw ((Error) cause);
            } else {
                throw new RuntimeException("Unable to create binding instance.", cause);
            }
        }
    }

    private static Constructor<? extends Unbinder> findBindingConstructorForClass(Class<?> cls) {
        Constructor<? extends Unbinder> bindingCtor;
        Constructor<? extends Unbinder> bindingCtor2 = BINDINGS.get(cls);
        if (bindingCtor2 != null || BINDINGS.containsKey(cls)) {
            if (debug) {
                Log.d(TAG, "HIT: Cached in binding map.");
            }
            return bindingCtor2;
        }
        String clsName = cls.getName();
        if (!clsName.startsWith("android.") && !clsName.startsWith("java.") && !clsName.startsWith("androidx.")) {
            try {
                ClassLoader classLoader = cls.getClassLoader();
                bindingCtor = classLoader.loadClass(clsName + "_ViewBinding").getConstructor(new Class[]{cls, View.class});
                if (debug) {
                    Log.d(TAG, "HIT: Loaded binding class and constructor.");
                }
            } catch (ClassNotFoundException e) {
                if (debug) {
                    Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
                }
                bindingCtor = findBindingConstructorForClass(cls.getSuperclass());
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException("Unable to find binding constructor for " + clsName, e2);
            }
            BINDINGS.put(cls, bindingCtor);
            return bindingCtor;
        } else if (!debug) {
            return null;
        } else {
            Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            return null;
        }
    }
}
