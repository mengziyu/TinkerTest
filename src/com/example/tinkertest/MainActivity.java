package com.example.tinkertest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tinker.lib.tinker.TinkerInstaller;

public class MainActivity extends Activity {
	
	private final static String PATCH_FILE_NAME="patch_signed.apk";
	private TextView tvShow;
	private DownloadTask downloadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvShow=(TextView) findViewById(R.id.tv_show);
//        tvShow.setText("程序有bug");
        tvShow.setText("我修复了bug");
        
        downloadTask=new DownloadTask();
        
    }
    public void loadPatch(View view) {
    	if(!isExitSDcard()){
    		showToast("没有SD卡");
    		return;
    	}
    	
    	downloadTask.execute("http://192.168.1.248:8080/"+PATCH_FILE_NAME);
        
    }
    
    public void loadPatchLocal(View view) {
    	if(!isExitSDcard()){
    		showToast("没有SD卡");
    		return;
    	}
    	
    	
    	TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),sdcardPath()+ "/"+PATCH_FILE_NAME);
    }
    
    
    private boolean isExitSDcard(){
    	return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    
    private String sdcardPath(){
    	return Environment.getExternalStorageDirectory().getPath();
    }
    
    private void showToast(String msg){
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
    class DownloadTask extends AsyncTask<String, Void, Integer>{

    	File file=null;
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		file=new File(sdcardPath()+"/"+PATCH_FILE_NAME);
    		if(file.exists()){
    			file.delete();
    		}
    		
    		try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    		showToast("开始下载补丁");
    	}
    	
		@Override
		protected Integer doInBackground(String... params) {
			HttpURLConnection connection=null;
			InputStream is=null;
			OutputStream os=null;
			try {
				URL url = new URL(params[0]);
				connection=(HttpURLConnection) url.openConnection();
				if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
					is=connection.getInputStream();
					os=new FileOutputStream(file);
					byte[] buf=new byte[1024];
					int len=0;
					while((len=is.read(buf))!=-1){
						os.write(buf, 0, len);
					}
					
					return 0;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(connection!=null){
					connection.disconnect();
				}
				
				if(os!=null){
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(is!=null){
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return -1;
		}

		
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result==0){
				showToast("下载完成，打补丁");
				TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),sdcardPath()+ "/"+PATCH_FILE_NAME);
			}
		}
		
    }
}
