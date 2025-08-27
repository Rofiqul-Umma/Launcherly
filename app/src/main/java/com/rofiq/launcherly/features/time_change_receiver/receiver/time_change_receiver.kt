import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimeChangeReceiver(
    private val onTimeOrDateChanged: () -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        onTimeOrDateChanged() // just trigger the VM method
    }
}
