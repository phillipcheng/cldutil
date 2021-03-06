package org.cldutil.hadooputil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class FirstFileFilter implements PathFilter{
	private static Logger logger =  LogManager.getLogger(FirstFileFilter.class);
	private Set<String> prefixes = new HashSet<String>();//prefix-name
	
	public FirstFileFilter(){
	}
	
	public Set<String> getPrefixes(){
		return prefixes;
	}
	
	@Override
	public boolean accept(Path p) {
		String name = p.getName();
		String partName = null;
		if (name.contains("-m-")){
			//generated by multiple output xxxx-m-000xx
			int idx = name.indexOf("-m-");
			partName = name.substring(0, idx);
		}else if (name.equals("_SUCCESS")){
			//
		}else if (name.contains("_")){
			//generated byId
			int idx = name.lastIndexOf("_");
			partName = name.substring(idx+1, name.length());
		}
		if (partName!=null){
			if (!prefixes.contains(partName)){
				prefixes.add(partName);
			}
		}
		return false;
	}	
}

class DirFilter implements PathFilter{	
	private static Logger logger =  LogManager.getLogger(DirFilter.class);
	private FileSystem fs;
	
	public DirFilter(FileSystem fs){
		this.fs = fs;
	}

	@Override
	public boolean accept(Path p) {
		try {
			FileStatus fst = fs.getFileStatus(p);
			return !fst.isFile();
		}catch(Exception e){
			logger.error("", e);
		}
		return false;
	}
}


public class HadoopUtil {
	
	private static Logger logger =  LogManager.getLogger(HadoopUtil.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddd");
	
	public static Path[] getLeafPath(FileSystem fs, Path start){
		List<Path> leafPaths = new ArrayList<Path>();
		List<Path> checkPaths = new ArrayList<Path>();
		checkPaths.add(start);
		DirFilter dfilter = new DirFilter(fs);
		while (checkPaths.size()>0){
			Path p = checkPaths.remove(0);
			try {
				FileStatus[] fstl = fs.listStatus(p, dfilter);
				if (fstl.length>0){
					//check whether this is leaf dir
					for (FileStatus fst:fstl){
						FileStatus[] subDirs = fs.listStatus(fst.getPath(), dfilter);
						if (subDirs.length==0){
							logger.info(String.format("add leaf dir: %s", fst.getPath().toString()));
							leafPaths.add(fst.getPath());
						}else{
							checkPaths.add(fst.getPath());
						}
					}
				}else{
					//p is a leaf dir
					logger.info(String.format("add leaf dir: %s", p.toString()));
					leafPaths.add(p);
				}
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		Path[] leafs = new Path[leafPaths.size()];
		return leafPaths.toArray(leafs);
	}
	
	//xxxx-m-000xx or none mapred output
	public static String[] getPartNames(FileSystem fs, Path start){
		FirstFileFilter fffilter = new FirstFileFilter();
		try {
			fs.listStatus(start, fffilter);
			Set<String> partNames = fffilter.getPrefixes();
			List<String> fpartNames = new ArrayList<String>();//filtered
			for (String partName:partNames){
				try {
					sdf.parse(partName);
				}catch(ParseException pe){
					fpartNames.add(partName);
				}
			}
			String[] retPrefix = new String[fpartNames.size()];
			retPrefix = fpartNames.toArray(retPrefix);
			return retPrefix;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	public static Path[] getFileWithNameContains(FileSystem fs, Path p, String key){
		try {
			FileStatus[] fsl = fs.listStatus(p, new FileNameFilter(key));
			Path[] paths = new Path[fsl.length];
			for (int i=0; i<fsl.length; i++){
				paths[i] = fsl[i].getPath();
			}
			return paths;
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}
	}
	
	
}


