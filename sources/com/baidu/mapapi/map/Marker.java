package com.baidu.mapapi.map;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.ParcelItem;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.j;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import kotlin.UByte;

public final class Marker extends Overlay {
    LatLng a;
    BitmapDescriptor b;
    float c;
    float d;
    boolean e;
    boolean f;
    float g;
    String h;
    int i;
    boolean j = false;
    boolean k = false;
    float l;
    int m;
    boolean n = false;
    boolean o = true;
    ArrayList<BitmapDescriptor> p;
    int q = 20;
    Animation r;
    float s = 1.0f;
    float t = 1.0f;
    float u = 1.0f;
    Point v;
    InfoWindow w;
    InfoWindow.a x;
    boolean y = false;

    Marker() {
        this.type = j.marker;
    }

    private void a(ArrayList<BitmapDescriptor> arrayList, Bundle bundle) {
        int i2;
        ArrayList arrayList2 = new ArrayList();
        Iterator<BitmapDescriptor> it = arrayList.iterator();
        while (true) {
            i2 = 0;
            if (!it.hasNext()) {
                break;
            }
            ParcelItem parcelItem = new ParcelItem();
            Bundle bundle2 = new Bundle();
            Bitmap bitmap = it.next().a;
            ByteBuffer allocate = ByteBuffer.allocate(bitmap.getWidth() * bitmap.getHeight() * 4);
            bitmap.copyPixelsToBuffer(allocate);
            byte[] array = allocate.array();
            bundle2.putByteArray("image_data", array);
            bundle2.putInt("image_width", bitmap.getWidth());
            bundle2.putInt("image_height", bitmap.getHeight());
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e2) {
                e2.printStackTrace();
            }
            if (messageDigest != null) {
                messageDigest.update(array, 0, array.length);
                byte[] digest = messageDigest.digest();
                StringBuilder sb = new StringBuilder("");
                while (i2 < digest.length) {
                    sb.append(Integer.toString((digest[i2] & UByte.MAX_VALUE) + UByte.MIN_VALUE, 16).substring(1));
                    i2++;
                }
                bundle2.putString("image_hashcode", sb.toString());
            }
            parcelItem.setBundle(bundle2);
            arrayList2.add(parcelItem);
        }
        if (arrayList2.size() > 0) {
            ParcelItem[] parcelItemArr = new ParcelItem[arrayList2.size()];
            while (i2 < arrayList2.size()) {
                parcelItemArr[i2] = (ParcelItem) arrayList2.get(i2);
                i2++;
            }
            bundle.putParcelableArray("icons", parcelItemArr);
        }
    }

    /* access modifiers changed from: package-private */
    public Bundle a(Bundle bundle) {
        super.a(bundle);
        Bundle bundle2 = new Bundle();
        BitmapDescriptor bitmapDescriptor = this.b;
        if (bitmapDescriptor != null) {
            bundle.putBundle("image_info", bitmapDescriptor.b());
        }
        GeoPoint ll2mc = CoordUtil.ll2mc(this.a);
        bundle.putInt("animatetype", this.m);
        bundle.putDouble("location_x", ll2mc.getLongitudeE6());
        bundle.putDouble("location_y", ll2mc.getLatitudeE6());
        bundle.putInt("perspective", this.e ? 1 : 0);
        bundle.putFloat("anchor_x", this.c);
        bundle.putFloat("anchor_y", this.d);
        bundle.putFloat("rotate", this.g);
        bundle.putInt("y_offset", this.i);
        bundle.putInt("isflat", this.j ? 1 : 0);
        bundle.putInt("istop", this.k ? 1 : 0);
        bundle.putInt("period", this.q);
        bundle.putFloat("alpha", this.l);
        bundle.putFloat("scaleX", this.s);
        bundle.putFloat("scaleY", this.t);
        bundle.putInt("isClickable", this.o ? 1 : 0);
        Point point = this.v;
        if (point != null) {
            bundle.putInt("fix_x", point.x);
            bundle.putInt("fix_y", this.v.y);
        }
        bundle.putInt("isfixed", this.n ? 1 : 0);
        ArrayList<BitmapDescriptor> arrayList = this.p;
        if (arrayList != null && arrayList.size() > 0) {
            a(this.p, bundle);
        }
        bundle2.putBundle("param", bundle);
        return bundle;
    }

    public void cancelAnimation() {
        Animation animation = this.r;
        if (animation != null) {
            animation.bdAnimation.b();
        }
    }

    public float getAlpha() {
        return this.l;
    }

    public float getAnchorX() {
        return this.c;
    }

    public float getAnchorY() {
        return this.d;
    }

    public Point getFixedPosition() {
        return this.v;
    }

    public BitmapDescriptor getIcon() {
        return this.b;
    }

    public ArrayList<BitmapDescriptor> getIcons() {
        return this.p;
    }

    public String getId() {
        return this.z;
    }

    public InfoWindow getInfoWindow() {
        return this.w;
    }

    public int getPeriod() {
        return this.q;
    }

    public LatLng getPosition() {
        return this.a;
    }

    public float getRotate() {
        return this.g;
    }

    public float getScale() {
        return this.u;
    }

    public float getScaleX() {
        return this.s;
    }

    public float getScaleY() {
        return this.t;
    }

    public String getTitle() {
        return this.h;
    }

    public int getYOffset() {
        return this.i;
    }

    public void hideInfoWindow() {
        InfoWindow.a aVar = this.x;
        if (aVar != null) {
            aVar.a(this.w);
            this.y = false;
        }
    }

    public boolean isClickable() {
        return this.o;
    }

    public boolean isDraggable() {
        return this.f;
    }

    public boolean isFixed() {
        return this.n;
    }

    public boolean isFlat() {
        return this.j;
    }

    public boolean isInfoWindowEnabled() {
        return this.y;
    }

    public boolean isPerspective() {
        return this.e;
    }

    public void setAlpha(float f2) {
        if (f2 < 0.0f || ((double) f2) > 1.0d) {
            this.l = 1.0f;
            return;
        }
        this.l = f2;
        this.listener.b(this);
    }

    public void setAnchor(float f2, float f3) {
        if (f2 >= 0.0f && f2 <= 1.0f && f3 >= 0.0f && f3 <= 1.0f) {
            this.c = f2;
            this.d = f3;
            this.listener.b(this);
        }
    }

    public void setAnimateType(int i2) {
        this.m = i2;
        this.listener.b(this);
    }

    public void setAnimation(Animation animation) {
        if (animation != null) {
            this.r = animation;
            animation.bdAnimation.a(this, animation);
        }
    }

    public void setClickable(boolean z) {
        this.o = z;
        this.listener.b(this);
    }

    public void setDraggable(boolean z) {
        this.f = z;
        this.listener.b(this);
    }

    public void setFixedScreenPosition(Point point) {
        if (point != null) {
            this.v = point;
            this.n = true;
            this.listener.b(this);
            return;
        }
        throw new IllegalArgumentException("BDMapSDKException: the screenPosition can not be null");
    }

    public void setFlat(boolean z) {
        this.j = z;
        this.listener.b(this);
    }

    public void setIcon(BitmapDescriptor bitmapDescriptor) {
        if (bitmapDescriptor != null) {
            this.b = bitmapDescriptor;
            this.listener.b(this);
            return;
        }
        throw new IllegalArgumentException("BDMapSDKException: marker's icon can not be null");
    }

    public void setIcons(ArrayList<BitmapDescriptor> arrayList) {
        BitmapDescriptor bitmapDescriptor;
        if (arrayList == null) {
            throw new IllegalArgumentException("BDMapSDKException: marker's icons can not be null");
        } else if (arrayList.size() != 0) {
            int i2 = 0;
            if (arrayList.size() == 1) {
                bitmapDescriptor = arrayList.get(0);
            } else {
                while (i2 < arrayList.size()) {
                    if (arrayList.get(i2) != null && arrayList.get(i2).a != null) {
                        i2++;
                    } else {
                        return;
                    }
                }
                this.p = (ArrayList) arrayList.clone();
                bitmapDescriptor = null;
            }
            this.b = bitmapDescriptor;
            this.listener.b(this);
        }
    }

    public void setPeriod(int i2) {
        if (i2 > 0) {
            this.q = i2;
            this.listener.b(this);
            return;
        }
        throw new IllegalArgumentException("BDMapSDKException: marker's period must be greater than zero ");
    }

    public void setPerspective(boolean z) {
        this.e = z;
        this.listener.b(this);
    }

    public void setPosition(LatLng latLng) {
        if (latLng != null) {
            this.a = latLng;
            this.listener.b(this);
            return;
        }
        throw new IllegalArgumentException("BDMapSDKException: marker's position can not be null");
    }

    public void setPositionWithInfoWindow(LatLng latLng) {
        if (latLng != null) {
            this.a = latLng;
            this.listener.b(this);
            InfoWindow infoWindow = this.w;
            if (infoWindow != null) {
                infoWindow.setPosition(latLng);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("BDMapSDKException: marker's position can not be null");
    }

    public void setRotate(float f2) {
        while (f2 < 0.0f) {
            f2 += 360.0f;
        }
        this.g = f2 % 360.0f;
        this.listener.b(this);
    }

    public void setScale(float f2) {
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        this.s = f2;
        this.t = f2;
        this.listener.b(this);
    }

    public void setScaleX(float f2) {
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        this.s = f2;
        this.listener.b(this);
    }

    public void setScaleY(float f2) {
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        this.t = f2;
        this.listener.b(this);
    }

    public void setTitle(String str) {
        this.h = str;
    }

    public void setToTop() {
        this.k = true;
        this.listener.b(this);
    }

    public void setYOffset(int i2) {
        this.i = i2;
        this.listener.b(this);
    }

    public void showInfoWindow(InfoWindow infoWindow) {
        if (infoWindow != null) {
            this.w = infoWindow;
            InfoWindow.a aVar = this.x;
            if (aVar != null) {
                aVar.b(infoWindow);
                this.y = true;
                return;
            }
            return;
        }
        throw new IllegalArgumentException("BDMapSDKException: the InfoWindow can not be null");
    }

    public void showSmoothMoveInfoWindow(InfoWindow infoWindow) {
        if (infoWindow != null) {
            if (!infoWindow.j) {
                throw new IllegalArgumentException("BDMapSDKException: the SmoothMoveInfoWindow must build with View");
            } else if (infoWindow.b != null) {
                this.w = infoWindow;
                infoWindow.i = true;
                InfoWindow.a aVar = this.x;
                if (aVar != null) {
                    aVar.b(infoWindow);
                    this.y = true;
                }
            } else {
                throw new IllegalArgumentException("BDMapSDKException: the SmoothMoveInfoWindow's View can not be null");
            }
        }
    }

    public void startAnimation() {
        Animation animation = this.r;
        if (animation != null) {
            animation.bdAnimation.a();
        }
    }

    public void updateInfoWindowBitmapDescriptor(BitmapDescriptor bitmapDescriptor) {
        InfoWindow infoWindow = this.w;
        if (infoWindow != null && !infoWindow.k) {
            this.w.setBitmapDescriptor(bitmapDescriptor);
        }
    }

    public void updateInfoWindowPosition(LatLng latLng) {
        InfoWindow infoWindow = this.w;
        if (infoWindow != null) {
            infoWindow.setPosition(latLng);
        }
    }

    public void updateInfoWindowView(View view) {
        InfoWindow infoWindow = this.w;
        if (infoWindow != null && infoWindow.j) {
            this.w.setView(view);
        }
    }

    public void updateInfoWindowYOffset(int i2) {
        InfoWindow infoWindow = this.w;
        if (infoWindow != null) {
            infoWindow.setYOffset(i2);
        }
    }
}
