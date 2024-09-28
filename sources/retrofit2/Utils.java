package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import okhttp3.ResponseBody;
import okio.Buffer;

final class Utils {
    static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

    private Utils() {
    }

    static Class<?> getRawType(Type type) {
        checkNotNull(type, "type == null");
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class) {
                return (Class) rawType;
            }
            throw new IllegalArgumentException();
        } else if (type instanceof GenericArrayType) {
            return Array.newInstance(getRawType(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        } else {
            if (type instanceof TypeVariable) {
                return Object.class;
            }
            if (type instanceof WildcardType) {
                return getRawType(((WildcardType) type).getUpperBounds()[0]);
            }
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
        }
    }

    static boolean equals(Type a, Type b) {
        if (a == b) {
            return true;
        }
        if (a instanceof Class) {
            return a.equals(b);
        }
        if (a instanceof ParameterizedType) {
            if (!(b instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType pa = (ParameterizedType) a;
            ParameterizedType pb = (ParameterizedType) b;
            if (!equal(pa.getOwnerType(), pb.getOwnerType()) || !pa.getRawType().equals(pb.getRawType()) || !Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments())) {
                return false;
            }
            return true;
        } else if (a instanceof GenericArrayType) {
            if (!(b instanceof GenericArrayType)) {
                return false;
            }
            return equals(((GenericArrayType) a).getGenericComponentType(), ((GenericArrayType) b).getGenericComponentType());
        } else if (a instanceof WildcardType) {
            if (!(b instanceof WildcardType)) {
                return false;
            }
            WildcardType wa = (WildcardType) a;
            WildcardType wb = (WildcardType) b;
            if (!Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds()) || !Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds())) {
                return false;
            }
            return true;
        } else if (!(a instanceof TypeVariable) || !(b instanceof TypeVariable)) {
            return false;
        } else {
            TypeVariable<?> va = (TypeVariable) a;
            TypeVariable<?> vb = (TypeVariable) b;
            if (va.getGenericDeclaration() != vb.getGenericDeclaration() || !va.getName().equals(vb.getName())) {
                return false;
            }
            return true;
        }
    }

    static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
        if (toResolve == rawType) {
            return context;
        }
        if (toResolve.isInterface()) {
            Class<?>[] interfaces = rawType.getInterfaces();
            int length = interfaces.length;
            for (int i = 0; i < length; i++) {
                if (interfaces[i] == toResolve) {
                    return rawType.getGenericInterfaces()[i];
                }
                if (toResolve.isAssignableFrom(interfaces[i])) {
                    return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
                }
            }
        }
        if (!rawType.isInterface()) {
            while (rawType != Object.class) {
                Class<? super Object> superclass = rawType.getSuperclass();
                if (superclass == toResolve) {
                    return rawType.getGenericSuperclass();
                }
                if (toResolve.isAssignableFrom(superclass)) {
                    return getGenericSupertype(rawType.getGenericSuperclass(), superclass, toResolve);
                }
                rawType = superclass;
            }
        }
        return toResolve;
    }

    private static int indexOf(Object[] array, Object toFind) {
        for (int i = 0; i < array.length; i++) {
            if (toFind.equals(array[i])) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    private static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    static int hashCodeOrZero(Object o) {
        if (o != null) {
            return o.hashCode();
        }
        return 0;
    }

    static String typeToString(Type type) {
        return type instanceof Class ? ((Class) type).getName() : type.toString();
    }

    static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
        if (supertype.isAssignableFrom(contextRawType)) {
            return resolve(context, contextRawType, getGenericSupertype(context, contextRawType, supertype));
        }
        throw new IllegalArgumentException();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: java.lang.reflect.Type[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.reflect.Type resolve(java.lang.reflect.Type r9, java.lang.Class<?> r10, java.lang.reflect.Type r11) {
        /*
        L_0x0000:
            boolean r0 = r11 instanceof java.lang.reflect.TypeVariable
            if (r0 == 0) goto L_0x000f
            r0 = r11
            java.lang.reflect.TypeVariable r0 = (java.lang.reflect.TypeVariable) r0
            java.lang.reflect.Type r11 = resolveTypeVariable(r9, r10, r0)
            if (r11 != r0) goto L_0x000e
            return r11
        L_0x000e:
            goto L_0x0000
        L_0x000f:
            boolean r0 = r11 instanceof java.lang.Class
            if (r0 == 0) goto L_0x0031
            r0 = r11
            java.lang.Class r0 = (java.lang.Class) r0
            boolean r0 = r0.isArray()
            if (r0 == 0) goto L_0x0031
            r0 = r11
            java.lang.Class r0 = (java.lang.Class) r0
            java.lang.Class r1 = r0.getComponentType()
            java.lang.reflect.Type r2 = resolve(r9, r10, r1)
            if (r1 != r2) goto L_0x002b
            r3 = r0
            goto L_0x0030
        L_0x002b:
            retrofit2.Utils$GenericArrayTypeImpl r3 = new retrofit2.Utils$GenericArrayTypeImpl
            r3.<init>(r2)
        L_0x0030:
            return r3
        L_0x0031:
            boolean r0 = r11 instanceof java.lang.reflect.GenericArrayType
            if (r0 == 0) goto L_0x004a
            r0 = r11
            java.lang.reflect.GenericArrayType r0 = (java.lang.reflect.GenericArrayType) r0
            java.lang.reflect.Type r1 = r0.getGenericComponentType()
            java.lang.reflect.Type r2 = resolve(r9, r10, r1)
            if (r1 != r2) goto L_0x0044
            r3 = r0
            goto L_0x0049
        L_0x0044:
            retrofit2.Utils$GenericArrayTypeImpl r3 = new retrofit2.Utils$GenericArrayTypeImpl
            r3.<init>(r2)
        L_0x0049:
            return r3
        L_0x004a:
            boolean r0 = r11 instanceof java.lang.reflect.ParameterizedType
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x008e
            r0 = r11
            java.lang.reflect.ParameterizedType r0 = (java.lang.reflect.ParameterizedType) r0
            java.lang.reflect.Type r3 = r0.getOwnerType()
            java.lang.reflect.Type r4 = resolve(r9, r10, r3)
            if (r4 == r3) goto L_0x005e
            goto L_0x005f
        L_0x005e:
            r1 = 0
        L_0x005f:
            java.lang.reflect.Type[] r2 = r0.getActualTypeArguments()
            r5 = 0
            int r6 = r2.length
        L_0x0065:
            if (r5 >= r6) goto L_0x0080
            r7 = r2[r5]
            java.lang.reflect.Type r7 = resolve(r9, r10, r7)
            r8 = r2[r5]
            if (r7 == r8) goto L_0x007d
            if (r1 != 0) goto L_0x007b
            java.lang.Object r8 = r2.clone()
            r2 = r8
            java.lang.reflect.Type[] r2 = (java.lang.reflect.Type[]) r2
            r1 = 1
        L_0x007b:
            r2[r5] = r7
        L_0x007d:
            int r5 = r5 + 1
            goto L_0x0065
        L_0x0080:
            if (r1 == 0) goto L_0x008c
            retrofit2.Utils$ParameterizedTypeImpl r5 = new retrofit2.Utils$ParameterizedTypeImpl
            java.lang.reflect.Type r6 = r0.getRawType()
            r5.<init>(r4, r6, r2)
            goto L_0x008d
        L_0x008c:
            r5 = r0
        L_0x008d:
            return r5
        L_0x008e:
            boolean r0 = r11 instanceof java.lang.reflect.WildcardType
            if (r0 == 0) goto L_0x00d6
            r0 = r11
            java.lang.reflect.WildcardType r0 = (java.lang.reflect.WildcardType) r0
            java.lang.reflect.Type[] r3 = r0.getLowerBounds()
            java.lang.reflect.Type[] r4 = r0.getUpperBounds()
            int r5 = r3.length
            if (r5 != r1) goto L_0x00bb
            r5 = r3[r2]
            java.lang.reflect.Type r5 = resolve(r9, r10, r5)
            r6 = r3[r2]
            if (r5 == r6) goto L_0x00ba
            retrofit2.Utils$WildcardTypeImpl r6 = new retrofit2.Utils$WildcardTypeImpl
            java.lang.reflect.Type[] r7 = new java.lang.reflect.Type[r1]
            java.lang.Class<java.lang.Object> r8 = java.lang.Object.class
            r7[r2] = r8
            java.lang.reflect.Type[] r1 = new java.lang.reflect.Type[r1]
            r1[r2] = r5
            r6.<init>(r7, r1)
            return r6
        L_0x00ba:
            goto L_0x00d4
        L_0x00bb:
            int r5 = r4.length
            if (r5 != r1) goto L_0x00d4
            r5 = r4[r2]
            java.lang.reflect.Type r5 = resolve(r9, r10, r5)     // Catch:{ all -> 0x00d7 }
            r6 = r4[r2]
            if (r5 == r6) goto L_0x00d5
            retrofit2.Utils$WildcardTypeImpl r6 = new retrofit2.Utils$WildcardTypeImpl
            java.lang.reflect.Type[] r1 = new java.lang.reflect.Type[r1]
            r1[r2] = r5
            java.lang.reflect.Type[] r2 = EMPTY_TYPE_ARRAY
            r6.<init>(r1, r2)
            return r6
        L_0x00d4:
        L_0x00d5:
            return r0
        L_0x00d6:
            return r11
        L_0x00d7:
            r9 = move-exception
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: retrofit2.Utils.resolve(java.lang.reflect.Type, java.lang.Class, java.lang.reflect.Type):java.lang.reflect.Type");
    }

    private static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
        Class<?> declaredByRaw = declaringClassOf(unknown);
        if (declaredByRaw == null) {
            return unknown;
        }
        Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
        if (!(declaredBy instanceof ParameterizedType)) {
            return unknown;
        }
        return ((ParameterizedType) declaredBy).getActualTypeArguments()[indexOf(declaredByRaw.getTypeParameters(), unknown)];
    }

    private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
        Object genericDeclaration = typeVariable.getGenericDeclaration();
        if (genericDeclaration instanceof Class) {
            return (Class) genericDeclaration;
        }
        return null;
    }

    static void checkNotPrimitive(Type type) {
        if ((type instanceof Class) && ((Class) type).isPrimitive()) {
            throw new IllegalArgumentException();
        }
    }

    static <T> T checkNotNull(@Nullable T object, String message) {
        if (object != null) {
            return object;
        }
        throw new NullPointerException(message);
    }

    static boolean isAnnotationPresent(Annotation[] annotations, Class<? extends Annotation> cls) {
        for (Annotation annotation : annotations) {
            if (cls.isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }

    static ResponseBody buffer(ResponseBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.source().readAll(buffer);
        return ResponseBody.create(body.contentType(), body.contentLength(), buffer);
    }

    static <T> void validateServiceInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        } else if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
    }

    static Type getParameterUpperBound(int index, ParameterizedType type) {
        Type[] types = type.getActualTypeArguments();
        if (index < 0 || index >= types.length) {
            throw new IllegalArgumentException("Index " + index + " not in range [0," + types.length + ") for " + type);
        }
        Type paramType = types[index];
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
    }

    static boolean hasUnresolvableType(Type type) {
        if (type instanceof Class) {
            return false;
        }
        if (type instanceof ParameterizedType) {
            for (Type typeArgument : ((ParameterizedType) type).getActualTypeArguments()) {
                if (hasUnresolvableType(typeArgument)) {
                    return true;
                }
            }
            return false;
        } else if (type instanceof GenericArrayType) {
            return hasUnresolvableType(((GenericArrayType) type).getGenericComponentType());
        } else {
            if ((type instanceof TypeVariable) || (type instanceof WildcardType)) {
                return true;
            }
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + (type == null ? "null" : type.getClass().getName()));
        }
    }

    static Type getCallResponseType(Type returnType) {
        if (returnType instanceof ParameterizedType) {
            return getParameterUpperBound(0, (ParameterizedType) returnType);
        }
        throw new IllegalArgumentException("Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType {
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;

        ParameterizedTypeImpl(Type ownerType2, Type rawType2, Type... typeArguments2) {
            if (rawType2 instanceof Class) {
                if ((ownerType2 == null) != (((Class) rawType2).getEnclosingClass() != null ? false : true)) {
                    throw new IllegalArgumentException();
                }
            }
            for (Type typeArgument : typeArguments2) {
                Utils.checkNotNull(typeArgument, "typeArgument == null");
                Utils.checkNotPrimitive(typeArgument);
            }
            this.ownerType = ownerType2;
            this.rawType = rawType2;
            this.typeArguments = (Type[]) typeArguments2.clone();
        }

        public Type[] getActualTypeArguments() {
            return (Type[]) this.typeArguments.clone();
        }

        public Type getRawType() {
            return this.rawType;
        }

        public Type getOwnerType() {
            return this.ownerType;
        }

        public boolean equals(Object other) {
            return (other instanceof ParameterizedType) && Utils.equals(this, (ParameterizedType) other);
        }

        public int hashCode() {
            return (Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode()) ^ Utils.hashCodeOrZero(this.ownerType);
        }

        public String toString() {
            Type[] typeArr = this.typeArguments;
            if (typeArr.length == 0) {
                return Utils.typeToString(this.rawType);
            }
            StringBuilder result = new StringBuilder((typeArr.length + 1) * 30);
            result.append(Utils.typeToString(this.rawType));
            result.append("<");
            result.append(Utils.typeToString(this.typeArguments[0]));
            for (int i = 1; i < this.typeArguments.length; i++) {
                result.append(", ");
                result.append(Utils.typeToString(this.typeArguments[i]));
            }
            result.append(">");
            return result.toString();
        }
    }

    private static final class GenericArrayTypeImpl implements GenericArrayType {
        private final Type componentType;

        GenericArrayTypeImpl(Type componentType2) {
            this.componentType = componentType2;
        }

        public Type getGenericComponentType() {
            return this.componentType;
        }

        public boolean equals(Object o) {
            return (o instanceof GenericArrayType) && Utils.equals(this, (GenericArrayType) o);
        }

        public int hashCode() {
            return this.componentType.hashCode();
        }

        public String toString() {
            return Utils.typeToString(this.componentType) + "[]";
        }
    }

    private static final class WildcardTypeImpl implements WildcardType {
        private final Type lowerBound;
        private final Type upperBound;

        WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            if (lowerBounds.length > 1) {
                throw new IllegalArgumentException();
            } else if (upperBounds.length != 1) {
                throw new IllegalArgumentException();
            } else if (lowerBounds.length == 1) {
                if (lowerBounds[0] != null) {
                    Utils.checkNotPrimitive(lowerBounds[0]);
                    if (upperBounds[0] == Object.class) {
                        this.lowerBound = lowerBounds[0];
                        this.upperBound = Object.class;
                        return;
                    }
                    throw new IllegalArgumentException();
                }
                throw null;
            } else if (upperBounds[0] != null) {
                Utils.checkNotPrimitive(upperBounds[0]);
                this.lowerBound = null;
                this.upperBound = upperBounds[0];
            } else {
                throw null;
            }
        }

        public Type[] getUpperBounds() {
            return new Type[]{this.upperBound};
        }

        public Type[] getLowerBounds() {
            Type type = this.lowerBound;
            if (type == null) {
                return Utils.EMPTY_TYPE_ARRAY;
            }
            return new Type[]{type};
        }

        public boolean equals(Object other) {
            return (other instanceof WildcardType) && Utils.equals(this, (WildcardType) other);
        }

        public int hashCode() {
            Type type = this.lowerBound;
            return (type != null ? type.hashCode() + 31 : 1) ^ (this.upperBound.hashCode() + 31);
        }

        public String toString() {
            if (this.lowerBound != null) {
                return "? super " + Utils.typeToString(this.lowerBound);
            } else if (this.upperBound == Object.class) {
                return "?";
            } else {
                return "? extends " + Utils.typeToString(this.upperBound);
            }
        }
    }
}
