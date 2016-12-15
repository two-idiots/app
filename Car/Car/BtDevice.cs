using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Bluetooth;

namespace Car
{
    class BtDevice
    {
        //private string name;
        //private string address;

        //public string Name
        //{
        //    get { return name; }
        //}

        //public string Address
        //{
        //    get { return address; }
        //}

        //public BtDevice(string name, string address)
        //{
        //    this.name = name;
        //    this.address = address;
        //}

        private BluetoothDevice btDevice;
        public BluetoothDevice Device
        {
            get { return btDevice; }
        }

        public BtDevice(BluetoothDevice btDevice)
        {
            this.btDevice = btDevice;
        }
    }
}