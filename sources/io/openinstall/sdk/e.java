package io.openinstall.sdk;

public final class e {
    public static final e a = new e(1, "未初始化");
    public static final e b = new e(2, "正在初始化");
    public static final e c = new e(-1, "初始化失败");
    public static final e d = new e(0, "初始化成功");
    public static final e e = new e(-2, "初始化错误");
    private final int f;
    private final String g;

    e(int i, String str) {
        this.f = i;
        this.g = str;
    }

    public static e a(int i) {
        return i != -2 ? i != -1 ? i != 0 ? i != 2 ? a : b : d : c : e;
    }

    public int a() {
        return this.f;
    }
}
