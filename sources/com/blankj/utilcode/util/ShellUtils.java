package com.blankj.utilcode.util;

import com.blankj.utilcode.util.Utils;
import java.util.List;

public final class ShellUtils {
    private static final String LINE_SEP = System.getProperty("line.separator");

    private ShellUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Utils.Task<CommandResult> execCmdAsync(String command, boolean isRooted, Utils.Callback<CommandResult> callback) {
        return execCmdAsync(new String[]{command}, isRooted, true, callback);
    }

    public static Utils.Task<CommandResult> execCmdAsync(List<String> commands, boolean isRooted, Utils.Callback<CommandResult> callback) {
        return execCmdAsync(commands == null ? null : (String[]) commands.toArray(new String[0]), isRooted, true, callback);
    }

    public static Utils.Task<CommandResult> execCmdAsync(String[] commands, boolean isRooted, Utils.Callback<CommandResult> callback) {
        return execCmdAsync(commands, isRooted, true, callback);
    }

    public static Utils.Task<CommandResult> execCmdAsync(String command, boolean isRooted, boolean isNeedResultMsg, Utils.Callback<CommandResult> callback) {
        return execCmdAsync(new String[]{command}, isRooted, isNeedResultMsg, callback);
    }

    public static Utils.Task<CommandResult> execCmdAsync(List<String> commands, boolean isRooted, boolean isNeedResultMsg, Utils.Callback<CommandResult> callback) {
        return execCmdAsync(commands == null ? null : (String[]) commands.toArray(new String[0]), isRooted, isNeedResultMsg, callback);
    }

