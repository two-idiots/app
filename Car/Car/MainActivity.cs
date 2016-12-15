using Android.App;
using Android.Widget;
using Android.OS;
using Android.Views;
using System.Threading;
using Android.Util;
using Android.Content;
using System;
using Android.Bluetooth;
using System.Collections.Generic;

namespace Car
{
    [Activity(Label = "Car", MainLauncher = true, Icon = "@drawable/icon")]
    public class MainActivity : Activity
    {
        private Thread thread;
        private string data = null;
        private BtManager btManager;
        private Button btn;

        private static ArrayAdapter<string> adapter;
        private static List<BtDevice> deviceList;

        private BroadcastReceiver receiver;
        
        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);

            // Set our view from the "main" layout resource
            // SetContentView (Resource.Layout.Main);
            SetContentView(Resource.Layout.Main);

            btManager = new BtManager(this);

            if (!btManager.isBluetoothEnabled())
                btManager.enable();

            init();
                        
            btn.Click += Btn_Click;
            
        }
        
        private void init()
        {
            btn = FindViewById<Button>(Resource.Id.btn);
            //View view = 
            //listView = FindViewById<ListView>(Resource.Id.deviceList);
            //var listView1 = FindViewById<ListView>(Resource.Id.deviceList);

            //deviceAdapter = new ArrayAdapter<string>(this, Resource.Layout.alertScroll);
            //listView.Adapter = deviceAdapter;

            deviceList = new List<BtDevice>();

            receiver = new Receiver();

            IntentFilter filter = new IntentFilter(BluetoothDevice.ActionFound);
            RegisterReceiver(receiver, filter);

            filter = new IntentFilter(BluetoothAdapter.ActionDiscoveryFinished);
            RegisterReceiver(receiver, filter);

            filter = new IntentFilter(BluetoothAdapter.ActionDiscoveryStarted);
            RegisterReceiver(receiver, filter);
        }

        protected override void OnDestroy()
        {
            base.OnDestroy();

            if (btManager.isBluetoothEnabled())
            {
                if (btManager.isBluetoothConnected())
                    btManager.close();

                btManager.disable();
            }

            UnregisterReceiver(receiver);
        }

        private void Btn_Click(object sender, System.EventArgs e)
        {
            //if(thread == null)
            //{
            //    thread = new Thread(() =>
            //    {
            //        while (true)
            //        {
            //            data = btManager.getDataFromDevice();

            //            Log.Debug("TAG", data);

            //            data = null;
            //        }
            //    });

            //    thread.IsBackground = true;
            //    thread.Start();
            //}

            adapter = new ArrayAdapter<string>(this, Android.Resource.Layout.SimpleListItem1);
                        
            btManager.getAllPairedDevices();
            btManager.deviceDiscovery();
            
            AlertDialog alert = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.SetTitle("질문");
            builder.SetPositiveButton("메롱", new System.EventHandler<Android.Content.DialogClickEventArgs>(
                (s, args) =>
                {
                    alert.Dismiss();
                }));
            builder.SetAdapter(adapter, new EventHandler<DialogClickEventArgs>((s, args) =>
            {
                Log.Debug("TAG", adapter.GetItem(args.Which));
                btManager.openDeviceConnection(deviceList[args.Which].Device);
            }));

            alert = builder.Create();
            alert.Show();
            // alert창 띄우기

            WindowManagerLayoutParams layoutParams = alert.Window.Attributes;
            layoutParams.Height = 750;
            alert.Window.Attributes = layoutParams;
            // alert 창 크기 조절
        }

        public class Receiver : BroadcastReceiver
        {
            public override void OnReceive(Context context, Intent intent)
            {
                string action = intent.Action;
                //Log.Debug("TAG", action);
                
                if(action == BluetoothDevice.ActionFound)
                {
                    BluetoothDevice device = (BluetoothDevice)intent.GetParcelableExtra(BluetoothDevice.ExtraDevice);
                    
                    Log.Debug("TAG", device.Name + "\n" + device.Address);
                    adapter.Add(device.Name);
                    deviceList.Add(new BtDevice(device));
                }
                else if (action == BluetoothAdapter.ActionDiscoveryFinished)
                {
                    Log.Debug("TAG", "Finish");
                }
                else if(action == BluetoothAdapter.ActionDiscoveryStarted)
                {
                    Log.Debug("TAG", "Start");
                    deviceList.Clear();
                    adapter.Clear();
                    //deviceAdapter.Clear();
                }
            }
        }
    }
}

