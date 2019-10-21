package com.along.gps.util;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MyThread {
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		List<String> list=new ArrayList<>();
		getAllFileName("E:\\gpsData\\json\\",list);
		System.out.println(list.size());
		System.out.println(asyncGet(list).size());
		long endTime = System.currentTimeMillis();
		System.out.println("运行时间:" + (endTime - startTime) + "ms");
	}

	//通过文件名集合读取数据
	public static List<String> asyncGet(List<String> list)
			throws Exception {
			List<String> objectList = new ArrayList<>();
			CompletableFuture[] futures = list.stream()
					.map(p -> CompletableFuture.supplyAsync(() -> {
								try {
									return FileUtil.readFile(p);
								} catch (IOException e) {
									e.printStackTrace();
									return null;
								}

							})
					).toArray(CompletableFuture[]::new);
			CompletableFuture<Void> completableFuture = CompletableFuture.allOf(futures);
			completableFuture.get();
		//completableFuture.get(3000, TimeUnit.MILLISECONDS);
		for (CompletableFuture future:futures) {
			objectList.addAll((List<String>)future.get());
		}
		return objectList;
	}

	//获取文件集合
	public static void getAllFileName(String path,List<String> fileNameList) {
		//ArrayList<String> files = new ArrayList<String>();
		boolean flag = false;
		File file = new File(path);
		File[] tempList = file.listFiles();

		for (int i = 0; i < tempList.length/2; i++) {
		//for (int i = 0; i < 100; i++) {
			if (tempList[i].isFile()) {
//              System.out.println("文     件：" + tempList[i]);
				//fileNameList.add(tempList[i].toString());
				fileNameList.add(path+tempList[i].getName());
			}
			/*if (tempList[i].isDirectory()) {
//              System.out.println("文件夹：" + tempList[i]);
				getAllFileName(tempList[i].getAbsolutePath(),fileNameList);
			}*/
		}
		return;
	}

}



