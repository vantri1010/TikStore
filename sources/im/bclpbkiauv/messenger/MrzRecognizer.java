package im.bclpbkiauv.messenger;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import androidx.exifinterface.media.ExifInterface;
import com.bigkoo.pickerview.utils.LunarCalendar;
import com.google.zxing.client.result.ExpandedProductParsedResult;
import java.util.Calendar;
import java.util.HashMap;

public class MrzRecognizer {

    public static class Result {
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_UNKNOWN = 0;
        public static final int TYPE_DRIVER_LICENSE = 4;
        public static final int TYPE_ID = 2;
        public static final int TYPE_INTERNAL_PASSPORT = 3;
        public static final int TYPE_PASSPORT = 1;
        public int birthDay;
        public int birthMonth;
        public int birthYear;
        public boolean doesNotExpire;
        public int expiryDay;
        public int expiryMonth;
        public int expiryYear;
        public String firstName;
        public int gender;
        public String issuingCountry;
        public String lastName;
        public boolean mainCheckDigitIsValid;
        public String middleName;
        public String nationality;
        public String number;
        public String rawMRZ;
        public int type;
    }

    private static native Rect[][] binarizeAndFindCharacters(Bitmap bitmap, Bitmap bitmap2);

    private static native int[] findCornerPoints(Bitmap bitmap);

    private static native String performRecognition(Bitmap bitmap, int i, int i2, AssetManager assetManager);

    private static native void setYuvBitmapPixels(Bitmap bitmap, byte[] bArr);

