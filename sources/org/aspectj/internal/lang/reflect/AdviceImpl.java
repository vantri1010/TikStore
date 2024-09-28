package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.aspectj.lang.annotation.AdviceName;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.PointcutExpression;

public class AdviceImpl implements Advice {
    private static final String AJC_INTERNAL = "org.aspectj.runtime.internal";
    private final Method adviceMethod;
    private AjType[] exceptionTypes;
    private Type[] genericParameterTypes;
    private boolean hasExtraParam = false;
    private final AdviceKind kind;
    private AjType[] parameterTypes;
    private PointcutExpression pointcutExpression;

    protected AdviceImpl(Method method, String pointcut, AdviceKind type) {
        this.kind = type;
        this.adviceMethod = method;
        this.pointcutExpression = new PointcutExpressionImpl(pointcut);
    }

    protected AdviceImpl(Method method, String pointcut, AdviceKind type, String extraParamName) {
        this(method, pointcut, type);
    }

    public AjType getDeclaringType() {
        return AjTypeSystem.getAjType(this.adviceMethod.getDeclaringClass());
    }

    public Type[] getGenericParameterTypes() {
        if (this.genericParameterTypes == null) {
            Type[] genTypes = this.adviceMethod.getGenericParameterTypes();
            int syntheticCount = 0;
            for (Type t : genTypes) {
                if ((t instanceof Class) && ((Class) t).getPackage().getName().equals(AJC_INTERNAL)) {
                    syntheticCount++;
                }
            }
            this.genericParameterTypes = new Type[(genTypes.length - syntheticCount)];
            int i = 0;
            while (true) {
                Type[] typeArr = this.genericParameterTypes;
                if (i >= typeArr.length) {
                    break;
                }
                if (genTypes[i] instanceof Class) {
                    typeArr[i] = AjTypeSystem.getAjType((Class) genTypes[i]);
                } else {
                    typeArr[i] = genTypes[i];
                }
                i++;
            }
        }
        return this.genericParameterTypes;
    }

    public AjType<?>[] getParameterTypes() {
        if (this.parameterTypes == null) {
            Class<?>[] ptypes = this.adviceMethod.getParameterTypes();
            int syntheticCount = 0;
            for (Class<?> c : ptypes) {
                if (c.getPackage().getName().equals(AJC_INTERNAL)) {
                    syntheticCount++;
                }
            }
            this.parameterTypes = new AjType[(ptypes.length - syntheticCount)];
            int i = 0;
            while (true) {
                AjType[] ajTypeArr = this.parameterTypes;
                if (i >= ajTypeArr.length) {
                    break;
                }
                ajTypeArr[i] = AjTypeSystem.getAjType(ptypes[i]);
                i++;
            }
        }
        return this.parameterTypes;
    }

    public AjType<?>[] getExceptionTypes() {
        if (this.exceptionTypes == null) {
            Class<?>[] exTypes = this.adviceMethod.getExceptionTypes();
            this.exceptionTypes = new AjType[exTypes.length];
            for (int i = 0; i < exTypes.length; i++) {
                this.exceptionTypes[i] = AjTypeSystem.getAjType(exTypes[i]);
            }
        }
        return this.exceptionTypes;
    }

    public AdviceKind getKind() {
        return this.kind;
    }

    public String getName() {
        String adviceName = this.adviceMethod.getName();
        if (!adviceName.startsWith("ajc$")) {
            return adviceName;
        }
        AdviceName name = (AdviceName) this.adviceMethod.getAnnotation(AdviceName.class);
        if (name != null) {
            return name.value();
        }
        return "";
    }

