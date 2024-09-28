package com.serenegiant.usb;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class DeviceFilter {
    private static final String TAG = "DeviceFilter";
    public final boolean isExclude;
    public final int mClass;
    public final String mManufacturerName;
    public final int mProductId;
    public final String mProductName;
    public final int mProtocol;
    public final String mSerialNumber;
    public final int mSubclass;
    public final int mVendorId;

    public DeviceFilter(int vid, int pid, int clasz, int subclass, int protocol, String manufacturer, String product, String serialNum) {
        this(vid, pid, clasz, subclass, protocol, manufacturer, product, serialNum, false);
    }

    public DeviceFilter(int vid, int pid, int clasz, int subclass, int protocol, String manufacturer, String product, String serialNum, boolean isExclude2) {
        this.mVendorId = vid;
        this.mProductId = pid;
        this.mClass = clasz;
        this.mSubclass = subclass;
        this.mProtocol = protocol;
        this.mManufacturerName = manufacturer;
        this.mProductName = product;
        this.mSerialNumber = serialNum;
        this.isExclude = isExclude2;
    }

    public DeviceFilter(UsbDevice device) {
        this(device, false);
    }

    public DeviceFilter(UsbDevice device, boolean isExclude2) {
        this.mVendorId = device.getVendorId();
        this.mProductId = device.getProductId();
        this.mClass = device.getDeviceClass();
        this.mSubclass = device.getDeviceSubclass();
        this.mProtocol = device.getDeviceProtocol();
        this.mManufacturerName = null;
        this.mProductName = null;
        this.mSerialNumber = null;
        this.isExclude = isExclude2;
    }

    public static List<DeviceFilter> getDeviceFilters(Context context, int deviceFilterXmlId) {
        DeviceFilter deviceFilter;
        XmlPullParser parser = context.getResources().getXml(deviceFilterXmlId);
        List<DeviceFilter> deviceFilters = new ArrayList<>();
        try {
            for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
                if (eventType == 2 && (deviceFilter = readEntryOne(context, parser)) != null) {
                    deviceFilters.add(deviceFilter);
                }
            }
        } catch (XmlPullParserException e) {
            Log.d(TAG, "XmlPullParserException", e);
        } catch (IOException e2) {
            Log.d(TAG, "IOException", e2);
        }
        return Collections.unmodifiableList(deviceFilters);
    }

    private static final int getAttributeInteger(Context context, XmlPullParser parser, String namespace, String name, int defaultValue) {
        int result = defaultValue;
        try {
            String v = parser.getAttributeValue(namespace, name);
            if (TextUtils.isEmpty(v) || !v.startsWith("@")) {
                int radix = 10;
                if (v != null && v.length() > 2 && v.charAt(0) == '0' && (v.charAt(1) == 'x' || v.charAt(1) == 'X')) {
                    radix = 16;
                    v = v.substring(2);
                }
                return Integer.parseInt(v, radix);
            }
            int resId = context.getResources().getIdentifier(v.substring(1), (String) null, context.getPackageName());
            if (resId > 0) {
                result = context.getResources().getInteger(resId);
            }
            return result;
        } catch (Resources.NotFoundException e) {
            return defaultValue;
        } catch (NumberFormatException e2) {
            return defaultValue;
        } catch (NullPointerException e3) {
            return defaultValue;
        }
    }

    private static final boolean getAttributeBoolean(Context context, XmlPullParser parser, String namespace, String name, boolean defaultValue) {
        boolean result = defaultValue;
        try {
            String v = parser.getAttributeValue(namespace, name);
            if ("TRUE".equalsIgnoreCase(v)) {
                return true;
            }
            if ("FALSE".equalsIgnoreCase(v)) {
                return false;
            }
            boolean result2 = true;
            if (TextUtils.isEmpty(v) || !v.startsWith("@")) {
                int radix = 10;
                if (v != null && v.length() > 2 && v.charAt(0) == '0' && (v.charAt(1) == 'x' || v.charAt(1) == 'X')) {
                    radix = 16;
                    v = v.substring(2);
                }
                if (Integer.parseInt(v, radix) == 0) {
                    result2 = false;
                }
                return result2;
            }
            int resId = context.getResources().getIdentifier(v.substring(1), (String) null, context.getPackageName());
            if (resId > 0) {
                result = context.getResources().getBoolean(resId);
            }
            return result;
        } catch (Resources.NotFoundException e) {
            return defaultValue;
        } catch (NumberFormatException e2) {
            return defaultValue;
        } catch (NullPointerException e3) {
            return defaultValue;
        }
    }

    private static final String getAttributeString(Context context, XmlPullParser parser, String namespace, String name, String defaultValue) {
        int resId;
        String str = defaultValue;
        try {
            String result = parser.getAttributeValue(namespace, name);
            if (result == null) {
                result = defaultValue;
            }
            if (TextUtils.isEmpty(result) || !result.startsWith("@") || (resId = context.getResources().getIdentifier(result.substring(1), (String) null, context.getPackageName())) <= 0) {
                return result;
            }
            return context.getResources().getString(resId);
        } catch (Resources.NotFoundException e) {
            return defaultValue;
        } catch (NumberFormatException e2) {
            return defaultValue;
        } catch (NullPointerException e3) {
            return defaultValue;
        }
    }

    public static DeviceFilter readEntryOne(Context context, XmlPullParser parser) throws XmlPullParserException, IOException {
        Context context2 = context;
        XmlPullParser xmlPullParser = parser;
        int vendorId = -1;
        int productId = -1;
        int deviceClass = -1;
        int deviceSubclass = -1;
        int deviceProtocol = -1;
        boolean exclude = false;
        String manufacturerName = null;
        String productName = null;
        String serialNumber = null;
        boolean hasValue = false;
        int eventType = parser.getEventType();
        while (eventType != 1) {
            String tag = parser.getName();
            if (TextUtils.isEmpty(tag) || !tag.equalsIgnoreCase("usb-device")) {
            } else if (eventType == 2) {
                vendorId = getAttributeInteger(context2, xmlPullParser, (String) null, "vendor-id", -1);
                if (vendorId == -1 && (vendorId = getAttributeInteger(context2, xmlPullParser, (String) null, "vendorId", -1)) == -1) {
                    vendorId = getAttributeInteger(context2, xmlPullParser, (String) null, "venderId", -1);
                }
                productId = getAttributeInteger(context2, xmlPullParser, (String) null, "product-id", -1);
                if (productId == -1) {
                    productId = getAttributeInteger(context2, xmlPullParser, (String) null, "productId", -1);
                }
                deviceClass = getAttributeInteger(context2, xmlPullParser, (String) null, "class", -1);
                deviceSubclass = getAttributeInteger(context2, xmlPullParser, (String) null, "subclass", -1);
                deviceProtocol = getAttributeInteger(context2, xmlPullParser, (String) null, "protocol", -1);
                manufacturerName = getAttributeString(context2, xmlPullParser, (String) null, "manufacturer-name", (String) null);
                if (TextUtils.isEmpty(manufacturerName)) {
                    manufacturerName = getAttributeString(context2, xmlPullParser, (String) null, "manufacture", (String) null);
                }
                productName = getAttributeString(context2, xmlPullParser, (String) null, "product-name", (String) null);
                if (TextUtils.isEmpty(productName)) {
                    productName = getAttributeString(context2, xmlPullParser, (String) null, "product", (String) null);
                }
                serialNumber = getAttributeString(context2, xmlPullParser, (String) null, "serial-number", (String) null);
                if (TextUtils.isEmpty(serialNumber)) {
                    serialNumber = getAttributeString(context2, xmlPullParser, (String) null, "serial", (String) null);
                }
                exclude = getAttributeBoolean(context2, xmlPullParser, (String) null, "exclude", false);
                String str = tag;
                hasValue = true;
            } else if (eventType != 3) {
            } else if (hasValue) {
                String str2 = tag;
                return new DeviceFilter(vendorId, productId, deviceClass, deviceSubclass, deviceProtocol, manufacturerName, productName, serialNumber, exclude);
            }
            eventType = parser.next();
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0007, code lost:
        r0 = r2.mSubclass;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000d, code lost:
        r0 = r2.mProtocol;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean matches(int r3, int r4, int r5) {
        /*
            r2 = this;
            int r0 = r2.mClass
            r1 = -1
            if (r0 == r1) goto L_0x0007
            if (r3 != r0) goto L_0x0014
        L_0x0007:
            int r0 = r2.mSubclass
            if (r0 == r1) goto L_0x000d
            if (r4 != r0) goto L_0x0014
        L_0x000d:
            int r0 = r2.mProtocol
            if (r0 == r1) goto L_0x0016
            if (r5 != r0) goto L_0x0014
            goto L_0x0016
        L_0x0014:
            r0 = 0
            goto L_0x0017
        L_0x0016:
            r0 = 1
        L_0x0017:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.usb.DeviceFilter.matches(int, int, int):boolean");
    }

    public boolean matches(UsbDevice device) {
        if (this.mVendorId != -1 && device.getVendorId() != this.mVendorId) {
            return false;
        }
        if (this.mProductId != -1 && device.getProductId() != this.mProductId) {
            return false;
        }
        if (matches(device.getDeviceClass(), device.getDeviceSubclass(), device.getDeviceProtocol())) {
            return true;
        }
        int count = device.getInterfaceCount();
        for (int i = 0; i < count; i++) {
            UsbInterface intf = device.getInterface(i);
            if (matches(intf.getInterfaceClass(), intf.getInterfaceSubclass(), intf.getInterfaceProtocol())) {
                return true;
            }
        }
        return false;
    }

    public boolean isExclude(UsbDevice device) {
        return this.isExclude && matches(device);
    }

    public boolean matches(DeviceFilter f) {
        String str;
        String str2;
        String str3;
        if (this.isExclude != f.isExclude) {
            return false;
        }
        int i = this.mVendorId;
        if (i != -1 && f.mVendorId != i) {
            return false;
        }
        int i2 = this.mProductId;
        if (i2 != -1 && f.mProductId != i2) {
            return false;
        }
        if (f.mManufacturerName != null && this.mManufacturerName == null) {
            return false;
        }
        if (f.mProductName != null && this.mProductName == null) {
            return false;
        }
        if (f.mSerialNumber != null && this.mSerialNumber == null) {
            return false;
        }
        String str4 = this.mManufacturerName;
        if (str4 != null && (str3 = f.mManufacturerName) != null && !str4.equals(str3)) {
            return false;
        }
        String str5 = this.mProductName;
        if (str5 != null && (str2 = f.mProductName) != null && !str5.equals(str2)) {
            return false;
        }
        String str6 = this.mSerialNumber;
        if (str6 == null || (str = f.mSerialNumber) == null || str6.equals(str)) {
            return matches(f.mClass, f.mSubclass, f.mProtocol);
        }
        return false;
    }

    public boolean equals(Object obj) {
        int i;
        int i2;
        int i3;
        int i4;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        int i5 = this.mVendorId;
        if (i5 == -1 || (i = this.mProductId) == -1 || (i2 = this.mClass) == -1 || (i3 = this.mSubclass) == -1 || (i4 = this.mProtocol) == -1) {
            return false;
        }
        if (obj instanceof DeviceFilter) {
            DeviceFilter filter = (DeviceFilter) obj;
            if (filter.mVendorId != i5 || filter.mProductId != i || filter.mClass != i2 || filter.mSubclass != i3 || filter.mProtocol != i4) {
                return false;
            }
            if ((filter.mManufacturerName != null && this.mManufacturerName == null) || ((filter.mManufacturerName == null && this.mManufacturerName != null) || ((filter.mProductName != null && this.mProductName == null) || ((filter.mProductName == null && this.mProductName != null) || ((filter.mSerialNumber != null && this.mSerialNumber == null) || (filter.mSerialNumber == null && this.mSerialNumber != null)))))) {
                return false;
            }
            String str6 = filter.mManufacturerName;
            if ((str6 == null || (str5 = this.mManufacturerName) == null || str5.equals(str6)) && (((str = filter.mProductName) == null || (str4 = this.mProductName) == null || str4.equals(str)) && (((str2 = filter.mSerialNumber) == null || (str3 = this.mSerialNumber) == null || str3.equals(str2)) && filter.isExclude != this.isExclude))) {
                return true;
            }
            return false;
        } else if (!(obj instanceof UsbDevice)) {
            return false;
        } else {
            UsbDevice device = (UsbDevice) obj;
            if (!this.isExclude && device.getVendorId() == this.mVendorId && device.getProductId() == this.mProductId && device.getDeviceClass() == this.mClass && device.getDeviceSubclass() == this.mSubclass && device.getDeviceProtocol() == this.mProtocol) {
                return true;
            }
            return false;
        }
    }

    public int hashCode() {
        return ((this.mVendorId << 16) | this.mProductId) ^ (((this.mClass << 16) | (this.mSubclass << 8)) | this.mProtocol);
    }

    public String toString() {
        return "DeviceFilter[mVendorId=" + this.mVendorId + ",mProductId=" + this.mProductId + ",mClass=" + this.mClass + ",mSubclass=" + this.mSubclass + ",mProtocol=" + this.mProtocol + ",mManufacturerName=" + this.mManufacturerName + ",mProductName=" + this.mProductName + ",mSerialNumber=" + this.mSerialNumber + ",isExclude=" + this.isExclude + "]";
    }
}