    public static Result recognize(Bitmap bitmap, boolean tryDriverLicenseFirst) {
        Result res;
        Result res2;
        if (tryDriverLicenseFirst && (res2 = recognizeBarcode(bitmap)) != null) {
            return res2;
        }
        try {
            Result res3 = recognizeMRZ(bitmap);
            if (res3 != null) {
                return res3;
            }
        } catch (Exception e) {
        }
        if (tryDriverLicenseFirst || (res = recognizeBarcode(bitmap)) == null) {
            return null;
        }
        return res;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:40:0x010b, code lost:
        if (r6.equals("1") != false) goto L_0x010f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0125  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static im.bclpbkiauv.messenger.MrzRecognizer.Result recognizeBarcode(android.graphics.Bitmap r13) {
        /*
            com.google.android.gms.vision.barcode.BarcodeDetector$Builder r0 = new com.google.android.gms.vision.barcode.BarcodeDetector$Builder
            android.content.Context r1 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r0.<init>(r1)
            com.google.android.gms.vision.barcode.BarcodeDetector r0 = r0.build()
            int r1 = r13.getWidth()
            r2 = 1500(0x5dc, float:2.102E-42)
            r3 = 1
            if (r1 > r2) goto L_0x001a
            int r1 = r13.getHeight()
            if (r1 <= r2) goto L_0x0045
        L_0x001a:
            r1 = 1153138688(0x44bb8000, float:1500.0)
            int r2 = r13.getWidth()
            int r4 = r13.getHeight()
            int r2 = java.lang.Math.max(r2, r4)
            float r2 = (float) r2
            float r1 = r1 / r2
            int r2 = r13.getWidth()
            float r2 = (float) r2
            float r2 = r2 * r1
            int r2 = java.lang.Math.round(r2)
            int r4 = r13.getHeight()
            float r4 = (float) r4
            float r4 = r4 * r1
            int r4 = java.lang.Math.round(r4)
            android.graphics.Bitmap r13 = android.graphics.Bitmap.createScaledBitmap(r13, r2, r4, r3)
        L_0x0045:
            com.google.android.gms.vision.Frame$Builder r1 = new com.google.android.gms.vision.Frame$Builder
            r1.<init>()
            com.google.android.gms.vision.Frame$Builder r1 = r1.setBitmap(r13)
            com.google.android.gms.vision.Frame r1 = r1.build()
            android.util.SparseArray r1 = r0.detect(r1)
            r2 = 0
        L_0x0057:
            int r4 = r1.size()
            if (r2 >= r4) goto L_0x0263
            java.lang.Object r4 = r1.valueAt(r2)
            com.google.android.gms.vision.barcode.Barcode r4 = (com.google.android.gms.vision.barcode.Barcode) r4
            int r5 = r4.valueFormat
            r6 = 12
            r7 = 0
            r8 = 2
            r9 = 4
            if (r5 != r6) goto L_0x01ae
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r5 = r4.driverLicense
            if (r5 == 0) goto L_0x01ae
            im.bclpbkiauv.messenger.MrzRecognizer$Result r5 = new im.bclpbkiauv.messenger.MrzRecognizer$Result
            r5.<init>()
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r6 = r4.driverLicense
            java.lang.String r6 = r6.documentType
            java.lang.String r10 = "ID"
            boolean r6 = r10.equals(r6)
            if (r6 == 0) goto L_0x0082
            r9 = 2
        L_0x0082:
            r5.type = r9
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r6 = r4.driverLicense
            java.lang.String r6 = r6.issuingCountry
            int r9 = r6.hashCode()
            r10 = 66480(0x103b0, float:9.3158E-41)
            java.lang.String r11 = "USA"
            r12 = -1
            if (r9 == r10) goto L_0x00a2
            r10 = 84323(0x14963, float:1.18162E-40)
            if (r9 == r10) goto L_0x009a
        L_0x0099:
            goto L_0x00ac
        L_0x009a:
            boolean r6 = r6.equals(r11)
            if (r6 == 0) goto L_0x0099
            r6 = 0
            goto L_0x00ad
        L_0x00a2:
            java.lang.String r9 = "CAN"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x0099
            r6 = 1
            goto L_0x00ad
        L_0x00ac:
            r6 = -1
        L_0x00ad:
            if (r6 == 0) goto L_0x00b9
            if (r6 == r3) goto L_0x00b2
            goto L_0x00c0
        L_0x00b2:
            java.lang.String r6 = "CA"
            r5.issuingCountry = r6
            r5.nationality = r6
            goto L_0x00c0
        L_0x00b9:
            java.lang.String r6 = "US"
            r5.issuingCountry = r6
            r5.nationality = r6
        L_0x00c0:
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r6 = r4.driverLicense
            java.lang.String r6 = r6.firstName
            java.lang.String r6 = capitalize(r6)
            r5.firstName = r6
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r6 = r4.driverLicense
            java.lang.String r6 = r6.lastName
            java.lang.String r6 = capitalize(r6)
            r5.lastName = r6
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r6 = r4.driverLicense
            java.lang.String r6 = r6.middleName
            java.lang.String r6 = capitalize(r6)
            r5.middleName = r6
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r6 = r4.driverLicense
            java.lang.String r6 = r6.licenseNumber
            r5.number = r6
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r6 = r4.driverLicense
            java.lang.String r6 = r6.gender
            if (r6 == 0) goto L_0x0119
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r6 = r4.driverLicense
            java.lang.String r6 = r6.gender
            int r9 = r6.hashCode()
            r10 = 49
            if (r9 == r10) goto L_0x0105
            r7 = 50
            if (r9 == r7) goto L_0x00fb
        L_0x00fa:
            goto L_0x010e
        L_0x00fb:
            java.lang.String r7 = "2"
            boolean r6 = r6.equals(r7)
            if (r6 == 0) goto L_0x00fa
            r7 = 1
            goto L_0x010f
        L_0x0105:
            java.lang.String r9 = "1"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x00fa
            goto L_0x010f
        L_0x010e:
            r7 = -1
        L_0x010f:
            if (r7 == 0) goto L_0x0117
            if (r7 == r3) goto L_0x0114
            goto L_0x0119
        L_0x0114:
            r5.gender = r8
            goto L_0x0119
        L_0x0117:
            r5.gender = r3
        L_0x0119:
            java.lang.String r3 = r5.issuingCountry
            boolean r3 = r11.equals(r3)
            if (r3 == 0) goto L_0x0125
            r3 = 4
            r6 = 2
            r7 = 0
            goto L_0x0128
        L_0x0125:
            r3 = 0
            r7 = 4
            r6 = 6
        L_0x0128:
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r8 = r4.driverLicense     // Catch:{ NumberFormatException -> 0x01ab }
            java.lang.String r8 = r8.birthDate     // Catch:{ NumberFormatException -> 0x01ab }
            r9 = 8
            if (r8 == 0) goto L_0x016a
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r8 = r4.driverLicense     // Catch:{ NumberFormatException -> 0x01ab }
            java.lang.String r8 = r8.birthDate     // Catch:{ NumberFormatException -> 0x01ab }
            int r8 = r8.length()     // Catch:{ NumberFormatException -> 0x01ab }
            if (r8 != r9) goto L_0x016a
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r8 = r4.driverLicense     // Catch:{ NumberFormatException -> 0x01ab }
            java.lang.String r8 = r8.birthDate     // Catch:{ NumberFormatException -> 0x01ab }
            int r10 = r3 + 4
            java.lang.String r8 = r8.substring(r3, r10)     // Catch:{ NumberFormatException -> 0x01ab }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ NumberFormatException -> 0x01ab }
            r5.birthYear = r8     // Catch:{ NumberFormatException -> 0x01ab }
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r8 = r4.driverLicense     // Catch:{ NumberFormatException -> 0x01ab }
            java.lang.String r8 = r8.birthDate     // Catch:{ NumberFormatException -> 0x01ab }
            int r10 = r7 + 2
            java.lang.String r8 = r8.substring(r7, r10)     // Catch:{ NumberFormatException -> 0x01ab }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ NumberFormatException -> 0x01ab }
            r5.birthMonth = r8     // Catch:{ NumberFormatException -> 0x01ab }
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r8 = r4.driverLicense     // Catch:{ NumberFormatException -> 0x01ab }
            java.lang.String r8 = r8.birthDate     // Catch:{ NumberFormatException -> 0x01ab }
            int r10 = r6 + 2
            java.lang.String r8 = r8.substring(r6, r10)     // Catch:{ NumberFormatException -> 0x01ab }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ NumberFormatException -> 0x01ab }
            r5.birthDay = r8     // Catch:{ NumberFormatException -> 0x01ab }
        L_0x016a:
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r8 = r4.driverLicense     // Catch:{ NumberFormatException -> 0x01ab }
            java.lang.String r8 = r8.expiryDate     // Catch:{ NumberFormatException -> 0x01ab }
            if (r8 == 0) goto L_0x01ac
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r8 = r4.driverLicense     // Catch:{ NumberFormatException -> 0x01ab }
            java.lang.String r8 = r8.expiryDate     // Catch:{ NumberFormatException -> 0x01ab }
            int r8 = r8.length()     // Catch:{ NumberFormatException -> 0x01ab }
            if (r8 != r9) goto L_0x01ac
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r8 = r4.driverLicense     // Catch:{ NumberFormatException -> 0x01ab }
            java.lang.String r8 = r8.expiryDate     // Catch:{ NumberFormatException -> 0x01ab }
            int r9 = r3 + 4
            java.lang.String r8 = r8.substring(r3, r9)     // Catch:{ NumberFormatException -> 0x01ab }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ NumberFormatException -> 0x01ab }
            r5.expiryYear = r8     // Catch:{ NumberFormatException -> 0x01ab }
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r8 = r4.driverLicense     // Catch:{ NumberFormatException -> 0x01ab }
            java.lang.String r8 = r8.expiryDate     // Catch:{ NumberFormatException -> 0x01ab }
            int r9 = r7 + 2
            java.lang.String r8 = r8.substring(r7, r9)     // Catch:{ NumberFormatException -> 0x01ab }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ NumberFormatException -> 0x01ab }
            r5.expiryMonth = r8     // Catch:{ NumberFormatException -> 0x01ab }
            com.google.android.gms.vision.barcode.Barcode$DriverLicense r8 = r4.driverLicense     // Catch:{ NumberFormatException -> 0x01ab }
            java.lang.String r8 = r8.expiryDate     // Catch:{ NumberFormatException -> 0x01ab }
            int r9 = r6 + 2
            java.lang.String r8 = r8.substring(r6, r9)     // Catch:{ NumberFormatException -> 0x01ab }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ NumberFormatException -> 0x01ab }
            r5.expiryDay = r8     // Catch:{ NumberFormatException -> 0x01ab }
            goto L_0x01ac
        L_0x01ab:
            r8 = move-exception
        L_0x01ac:
            return r5
        L_0x01ae:
            int r5 = r4.valueFormat
            r6 = 7
            if (r5 != r6) goto L_0x025f
            int r5 = r4.format
            r6 = 2048(0x800, float:2.87E-42)
            if (r5 != r6) goto L_0x025f
            java.lang.String r5 = r4.rawValue
            java.lang.String r6 = "^[A-Za-z0-9=]+$"
            boolean r5 = r5.matches(r6)
            if (r5 == 0) goto L_0x025f
            java.lang.String r5 = r4.rawValue     // Catch:{ Exception -> 0x025e }
            byte[] r5 = android.util.Base64.decode(r5, r7)     // Catch:{ Exception -> 0x025e }
            java.lang.String r6 = new java.lang.String     // Catch:{ Exception -> 0x025e }
            java.lang.String r10 = "windows-1251"
            r6.<init>(r5, r10)     // Catch:{ Exception -> 0x025e }
            java.lang.String r10 = "\\|"
            java.lang.String[] r6 = r6.split(r10)     // Catch:{ Exception -> 0x025e }
            int r10 = r6.length     // Catch:{ Exception -> 0x025e }
            r11 = 10
            if (r10 < r11) goto L_0x025d
            im.bclpbkiauv.messenger.MrzRecognizer$Result r10 = new im.bclpbkiauv.messenger.MrzRecognizer$Result     // Catch:{ Exception -> 0x025e }
            r10.<init>()     // Catch:{ Exception -> 0x025e }
            r10.type = r9     // Catch:{ Exception -> 0x025e }
            java.lang.String r11 = "RU"
            r10.issuingCountry = r11     // Catch:{ Exception -> 0x025e }
            r10.nationality = r11     // Catch:{ Exception -> 0x025e }
            r11 = r6[r7]     // Catch:{ Exception -> 0x025e }
            r10.number = r11     // Catch:{ Exception -> 0x025e }
            r11 = r6[r8]     // Catch:{ Exception -> 0x025e }
            java.lang.String r11 = r11.substring(r7, r9)     // Catch:{ Exception -> 0x025e }
            int r11 = java.lang.Integer.parseInt(r11)     // Catch:{ Exception -> 0x025e }
            r10.expiryYear = r11     // Catch:{ Exception -> 0x025e }
            r11 = r6[r8]     // Catch:{ Exception -> 0x025e }
            r12 = 6
            java.lang.String r11 = r11.substring(r9, r12)     // Catch:{ Exception -> 0x025e }
            int r11 = java.lang.Integer.parseInt(r11)     // Catch:{ Exception -> 0x025e }
            r10.expiryMonth = r11     // Catch:{ Exception -> 0x025e }
            r8 = r6[r8]     // Catch:{ Exception -> 0x025e }
            java.lang.String r8 = r8.substring(r12)     // Catch:{ Exception -> 0x025e }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ Exception -> 0x025e }
            r10.expiryDay = r8     // Catch:{ Exception -> 0x025e }
            r8 = 3
            r8 = r6[r8]     // Catch:{ Exception -> 0x025e }
            java.lang.String r8 = cyrillicToLatin(r8)     // Catch:{ Exception -> 0x025e }
            java.lang.String r8 = capitalize(r8)     // Catch:{ Exception -> 0x025e }
            r10.lastName = r8     // Catch:{ Exception -> 0x025e }
            r8 = r6[r9]     // Catch:{ Exception -> 0x025e }
            java.lang.String r8 = cyrillicToLatin(r8)     // Catch:{ Exception -> 0x025e }
            java.lang.String r8 = capitalize(r8)     // Catch:{ Exception -> 0x025e }
            r10.firstName = r8     // Catch:{ Exception -> 0x025e }
            r8 = 5
            r8 = r6[r8]     // Catch:{ Exception -> 0x025e }
            java.lang.String r8 = cyrillicToLatin(r8)     // Catch:{ Exception -> 0x025e }
            java.lang.String r8 = capitalize(r8)     // Catch:{ Exception -> 0x025e }
            r10.middleName = r8     // Catch:{ Exception -> 0x025e }
            r8 = r6[r12]     // Catch:{ Exception -> 0x025e }
            java.lang.String r7 = r8.substring(r7, r9)     // Catch:{ Exception -> 0x025e }
            int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x025e }
            r10.birthYear = r7     // Catch:{ Exception -> 0x025e }
            r7 = r6[r12]     // Catch:{ Exception -> 0x025e }
            java.lang.String r7 = r7.substring(r9, r12)     // Catch:{ Exception -> 0x025e }
            int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x025e }
            r10.birthMonth = r7     // Catch:{ Exception -> 0x025e }
            r7 = r6[r12]     // Catch:{ Exception -> 0x025e }
            java.lang.String r7 = r7.substring(r12)     // Catch:{ Exception -> 0x025e }
            int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x025e }
            r10.birthDay = r7     // Catch:{ Exception -> 0x025e }
            return r10
        L_0x025d:
            goto L_0x025f
        L_0x025e:
            r5 = move-exception
        L_0x025f:
            int r2 = r2 + 1
            goto L_0x0057
        L_0x0263:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MrzRecognizer.recognizeBarcode(android.graphics.Bitmap):im.bclpbkiauv.messenger.MrzRecognizer$Result");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0662, code lost:
        if (r1[1].charAt(14) == '<') goto L_0x0667;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x04cc, code lost:
        if (r1[1].charAt(27) == '<') goto L_0x04d1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static im.bclpbkiauv.messenger.MrzRecognizer.Result recognizeMRZ(android.graphics.Bitmap r44) {
        /*
            r0 = r44
            r1 = 1065353216(0x3f800000, float:1.0)
            int r2 = r44.getWidth()
            r3 = 1
            r4 = 512(0x200, float:7.175E-43)
            if (r2 > r4) goto L_0x0019
            int r2 = r44.getHeight()
            r4 = 512(0x200, float:7.175E-43)
            if (r2 <= r4) goto L_0x0016
            goto L_0x0019
        L_0x0016:
            r2 = r44
            goto L_0x0044
        L_0x0019:
            r2 = 1140850688(0x44000000, float:512.0)
            int r4 = r44.getWidth()
            int r5 = r44.getHeight()
            int r4 = java.lang.Math.max(r4, r5)
            float r4 = (float) r4
            float r1 = r2 / r4
            int r2 = r44.getWidth()
            float r2 = (float) r2
            float r2 = r2 * r1
            int r2 = java.lang.Math.round(r2)
            int r4 = r44.getHeight()
            float r4 = (float) r4
            float r4 = r4 * r1
            int r4 = java.lang.Math.round(r4)
            android.graphics.Bitmap r2 = android.graphics.Bitmap.createScaledBitmap(r0, r2, r4, r3)
        L_0x0044:
            int[] r4 = findCornerPoints(r2)
            r5 = 1065353216(0x3f800000, float:1.0)
            float r5 = r5 / r1
            r6 = 7
            r7 = 6
            r8 = 3
            r9 = 5
            r10 = 2
            r11 = 0
            if (r4 == 0) goto L_0x01cd
            android.graphics.Point r12 = new android.graphics.Point
            r13 = r4[r11]
            r14 = r4[r3]
            r12.<init>(r13, r14)
            android.graphics.Point r13 = new android.graphics.Point
            r14 = r4[r10]
            r15 = r4[r8]
            r13.<init>(r14, r15)
            android.graphics.Point r14 = new android.graphics.Point
            r15 = 4
            r15 = r4[r15]
            r8 = r4[r9]
            r14.<init>(r15, r8)
            r8 = r14
            android.graphics.Point r14 = new android.graphics.Point
            r15 = r4[r7]
            r7 = r4[r6]
            r14.<init>(r15, r7)
            r7 = r14
            int r14 = r13.x
            int r15 = r12.x
            if (r14 >= r15) goto L_0x0086
            r14 = r13
            r13 = r12
            r12 = r14
            r14 = r7
            r7 = r8
            r8 = r14
        L_0x0086:
            int r14 = r13.x
            int r15 = r12.x
            int r14 = r14 - r15
            double r14 = (double) r14
            int r6 = r13.y
            int r9 = r12.y
            int r6 = r6 - r9
            r19 = r4
            double r3 = (double) r6
            double r3 = java.lang.Math.hypot(r14, r3)
            int r6 = r7.x
            int r14 = r8.x
            int r6 = r6 - r14
            double r14 = (double) r6
            int r6 = r7.y
            int r9 = r8.y
            int r6 = r6 - r9
            double r10 = (double) r6
            double r10 = java.lang.Math.hypot(r14, r10)
            int r6 = r8.x
            int r9 = r12.x
            int r6 = r6 - r9
            double r14 = (double) r6
            int r6 = r8.y
            int r9 = r12.y
            int r6 = r6 - r9
            r23 = r1
            r24 = r2
            double r1 = (double) r6
            double r1 = java.lang.Math.hypot(r14, r1)
            int r6 = r7.x
            int r9 = r13.x
            int r6 = r6 - r9
            double r14 = (double) r6
            int r6 = r7.y
            int r9 = r13.y
            int r6 = r6 - r9
            r25 = r8
            double r8 = (double) r6
            double r14 = java.lang.Math.hypot(r14, r8)
            double r26 = r3 / r1
            double r28 = r3 / r14
            double r30 = r10 / r1
            double r32 = r10 / r14
            r8 = 4608758678669597082(0x3ff599999999999a, double:1.35)
            int r6 = (r26 > r8 ? 1 : (r26 == r8 ? 0 : -1))
            if (r6 < 0) goto L_0x01c8
            r34 = 4610560118520545280(0x3ffc000000000000, double:1.75)
            int r6 = (r26 > r34 ? 1 : (r26 == r34 ? 0 : -1))
            if (r6 > 0) goto L_0x01c8
            int r6 = (r30 > r8 ? 1 : (r30 == r8 ? 0 : -1))
            if (r6 < 0) goto L_0x01c8
            int r6 = (r30 > r34 ? 1 : (r30 == r34 ? 0 : -1))
            if (r6 > 0) goto L_0x01c8
            int r6 = (r28 > r8 ? 1 : (r28 == r8 ? 0 : -1))
            if (r6 < 0) goto L_0x01c8
            int r6 = (r28 > r34 ? 1 : (r28 == r34 ? 0 : -1))
            if (r6 > 0) goto L_0x01c8
            int r6 = (r32 > r8 ? 1 : (r32 == r8 ? 0 : -1))
            if (r6 < 0) goto L_0x01c8
            int r6 = (r32 > r34 ? 1 : (r32 == r34 ? 0 : -1))
            if (r6 > 0) goto L_0x01c8
            double r8 = r26 + r28
            double r8 = r8 + r30
            double r8 = r8 + r32
            r34 = 4616189618054758400(0x4010000000000000, double:4.0)
            double r34 = r8 / r34
            r6 = 1024(0x400, float:1.435E-42)
            r8 = 4652218415073722368(0x4090000000000000, double:1024.0)
            double r8 = r8 / r34
            long r8 = java.lang.Math.round(r8)
            int r9 = (int) r8
            android.graphics.Bitmap$Config r8 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r6 = android.graphics.Bitmap.createBitmap(r6, r9, r8)
            android.graphics.Canvas r8 = new android.graphics.Canvas
            r8.<init>(r6)
            r9 = 8
            float[] r9 = new float[r9]
            r36 = 0
            r22 = 0
            r9[r22] = r36
            r20 = 1
            r9[r20] = r36
            r20 = r9
            int r9 = r6.getWidth()
            float r9 = (float) r9
            r21 = 2
            r20[r21] = r9
            r9 = 0
            r16 = 3
            r20[r16] = r9
            r9 = 4
            r42 = r1
            int r1 = r6.getWidth()
            float r1 = (float) r1
            r20[r9] = r1
            int r1 = r6.getHeight()
            float r1 = (float) r1
            r2 = 5
            r20[r2] = r1
            r1 = 0
            r2 = 6
            r20[r2] = r1
            int r1 = r6.getHeight()
            float r1 = (float) r1
            r2 = 7
            r20[r2] = r1
            r39 = r20
            r1 = 8
            float[] r1 = new float[r1]
            int r2 = r12.x
            float r2 = (float) r2
            float r2 = r2 * r5
            r9 = 0
            r1[r9] = r2
            int r2 = r12.y
            float r2 = (float) r2
            float r2 = r2 * r5
            r9 = 1
            r1[r9] = r2
            int r2 = r13.x
            float r2 = (float) r2
            float r2 = r2 * r5
            r20 = 2
            r1[r20] = r2
            int r2 = r13.y
            float r2 = (float) r2
            float r2 = r2 * r5
            r16 = 3
            r1[r16] = r2
            r2 = 4
            int r9 = r7.x
            float r9 = (float) r9
            float r9 = r9 * r5
            r1[r2] = r9
            int r2 = r7.y
            float r2 = (float) r2
            float r2 = r2 * r5
            r9 = 5
            r1[r9] = r2
            r2 = r25
            int r9 = r2.x
            float r9 = (float) r9
            float r9 = r9 * r5
            r17 = 6
            r1[r17] = r9
            int r9 = r2.y
            float r9 = (float) r9
            float r9 = r9 * r5
            r18 = 7
            r1[r18] = r9
            android.graphics.Matrix r9 = new android.graphics.Matrix
            r9.<init>()
            r38 = 0
            r40 = 0
            int r2 = r1.length
            r20 = 1
            int r41 = r2 >> 1
            r2 = r9
            r36 = r2
            r37 = r1
            r36.setPolyToPoly(r37, r38, r39, r40, r41)
            android.graphics.Paint r9 = new android.graphics.Paint
            r36 = r1
            r1 = 2
            r9.<init>(r1)
            r8.drawBitmap(r0, r2, r9)
            r0 = r6
            goto L_0x01ca
        L_0x01c8:
            r42 = r1
        L_0x01ca:
            r1 = r23
            goto L_0x0213
        L_0x01cd:
            r23 = r1
            r24 = r2
            r19 = r4
            int r1 = r44.getWidth()
            r2 = 1500(0x5dc, float:2.102E-42)
            if (r1 > r2) goto L_0x01e7
            int r1 = r44.getHeight()
            r2 = 1500(0x5dc, float:2.102E-42)
            if (r1 <= r2) goto L_0x01e4
            goto L_0x01e7
        L_0x01e4:
            r1 = r23
            goto L_0x0213
        L_0x01e7:
            r1 = 1153138688(0x44bb8000, float:1500.0)
            int r2 = r44.getWidth()
            int r3 = r44.getHeight()
            int r2 = java.lang.Math.max(r2, r3)
            float r2 = (float) r2
            float r1 = r1 / r2
            int r2 = r44.getWidth()
            float r2 = (float) r2
            float r2 = r2 * r1
            int r2 = java.lang.Math.round(r2)
            int r3 = r44.getHeight()
            float r3 = (float) r3
            float r3 = r3 * r1
            int r3 = java.lang.Math.round(r3)
            r4 = 1
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r2, r3, r4)
        L_0x0213:
            r2 = 0
            r3 = 0
            r4 = r3
            android.graphics.Rect[][] r4 = (android.graphics.Rect[][]) r4
            r6 = 0
            r7 = 0
            r8 = 0
        L_0x021b:
            r10 = 30
            r11 = 3
            if (r8 >= r11) goto L_0x02a5
            r11 = 0
            r12 = r0
            r9 = 1
            if (r8 == r9) goto L_0x0242
            r13 = 2
            if (r8 == r13) goto L_0x0229
            goto L_0x025b
        L_0x0229:
            android.graphics.Matrix r14 = new android.graphics.Matrix
            r14.<init>()
            r11 = r14
            r14 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r15 = r0.getWidth()
            int r15 = r15 / r13
            float r15 = (float) r15
            int r20 = r0.getHeight()
            int r9 = r20 / 2
            float r9 = (float) r9
            r11.setRotate(r14, r15, r9)
            goto L_0x025b
        L_0x0242:
            r13 = 2
            android.graphics.Matrix r9 = new android.graphics.Matrix
            r9.<init>()
            r11 = r9
            r9 = 1065353216(0x3f800000, float:1.0)
            int r14 = r0.getWidth()
            int r14 = r14 / r13
            float r14 = (float) r14
            int r15 = r0.getHeight()
            int r15 = r15 / r13
            float r13 = (float) r15
            r11.setRotate(r9, r14, r13)
        L_0x025b:
            if (r11 == 0) goto L_0x0273
            r26 = 0
            r27 = 0
            int r28 = r0.getWidth()
            int r29 = r0.getHeight()
            r31 = 1
            r25 = r0
            r30 = r11
            android.graphics.Bitmap r12 = android.graphics.Bitmap.createBitmap(r25, r26, r27, r28, r29, r30, r31)
        L_0x0273:
            int r9 = r12.getWidth()
            int r13 = r12.getHeight()
            android.graphics.Bitmap$Config r14 = android.graphics.Bitmap.Config.ALPHA_8
            android.graphics.Bitmap r2 = android.graphics.Bitmap.createBitmap(r9, r13, r14)
            android.graphics.Rect[][] r4 = binarizeAndFindCharacters(r12, r2)
            if (r4 != 0) goto L_0x0288
            return r3
        L_0x0288:
            int r9 = r4.length
            r13 = 0
        L_0x028a:
            if (r13 >= r9) goto L_0x029b
            r14 = r4[r13]
            int r15 = r14.length
            int r6 = java.lang.Math.max(r15, r6)
            int r15 = r14.length
            if (r15 <= 0) goto L_0x0298
            int r7 = r7 + 1
        L_0x0298:
            int r13 = r13 + 1
            goto L_0x028a
        L_0x029b:
            r9 = 2
            if (r7 < r9) goto L_0x02a1
            if (r6 < r10) goto L_0x02a1
            goto L_0x02a5
        L_0x02a1:
            int r8 = r8 + 1
            goto L_0x021b
        L_0x02a5:
            if (r6 < r10) goto L_0x08ce
            r8 = 2
            if (r7 >= r8) goto L_0x02b9
            r28 = r0
            r29 = r1
            r41 = r2
            r0 = r3
            r36 = r4
            r32 = r5
            r33 = r6
            goto L_0x08db
        L_0x02b9:
            r8 = 0
            r9 = r4[r8]
            int r8 = r9.length
            r11 = 10
            int r8 = r8 * 10
            int r9 = r4.length
            r12 = 15
            int r9 = r9 * 15
            android.graphics.Bitmap$Config r13 = android.graphics.Bitmap.Config.ALPHA_8
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r8, r9, r13)
            android.graphics.Canvas r9 = new android.graphics.Canvas
            r9.<init>(r8)
            r13 = r9
            android.graphics.Paint r9 = new android.graphics.Paint
            r14 = 2
            r9.<init>(r14)
            r14 = r9
            android.graphics.Rect r9 = new android.graphics.Rect
            r15 = 0
            r9.<init>(r15, r15, r11, r12)
            r15 = r9
            r9 = 0
            int r10 = r4.length
            r20 = r9
            r9 = 0
        L_0x02e5:
            if (r9 >= r10) goto L_0x0335
            r3 = r4[r9]
            r25 = 0
            int r12 = r3.length
            r11 = 0
        L_0x02ed:
            if (r11 >= r12) goto L_0x0321
            r28 = r0
            r0 = r3[r11]
            r29 = r1
            int r1 = r25 * 10
            r30 = r3
            int r3 = r20 * 15
            int r31 = r25 * 10
            r32 = r5
            r27 = 10
            int r5 = r31 + 10
            int r31 = r20 * 15
            r33 = r6
            r26 = 15
            int r6 = r31 + 15
            r15.set(r1, r3, r5, r6)
            r13.drawBitmap(r2, r0, r15, r14)
            int r25 = r25 + 1
            int r11 = r11 + 1
            r0 = r28
            r1 = r29
            r3 = r30
            r5 = r32
            r6 = r33
            goto L_0x02ed
        L_0x0321:
            r28 = r0
            r29 = r1
            r30 = r3
            r32 = r5
            r33 = r6
            int r20 = r20 + 1
            int r9 = r9 + 1
            r3 = 0
            r11 = 10
            r12 = 15
            goto L_0x02e5
        L_0x0335:
            r28 = r0
            r29 = r1
            r32 = r5
            r33 = r6
            int r0 = r4.length
            r1 = 0
            r3 = r4[r1]
            int r1 = r3.length
            android.content.Context r3 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            android.content.res.AssetManager r3 = r3.getAssets()
            java.lang.String r0 = performRecognition(r8, r0, r1, r3)
            if (r0 != 0) goto L_0x0350
            r1 = 0
            return r1
        L_0x0350:
            java.lang.String r1 = "\n"
            java.lang.String[] r1 = android.text.TextUtils.split(r0, r1)
            im.bclpbkiauv.messenger.MrzRecognizer$Result r3 = new im.bclpbkiauv.messenger.MrzRecognizer$Result
            r3.<init>()
            int r5 = r1.length
            r6 = 2
            if (r5 < r6) goto L_0x08c6
            r5 = 0
            r6 = r1[r5]
            int r6 = r6.length()
            r9 = 30
            if (r6 < r9) goto L_0x08c6
            r6 = 1
            r10 = r1[r6]
            int r6 = r10.length()
            r10 = r1[r5]
            int r10 = r10.length()
            if (r6 != r10) goto L_0x08c6
            java.lang.String r6 = "\n"
            java.lang.String r6 = android.text.TextUtils.join(r6, r1)
            r3.rawMRZ = r6
            java.util.HashMap r6 = getCountriesMap()
            r10 = r1[r5]
            char r10 = r10.charAt(r5)
            r5 = 80
            if (r10 != r5) goto L_0x058f
            r5 = 1
            r3.type = r5
            r18 = 0
            r22 = r1[r18]
            int r5 = r22.length()
            r11 = 44
            if (r5 != r11) goto L_0x0587
            r5 = r1[r18]
            r9 = 2
            r11 = 5
            java.lang.String r5 = r5.substring(r9, r11)
            r3.issuingCountry = r5
            r5 = r1[r18]
            java.lang.String r9 = "<<"
            r12 = 6
            int r5 = r5.indexOf(r9, r12)
            r9 = -1
            if (r5 == r9) goto L_0x040c
            r9 = r1[r18]
            java.lang.String r9 = r9.substring(r11, r5)
            r11 = 32
            r12 = 60
            java.lang.String r9 = r9.replace(r12, r11)
            r11 = 79
            r12 = 48
            java.lang.String r9 = r9.replace(r12, r11)
            java.lang.String r9 = r9.trim()
            r3.lastName = r9
            r9 = r1[r18]
            int r11 = r5 + 2
            java.lang.String r9 = r9.substring(r11)
            r40 = r0
            r0 = 60
            r11 = 32
            java.lang.String r9 = r9.replace(r0, r11)
            r0 = 79
            java.lang.String r9 = r9.replace(r12, r0)
            java.lang.String r0 = r9.trim()
            r3.firstName = r0
            java.lang.String r0 = r3.firstName
            java.lang.String r9 = "   "
            boolean r0 = r0.contains(r9)
            if (r0 == 0) goto L_0x040a
            java.lang.String r0 = r3.firstName
            java.lang.String r9 = r3.firstName
            java.lang.String r11 = "   "
            int r9 = r9.indexOf(r11)
            r11 = 0
            java.lang.String r0 = r0.substring(r11, r9)
            r3.firstName = r0
            goto L_0x040f
        L_0x040a:
            r11 = 0
            goto L_0x040f
        L_0x040c:
            r40 = r0
            r11 = 0
        L_0x040f:
            r0 = 1
            r9 = r1[r0]
            r12 = 9
            java.lang.String r9 = r9.substring(r11, r12)
            r11 = 32
            r12 = 60
            java.lang.String r9 = r9.replace(r12, r11)
            r11 = 79
            r12 = 48
            java.lang.String r9 = r9.replace(r11, r12)
            java.lang.String r11 = r9.trim()
            int r12 = checksum(r11)
            r9 = r1[r0]
            r0 = 9
            char r0 = r9.charAt(r0)
            int r0 = getNumber(r0)
            if (r12 != r0) goto L_0x0440
            r3.number = r11
        L_0x0440:
            r0 = 1
            r9 = r1[r0]
            r41 = r2
            r0 = 13
            r2 = 10
            java.lang.String r2 = r9.substring(r2, r0)
            r3.nationality = r2
            r2 = 1
            r9 = r1[r2]
            r2 = 19
            java.lang.String r0 = r9.substring(r0, r2)
            r2 = 79
            r9 = 48
            java.lang.String r0 = r0.replace(r2, r9)
            r2 = 49
            r9 = 73
            java.lang.String r0 = r0.replace(r9, r2)
            int r2 = checksum(r0)
            r36 = r4
            r9 = 1
            r4 = r1[r9]
            r9 = 19
            char r4 = r4.charAt(r9)
            int r4 = getNumber(r4)
            if (r2 != r4) goto L_0x0480
            parseBirthDate(r0, r3)
        L_0x0480:
            r4 = 1
            r9 = r1[r4]
            r4 = 20
            char r4 = r9.charAt(r4)
            int r4 = parseGender(r4)
            r3.gender = r4
            r4 = 1
            r9 = r1[r4]
            r4 = 21
            r17 = r0
            r0 = 27
            java.lang.String r4 = r9.substring(r4, r0)
            r0 = 48
            r9 = 79
            java.lang.String r0 = r4.replace(r9, r0)
            r4 = 49
            r9 = 73
            java.lang.String r0 = r0.replace(r9, r4)
            int r4 = checksum(r0)
            r44 = r2
            r9 = 1
            r2 = r1[r9]
            r9 = 27
            char r2 = r2.charAt(r9)
            int r2 = getNumber(r2)
            if (r4 == r2) goto L_0x04cf
            r18 = r4
            r2 = 1
            r4 = r1[r2]
            char r4 = r4.charAt(r9)
            r9 = 60
            if (r4 != r9) goto L_0x04d4
            goto L_0x04d1
        L_0x04cf:
            r18 = r4
        L_0x04d1:
            parseExpiryDate(r0, r3)
        L_0x04d4:
            java.lang.String r4 = r3.issuingCountry
            java.lang.String r9 = "RUS"
            boolean r4 = r9.equals(r4)
            if (r4 == 0) goto L_0x0557
            r4 = 0
            r9 = r1[r4]
            r2 = 1
            char r9 = r9.charAt(r2)
            r2 = r9
            r9 = 78
            if (r2 != r9) goto L_0x0557
            r2 = 3
            r3.type = r2
            java.lang.String r2 = r3.firstName
            java.lang.String r9 = " "
            java.lang.String[] r2 = r2.split(r9)
            r9 = r2[r4]
            java.lang.String r4 = russianPassportTranslit(r9)
            java.lang.String r4 = cyrillicToLatin(r4)
            r3.firstName = r4
            int r4 = r2.length
            r9 = 1
            if (r4 <= r9) goto L_0x0512
            r4 = r2[r9]
            java.lang.String r4 = russianPassportTranslit(r4)
            java.lang.String r4 = cyrillicToLatin(r4)
            r3.middleName = r4
        L_0x0512:
            java.lang.String r4 = r3.lastName
            java.lang.String r4 = russianPassportTranslit(r4)
            java.lang.String r4 = cyrillicToLatin(r4)
            r3.lastName = r4
            java.lang.String r4 = r3.number
            if (r4 == 0) goto L_0x0552
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r9 = r3.number
            r21 = r0
            r16 = r2
            r0 = 3
            r2 = 0
            java.lang.String r2 = r9.substring(r2, r0)
            r4.append(r2)
            r2 = 1
            r2 = r1[r2]
            r9 = 28
            char r2 = r2.charAt(r9)
            r4.append(r2)
            java.lang.String r2 = r3.number
            java.lang.String r0 = r2.substring(r0)
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r3.number = r0
            goto L_0x0556
        L_0x0552:
            r21 = r0
            r16 = r2
        L_0x0556:
            goto L_0x056d
        L_0x0557:
            r21 = r0
            java.lang.String r0 = r3.firstName
            r2 = 56
            r4 = 66
            java.lang.String r0 = r0.replace(r2, r4)
            r3.firstName = r0
            java.lang.String r0 = r3.lastName
            java.lang.String r0 = r0.replace(r2, r4)
            r3.lastName = r0
        L_0x056d:
            java.lang.String r0 = r3.lastName
            java.lang.String r0 = capitalize(r0)
            r3.lastName = r0
            java.lang.String r0 = r3.firstName
            java.lang.String r0 = capitalize(r0)
            r3.firstName = r0
            java.lang.String r0 = r3.middleName
            java.lang.String r0 = capitalize(r0)
            r3.middleName = r0
            goto L_0x089f
        L_0x0587:
            r40 = r0
            r41 = r2
            r36 = r4
            goto L_0x089f
        L_0x058f:
            r40 = r0
            r41 = r2
            r36 = r4
            r2 = 73
            if (r10 == r2) goto L_0x05a4
            r2 = 65
            if (r10 == r2) goto L_0x05a4
            r2 = 67
            if (r10 != r2) goto L_0x05a2
            goto L_0x05a4
        L_0x05a2:
            r0 = 0
            return r0
        L_0x05a4:
            r2 = 2
            r3.type = r2
            int r4 = r1.length
            r5 = 3
            if (r4 != r5) goto L_0x06b7
            r4 = 0
            r5 = r1[r4]
            int r5 = r5.length()
            r9 = 30
            if (r5 != r9) goto L_0x06b7
            r5 = r1[r2]
            int r5 = r5.length()
            if (r5 != r9) goto L_0x06b7
            r5 = r1[r4]
            r9 = 5
            java.lang.String r5 = r5.substring(r2, r9)
            r3.issuingCountry = r5
            r2 = r1[r4]
            r5 = 14
            java.lang.String r2 = r2.substring(r9, r5)
            r9 = 32
            r11 = 60
            java.lang.String r2 = r2.replace(r11, r9)
            r9 = 79
            r11 = 48
            java.lang.String r2 = r2.replace(r9, r11)
            java.lang.String r2 = r2.trim()
            int r9 = checksum(r2)
            r12 = r1[r4]
            char r12 = r12.charAt(r5)
            int r12 = r12 - r11
            if (r9 != r12) goto L_0x05f2
            r3.number = r2
        L_0x05f2:
            r0 = 1
            r12 = r1[r0]
            r5 = 6
            java.lang.String r12 = r12.substring(r4, r5)
            r4 = 79
            java.lang.String r12 = r12.replace(r4, r11)
            r4 = 49
            r11 = 73
            java.lang.String r12 = r12.replace(r11, r4)
            int r4 = checksum(r12)
            r11 = r1[r0]
            char r5 = r11.charAt(r5)
            int r5 = getNumber(r5)
            if (r4 != r5) goto L_0x061b
            parseBirthDate(r12, r3)
        L_0x061b:
            r5 = r1[r0]
            r11 = 7
            char r5 = r5.charAt(r11)
            int r5 = parseGender(r5)
            r3.gender = r5
            r5 = r1[r0]
            r11 = 8
            r0 = 14
            java.lang.String r5 = r5.substring(r11, r0)
            r0 = 48
            r11 = 79
            java.lang.String r5 = r5.replace(r11, r0)
            r0 = 49
            r11 = 73
            java.lang.String r0 = r5.replace(r11, r0)
            int r5 = checksum(r0)
            r16 = r2
            r11 = 1
            r2 = r1[r11]
            r11 = 14
            char r2 = r2.charAt(r11)
            int r2 = getNumber(r2)
            if (r5 == r2) goto L_0x0665
            r44 = r4
            r2 = 1
            r4 = r1[r2]
            char r4 = r4.charAt(r11)
            r11 = 60
            if (r4 != r11) goto L_0x066a
            goto L_0x0667
        L_0x0665:
            r44 = r4
        L_0x0667:
            parseExpiryDate(r0, r3)
        L_0x066a:
            r2 = 1
            r2 = r1[r2]
            r4 = 18
            r11 = 15
            java.lang.String r2 = r2.substring(r11, r4)
            r3.nationality = r2
            r2 = 2
            r4 = r1[r2]
            java.lang.String r11 = "<<"
            int r4 = r4.indexOf(r11)
            r11 = -1
            if (r4 == r11) goto L_0x06b1
            r11 = r1[r2]
            r2 = 0
            java.lang.String r2 = r11.substring(r2, r4)
            r17 = r0
            r0 = 60
            r11 = 32
            java.lang.String r2 = r2.replace(r0, r11)
            java.lang.String r2 = r2.trim()
            r3.lastName = r2
            r2 = 2
            r2 = r1[r2]
            r18 = r5
            int r5 = r4 + 2
            java.lang.String r2 = r2.substring(r5)
            java.lang.String r0 = r2.replace(r0, r11)
            java.lang.String r0 = r0.trim()
            r3.firstName = r0
            goto L_0x0872
        L_0x06b1:
            r17 = r0
            r18 = r5
            goto L_0x0872
        L_0x06b7:
            int r2 = r1.length
            r4 = 2
            if (r2 != r4) goto L_0x0872
            r2 = 0
            r5 = r1[r2]
            int r5 = r5.length()
            r9 = 36
            if (r5 != r9) goto L_0x0872
            r5 = r1[r2]
            r9 = 5
            java.lang.String r4 = r5.substring(r4, r9)
            r3.issuingCountry = r4
            java.lang.String r4 = r3.issuingCountry
            java.lang.String r5 = "FRA"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x0786
            r4 = 73
            if (r10 != r4) goto L_0x0786
            r4 = r1[r2]
            r0 = 1
            char r4 = r4.charAt(r0)
            r5 = 68
            if (r4 != r5) goto L_0x0786
            java.lang.String r4 = "FRA"
            r3.nationality = r4
            r4 = r1[r2]
            r2 = 30
            r5 = 5
            java.lang.String r2 = r4.substring(r5, r2)
            r4 = 32
            r5 = 60
            java.lang.String r2 = r2.replace(r5, r4)
            java.lang.String r2 = r2.trim()
            r3.lastName = r2
            r0 = 1
            r2 = r1[r0]
            r9 = 13
            r11 = 27
            java.lang.String r2 = r2.substring(r9, r11)
            java.lang.String r9 = "<<"
            java.lang.String r11 = ", "
            java.lang.String r2 = r2.replace(r9, r11)
            java.lang.String r2 = r2.replace(r5, r4)
            java.lang.String r2 = r2.trim()
            r3.firstName = r2
            r2 = r1[r0]
            r4 = 12
            r5 = 0
            java.lang.String r2 = r2.substring(r5, r4)
            r4 = 79
            r5 = 48
            java.lang.String r2 = r2.replace(r4, r5)
            int r4 = checksum(r2)
            r5 = r1[r0]
            r9 = 12
            char r5 = r5.charAt(r9)
            int r5 = getNumber(r5)
            if (r4 != r5) goto L_0x0745
            r3.number = r2
        L_0x0745:
            r4 = r1[r0]
            r5 = 33
            r9 = 27
            java.lang.String r4 = r4.substring(r9, r5)
            r5 = 79
            r9 = 48
            java.lang.String r4 = r4.replace(r5, r9)
            r5 = 49
            r9 = 73
            java.lang.String r4 = r4.replace(r9, r5)
            int r5 = checksum(r4)
            r9 = r1[r0]
            r11 = 33
            char r9 = r9.charAt(r11)
            int r9 = getNumber(r9)
            if (r5 != r9) goto L_0x0774
            parseBirthDate(r4, r3)
        L_0x0774:
            r5 = r1[r0]
            r9 = 34
            char r5 = r5.charAt(r9)
            int r5 = parseGender(r5)
            r3.gender = r5
            r3.doesNotExpire = r0
            goto L_0x0873
        L_0x0786:
            r2 = 0
            r4 = r1[r2]
            java.lang.String r5 = "<<"
            int r4 = r4.indexOf(r5)
            r5 = -1
            if (r4 == r5) goto L_0x07ba
            r5 = r1[r2]
            r9 = 5
            java.lang.String r5 = r5.substring(r9, r4)
            r9 = 32
            r11 = 60
            java.lang.String r5 = r5.replace(r11, r9)
            java.lang.String r5 = r5.trim()
            r3.lastName = r5
            r5 = r1[r2]
            int r2 = r4 + 2
            java.lang.String r2 = r5.substring(r2)
            java.lang.String r2 = r2.replace(r11, r9)
            java.lang.String r2 = r2.trim()
            r3.firstName = r2
            goto L_0x07be
        L_0x07ba:
            r9 = 32
            r11 = 60
        L_0x07be:
            r0 = 1
            r2 = r1[r0]
            r5 = 9
            r12 = 0
            java.lang.String r2 = r2.substring(r12, r5)
            java.lang.String r2 = r2.replace(r11, r9)
            r5 = 79
            r9 = 48
            java.lang.String r2 = r2.replace(r5, r9)
            java.lang.String r2 = r2.trim()
            int r5 = checksum(r2)
            r9 = r1[r0]
            r11 = 9
            char r9 = r9.charAt(r11)
            int r9 = getNumber(r9)
            if (r5 != r9) goto L_0x07ec
            r3.number = r2
        L_0x07ec:
            r9 = r1[r0]
            r11 = 13
            r12 = 10
            java.lang.String r9 = r9.substring(r12, r11)
            r3.nationality = r9
            r9 = r1[r0]
            r12 = 19
            java.lang.String r9 = r9.substring(r11, r12)
            r11 = 79
            r12 = 48
            java.lang.String r9 = r9.replace(r11, r12)
            r11 = 49
            r12 = 73
            java.lang.String r9 = r9.replace(r12, r11)
            int r11 = checksum(r9)
            r12 = r1[r0]
            r0 = 19
            char r0 = r12.charAt(r0)
            int r0 = getNumber(r0)
            if (r11 != r0) goto L_0x0825
            parseBirthDate(r9, r3)
        L_0x0825:
            r0 = 1
            r11 = r1[r0]
            r12 = 20
            char r11 = r11.charAt(r12)
            int r11 = parseGender(r11)
            r3.gender = r11
            r11 = r1[r0]
            r12 = 21
            r0 = 27
            java.lang.String r11 = r11.substring(r12, r0)
            r0 = 48
            r12 = 79
            java.lang.String r11 = r11.replace(r12, r0)
            r0 = 49
            r12 = 73
            java.lang.String r0 = r11.replace(r12, r0)
            int r11 = checksum(r0)
            r44 = r2
            r12 = 1
            r2 = r1[r12]
            r12 = 27
            char r2 = r2.charAt(r12)
            int r2 = getNumber(r2)
            if (r11 == r2) goto L_0x086e
            r2 = 1
            r2 = r1[r2]
            char r2 = r2.charAt(r12)
            r11 = 60
            if (r2 != r11) goto L_0x0873
        L_0x086e:
            parseExpiryDate(r0, r3)
            goto L_0x0873
        L_0x0872:
        L_0x0873:
            java.lang.String r0 = r3.firstName
            r2 = 79
            r4 = 48
            java.lang.String r0 = r0.replace(r4, r2)
            r5 = 56
            r9 = 66
            java.lang.String r0 = r0.replace(r5, r9)
            java.lang.String r0 = capitalize(r0)
            r3.firstName = r0
            java.lang.String r0 = r3.lastName
            java.lang.String r0 = r0.replace(r4, r2)
            r2 = 56
            r4 = 66
            java.lang.String r0 = r0.replace(r2, r4)
            java.lang.String r0 = capitalize(r0)
            r3.lastName = r0
        L_0x089f:
            java.lang.String r0 = r3.firstName
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x08b1
            java.lang.String r0 = r3.lastName
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x08b1
            r0 = 0
            return r0
        L_0x08b1:
            java.lang.String r0 = r3.issuingCountry
            java.lang.Object r0 = r6.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            r3.issuingCountry = r0
            java.lang.String r0 = r3.nationality
            java.lang.Object r0 = r6.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            r3.nationality = r0
            return r3
        L_0x08c6:
            r40 = r0
            r41 = r2
            r36 = r4
            r0 = 0
            return r0
        L_0x08ce:
            r28 = r0
            r29 = r1
            r41 = r2
            r0 = r3
            r36 = r4
            r32 = r5
            r33 = r6
        L_0x08db:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MrzRecognizer.recognizeMRZ(android.graphics.Bitmap):im.bclpbkiauv.messenger.MrzRecognizer$Result");
    }

    public static Result recognize(byte[] yuvData, int width, int height, int rotation) {
        int i = width;
        int i2 = height;
        int i3 = rotation;
        Bitmap bmp = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        setYuvBitmapPixels(bmp, yuvData);
        Matrix m = new Matrix();
        m.setRotate((float) i3);
        int minSize = Math.min(width, height);
        int dw = minSize;
        int dh = Math.round(((float) minSize) * 0.704f);
        boolean swap = i3 == 90 || i3 == 270;
        return recognize(Bitmap.createBitmap(bmp, swap ? (i / 2) - (dh / 2) : 0, swap ? 0 : (i2 / 2) - (dh / 2), swap ? dh : dw, swap ? dw : dh, m, false), false);
    }

    private static String capitalize(String s) {
        if (s == null) {
            return null;
        }
        char[] chars = s.toCharArray();
        boolean prevIsSpace = true;
        for (int i = 0; i < chars.length; i++) {
            if (prevIsSpace || !Character.isLetter(chars[i])) {
                prevIsSpace = chars[i] == ' ';
            } else {
                chars[i] = Character.toLowerCase(chars[i]);
            }
        }
        return new String(chars);
    }

    private static int checksum(String s) {
        int val = 0;
        char[] chars = s.toCharArray();
        int[] weights = {7, 3, 1};
        for (int i = 0; i < chars.length; i++) {
            int charVal = 0;
            if (chars[i] >= '0' && chars[i] <= '9') {
                charVal = chars[i] - '0';
            } else if (chars[i] >= 'A' && chars[i] <= 'Z') {
                charVal = (chars[i] - 'A') + 10;
            }
            val += weights[i % weights.length] * charVal;
        }
        return val % 10;
    }

    private static void parseBirthDate(String birthDate, Result result) {
        try {
            result.birthYear = Integer.parseInt(birthDate.substring(0, 2));
            result.birthYear = result.birthYear < (Calendar.getInstance().get(1) % 100) + -5 ? result.birthYear + 2000 : result.birthYear + LunarCalendar.MIN_YEAR;
            result.birthMonth = Integer.parseInt(birthDate.substring(2, 4));
            result.birthDay = Integer.parseInt(birthDate.substring(4));
        } catch (NumberFormatException e) {
        }
    }

    private static void parseExpiryDate(String expiryDate, Result result) {
        try {
            if ("<<<<<<".equals(expiryDate)) {
                result.doesNotExpire = true;
                return;
            }
            result.expiryYear = Integer.parseInt(expiryDate.substring(0, 2)) + 2000;
            result.expiryMonth = Integer.parseInt(expiryDate.substring(2, 4));
            result.expiryDay = Integer.parseInt(expiryDate.substring(4));
        } catch (NumberFormatException e) {
        }
    }

    private static int parseGender(char gender) {
        if (gender == 'F') {
            return 2;
        }
        if (gender != 'M') {
            return 0;
        }
        return 1;
    }

    private static String russianPassportTranslit(String s) {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int idx = "ABVGDE2JZIQKLMNOPRSTUFHC34WXY9678".indexOf(chars[i]);
            if (idx != -1) {
                chars[i] = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".charAt(idx);
            }
        }
        return new String(chars);
    }

    private static String cyrillicToLatin(String s) {
        String[] replacements = {ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, "B", ExifInterface.GPS_MEASUREMENT_INTERRUPTED, "G", "D", ExifInterface.LONGITUDE_EAST, ExifInterface.LONGITUDE_EAST, "ZH", "Z", "I", "I", "K", "L", "M", "N", "O", "P", "R", ExifInterface.LATITUDE_SOUTH, ExifInterface.GPS_DIRECTION_TRUE, "U", "F", "KH", "TS", "CH", "SH", "SHCH", "IE", "Y", "", ExifInterface.LONGITUDE_EAST, "IU", "IA"};
        String s2 = s;
        for (int i = 0; i < replacements.length; i++) {
            s2 = s2.replace("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".substring(i, i + 1), replacements[i]);
        }
        return s2;
    }

    private static int getNumber(char c) {
        if (c == 'O') {
            return 0;
        }
        if (c == 'I') {
            return 1;
        }
        if (c == 'B') {
            return 8;
        }
        return c - '0';
    }

    private static HashMap<String, String> getCountriesMap() {
        HashMap<String, String> countries = new HashMap<>();
        countries.put("AFG", "AF");
        countries.put("ALA", "AX");
        countries.put("ALB", "AL");
        countries.put("DZA", "DZ");
        countries.put("ASM", "AS");
        countries.put("AND", "AD");
        countries.put("AGO", "AO");
        countries.put("AIA", "AI");
        countries.put("ATA", "AQ");
        countries.put("ATG", "AG");
        countries.put("ARG", "AR");
        countries.put("ARM", "AM");
        countries.put("ABW", "AW");
        countries.put("AUS", "AU");
        countries.put("AUT", "AT");
        countries.put("AZE", "AZ");
        countries.put("BHS", "BS");
        countries.put("BHR", "BH");
        countries.put("BGD", "BD");
        countries.put("BRB", "BB");
        countries.put("BLR", "BY");
        countries.put("BEL", "BE");
        countries.put("BLZ", "BZ");
        countries.put("BEN", "BJ");
        countries.put("BMU", "BM");
        countries.put("BTN", "BT");
        countries.put("BOL", "BO");
        countries.put("BES", "BQ");
        countries.put("BIH", "BA");
        countries.put("BWA", "BW");
        countries.put("BVT", "BV");
        countries.put("BRA", "BR");
        countries.put("IOT", "IO");
        countries.put("BRN", "BN");
        countries.put("BGR", "BG");
        countries.put("BFA", "BF");
        countries.put("BDI", "BI");
        countries.put("CPV", "CV");
        countries.put("KHM", "KH");
        countries.put("CMR", "CM");
        countries.put("CAN", "CA");
        countries.put("CYM", "KY");
        countries.put("CAF", "CF");
        countries.put("TCD", "TD");
        countries.put("CHL", "CL");
        countries.put("CHN", "CN");
        countries.put("CXR", "CX");
        countries.put("CCK", "CC");
        countries.put("COL", "CO");
        countries.put("COM", "KM");
        countries.put("COG", "CG");
        countries.put("COD", "CD");
        countries.put("COK", "CK");
        countries.put("CRI", "CR");
        countries.put("CIV", "CI");
        countries.put("HRV", "HR");
        countries.put("CUB", "CU");
        countries.put("CUW", "CW");
        countries.put("CYP", "CY");
        countries.put("CZE", "CZ");
        countries.put("DNK", "DK");
        countries.put("DJI", "DJ");
        countries.put("DMA", "DM");
        countries.put("DOM", "DO");
        countries.put("ECU", "EC");
        countries.put("EGY", "EG");
        countries.put("SLV", "SV");
        countries.put("GNQ", "GQ");
        countries.put("ERI", "ER");
        countries.put("EST", "EE");
        countries.put("ETH", "ET");
        countries.put("FLK", "FK");
        countries.put("FRO", "FO");
        countries.put("FJI", "FJ");
        countries.put("FIN", "FI");
        countries.put("FRA", "FR");
        countries.put("GUF", "GF");
        countries.put("PYF", "PF");
        countries.put("ATF", "TF");
        countries.put("GAB", "GA");
        countries.put("GMB", "GM");
        countries.put("GEO", "GE");
        countries.put("D<<", "DE");
        countries.put("GHA", "GH");
        countries.put("GIB", "GI");
        countries.put("GRC", "GR");
        countries.put("GRL", "GL");
        countries.put("GRD", "GD");
        countries.put("GLP", "GP");
        countries.put("GUM", "GU");
        countries.put("GTM", "GT");
        countries.put("GGY", "GG");
        countries.put("GIN", "GN");
        countries.put("GNB", "GW");
        countries.put("GUY", "GY");
        countries.put("HTI", "HT");
        countries.put("HMD", "HM");
        countries.put("VAT", "VA");
        countries.put("HND", "HN");
        countries.put("HKG", "HK");
        countries.put("HUN", "HU");
        countries.put("ISL", "IS");
        countries.put("IND", "IN");
        countries.put("IDN", "ID");
        countries.put("IRN", "IR");
        countries.put("IRQ", "IQ");
        countries.put("IRL", "IE");
        countries.put("IMN", "IM");
        countries.put("ISR", "IL");
        countries.put("ITA", "IT");
        countries.put("JAM", "JM");
        countries.put("JPN", "JP");
        countries.put("JEY", "JE");
        countries.put("JOR", "JO");
        countries.put("KAZ", "KZ");
        countries.put("KEN", "KE");
        countries.put("KIR", "KI");
        countries.put("PRK", "KP");
        countries.put("KOR", "KR");
        countries.put("KWT", "KW");
        countries.put("KGZ", ExpandedProductParsedResult.KILOGRAM);
        countries.put("LAO", "LA");
        countries.put("LVA", "LV");
        countries.put("LBN", ExpandedProductParsedResult.POUND);
        countries.put("LSO", "LS");
        countries.put("LBR", "LR");
        countries.put("LBY", "LY");
        countries.put("LIE", "LI");
        countries.put("LTU", "LT");
        countries.put("LUX", "LU");
        countries.put("MAC", "MO");
        countries.put("MKD", "MK");
        countries.put("MDG", "MG");
        countries.put("MWI", "MW");
        countries.put("MYS", "MY");
        countries.put("MDV", "MV");
        countries.put("MLI", "ML");
        countries.put("MLT", "MT");
        countries.put("MHL", "MH");
        countries.put("MTQ", "MQ");
        countries.put("MRT", "MR");
        countries.put("MUS", "MU");
        countries.put("MYT", "YT");
        countries.put("MEX", "MX");
        countries.put("FSM", "FM");
        countries.put("MDA", "MD");
        countries.put("MCO", "MC");
        countries.put("MNG", "MN");
        countries.put("MNE", "ME");
        countries.put("MSR", "MS");
        countries.put("MAR", "MA");
        countries.put("MOZ", "MZ");
        countries.put("MMR", "MM");
        countries.put("NAM", "NA");
        countries.put("NRU", "NR");
        countries.put("NPL", "NP");
        countries.put("NLD", "NL");
        countries.put("NCL", "NC");
        countries.put("NZL", "NZ");
        countries.put("NIC", "NI");
        countries.put("NER", "NE");
        countries.put("NGA", "NG");
        countries.put("NIU", "NU");
        countries.put("NFK", "NF");
        countries.put("MNP", "MP");
        countries.put("NOR", "NO");
        countries.put("OMN", "OM");
        countries.put("PAK", "PK");
        countries.put("PLW", "PW");
        countries.put("PSE", "PS");
        countries.put("PAN", "PA");
        countries.put("PNG", "PG");
        countries.put("PRY", "PY");
        countries.put("PER", "PE");
        countries.put("PHL", "PH");
        countries.put("PCN", "PN");
        countries.put("POL", "PL");
        countries.put("PRT", "PT");
        countries.put("PRI", "PR");
        countries.put("QAT", "QA");
        countries.put("REU", "RE");
        countries.put("ROU", "RO");
        countries.put("RUS", "RU");
        countries.put("RWA", "RW");
        countries.put("BLM", "BL");
        countries.put("SHN", "SH");
        countries.put("KNA", "KN");
        countries.put("LCA", "LC");
        countries.put("MAF", "MF");
        countries.put("SPM", "PM");
        countries.put("VCT", "VC");
        countries.put("WSM", "WS");
        countries.put("SMR", "SM");
        countries.put("STP", "ST");
        countries.put("SAU", "SA");
        countries.put("SEN", "SN");
        countries.put("SRB", "RS");
        countries.put("SYC", "SC");
        countries.put("SLE", "SL");
        countries.put("SGP", "SG");
        countries.put("SXM", "SX");
        countries.put("SVK", "SK");
        countries.put("SVN", "SI");
        countries.put("SLB", "SB");
        countries.put("SOM", "SO");
        countries.put("ZAF", "ZA");
        countries.put("SGS", "GS");
        countries.put("SSD", "SS");
        countries.put("ESP", "ES");
        countries.put("LKA", "LK");
        countries.put("SDN", "SD");
        countries.put("SUR", "SR");
        countries.put("SJM", "SJ");
        countries.put("SWZ", "SZ");
        countries.put("SWE", "SE");
        countries.put("CHE", "CH");
        countries.put("SYR", "SY");
        countries.put("TWN", "TW");
        countries.put("TJK", "TJ");
        countries.put("TZA", "TZ");
        countries.put("THA", "TH");
        countries.put("TLS", "TL");
        countries.put("TGO", "TG");
        countries.put("TKL", "TK");
        countries.put("TON", "TO");
        countries.put("TTO", "TT");
        countries.put("TUN", "TN");
        countries.put("TUR", "TR");
        countries.put("TKM", "TM");
        countries.put("TCA", "TC");
        countries.put("TUV", "TV");
        countries.put("UGA", "UG");
        countries.put("UKR", "UA");
        countries.put("ARE", "AE");
        countries.put("GBR", "GB");
        countries.put("USA", "US");
        countries.put("UMI", "UM");
        countries.put("URY", "UY");
        countries.put("UZB", "UZ");
        countries.put("VUT", "VU");
        countries.put("VEN", "VE");
        countries.put("VNM", "VN");
        countries.put("VGB", "VG");
        countries.put("VIR", "VI");
        countries.put("WLF", "WF");
        countries.put("ESH", "EH");
        countries.put("YEM", "YE");
        countries.put("ZMB", "ZM");
        countries.put("ZWE", "ZW");
        return countries;
    }
}
