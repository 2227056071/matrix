package com.tencent.matrix.batterycanary.monitor.feature;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tencent.matrix.batterycanary.monitor.BatteryMonitorCore;
import com.tencent.matrix.batterycanary.monitor.feature.MonitorFeature.Snapshot.Differ.DigitDiffer;
import com.tencent.matrix.batterycanary.monitor.feature.MonitorFeature.Snapshot.Differ.ListDiffer;
import com.tencent.matrix.batterycanary.utils.BatteryCanaryUtil;
import com.tencent.matrix.batterycanary.utils.RadioStatUtil;
import com.tencent.matrix.util.MatrixLog;

/**
 * Device Status Monitoring:
 * - CpuFreq
 * - Battery Status
 * - Temperatures
 *
 * @author Kaede
 * @since 2020/11/1
 */
@SuppressWarnings("NotNullFieldNotInitialized")
public final class DeviceStatMonitorFeature implements MonitorFeature {
    private static final String TAG = "Matrix.battery.DeviceStatusMonitorFeature";

    @NonNull
    private BatteryMonitorCore mMonitor;

    @Override
    public void configure(BatteryMonitorCore monitor) {
        MatrixLog.i(TAG, "#configure monitor feature");
        mMonitor = monitor;
    }

    @Override
    public void onTurnOn() {
    }

    @Override
    public void onTurnOff() {

    }

    @Override
    public void onForeground(boolean isForeground) {
    }

    @Override
    public int weight() {
        return Integer.MIN_VALUE;
    }

    public CpuFreqSnapshot currentCpuFreq() {
        CpuFreqSnapshot snapshot = new CpuFreqSnapshot();
        try {
            snapshot.cpuFreqs = Snapshot.Entry.ListEntry.ofDigits(BatteryCanaryUtil.getCpuCurrentFreq());
        } catch (Throwable e) {
            MatrixLog.printErrStackTrace(TAG, e, "#currentCpuFreq error");
            snapshot.cpuFreqs = Snapshot.Entry.ListEntry.ofDigits(new int[]{});
        }
        return snapshot;
    }

    public BatteryTmpSnapshot currentBatteryTemperature(Context context) {
        BatteryTmpSnapshot snapshot = new BatteryTmpSnapshot();
        snapshot.temp = Snapshot.Entry.DigitEntry.of(mMonitor.getCurrentBatteryTemperature(context));
        return snapshot;
    }

    @Nullable
    protected RadioStatSnapshot currentRadioSnapshot(Context context) {
        RadioStatUtil.RadioStat stat = RadioStatUtil.getCurrentStat(context);
        if (stat == null) {
            return null;
        }
        RadioStatSnapshot snapshot = new RadioStatSnapshot();
        snapshot.wifiRxBytes = Snapshot.Entry.DigitEntry.of(stat.wifiRxBytes);
        snapshot.wifiTxBytes = Snapshot.Entry.DigitEntry.of(stat.wifiTxBytes);
        snapshot.mobileRxBytes = Snapshot.Entry.DigitEntry.of(stat.mobileRxBytes);
        snapshot.mobileTxBytes = Snapshot.Entry.DigitEntry.of(stat.mobileTxBytes);
        return snapshot;
    }

    public static class CpuFreqSnapshot extends Snapshot<CpuFreqSnapshot> {
        public Entry.ListEntry<Entry.DigitEntry<Integer>> cpuFreqs;

        @Override
        public Delta<CpuFreqSnapshot> diff(CpuFreqSnapshot bgn) {
            return new Delta<CpuFreqSnapshot>(bgn, this) {
                @Override
                protected CpuFreqSnapshot computeDelta() {
                    CpuFreqSnapshot delta = new CpuFreqSnapshot();
                    delta.cpuFreqs = ListDiffer.globalDiff(bgn.cpuFreqs, end.cpuFreqs);
                    return delta;
                }
            };
        }
    }

    public static class BatteryTmpSnapshot extends Snapshot<BatteryTmpSnapshot> {
        public Entry.DigitEntry<Integer> temp;

        @Override
        public Delta<BatteryTmpSnapshot>  diff(BatteryTmpSnapshot bgn) {
            return new Delta<BatteryTmpSnapshot>(bgn, this) {
                @Override
                protected BatteryTmpSnapshot computeDelta() {
                    BatteryTmpSnapshot delta = new BatteryTmpSnapshot();
                    delta.temp = DigitDiffer.globalDiff(bgn.temp, end.temp);
                    return delta;
                }
            };
        }
    }

    public static class RadioStatSnapshot extends Snapshot<RadioStatSnapshot> {
        public Entry.DigitEntry<Long> wifiRxBytes = Entry.DigitEntry.of(0L);
        public Entry.DigitEntry<Long> wifiTxBytes = Entry.DigitEntry.of(0L);
        public Entry.DigitEntry<Long> mobileRxBytes = Entry.DigitEntry.of(0L);
        public Entry.DigitEntry<Long> mobileTxBytes = Entry.DigitEntry.of(0L);

        @Override
        public Delta<RadioStatSnapshot> diff(RadioStatSnapshot bgn) {
            return new Delta<RadioStatSnapshot>(bgn, this) {
                @Override
                protected RadioStatSnapshot computeDelta() {
                    RadioStatSnapshot delta = new RadioStatSnapshot();
                    delta.wifiRxBytes = DigitDiffer.globalDiff(bgn.wifiRxBytes, end.wifiRxBytes);
                    delta.wifiTxBytes = DigitDiffer.globalDiff(bgn.wifiTxBytes, end.wifiTxBytes);
                    delta.mobileRxBytes = DigitDiffer.globalDiff(bgn.mobileRxBytes, end.mobileRxBytes);
                    delta.mobileTxBytes = DigitDiffer.globalDiff(bgn.mobileTxBytes, end.mobileTxBytes);
                    return delta;
                }
            };
        }
    }
}
