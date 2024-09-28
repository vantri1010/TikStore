package com.google.android.gms.vision.face.internal.client;

import android.content.Context;
import android.graphics.PointF;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.internal.vision.zzl;
import com.google.android.gms.internal.vision.zzm;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import java.nio.ByteBuffer;

public final class zza extends zzl<zze> {
    private final zzc zzce;

    public zza(Context context, zzc zzc) {
        super(context, "FaceNativeHandle", "face");
        this.zzce = zzc;
        zzp();
    }

    /* access modifiers changed from: protected */
    public final /* synthetic */ Object zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, DynamiteModule.LoadingException {
        zzg zzg;
        IBinder instantiate = dynamiteModule.instantiate("com.google.android.gms.vision.face.ChimeraNativeFaceDetectorCreator");
        if (instantiate == null) {
            zzg = null;
        } else {
            IInterface queryLocalInterface = instantiate.queryLocalInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetectorCreator");
            zzg = queryLocalInterface instanceof zzg ? (zzg) queryLocalInterface : new zzh(instantiate);
        }
        if (zzg == null) {
            return null;
        }
        return zzg.zza(ObjectWrapper.wrap(context), this.zzce);
    }

    public final Face[] zzb(ByteBuffer byteBuffer, zzm zzm) {
        FaceParcel[] faceParcelArr;
        Landmark[] landmarkArr;
        int i = 0;
        if (!isOperational()) {
            return new Face[0];
        }
        try {
            FaceParcel[] zzc = ((zze) zzp()).zzc(ObjectWrapper.wrap(byteBuffer), zzm);
            Face[] faceArr = new Face[zzc.length];
            int i2 = 0;
            while (i2 < zzc.length) {
                FaceParcel faceParcel = zzc[i2];
                int i3 = faceParcel.id;
                PointF pointF = new PointF(faceParcel.centerX, faceParcel.centerY);
                float f = faceParcel.width;
                float f2 = faceParcel.height;
                float f3 = faceParcel.zzcf;
                float f4 = faceParcel.zzcg;
                LandmarkParcel[] landmarkParcelArr = faceParcel.zzch;
                if (landmarkParcelArr == null) {
                    faceParcelArr = zzc;
                    landmarkArr = new Landmark[i];
                } else {
                    landmarkArr = new Landmark[landmarkParcelArr.length];
                    int i4 = 0;
                    while (i4 < landmarkParcelArr.length) {
                        LandmarkParcel landmarkParcel = landmarkParcelArr[i4];
                        landmarkArr[i4] = new Landmark(new PointF(landmarkParcel.x, landmarkParcel.y), landmarkParcel.type);
                        i4++;
                        zzc = zzc;
                        landmarkParcelArr = landmarkParcelArr;
                    }
                    faceParcelArr = zzc;
                }
                faceArr[i2] = new Face(i3, pointF, f, f2, f3, f4, landmarkArr, faceParcel.zzbs, faceParcel.zzbt, faceParcel.zzbu);
                i2++;
                zzc = faceParcelArr;
                i = 0;
            }
            return faceArr;
        } catch (RemoteException e) {
            Log.e("FaceNativeHandle", "Could not call native face detector", e);
            return new Face[0];
        }
    }

    public final boolean zzd(int i) {
        if (!isOperational()) {
            return false;
        }
        try {
            return ((zze) zzp()).zzd(i);
        } catch (RemoteException e) {
            Log.e("FaceNativeHandle", "Could not call native face detector", e);
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public final void zzm() throws RemoteException {
        ((zze) zzp()).zzn();
    }
}
