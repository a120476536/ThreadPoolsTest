package com.qiao.threadpoolstest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.qiao.utils.ThreadPool;
import com.qiao.utils.ThreadPools;
/**
 * 线程池管理 特殊标记：仅仅是为了测试线程，数据什么的我就扔一边了
 * @author 有点凉了
 * QQ群：123869487
 * 求基友共同进步，求大神入群指点
 */
public class MainActivity extends Activity{
	private static final String TAG="MainActivity";
	private ListView listView_main_show;
	List<Bitmap> bitMaps = new ArrayList<Bitmap>();
	List<Bitmap> bitMapsAll = null;
	private MyAdapter myAdapter = null;
	private Handler handler =new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<Bitmap> bitMapsOne =  (List<Bitmap>) msg.obj;
				for (int i = 0; i < bitMapsOne.size(); i++) {
					Log.i(TAG, "==-->m bitMapsOne.get(i)"+bitMapsOne.get(i));
				}
				
//				int one = bitMapsAll.size();
				bitMapsAll.addAll(bitMapsOne);
//				int two = bitMapsAll.size();
//				if (two>=one) {
					myAdapter.notifyDataSetChanged();
					
//				}
				break;

			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		bitMapsAll = new ArrayList<Bitmap>();
//		if (bitMapsAll.size()>0) {
			myAdapter = new MyAdapter(this, bitMapsAll);
//		}
		ThreadPools.startThread(new MyThread());
		listView_main_show.setAdapter(myAdapter);
		
	}
	/**
	 * 当前测试发生图片错位，解决方式可以在getView中传入url然后将url打上tag给每个imgView，，，，只是为了测试线程池管理线程，
	 * @author Administrator
	 *
	 */
	class MyThread implements ThreadPool{

		@Override
		public void start() {
			for (int i = 0; i < Images.imageThumbUrls.length; i++) {
				 BufferedInputStream bis = null;  
		         ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		         try {
					URL url = new URL(Images.imageThumbUrls[i]);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
					 connection.setDoInput(true);  
		                connection.setRequestMethod("GET");  
		                connection.connect();  
		                InputStream is = connection.getInputStream();  
		                int code = connection.getResponseCode();  
		                if (code == 200) {  
		                    bis = new BufferedInputStream(is);  
		                    int c = 0;  
		                    byte[] buf = new byte[1024 * 8];  
		                    while ((c = bis.read(buf)) != -1) {  
		                    	Log.i(TAG, "==-->m c:="+c);
		                        baos.write(buf, 0, c);  
		                        baos.flush();  
		                    }  
//		                    return baos.toByteArray();  
		                    Bitmap bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
//		                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, null);
		                    Log.i(TAG, "==-->m bitmap:="+bitmap);
		                    bitMaps.add(bitmap);
		                    Message msg = Message.obtain();
		                    msg.what=0;
		                    msg.obj=bitMaps;
		                    handler.sendMessage(msg);
		                }  
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			}
		}
		
	}
	
	
	class MyAdapter extends BaseAdapter{
		private List<Bitmap> bitMapsAll;
		private Context context;
		
		public MyAdapter(Context context,List<Bitmap> bitMapsAll){
			this.context=context;
			this.bitMapsAll=bitMapsAll;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bitMapsAll.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return bitMapsAll.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			String str = getItem(position).toString();
			Log.i(TAG, "==-->m str:="+str);
			ViewHolder mHolder = null;
			if (convertView==null) {
				mHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_show, null);
				mHolder.imageView_item_listview_main = (ImageView) convertView.findViewById(R.id.imageView_item_listview_main);
				convertView.setTag(mHolder);
			}else {
				mHolder = (ViewHolder) convertView.getTag();
				mHolder.imageView_item_listview_main.setTag(str);
				mHolder.imageView_item_listview_main.setImageBitmap(bitMapsAll.get(position));
				Log.i(TAG, "==-->bitMapsAll.get(position):="+bitMapsAll.get(position));
			}
			return convertView;
		}
		
		class ViewHolder{
			private ImageView imageView_item_listview_main;
		}
	}
	private void initView() {
		// TODO Auto-generated method stub
		listView_main_show = (ListView) findViewById(R.id.listView_main_show);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ThreadPools.endThread();//关闭线程
	}

}
