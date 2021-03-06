package org.cldutil.hadooputil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cldutil.util.ProxyConf;

public class HdfsDownloadUtil {
	
	private static Logger logger =  LogManager.getLogger(HdfsDownloadUtil.class);
	
	public static int tryMax = 3;
	
	private static InputStream getInputStream(String url, ProxyConf proxyConf){
		InputStream is=null;
		try{
			URL website = new URL(url);
			if (!proxyConf.isUseProxy()){
				is = website.openStream();
			}else{
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyConf.getHost(), proxyConf.getPort()));
				is = website.openConnection(proxy).getInputStream();
			}
		}catch(Exception e){
			logger.warn("", e);
		}
		return is;
	}
	
	public static void downloadFileToHdfs(String url, ProxyConf proxyConf, String filePath, String fsDefaultName){
		InputStream is = null;
		try{
			int tryNum=0;
			while (is==null && tryNum<tryMax){
				is = getInputStream(url, proxyConf);
				tryNum++;
			}
			if (is!=null){
				downloadFileToHdfs(is, filePath, fsDefaultName);
			}
		}catch(Exception e){
			logger.error(String.format("exception got while try to get url:%s", url),e);
		}finally{
			try{
				if (is!=null){
					is.close();
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}
	}
	
	public static void downloadFileToHdfs(InputStream is, String filePath, String fsDefaultName){
		FSDataOutputStream fos = null;
		FileSystem fs = null;
		
		try{
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", fsDefaultName);
			fs = FileSystem.get(conf);//fs can't be closed
			if (is!=null){
				Path fileNamePath = new Path(filePath);
				fos = fs.create(fileNamePath);
				fos.write(IOUtils.toByteArray(is));
			}else{
				logger.error("inputstream is null");
			}
		}catch(Exception e){
			logger.error("",e);
		}finally{
			try{
				if (fos!=null){
					fos.close();
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}
	}
	
	//hdfs://192.85.247.104:19000/reminder/sresult/earnforecast==>hdfs://192.85.247.104:19000 and /reminder/sresult/earnforecast
	public static void outputToHdfs(String[] lines, String filePath, String fsDefaultName){
		FSDataOutputStream fos = null;
		FileSystem fs = null;
		
		try{
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", fsDefaultName);
			fs = FileSystem.get(conf);//fs can't be closed
			Path fileNamePath = new Path(filePath);
			fos = fs.create(fileNamePath);
			for (String line:lines){
				fos.writeBytes(line);
				fos.writeBytes("\n");
			}
		}catch(Exception e){
			logger.error("",e);
		}finally{
			try{
				if (fos!=null){
					fos.close();
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}
	}
}
