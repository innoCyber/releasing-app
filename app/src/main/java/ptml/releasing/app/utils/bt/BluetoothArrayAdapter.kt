package ptml.releasing.app.utils.bt

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ptml.releasing.R

class BluetoothArrayAdapter(context: Context, val items: List<BluetoothDevice>) :
    ArrayAdapter<BluetoothDevice>(context, R.layout.item_bt_device, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_bt_device, parent, false)
        }

        val nameTextView = view?.findViewById<TextView>(R.id.tv_name)
        val addressTextView = view?.findViewById<TextView>(R.id.tv_address)
        val btDevice = items.get(position)
        nameTextView?.text = btDevice.name
        addressTextView?.text = btDevice.address

        return view ?: LayoutInflater.from(context).inflate(R.layout.item_bt_device, parent, false)
    }
}