package org.libsdl.app;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build.VERSION;
import android.util.Log;

public class SDLAudioManager {
    protected static final String TAG = "SDLAudio";
    protected static AudioRecord mAudioRecord;
    protected static AudioTrack mAudioTrack;

    public static native int nativeSetupJNI();

    public static void initialize() {
        mAudioTrack = null;
        mAudioRecord = null;
    }

    protected static String getAudioFormatString(int audioFormat) {
        switch (audioFormat) {
            case 2:
                return "16-bit";
            case 3:
                return "8-bit";
            case 4:
                return "float";
            default:
                return Integer.toString(audioFormat);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x009c  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00cb  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0131  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0149  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01fc  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01ff  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static int[] open(boolean r23, int r24, int r25, int r26, int r27) {
        /*
            r0 = r24
            r1 = r26
            r2 = r27
            java.lang.String r3 = "SDLAudio"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Opening "
            r4.append(r5)
            if (r23 == 0) goto L_0x0017
            java.lang.String r5 = "capture"
            goto L_0x0019
        L_0x0017:
            java.lang.String r5 = "playback"
        L_0x0019:
            r4.append(r5)
            java.lang.String r5 = ", requested "
            r4.append(r5)
            r4.append(r2)
            java.lang.String r5 = " frames of "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r5 = " channel "
            r4.append(r5)
            java.lang.String r5 = getAudioFormatString(r25)
            r4.append(r5)
            java.lang.String r5 = " audio at "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r5 = " Hz"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.util.Log.v(r3, r4)
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            r5 = 2
            if (r3 >= r4) goto L_0x0066
            if (r1 <= r5) goto L_0x0056
            r1 = 2
        L_0x0056:
            r3 = 8000(0x1f40, float:1.121E-41)
            if (r0 >= r3) goto L_0x005d
            r0 = 8000(0x1f40, float:1.121E-41)
        L_0x005c:
            goto L_0x0066
        L_0x005d:
            r3 = 48000(0xbb80, float:6.7262E-41)
            if (r0 <= r3) goto L_0x0066
            r0 = 48000(0xbb80, float:6.7262E-41)
            goto L_0x005c
        L_0x0066:
            r3 = 23
            r6 = 4
            r7 = r25
            if (r7 != r6) goto L_0x0078
            if (r23 == 0) goto L_0x0072
            r4 = 23
        L_0x0072:
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 >= r4) goto L_0x0078
            r4 = 2
            goto L_0x0079
        L_0x0078:
            r4 = r7
        L_0x0079:
            switch(r4) {
                case 2: goto L_0x009e;
                case 3: goto L_0x009c;
                case 4: goto L_0x009a;
                default: goto L_0x007c;
            }
        L_0x007c:
            java.lang.String r7 = "SDLAudio"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Requested format "
            r8.append(r9)
            r8.append(r4)
            java.lang.String r9 = ", getting ENCODING_PCM_16BIT"
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            android.util.Log.v(r7, r8)
            r4 = 2
            r7 = 2
            goto L_0x00a0
        L_0x009a:
            r7 = 4
            goto L_0x00a0
        L_0x009c:
            r7 = 1
            goto L_0x00a0
        L_0x009e:
            r7 = 2
        L_0x00a0:
            r13 = r7
            if (r23 == 0) goto L_0x00cb
            switch(r1) {
                case 1: goto L_0x00c8;
                case 2: goto L_0x00c5;
                default: goto L_0x00a6;
            }
        L_0x00a6:
            java.lang.String r3 = "SDLAudio"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Requested "
            r7.append(r8)
            r7.append(r1)
            java.lang.String r8 = " channels, getting stereo"
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            android.util.Log.v(r3, r7)
            r1 = 2
            r3 = 12
            goto L_0x0127
        L_0x00c5:
            r3 = 12
            goto L_0x0127
        L_0x00c8:
            r3 = 16
            goto L_0x0127
        L_0x00cb:
            switch(r1) {
                case 1: goto L_0x0125;
                case 2: goto L_0x0122;
                case 3: goto L_0x011f;
                case 4: goto L_0x011c;
                case 5: goto L_0x0119;
                case 6: goto L_0x0116;
                case 7: goto L_0x0113;
                case 8: goto L_0x00ed;
                default: goto L_0x00ce;
            }
        L_0x00ce:
            java.lang.String r3 = "SDLAudio"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Requested "
            r7.append(r8)
            r7.append(r1)
            java.lang.String r8 = " channels, getting stereo"
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            android.util.Log.v(r3, r7)
            r1 = 2
            r3 = 12
            goto L_0x0127
        L_0x00ed:
            int r7 = android.os.Build.VERSION.SDK_INT
            if (r7 < r3) goto L_0x00f4
            r3 = 6396(0x18fc, float:8.963E-42)
            goto L_0x0127
        L_0x00f4:
            java.lang.String r3 = "SDLAudio"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Requested "
            r7.append(r8)
            r7.append(r1)
            java.lang.String r8 = " channels, getting 5.1 surround"
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            android.util.Log.v(r3, r7)
            r1 = 6
            r3 = 252(0xfc, float:3.53E-43)
            goto L_0x0127
        L_0x0113:
            r3 = 1276(0x4fc, float:1.788E-42)
            goto L_0x0127
        L_0x0116:
            r3 = 252(0xfc, float:3.53E-43)
            goto L_0x0127
        L_0x0119:
            r3 = 220(0xdc, float:3.08E-43)
            goto L_0x0127
        L_0x011c:
            r3 = 204(0xcc, float:2.86E-43)
            goto L_0x0127
        L_0x011f:
            r3 = 28
            goto L_0x0127
        L_0x0122:
            r3 = 12
            goto L_0x0127
        L_0x0125:
            r3 = 4
        L_0x0127:
            int r14 = r13 * r1
            if (r23 == 0) goto L_0x0131
            int r7 = android.media.AudioRecord.getMinBufferSize(r0, r3, r4)
            goto L_0x0135
        L_0x0131:
            int r7 = android.media.AudioTrack.getMinBufferSize(r0, r3, r4)
        L_0x0135:
            r15 = r7
            int r7 = r15 + r14
            r12 = 1
            int r7 = r7 - r12
            int r7 = r7 / r14
            int r2 = java.lang.Math.max(r2, r7)
            int[] r11 = new int[r6]
            r16 = 3
            r17 = 0
            r18 = 0
            if (r23 == 0) goto L_0x019b
            android.media.AudioRecord r6 = mAudioRecord
            if (r6 != 0) goto L_0x017d
            android.media.AudioRecord r19 = new android.media.AudioRecord
            r7 = 0
            int r20 = r2 * r14
            r6 = r19
            r8 = r0
            r9 = r3
            r10 = r4
            r21 = r11
            r11 = r20
            r6.<init>(r7, r8, r9, r10, r11)
            mAudioRecord = r19
            android.media.AudioRecord r6 = mAudioRecord
            int r6 = r6.getState()
            if (r6 == r12) goto L_0x0177
            java.lang.String r5 = "SDLAudio"
            java.lang.String r6 = "Failed during initialization of AudioRecord"
            android.util.Log.e(r5, r6)
            android.media.AudioRecord r5 = mAudioRecord
            r5.release()
            mAudioRecord = r18
            return r18
        L_0x0177:
            android.media.AudioRecord r6 = mAudioRecord
            r6.startRecording()
            goto L_0x017f
        L_0x017d:
            r21 = r11
        L_0x017f:
            android.media.AudioRecord r6 = mAudioRecord
            int r6 = r6.getSampleRate()
            r21[r17] = r6
            android.media.AudioRecord r6 = mAudioRecord
            int r6 = r6.getAudioFormat()
            r21[r12] = r6
            android.media.AudioRecord r6 = mAudioRecord
            int r6 = r6.getChannelCount()
            r21[r5] = r6
            r21[r16] = r2
            r5 = 1
            goto L_0x01ee
        L_0x019b:
            r21 = r11
            android.media.AudioTrack r6 = mAudioTrack
            if (r6 != 0) goto L_0x01d2
            android.media.AudioTrack r19 = new android.media.AudioTrack
            r7 = 3
            int r11 = r2 * r14
            r20 = 1
            r6 = r19
            r8 = r0
            r9 = r3
            r10 = r4
            r5 = 1
            r12 = r20
            r6.<init>(r7, r8, r9, r10, r11, r12)
            mAudioTrack = r19
            android.media.AudioTrack r6 = mAudioTrack
            int r6 = r6.getState()
            if (r6 == r5) goto L_0x01cc
            java.lang.String r5 = "SDLAudio"
            java.lang.String r6 = "Failed during initialization of Audio Track"
            android.util.Log.e(r5, r6)
            android.media.AudioTrack r5 = mAudioTrack
            r5.release()
            mAudioTrack = r18
            return r18
        L_0x01cc:
            android.media.AudioTrack r6 = mAudioTrack
            r6.play()
            goto L_0x01d3
        L_0x01d2:
            r5 = 1
        L_0x01d3:
            android.media.AudioTrack r6 = mAudioTrack
            int r6 = r6.getSampleRate()
            r21[r17] = r6
            android.media.AudioTrack r6 = mAudioTrack
            int r6 = r6.getAudioFormat()
            r21[r5] = r6
            android.media.AudioTrack r6 = mAudioTrack
            int r6 = r6.getChannelCount()
            r7 = 2
            r21[r7] = r6
            r21[r16] = r2
        L_0x01ee:
            java.lang.String r6 = "SDLAudio"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Opening "
            r7.append(r8)
            if (r23 == 0) goto L_0x01ff
            java.lang.String r8 = "capture"
            goto L_0x0201
        L_0x01ff:
            java.lang.String r8 = "playback"
        L_0x0201:
            r7.append(r8)
            java.lang.String r8 = ", got "
            r7.append(r8)
            r8 = r21[r16]
            r7.append(r8)
            java.lang.String r8 = " frames of "
            r7.append(r8)
            r8 = 2
            r8 = r21[r8]
            r7.append(r8)
            java.lang.String r8 = " channel "
            r7.append(r8)
            r5 = r21[r5]
            java.lang.String r5 = getAudioFormatString(r5)
            r7.append(r5)
            java.lang.String r5 = " audio at "
            r7.append(r5)
            r5 = r21[r17]
            r7.append(r5)
            java.lang.String r5 = " Hz"
            r7.append(r5)
            java.lang.String r5 = r7.toString()
            android.util.Log.v(r6, r5)
            return r21
        */
        throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDLAudioManager.open(boolean, int, int, int, int):int[]");
    }

    public static int[] audioOpen(int sampleRate, int audioFormat, int desiredChannels, int desiredFrames) {
        return open(false, sampleRate, audioFormat, desiredChannels, desiredFrames);
    }

    public static void audioWriteFloatBuffer(float[] buffer) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < buffer.length) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i, 0);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(float)");
                return;
            }
        }
    }

    public static void audioWriteShortBuffer(short[] buffer) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < buffer.length) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(short)");
                return;
            }
        }
    }

    public static void audioWriteByteBuffer(byte[] buffer) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < buffer.length) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(byte)");
                return;
            }
        }
    }

    public static int[] captureOpen(int sampleRate, int audioFormat, int desiredChannels, int desiredFrames) {
        return open(true, sampleRate, audioFormat, desiredChannels, desiredFrames);
    }

    public static int captureReadFloatBuffer(float[] buffer, boolean blocking) {
        return mAudioRecord.read(buffer, 0, buffer.length, blocking ^ true ? 1 : 0);
    }

    public static int captureReadShortBuffer(short[] buffer, boolean blocking) {
        if (VERSION.SDK_INT < 23) {
            return mAudioRecord.read(buffer, 0, buffer.length);
        }
        return mAudioRecord.read(buffer, 0, buffer.length, blocking ^ true ? 1 : 0);
    }

    public static int captureReadByteBuffer(byte[] buffer, boolean blocking) {
        if (VERSION.SDK_INT < 23) {
            return mAudioRecord.read(buffer, 0, buffer.length);
        }
        return mAudioRecord.read(buffer, 0, buffer.length, blocking ^ true ? 1 : 0);
    }

    public static void audioClose() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public static void captureClose() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }
}
