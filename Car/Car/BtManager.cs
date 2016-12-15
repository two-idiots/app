using System;
using System.Collections.Generic;
using Android.Content;
using Android.Widget;
using Java.IO;
using Android.Bluetooth;
using Java.Util;
using Android.Util;

namespace Car
{
    class BtManager
    {
        // unique ID which help us conect to any device
        private const string uuid = "00001101-0000-1000-8000-00805F9B34FB";
        // represent bluetooth data comming from UART
        private BluetoothDevice btDevice;
        // get input/output stream of this communication
        private BluetoothSocket mSocket;
        // convert byte[] to readable strings
        private BufferedReader reader;
        private System.IO.Stream mStream;
        private InputStreamReader mReader;
        private BluetoothAdapter btAdapter;

        private Toast toast;
        private Context mContext;

        public BtManager(Context mContext)
        {
            reader = null;
            this.mContext = mContext;

            btAdapter = BluetoothAdapter.DefaultAdapter;
                                    
            if(btAdapter == null)
                Toast.MakeText(mContext, "해당 기기는 블루투스를 지원하지 않습니다.", ToastLength.Short).Show();
        }

        public bool isBluetoothConnected()
        {
            if(mSocket == null)
                return false;

            return mSocket.IsConnected;
        }

        public bool isBluetoothEnabled()
        {
            return btAdapter.IsEnabled;
        }

        public void enable()
        {
            btAdapter.Enable();

            getAllPairedDevices();
        }

        public void disable()
        {
            btAdapter.Disable();
        }

        private UUID getUUIDFromString()
        {
            return UUID.FromString(uuid);
        }

        private void close(IDisposable aConnectedObject)
        {
            if (aConnectedObject == null)
                return;

            try
            {
                aConnectedObject.Dispose();
            }catch(Exception ex)
            {
                throw;
            }

            aConnectedObject = null;
        }

        public void close()
        {
            close(mSocket);
            close(mStream);
            close(mReader);
        }

        public void openDeviceConnection(BluetoothDevice btDevice)
        {
            try
            {
                if (btAdapter.IsDiscovering)
                    btAdapter.CancelDiscovery();

                mSocket = btDevice.CreateRfcommSocketToServiceRecord(getUUIDFromString());
                // blocking operation

                mSocket.Connect();
                // input stream
                mStream = mSocket.InputStream;
                // output stream
                //mSocket.OutputStream;
                mReader = new InputStreamReader(mStream);
                reader = new BufferedReader(mReader);
            }catch(IOException ioEX)
            {
                close(mSocket);
                close(mStream);
                close(mReader);
                throw ioEX;
            }
        }

        public String getDataFromDevice()
        {
            return mReader.Read().ToString();
        }

        public void getAllPairedDevices()
        {
            ICollection<BluetoothDevice> devices = btAdapter.BondedDevices;

            Log.Debug("TAG", "Test");

            if(devices != null && devices.Count > 0)
            {
                foreach (BluetoothDevice device in devices)
                {
                    Log.Debug("TAG", device.ToString());
                    //openDeviceConnection(device);
                }
            }
        }

        public void deviceDiscovery()
        {
            if (btAdapter.IsDiscovering)
                btAdapter.CancelDiscovery();

            btAdapter.StartDiscovery();
        }
    }
}