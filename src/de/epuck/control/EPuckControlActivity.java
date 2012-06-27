package de.epuck.control;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class EPuckControlActivity extends Activity {
    /** Called when the activity is first created. */
	private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
	OutputStream tmpOut = null;
	private OutputStream mmOutStream;
	public SeekBar sb1;
	public SeekBar sb2;
	public TextView tw;
	public boolean exit=false;
	CounterTask ct;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sb1=(SeekBar) findViewById(R.id.seekBar1);
        sb2=(SeekBar) findViewById(R.id.seekBar2);
        tw=(TextView) findViewById(R.id.tw1);
        
        tw.setText(Integer.toString(sb1.getProgress()));
        
       
        
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        
        int REQUEST_ENABLE_BT = 3;
        
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        
        //HashMap<String,String> mdevice= new HashMap<String,String>();
        
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
        	// Loop through paired devices
        	for (BluetoothDevice device : pairedDevices) {
        		// Add the name and address to an array adapter to show in a ListView
        		 Log.v("verbose", device.getAddress().toString());
        		 Log.v("verbose",device.getName().toString());
        		 if(device.getName().equals("e-puck_1031")){
        			 mmDevice=device;
        			 try {
        					mmSocket=mmDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        				} catch (IOException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        		 }
        	}
        }
        
        if(mmSocket==null)
        	return;
        
        mBluetoothAdapter.cancelDiscovery();
        
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
        
        try {
            //tmpIn = socket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) { }

        mmOutStream = tmpOut;
        
        // Do work to manage the connection (in a separate thread)
        
        write("\n\n");
        
        ct = new CounterTask();
        ct.execute();
        
        
        //write("d,1000,1000\n".getBytes());
        /*try {
			wait(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        //write("d,0,0\n".getBytes());
        
        

        
                
    }
    
    public void onStop(){
    	super.onStop();
    	exit=true;
    	write("d,0,0\n");
    	write("f,0\n");
    	
    	ct.cancel(exit);
    	
    	try {
			mmOutStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
    	
    	try {
			mmSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    }
    
    public void write(String cmd){
        try {
            mmOutStream.write(cmd.getBytes());
        } catch (IOException e) { }
    }
    
    private class CounterTask extends AsyncTask<Void, Integer, Void> {

    	protected Void doInBackground(Void... params) {
            	write("f,1\n");
            	try {
    				Thread.sleep(700);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            	publishProgress(0,0);
            	while(exit==false){
            		try {
        				Thread.sleep(100);
        			} catch (InterruptedException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
            		publishProgress();
            	}
    		return null;
    	}
    	
    	protected void onProgressUpdate(Integer...value) {
    		write("d,"+Integer.toString(sb1.getProgress()-1000)+","+Integer.toString(sb2.getProgress()-1000)+"\n");
    		//super.onProgressUpdate(values);
    	}
    }
    public void prog1(){
    	sb1.refreshDrawableState();
    	//sb2.refreshDrawableState();
    	sb1.isClickable();
    	//sb2.isClickable();
    }
    
    public void prog2(){
    	sb2.refreshDrawableState();
    	//sb1.isClickable();
    	sb2.isClickable();
    }
    
    /*public void onTouch(){
    	prog();    
    }*/

    /*private class ProgressTask extends AsyncTask<Void, Integer, Void> {
    	protected Void doInBackground(Void... params){
    		while(sb1.)
    		return null;
    	}
    	protected void onProgressUpdate(Integer...value){
    		
    	}
    	
    }*/


}