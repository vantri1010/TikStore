package com.blankj.utilcode.util;

import android.util.Log;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public final class BusUtils {
    private static final Object NULL = "nULl";
    private static final String TAG = "BusUtils";
    private final Map<String, Set<Object>> mClassName_BusesMap;
    private final Map<String, Map<String, Object>> mClassName_Tag_Arg4StickyMap;
    private final Map<String, List<String>> mClassName_TagsMap;
    private final Map<String, List<BusInfo>> mTag_BusInfoListMap;

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.CLASS)
    public @interface Bus {
        int priority() default 0;

        boolean sticky() default false;

        String tag();

        ThreadMode threadMode() default ThreadMode.POSTING;
    }

    public enum ThreadMode {
        MAIN,
        IO,
        CPU,
        CACHED,
        SINGLE,
        POSTING
    }

    private BusUtils() {
        this.mTag_BusInfoListMap = new HashMap();
        this.mClassName_BusesMap = new ConcurrentHashMap();
        this.mClassName_TagsMap = new HashMap();
        this.mClassName_Tag_Arg4StickyMap = new ConcurrentHashMap();
        init();
    }

    private void init() {
    }

    private void registerBus(String tag, String className, String funName, String paramType, String paramName, boolean sticky, String threadMode) {
        registerBus(tag, className, funName, paramType, paramName, sticky, threadMode, 0);
    }

    private void registerBus(String tag, String className, String funName, String paramType, String paramName, boolean sticky, String threadMode, int priority) {
        String str = tag;
        List<BusInfo> busInfoList = this.mTag_BusInfoListMap.get(tag);
        if (busInfoList == null) {
            busInfoList = new ArrayList<>();
            this.mTag_BusInfoListMap.put(tag, busInfoList);
        }
        busInfoList.add(new BusInfo(className, funName, paramType, paramName, sticky, threadMode, priority));
    }

    public static void register(Object bus) {
        getInstance().registerInner(bus);
    }

    public static void unregister(Object bus) {
        getInstance().unregisterInner(bus);
    }

    public static void post(String tag) {
        post(tag, NULL);
    }

    public static void post(String tag, Object arg) {
        getInstance().postInner(tag, arg);
    }

    public static void postSticky(String tag) {
        postSticky(tag, NULL);
    }

    public static void postSticky(String tag, Object arg) {
        getInstance().postStickyInner(tag, arg);
    }

    public static void removeSticky(String tag) {
        getInstance().removeStickyInner(tag);
    }

    public static String toString_() {
        return getInstance().toString();
    }

    public String toString() {
        return "BusUtils: " + this.mTag_BusInfoListMap;
    }

    private static BusUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    private void registerInner(Object bus) {
        if (bus != null) {
            Class aClass = bus.getClass();
            String className = aClass.getName();
            synchronized (this.mClassName_BusesMap) {
                Set<Object> buses = this.mClassName_BusesMap.get(className);
                if (buses == null) {
                    buses = new CopyOnWriteArraySet<>();
                    this.mClassName_BusesMap.put(className, buses);
                }
                buses.add(bus);
            }
            if (this.mClassName_TagsMap.get(className) == null) {
                synchronized (this.mClassName_TagsMap) {
                    if (this.mClassName_TagsMap.get(className) == null) {
                        List<String> tags = new ArrayList<>();
                        for (Map.Entry<String, List<BusInfo>> entry : this.mTag_BusInfoListMap.entrySet()) {
                            for (BusInfo busInfo : entry.getValue()) {
                                try {
                                    if (Class.forName(busInfo.className).isAssignableFrom(aClass)) {
                                        tags.add(entry.getKey());
                                        busInfo.classNames.add(className);
                                    }
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        this.mClassName_TagsMap.put(className, tags);
                    }
                }
            }
            processSticky(bus);
        }
    }

    private void processSticky(Object bus) {
        Map<String, Object> tagArgMap = this.mClassName_Tag_Arg4StickyMap.get(bus.getClass().getName());
        if (tagArgMap != null) {
            synchronized (this.mClassName_Tag_Arg4StickyMap) {
                for (Map.Entry<String, Object> tagArgEntry : tagArgMap.entrySet()) {
                    postInner(tagArgEntry.getKey(), tagArgEntry.getValue());
                }
            }
        }
    }

    private void unregisterInner(Object bus) {
        if (bus != null) {
            String className = bus.getClass().getName();
            synchronized (this.mClassName_BusesMap) {
                Set<Object> buses = this.mClassName_BusesMap.get(className);
                if (buses != null) {
                    if (buses.contains(bus)) {
                        buses.remove(bus);
                        return;
                    }
                }
                Log.e(TAG, "The bus of <" + bus + "> was not registered before.");
            }
        }
    }

    private void postInner(String tag, Object arg) {
        postInner(tag, arg, false);
    }

    private void postInner(String tag, Object arg, boolean sticky) {
        List<BusInfo> busInfoList = this.mTag_BusInfoListMap.get(tag);
        if (busInfoList == null) {
            Log.e(TAG, "The bus of tag <" + tag + "> is not exists.");
            return;
        }
        for (BusInfo busInfo : busInfoList) {
            if (busInfo.method == null) {
                Method method = getMethodByBusInfo(busInfo);
                if (method != null) {
                    busInfo.method = method;
                } else {
                    return;
                }
            }
            invokeMethod(tag, arg, busInfo, sticky);
        }
    }

    private Method getMethodByBusInfo(BusInfo busInfo) {
        try {
            if ("".equals(busInfo.paramType)) {
                return Class.forName(busInfo.className).getDeclaredMethod(busInfo.funName, new Class[0]);
            }
            return Class.forName(busInfo.className).getDeclaredMethod(busInfo.funName, new Class[]{getClassName(busInfo.paramType)});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.Class getClassName(java.lang.String r2) throws java.lang.ClassNotFoundException {
        /*
            r1 = this;
            int r0 = r2.hashCode()
            switch(r0) {
                case -1325958191: goto L_0x004e;
                case 104431: goto L_0x0044;
                case 3039496: goto L_0x003a;
                case 3052374: goto L_0x0030;
                case 3327612: goto L_0x0026;
                case 64711720: goto L_0x001c;
                case 97526364: goto L_0x0012;
                case 109413500: goto L_0x0008;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x0058
        L_0x0008:
            java.lang.String r0 = "short"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 3
            goto L_0x0059
        L_0x0012:
            java.lang.String r0 = "float"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 6
            goto L_0x0059
        L_0x001c:
            java.lang.String r0 = "boolean"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 0
            goto L_0x0059
        L_0x0026:
            java.lang.String r0 = "long"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 2
            goto L_0x0059
        L_0x0030:
            java.lang.String r0 = "char"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 7
            goto L_0x0059
        L_0x003a:
            java.lang.String r0 = "byte"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 4
            goto L_0x0059
        L_0x0044:
            java.lang.String r0 = "int"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 1
            goto L_0x0059
        L_0x004e:
            java.lang.String r0 = "double"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 5
            goto L_0x0059
        L_0x0058:
            r0 = -1
        L_0x0059:
            switch(r0) {
                case 0: goto L_0x0076;
                case 1: goto L_0x0073;
                case 2: goto L_0x0070;
                case 3: goto L_0x006d;
                case 4: goto L_0x006a;
                case 5: goto L_0x0067;
                case 6: goto L_0x0064;
                case 7: goto L_0x0061;
                default: goto L_0x005c;
            }
        L_0x005c:
            java.lang.Class r0 = java.lang.Class.forName(r2)
            return r0
        L_0x0061:
            java.lang.Class r0 = java.lang.Character.TYPE
            return r0
        L_0x0064:
            java.lang.Class r0 = java.lang.Float.TYPE
            return r0
        L_0x0067:
            java.lang.Class r0 = java.lang.Double.TYPE
            return r0
        L_0x006a:
            java.lang.Class r0 = java.lang.Byte.TYPE
            return r0
        L_0x006d:
            java.lang.Class r0 = java.lang.Short.TYPE
            return r0
        L_0x0070:
            java.lang.Class r0 = java.lang.Long.TYPE
            return r0
        L_0x0073:
            java.lang.Class r0 = java.lang.Integer.TYPE
            return r0
        L_0x0076:
            java.lang.Class r0 = java.lang.Boolean.TYPE
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.BusUtils.getClassName(java.lang.String):java.lang.Class");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void invokeMethod(java.lang.String r8, java.lang.Object r9, com.blankj.utilcode.util.BusUtils.BusInfo r10, boolean r11) {
        /*
            r7 = this;
            com.blankj.utilcode.util.BusUtils$1 r6 = new com.blankj.utilcode.util.BusUtils$1
            r0 = r6
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r10
            r5 = r11
            r0.<init>(r2, r3, r4, r5)
            java.lang.String r1 = r10.threadMode
            int r2 = r1.hashCode()
            r3 = 4
            r4 = 3
            r5 = 2
            r6 = 1
            switch(r2) {
                case -1848936376: goto L_0x0041;
                case 2342: goto L_0x0037;
                case 66952: goto L_0x002d;
                case 2358713: goto L_0x0023;
                case 1980249378: goto L_0x0019;
                default: goto L_0x0018;
            }
        L_0x0018:
            goto L_0x004b
        L_0x0019:
            java.lang.String r2 = "CACHED"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0018
            r1 = 3
            goto L_0x004c
        L_0x0023:
            java.lang.String r2 = "MAIN"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0018
            r1 = 0
            goto L_0x004c
        L_0x002d:
            java.lang.String r2 = "CPU"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0018
            r1 = 2
            goto L_0x004c
        L_0x0037:
            java.lang.String r2 = "IO"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0018
            r1 = 1
            goto L_0x004c
        L_0x0041:
            java.lang.String r2 = "SINGLE"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0018
            r1 = 4
            goto L_0x004c
        L_0x004b:
            r1 = -1
        L_0x004c:
            if (r1 == 0) goto L_0x007a
            if (r1 == r6) goto L_0x0072
            if (r1 == r5) goto L_0x006a
            if (r1 == r4) goto L_0x0062
            if (r1 == r3) goto L_0x005a
            r0.run()
            return
        L_0x005a:
            java.util.concurrent.ExecutorService r1 = com.blankj.utilcode.util.ThreadUtils.getSinglePool()
            r1.execute(r0)
            return
        L_0x0062:
            java.util.concurrent.ExecutorService r1 = com.blankj.utilcode.util.ThreadUtils.getCachedPool()
            r1.execute(r0)
            return
        L_0x006a:
            java.util.concurrent.ExecutorService r1 = com.blankj.utilcode.util.ThreadUtils.getCpuPool()
            r1.execute(r0)
            return
        L_0x0072:
            java.util.concurrent.ExecutorService r1 = com.blankj.utilcode.util.ThreadUtils.getIoPool()
            r1.execute(r0)
            return
        L_0x007a:
            com.blankj.utilcode.util.Utils.runOnUiThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.BusUtils.invokeMethod(java.lang.String, java.lang.Object, com.blankj.utilcode.util.BusUtils$BusInfo, boolean):void");
    }

    /* access modifiers changed from: private */
    public void realInvokeMethod(String tag, Object arg, BusInfo busInfo, boolean sticky) {
        Set<Object> buses = new HashSet<>();
        for (String className : busInfo.classNames) {
            Set<Object> subBuses = this.mClassName_BusesMap.get(className);
            if (subBuses != null && !subBuses.isEmpty()) {
                buses.addAll(subBuses);
            }
        }
        if (buses.size() != 0) {
            try {
                if (arg == NULL) {
                    for (Object bus : buses) {
                        busInfo.method.invoke(bus, new Object[0]);
                    }
                    return;
                }
                for (Object bus2 : buses) {
                    busInfo.method.invoke(bus2, new Object[]{arg});
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
            }
        } else if (!sticky) {
            Log.e(TAG, "The bus of tag <" + tag + "> was not registered before.");
        }
    }

    private void postStickyInner(String tag, Object arg) {
        List<BusInfo> busInfoList = this.mTag_BusInfoListMap.get(tag);
        if (busInfoList == null) {
            Log.e(TAG, "The bus of tag <" + tag + "> is not exists.");
            return;
        }
        for (BusInfo busInfo : busInfoList) {
            if (!busInfo.sticky) {
                postInner(tag, arg);
                return;
            }
            synchronized (this.mClassName_Tag_Arg4StickyMap) {
                Map<String, Object> tagArgMap = this.mClassName_Tag_Arg4StickyMap.get(busInfo.className);
                if (tagArgMap == null) {
                    tagArgMap = new HashMap<>();
                    this.mClassName_Tag_Arg4StickyMap.put(busInfo.className, tagArgMap);
                }
                tagArgMap.put(tag, arg);
            }
            postInner(tag, arg, true);
        }
    }

    private void removeStickyInner(String tag) {
        List<BusInfo> busInfoList = this.mTag_BusInfoListMap.get(tag);
        if (busInfoList == null) {
            Log.e(TAG, "The bus of tag <" + tag + "> is not exists.");
            return;
        }
        for (BusInfo busInfo : busInfoList) {
            if (!busInfo.sticky) {
                Log.e(TAG, "The bus of tag <" + tag + "> is not sticky.");
                return;
            }
            synchronized (this.mClassName_Tag_Arg4StickyMap) {
                Map<String, Object> tagArgMap = this.mClassName_Tag_Arg4StickyMap.get(busInfo.className);
                if (tagArgMap != null) {
                    if (tagArgMap.containsKey(tag)) {
                        tagArgMap.remove(tag);
                    }
                }
                Log.e(TAG, "The sticky bus of tag <" + tag + "> didn't post.");
                return;
            }
        }
    }

    private static final class BusInfo {
        String className;
        List<String> classNames = new CopyOnWriteArrayList();
        String funName;
        Method method;
        String paramName;
        String paramType;
        int priority;
        boolean sticky;
        String threadMode;

        BusInfo(String className2, String funName2, String paramType2, String paramName2, boolean sticky2, String threadMode2, int priority2) {
            this.className = className2;
            this.funName = funName2;
            this.paramType = paramType2;
            this.paramName = paramName2;
            this.sticky = sticky2;
            this.threadMode = threadMode2;
            this.priority = priority2;
        }

        public String toString() {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append("BusInfo { desc: ");
            sb.append(this.className);
            sb.append("#");
            sb.append(this.funName);
            if ("".equals(this.paramType)) {
                str = "()";
            } else {
                str = SQLBuilder.PARENTHESES_LEFT + this.paramType + " " + this.paramName + SQLBuilder.PARENTHESES_RIGHT;
            }
            sb.append(str);
            sb.append(", sticky: ");
            sb.append(this.sticky);
            sb.append(", threadMode: ");
            sb.append(this.threadMode);
            sb.append(", method: ");
            sb.append(this.method);
            sb.append(", priority: ");
            sb.append(this.priority);
            sb.append(" }");
            return sb.toString();
        }
    }

    private static class LazyHolder {
        /* access modifiers changed from: private */
        public static final BusUtils INSTANCE = new BusUtils();

        private LazyHolder() {
        }
    }
}