    public static Utils.Task<CommandResult> execCmdAsync(final String[] commands, final boolean isRooted, final boolean isNeedResultMsg, Utils.Callback<CommandResult> callback) {
        if (callback != null) {
            return Utils.doAsync(new Utils.Task<CommandResult>(callback) {
                public CommandResult doInBackground() {
                    return ShellUtils.execCmd(commands, isRooted, isNeedResultMsg);
                }
            });
        }
        throw new NullPointerException("Argument 'callback' of type Utils.Callback<CommandResult> (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static CommandResult execCmd(String command, boolean isRooted) {
        return execCmd(new String[]{command}, isRooted, true);
    }

    public static CommandResult execCmd(List<String> commands, boolean isRooted) {
        return execCmd(commands == null ? null : (String[]) commands.toArray(new String[0]), isRooted, true);
    }

    public static CommandResult execCmd(String[] commands, boolean isRooted) {
        return execCmd(commands, isRooted, true);
    }

    public static CommandResult execCmd(String command, boolean isRooted, boolean isNeedResultMsg) {
        return execCmd(new String[]{command}, isRooted, isNeedResultMsg);
    }

    public static CommandResult execCmd(List<String> commands, boolean isRooted, boolean isNeedResultMsg) {
        return execCmd(commands == null ? null : (String[]) commands.toArray(new String[0]), isRooted, isNeedResultMsg);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00e6, code lost:
        if (r3 != null) goto L_0x00e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00e8, code lost:
        r3.destroy();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0116, code lost:
        if (r3 == null) goto L_0x0119;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x011b, code lost:
        if (r6 != null) goto L_0x011f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x011d, code lost:
        r9 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x011f, code lost:
        r9 = r6.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0123, code lost:
        if (r7 != null) goto L_0x0126;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0126, code lost:
        r2 = r7.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x012d, code lost:
        return new com.blankj.utilcode.util.ShellUtils.CommandResult(r1, r9, r2);
     */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00d0 A[SYNTHETIC, Splitter:B:36:0x00d0] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00dc A[SYNTHETIC, Splitter:B:41:0x00dc] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.blankj.utilcode.util.ShellUtils.CommandResult execCmd(java.lang.String[] r13, boolean r14, boolean r15) {
        /*
            java.lang.String r0 = "UTF-8"
            r1 = -1
            java.lang.String r2 = ""
            if (r13 == 0) goto L_0x0158
            int r3 = r13.length
            if (r3 != 0) goto L_0x000c
            goto L_0x0158
        L_0x000c:
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            java.lang.Runtime r9 = java.lang.Runtime.getRuntime()     // Catch:{ Exception -> 0x00ee }
            if (r14 == 0) goto L_0x001b
            java.lang.String r10 = "su"
            goto L_0x001d
        L_0x001b:
            java.lang.String r10 = "sh"
        L_0x001d:
            java.lang.Process r9 = r9.exec(r10)     // Catch:{ Exception -> 0x00ee }
            r3 = r9
            java.io.DataOutputStream r9 = new java.io.DataOutputStream     // Catch:{ Exception -> 0x00ee }
            java.io.OutputStream r10 = r3.getOutputStream()     // Catch:{ Exception -> 0x00ee }
            r9.<init>(r10)     // Catch:{ Exception -> 0x00ee }
            r8 = r9
            int r9 = r13.length     // Catch:{ Exception -> 0x00ee }
            r10 = 0
        L_0x002e:
            if (r10 >= r9) goto L_0x0047
            r11 = r13[r10]     // Catch:{ Exception -> 0x00ee }
            if (r11 != 0) goto L_0x0035
            goto L_0x0044
        L_0x0035:
            byte[] r12 = r11.getBytes()     // Catch:{ Exception -> 0x00ee }
            r8.write(r12)     // Catch:{ Exception -> 0x00ee }
            java.lang.String r12 = LINE_SEP     // Catch:{ Exception -> 0x00ee }
            r8.writeBytes(r12)     // Catch:{ Exception -> 0x00ee }
            r8.flush()     // Catch:{ Exception -> 0x00ee }
        L_0x0044:
            int r10 = r10 + 1
            goto L_0x002e
        L_0x0047:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ee }
            r9.<init>()     // Catch:{ Exception -> 0x00ee }
            java.lang.String r10 = "exit"
            r9.append(r10)     // Catch:{ Exception -> 0x00ee }
            java.lang.String r10 = LINE_SEP     // Catch:{ Exception -> 0x00ee }
            r9.append(r10)     // Catch:{ Exception -> 0x00ee }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x00ee }
            r8.writeBytes(r9)     // Catch:{ Exception -> 0x00ee }
            r8.flush()     // Catch:{ Exception -> 0x00ee }
            int r9 = r3.waitFor()     // Catch:{ Exception -> 0x00ee }
            r1 = r9
            if (r15 == 0) goto L_0x00c5
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ee }
            r9.<init>()     // Catch:{ Exception -> 0x00ee }
            r6 = r9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ee }
            r9.<init>()     // Catch:{ Exception -> 0x00ee }
            r7 = r9
            java.io.BufferedReader r9 = new java.io.BufferedReader     // Catch:{ Exception -> 0x00ee }
            java.io.InputStreamReader r10 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x00ee }
            java.io.InputStream r11 = r3.getInputStream()     // Catch:{ Exception -> 0x00ee }
            r10.<init>(r11, r0)     // Catch:{ Exception -> 0x00ee }
            r9.<init>(r10)     // Catch:{ Exception -> 0x00ee }
            r4 = r9
            java.io.BufferedReader r9 = new java.io.BufferedReader     // Catch:{ Exception -> 0x00ee }
            java.io.InputStreamReader r10 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x00ee }
            java.io.InputStream r11 = r3.getErrorStream()     // Catch:{ Exception -> 0x00ee }
            r10.<init>(r11, r0)     // Catch:{ Exception -> 0x00ee }
            r9.<init>(r10)     // Catch:{ Exception -> 0x00ee }
            r5 = r9
            java.lang.String r0 = r4.readLine()     // Catch:{ Exception -> 0x00ee }
            r9 = r0
            if (r0 == 0) goto L_0x00ab
            r6.append(r9)     // Catch:{ Exception -> 0x00ee }
        L_0x009b:
            java.lang.String r0 = r4.readLine()     // Catch:{ Exception -> 0x00ee }
            r9 = r0
            if (r0 == 0) goto L_0x00ab
            java.lang.String r0 = LINE_SEP     // Catch:{ Exception -> 0x00ee }
            r6.append(r0)     // Catch:{ Exception -> 0x00ee }
            r6.append(r9)     // Catch:{ Exception -> 0x00ee }
            goto L_0x009b
        L_0x00ab:
            java.lang.String r0 = r5.readLine()     // Catch:{ Exception -> 0x00ee }
            r9 = r0
            if (r0 == 0) goto L_0x00c5
            r7.append(r9)     // Catch:{ Exception -> 0x00ee }
        L_0x00b5:
            java.lang.String r0 = r5.readLine()     // Catch:{ Exception -> 0x00ee }
            r9 = r0
            if (r0 == 0) goto L_0x00c5
            java.lang.String r0 = LINE_SEP     // Catch:{ Exception -> 0x00ee }
            r7.append(r0)     // Catch:{ Exception -> 0x00ee }
            r7.append(r9)     // Catch:{ Exception -> 0x00ee }
            goto L_0x00b5
        L_0x00c5:
            r8.close()     // Catch:{ IOException -> 0x00ca }
            goto L_0x00ce
        L_0x00ca:
            r0 = move-exception
            r0.printStackTrace()
        L_0x00ce:
            if (r4 == 0) goto L_0x00d9
            r4.close()     // Catch:{ IOException -> 0x00d4 }
            goto L_0x00d9
        L_0x00d4:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x00da
        L_0x00d9:
        L_0x00da:
            if (r5 == 0) goto L_0x00e5
            r5.close()     // Catch:{ IOException -> 0x00e0 }
            goto L_0x00e5
        L_0x00e0:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x00e6
        L_0x00e5:
        L_0x00e6:
            if (r3 == 0) goto L_0x0119
        L_0x00e8:
            r3.destroy()
            goto L_0x0119
        L_0x00ec:
            r0 = move-exception
            goto L_0x012e
        L_0x00ee:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x00ec }
            if (r8 == 0) goto L_0x00fd
            r8.close()     // Catch:{ IOException -> 0x00f8 }
            goto L_0x00fd
        L_0x00f8:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x00fe
        L_0x00fd:
        L_0x00fe:
            if (r4 == 0) goto L_0x0109
            r4.close()     // Catch:{ IOException -> 0x0104 }
            goto L_0x0109
        L_0x0104:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x010a
        L_0x0109:
        L_0x010a:
            if (r5 == 0) goto L_0x0115
            r5.close()     // Catch:{ IOException -> 0x0110 }
            goto L_0x0115
        L_0x0110:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0116
        L_0x0115:
        L_0x0116:
            if (r3 == 0) goto L_0x0119
            goto L_0x00e8
        L_0x0119:
            com.blankj.utilcode.util.ShellUtils$CommandResult r0 = new com.blankj.utilcode.util.ShellUtils$CommandResult
            if (r6 != 0) goto L_0x011f
            r9 = r2
            goto L_0x0123
        L_0x011f:
            java.lang.String r9 = r6.toString()
        L_0x0123:
            if (r7 != 0) goto L_0x0126
            goto L_0x012a
        L_0x0126:
            java.lang.String r2 = r7.toString()
        L_0x012a:
            r0.<init>(r1, r9, r2)
            return r0
        L_0x012e:
            if (r8 == 0) goto L_0x0139
            r8.close()     // Catch:{ IOException -> 0x0134 }
            goto L_0x0139
        L_0x0134:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x013a
        L_0x0139:
        L_0x013a:
            if (r4 == 0) goto L_0x0145
            r4.close()     // Catch:{ IOException -> 0x0140 }
            goto L_0x0145
        L_0x0140:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x0146
        L_0x0145:
        L_0x0146:
            if (r5 == 0) goto L_0x0151
            r5.close()     // Catch:{ IOException -> 0x014c }
            goto L_0x0151
        L_0x014c:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x0152
        L_0x0151:
        L_0x0152:
            if (r3 == 0) goto L_0x0157
            r3.destroy()
        L_0x0157:
            throw r0
        L_0x0158:
            com.blankj.utilcode.util.ShellUtils$CommandResult r0 = new com.blankj.utilcode.util.ShellUtils$CommandResult
            r0.<init>(r1, r2, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.ShellUtils.execCmd(java.lang.String[], boolean, boolean):com.blankj.utilcode.util.ShellUtils$CommandResult");
    }

    public static class CommandResult {
        public String errorMsg;
        public int result;
        public String successMsg;

        public CommandResult(int result2, String successMsg2, String errorMsg2) {
            this.result = result2;
            this.successMsg = successMsg2;
            this.errorMsg = errorMsg2;
        }

        public String toString() {
            return "result: " + this.result + "\nsuccessMsg: " + this.successMsg + "\nerrorMsg: " + this.errorMsg;
        }
    }
}
