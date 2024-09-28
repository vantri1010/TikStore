package im.bclpbkiauv.ui.hui.visualcall;

public class ParserJsonUtils {
    /* JADX WARNING: Removed duplicated region for block: B:92:0x017d A[SYNTHETIC, Splitter:B:92:0x017d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static im.bclpbkiauv.ui.hui.visualcall.RTCAuthInfo parserLoginJson(java.lang.String r28) {
        /*
            java.lang.String r0 = "password"
            java.lang.String r1 = "username"
            java.lang.String r2 = "gslb"
            java.lang.String r3 = "turn"
            java.lang.String r4 = "key"
            java.lang.String r5 = "token"
            java.lang.String r6 = "timestamp"
            java.lang.String r7 = "nonce"
            java.lang.String r8 = "userid"
            java.lang.String r9 = "appid"
            java.lang.String r10 = "server"
            im.bclpbkiauv.ui.hui.visualcall.RTCAuthInfo r11 = new im.bclpbkiauv.ui.hui.visualcall.RTCAuthInfo
            r11.<init>()
            im.bclpbkiauv.ui.hui.visualcall.RTCAuthInfo$RTCAuthInfo_Data r12 = new im.bclpbkiauv.ui.hui.visualcall.RTCAuthInfo$RTCAuthInfo_Data
            r12.<init>()
            im.bclpbkiauv.ui.hui.visualcall.RTCAuthInfo$RTCAuthInfo_Data$RTCAuthInfo_Data_Turn r13 = new im.bclpbkiauv.ui.hui.visualcall.RTCAuthInfo$RTCAuthInfo_Data$RTCAuthInfo_Data_Turn
            r13.<init>()
            r14 = 0
            java.lang.String r15 = ""
            java.lang.String r16 = ""
            java.lang.String r17 = ""
            r18 = 0
            java.lang.String r20 = ""
            java.lang.String r21 = ""
            java.lang.String r22 = ""
            java.lang.String r23 = ""
            r24 = 0
            r25 = r14
            org.json.JSONObject r14 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0221 }
            r26 = r15
            r15 = r28
            r14.<init>(r15)     // Catch:{ JSONException -> 0x021a }
            boolean r27 = r14.has(r10)     // Catch:{ JSONException -> 0x021a }
            if (r27 == 0) goto L_0x0056
            int r10 = r14.getInt(r10)     // Catch:{ JSONException -> 0x004e }
            goto L_0x0058
        L_0x004e:
            r0 = move-exception
            r6 = r13
            r14 = r25
            r15 = r26
            goto L_0x0227
        L_0x0056:
            r10 = r25
        L_0x0058:
            java.lang.String r15 = "data"
            org.json.JSONObject r15 = r14.getJSONObject(r15)     // Catch:{ JSONException -> 0x0214 }
            boolean r25 = r15.has(r9)     // Catch:{ JSONException -> 0x0214 }
            if (r25 == 0) goto L_0x0070
            java.lang.String r9 = r15.getString(r9)     // Catch:{ JSONException -> 0x0069 }
            goto L_0x0072
        L_0x0069:
            r0 = move-exception
            r14 = r10
            r6 = r13
            r15 = r26
            goto L_0x0227
        L_0x0070:
            r9 = r26
        L_0x0072:
            boolean r25 = r15.has(r8)     // Catch:{ JSONException -> 0x020f }
            if (r25 == 0) goto L_0x0085
            java.lang.String r8 = r15.getString(r8)     // Catch:{ JSONException -> 0x007f }
            r16 = r8
            goto L_0x0087
        L_0x007f:
            r0 = move-exception
            r15 = r9
            r14 = r10
            r6 = r13
            goto L_0x0227
        L_0x0085:
            r8 = r16
        L_0x0087:
            boolean r16 = r15.has(r7)     // Catch:{ JSONException -> 0x0208 }
            if (r16 == 0) goto L_0x009c
            java.lang.String r7 = r15.getString(r7)     // Catch:{ JSONException -> 0x0094 }
            r17 = r7
            goto L_0x009e
        L_0x0094:
            r0 = move-exception
            r16 = r8
            r15 = r9
            r14 = r10
            r6 = r13
            goto L_0x0227
        L_0x009c:
            r7 = r17
        L_0x009e:
            boolean r16 = r15.has(r6)     // Catch:{ JSONException -> 0x01ff }
            if (r16 == 0) goto L_0x00ba
            long r16 = r15.getLong(r6)     // Catch:{ JSONException -> 0x00b0 }
            r18 = r16
            r6 = r13
            r27 = r14
            r13 = r18
            goto L_0x00bf
        L_0x00b0:
            r0 = move-exception
            r17 = r7
            r16 = r8
            r15 = r9
            r14 = r10
            r6 = r13
            goto L_0x0227
        L_0x00ba:
            r6 = r13
            r27 = r14
            r13 = r18
        L_0x00bf:
            boolean r16 = r15.has(r5)     // Catch:{ JSONException -> 0x01f5 }
            if (r16 == 0) goto L_0x00cc
            java.lang.String r5 = r15.getString(r5)     // Catch:{ JSONException -> 0x01f5 }
            r20 = r5
            goto L_0x00ce
        L_0x00cc:
            r5 = r20
        L_0x00ce:
            boolean r16 = r15.has(r4)     // Catch:{ JSONException -> 0x01e9 }
            if (r16 == 0) goto L_0x00db
            java.lang.String r4 = r15.getString(r4)     // Catch:{ JSONException -> 0x01e9 }
            r21 = r4
            goto L_0x00dd
        L_0x00db:
            r4 = r21
        L_0x00dd:
            boolean r16 = r15.has(r3)     // Catch:{ JSONException -> 0x01da }
            if (r16 == 0) goto L_0x0109
            org.json.JSONObject r3 = r15.getJSONObject(r3)     // Catch:{ JSONException -> 0x01da }
            boolean r16 = r3.has(r1)     // Catch:{ JSONException -> 0x01da }
            if (r16 == 0) goto L_0x00f3
            java.lang.String r1 = r3.getString(r1)     // Catch:{ JSONException -> 0x01da }
            r22 = r1
        L_0x00f3:
            boolean r1 = r3.has(r0)     // Catch:{ JSONException -> 0x01da }
            if (r1 == 0) goto L_0x0104
            java.lang.String r0 = r3.getString(r0)     // Catch:{ JSONException -> 0x01da }
            r23 = r0
            r1 = r22
            r3 = r23
            goto L_0x010d
        L_0x0104:
            r1 = r22
            r3 = r23
            goto L_0x010d
        L_0x0109:
            r1 = r22
            r3 = r23
        L_0x010d:
            boolean r0 = r15.has(r2)     // Catch:{ JSONException -> 0x01c7 }
            if (r0 == 0) goto L_0x0160
            org.json.JSONArray r0 = r15.getJSONArray(r2)     // Catch:{ JSONException -> 0x014b }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ JSONException -> 0x014b }
            r2.<init>()     // Catch:{ JSONException -> 0x014b }
            r16 = 0
            r25 = r15
            r15 = r16
        L_0x0122:
            r16 = r3
            int r3 = r0.length()     // Catch:{ JSONException -> 0x0136 }
            if (r15 >= r3) goto L_0x0166
            java.lang.String r3 = r0.getString(r15)     // Catch:{ JSONException -> 0x0136 }
            r2.add(r3)     // Catch:{ JSONException -> 0x0136 }
            int r15 = r15 + 1
            r3 = r16
            goto L_0x0122
        L_0x0136:
            r0 = move-exception
            r22 = r1
            r24 = r2
            r21 = r4
            r20 = r5
            r17 = r7
            r15 = r9
            r18 = r13
            r23 = r16
            r16 = r8
            r14 = r10
            goto L_0x0227
        L_0x014b:
            r0 = move-exception
            r16 = r3
            r22 = r1
            r21 = r4
            r20 = r5
            r17 = r7
            r15 = r9
            r18 = r13
            r23 = r16
            r16 = r8
            r14 = r10
            goto L_0x0227
        L_0x0160:
            r16 = r3
            r25 = r15
            r2 = r24
        L_0x0166:
            r11.setServer(r10)     // Catch:{ JSONException -> 0x01b0 }
            r12.setAppid(r9)     // Catch:{ JSONException -> 0x01b0 }
            r12.setUserid(r8)     // Catch:{ JSONException -> 0x01b0 }
            r12.setNonce(r7)     // Catch:{ JSONException -> 0x01b0 }
            r12.setTimestamp(r13)     // Catch:{ JSONException -> 0x01b0 }
            r12.setToken(r5)     // Catch:{ JSONException -> 0x01b0 }
            r12.setKey(r4)     // Catch:{ JSONException -> 0x01b0 }
            if (r2 == 0) goto L_0x018c
            int r0 = r2.size()     // Catch:{ JSONException -> 0x0136 }
            java.lang.String[] r0 = new java.lang.String[r0]     // Catch:{ JSONException -> 0x0136 }
            java.lang.Object[] r0 = r2.toArray(r0)     // Catch:{ JSONException -> 0x0136 }
            java.lang.String[] r0 = (java.lang.String[]) r0     // Catch:{ JSONException -> 0x0136 }
            r12.setGslb(r0)     // Catch:{ JSONException -> 0x0136 }
        L_0x018c:
            r6.setUsername(r1)     // Catch:{ JSONException -> 0x01b0 }
            r3 = r16
            r6.setPassword(r3)     // Catch:{ JSONException -> 0x019b }
            r12.setTurn(r6)     // Catch:{ JSONException -> 0x019b }
            r11.setData(r12)     // Catch:{ JSONException -> 0x019b }
            return r11
        L_0x019b:
            r0 = move-exception
            r22 = r1
            r24 = r2
            r23 = r3
            r21 = r4
            r20 = r5
            r17 = r7
            r16 = r8
            r15 = r9
            r18 = r13
            r14 = r10
            goto L_0x0227
        L_0x01b0:
            r0 = move-exception
            r3 = r16
            r22 = r1
            r24 = r2
            r23 = r3
            r21 = r4
            r20 = r5
            r17 = r7
            r16 = r8
            r15 = r9
            r18 = r13
            r14 = r10
            goto L_0x0227
        L_0x01c7:
            r0 = move-exception
            r22 = r1
            r23 = r3
            r21 = r4
            r20 = r5
            r17 = r7
            r16 = r8
            r15 = r9
            r18 = r13
            r14 = r10
            goto L_0x0227
        L_0x01da:
            r0 = move-exception
            r21 = r4
            r20 = r5
            r17 = r7
            r16 = r8
            r15 = r9
            r18 = r13
            r14 = r10
            goto L_0x0227
        L_0x01e9:
            r0 = move-exception
            r20 = r5
            r17 = r7
            r16 = r8
            r15 = r9
            r18 = r13
            r14 = r10
            goto L_0x0227
        L_0x01f5:
            r0 = move-exception
            r17 = r7
            r16 = r8
            r15 = r9
            r18 = r13
            r14 = r10
            goto L_0x0227
        L_0x01ff:
            r0 = move-exception
            r6 = r13
            r17 = r7
            r16 = r8
            r15 = r9
            r14 = r10
            goto L_0x0227
        L_0x0208:
            r0 = move-exception
            r6 = r13
            r16 = r8
            r15 = r9
            r14 = r10
            goto L_0x0227
        L_0x020f:
            r0 = move-exception
            r6 = r13
            r15 = r9
            r14 = r10
            goto L_0x0227
        L_0x0214:
            r0 = move-exception
            r6 = r13
            r14 = r10
            r15 = r26
            goto L_0x0227
        L_0x021a:
            r0 = move-exception
            r6 = r13
            r14 = r25
            r15 = r26
            goto L_0x0227
        L_0x0221:
            r0 = move-exception
            r6 = r13
            r26 = r15
            r14 = r25
        L_0x0227:
            r0.printStackTrace()
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.visualcall.ParserJsonUtils.parserLoginJson(java.lang.String):im.bclpbkiauv.ui.hui.visualcall.RTCAuthInfo");
    }
}
