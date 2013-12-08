package org.qeo.qeomessaging.android;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

class PostImage extends AsyncTask<Void, Void, Void>    {
	
	

		//private byte[] imageBytes;
		private String filename;
		private String URL;
		private byte[] imageBytes;
		PostImage(String URL,String filename,byte[] imageData)    {
        	//this.imageBytes = bytes;
			this.URL = URL;
        	this.filename = filename;
        	this.imageBytes = imageData;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) { 
            try {
				upload(this.filename, this.imageBytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return null;
        }   
        
        private void upload(String filename,byte[] imageBytes) throws IOException {
        	URL url = new URL(this.URL);
    		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
    		
    		DataOutputStream outputStream = null;

    		
    		String lineEnd = "\r\n";
    		String twoHyphens = "--";
    		String boundary =  "*****";

    		int bytesRead, bytesAvailable, bufferSize;
    		byte[] buffer;
    		int maxBufferSize = 1*1024*1024;

    		try {
    			InputStream fileInputStream = new ByteArrayInputStream(imageBytes);
    	
    			
    			connection = (HttpURLConnection) url.openConnection();
    	
    			// Allow Inputs & Outputs
    			connection.setDoInput(true);
    			connection.setDoOutput(true);
    			connection.setUseCaches(false);
    	
    			// Enable POST method
    			connection.setRequestMethod("POST");
    	
    			connection.setRequestProperty("Connection", "Keep-Alive");
    			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
    	
    			outputStream = new DataOutputStream( connection.getOutputStream() );
    			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
    			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + filename +"\"" + lineEnd);
    			outputStream.writeBytes(lineEnd);
    	
    			bytesAvailable = fileInputStream.available();
    			bufferSize = Math.min(bytesAvailable, maxBufferSize);
    			buffer = new byte[bufferSize];
    	
    			// Read file
    			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    	
    			while (bytesRead > 0)
    			{
    				outputStream.write(buffer, 0, bufferSize);
    				bytesAvailable = fileInputStream.available();
    				bufferSize = Math.min(bytesAvailable, maxBufferSize);
    				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    			}
    	
    			outputStream.writeBytes(lineEnd);
    			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    	
    			fileInputStream.close();
    			outputStream.flush();
    			outputStream.close();
    			
    			if(connection.getResponseCode() == 200)
    			{
    				StringBuilder sb;
    				BufferedReader rd = null;
    				try {
    					rd = new BufferedReader(
    							new InputStreamReader(connection.getInputStream()));
    					sb = new StringBuilder();
    					String line;
    					while ((line = rd.readLine()) != null) {
    						sb.append(line);
    					}
    				} finally {
    					rd.close();
    				}

    				connection.disconnect();
    				System.out.println( sb.toString());
    			}
    			
    		}
    		catch (Exception ex)
    		{
    			Log.e("ERROR!", ex.toString());
    		}
        }
}