    public PointcutExpression getPointcutExpression() {
        return this.pointcutExpression;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00a8, code lost:
        if (r8 != 3) goto L_0x00df;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String toString() {
        /*
            r10 = this;
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r0.<init>()
            java.lang.String r1 = r10.getName()
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0020
            java.lang.String r1 = "@AdviceName(\""
            r0.append(r1)
            java.lang.String r1 = r10.getName()
            r0.append(r1)
            java.lang.String r1 = "\") "
            r0.append(r1)
        L_0x0020:
            org.aspectj.lang.reflect.AdviceKind r1 = r10.getKind()
            org.aspectj.lang.reflect.AdviceKind r2 = org.aspectj.lang.reflect.AdviceKind.AROUND
            java.lang.String r3 = " "
            if (r1 != r2) goto L_0x003a
            java.lang.reflect.Method r1 = r10.adviceMethod
            java.lang.reflect.Type r1 = r1.getGenericReturnType()
            java.lang.String r1 = r1.toString()
            r0.append(r1)
            r0.append(r3)
        L_0x003a:
            int[] r1 = org.aspectj.internal.lang.reflect.AdviceImpl.AnonymousClass1.$SwitchMap$org$aspectj$lang$reflect$AdviceKind
            org.aspectj.lang.reflect.AdviceKind r2 = r10.getKind()
            int r2 = r2.ordinal()
            r1 = r1[r2]
            r2 = 3
            r4 = 2
            java.lang.String r5 = "after("
            r6 = 1
            if (r1 == r6) goto L_0x006c
            if (r1 == r4) goto L_0x0068
            if (r1 == r2) goto L_0x0064
            r5 = 4
            if (r1 == r5) goto L_0x005e
            r5 = 5
            if (r1 == r5) goto L_0x0058
            goto L_0x0070
        L_0x0058:
            java.lang.String r1 = "before("
            r0.append(r1)
            goto L_0x0070
        L_0x005e:
            java.lang.String r1 = "around("
            r0.append(r1)
            goto L_0x0070
        L_0x0064:
            r0.append(r5)
            goto L_0x0070
        L_0x0068:
            r0.append(r5)
            goto L_0x0070
        L_0x006c:
            r0.append(r5)
        L_0x0070:
            org.aspectj.lang.reflect.AjType[] r1 = r10.getParameterTypes()
            int r5 = r1.length
            boolean r6 = r10.hasExtraParam
            if (r6 == 0) goto L_0x007b
            int r5 = r5 + -1
        L_0x007b:
            r6 = 0
        L_0x007c:
            java.lang.String r7 = ","
            if (r6 >= r5) goto L_0x0093
            r8 = r1[r6]
            java.lang.String r8 = r8.getName()
            r0.append(r8)
            int r8 = r6 + 1
            if (r8 >= r5) goto L_0x0090
            r0.append(r7)
        L_0x0090:
            int r6 = r6 + 1
            goto L_0x007c
        L_0x0093:
            java.lang.String r6 = ") "
            r0.append(r6)
            int[] r8 = org.aspectj.internal.lang.reflect.AdviceImpl.AnonymousClass1.$SwitchMap$org$aspectj$lang$reflect$AdviceKind
            org.aspectj.lang.reflect.AdviceKind r9 = r10.getKind()
            int r9 = r9.ordinal()
            r8 = r8[r9]
            java.lang.String r9 = "("
            if (r8 == r4) goto L_0x00ab
            if (r8 == r2) goto L_0x00c5
            goto L_0x00df
        L_0x00ab:
            java.lang.String r2 = "returning"
            r0.append(r2)
            boolean r2 = r10.hasExtraParam
            if (r2 == 0) goto L_0x00c5
            r0.append(r9)
            int r2 = r5 + -1
            r2 = r1[r2]
            java.lang.String r2 = r2.getName()
            r0.append(r2)
            r0.append(r6)
        L_0x00c5:
            java.lang.String r2 = "throwing"
            r0.append(r2)
            boolean r2 = r10.hasExtraParam
            if (r2 == 0) goto L_0x00df
            r0.append(r9)
            int r2 = r5 + -1
            r2 = r1[r2]
            java.lang.String r2 = r2.getName()
            r0.append(r2)
            r0.append(r6)
        L_0x00df:
            org.aspectj.lang.reflect.AjType[] r2 = r10.getExceptionTypes()
            int r4 = r2.length
            if (r4 <= 0) goto L_0x0106
            java.lang.String r4 = "throws "
            r0.append(r4)
            r4 = 0
        L_0x00ec:
            int r6 = r2.length
            if (r4 >= r6) goto L_0x0103
            r6 = r2[r4]
            java.lang.String r6 = r6.getName()
            r0.append(r6)
            int r6 = r4 + 1
            int r8 = r2.length
            if (r6 >= r8) goto L_0x0100
            r0.append(r7)
        L_0x0100:
            int r4 = r4 + 1
            goto L_0x00ec
        L_0x0103:
            r0.append(r3)
        L_0x0106:
            java.lang.String r3 = ": "
            r0.append(r3)
            org.aspectj.lang.reflect.PointcutExpression r3 = r10.getPointcutExpression()
            java.lang.String r3 = r3.asString()
            r0.append(r3)
            java.lang.String r3 = r0.toString()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.aspectj.internal.lang.reflect.AdviceImpl.toString():java.lang.String");
    }

    /* renamed from: org.aspectj.internal.lang.reflect.AdviceImpl$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$aspectj$lang$reflect$AdviceKind;

        static {
            int[] iArr = new int[AdviceKind.values().length];
            $SwitchMap$org$aspectj$lang$reflect$AdviceKind = iArr;
            try {
                iArr[AdviceKind.AFTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$AdviceKind[AdviceKind.AFTER_RETURNING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$AdviceKind[AdviceKind.AFTER_THROWING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$AdviceKind[AdviceKind.AROUND.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$AdviceKind[AdviceKind.BEFORE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }
}
