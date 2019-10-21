package com.along.gps.util;

import org.apache.poi.hssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
	public static String uploadExcel(MultipartFile file, HttpServletRequest request){
		if(!file.isEmpty())
		{
			String  pa= SysUtil.WEB_FILE_LOCATION;;
			String path=request.getServletContext().getRealPath("");
			//上传文件名
			String filename=file.getOriginalFilename();
			File filepath=new File(path,filename);
			//判断路劲是否存在，如果不存在就创建一个
			if (!filepath.getParentFile().exists()){
				filepath.getParentFile().mkdirs();
			}
			//将上传文件保存到一个目标文件当中
			try {
				file.transferTo(new File(pa+File.separator+filename));
				return "文件上传成功";
			} catch (IOException e) {
				e.printStackTrace();
			}

		}else {
			return "文件为空，请重新上传";
		}
		return "异常";
	}
	//下载
	public static void downloadExcel(HttpServletResponse response, HttpServletRequest request,String url) {
		try {
			//获取文件的路径
			String excelPath = request.getSession().getServletContext().getRealPath(url);
			String fileName = "xx.xls".toString(); // 文件的默认保存名
			// 读到流中
			InputStream inStream = new FileInputStream(excelPath);//文件的存放路径
			// 设置输出的格式
			response.reset();
			response.setContentType("bin");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + URLEncoder.encode("xx.xls", "UTF-8"));
			// 循环取出流中的数据
			byte[] b = new byte[200];
			int len;

			while ((len = inStream.read(b)) > 0){
				response.getOutputStream().write(b, 0, len);
			}
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	/***
	 * 导出Excel
	 * @param excelName 要导出的excel名称
	 * @param list      要导出的数据集合
	 * @param fieldMap  中英文字段对应Map,即要导出的excel表头
	 * @param response  使用response可以导出到浏览器
	 * @param <T>
	 */
	public  <T> void exportExcel(String excelName, List<T> list, LinkedHashMap<String, String> fieldMap, HttpServletResponse response){

		// 设置默认文件名为当前时间：年月日时分秒
		if (excelName==null || excelName=="") {
			excelName = new SimpleDateFormat("yyyyMMddhhmmss").format(
					new Date()).toString();
		}
		// 设置response头信息
		response.reset();
		// 改成输出excel文件
		response.setContentType("application/vnd.ms-excel");
		try {
			//导出的response信息
			response.setHeader("Content-disposition", "attachment; filename="
					+new String(excelName.getBytes("gb2312"), "ISO-8859-1")  + ".xls");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		try {
			//创建一个WorkBook,对应一个Excel文件
			HSSFWorkbook wb = new HSSFWorkbook();
			//在Workbook中，创建一个sheet，对应Excel中的工作薄（sheet）
			HSSFSheet sheet = wb.createSheet(excelName);
			//创建单元格，并设置值表头 设置表头居中
			HSSFCellStyle style = wb.createCellStyle();
			//创建一个居中格式
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			// 填充工作表
			fillSheet(sheet,list,fieldMap,style);
			//将文件输出
			OutputStream ouputStream = response.getOutputStream();
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 向工作表中填充数据
	 @param sheet
	  *            excel的工作表名称
	  * @param list
	 *            数据源
	 * @param fieldMap
	 *            中英文字段对应关系的Map
	 * @param style
	 *            表格中的格式
	 * @throws Exception
	 *             异常
	 */
	public static <T> void fillSheet(HSSFSheet sheet, List<T> list,
	                                 LinkedHashMap<String, String> fieldMap,HSSFCellStyle style) throws Exception {
		// 定义存放英文字段名和中文字段名的数组
		String[] enFields = new String[fieldMap.size()];
		String[] cnFields = new String[fieldMap.size()];

		// 填充数组
		int count = 0;
		for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
			//获取key值,  :字段名
			enFields[count] = entry.getKey();
			//获得value值 :字段名的中文名
			cnFields[count] = entry.getValue();
			count++;
		}
		//在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int)0);

		// 填充表头
		for (int i = 0; i < cnFields.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(cnFields[i]);
			cell.setCellStyle(style);
			//解决自动设置列宽中文失效的问题
			sheet.autoSizeColumn(i,true);
		}
		int cellwidth=0;
		// 填充内容
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(i + 1);
			// 获取单个对象
			T item = list.get(i);
			for (int j = 0; j < enFields.length; j++) {
				Object objValue = getFieldValueByNameSequence(enFields[j], item);
				String fieldValue = objValue == null ? "" : objValue.toString();
				cellwidth=cellwidth<fieldValue.getBytes().length?fieldValue.getBytes().length:cellwidth;
				cellwidth=cellwidth<cnFields[j].getBytes().length?cnFields[j].getBytes().length:cellwidth;
				row.createCell(j).setCellValue(fieldValue);
				//得到每列最大的一个字段的长度加2
				sheet.setColumnWidth(j, (cellwidth+2)*256);
			}
		}
	}
	/**
	 * 根据带路径或不带路径的属性名获取属性值,即接受简单属性名，
	 * 如userName等，又接受带路径的属性名，如student.department.name等
	 *
	 * @param fieldNameSequence 带路径的属性名或简单属性名
	 * @param o                 对象
	 * @return                  属性值
	 * @throws Exception        异常
	 *
	 */
	public static Object getFieldValueByNameSequence(String fieldNameSequence,
	                                                 Object o) throws Exception {
		Object value = null;

		// 将fieldNameSequence进行拆分
		String[] attributes = fieldNameSequence.split("\\.");
		if (attributes.length == 1) {
			value = getFieldValueByName(fieldNameSequence, o);
		} else {
			// 根据数组中第一个连接属性名获取连接属性对象，如student.department.name
			Object fieldObj = getFieldValueByName(attributes[0], o);
			//截取除第一个属性名之后的路径
			String subFieldNameSequence = fieldNameSequence
					.substring(fieldNameSequence.indexOf(".") + 1);
			//递归得到最终的属性对象的值
			value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
		}
		return value;

	}

	/**
	 * 根据字段名获取字段值
	 *
	 * @param fieldName  字段名
	 * @param o          对象
	 * @return           字段值
	 * @throws Exception 异常
	 */
	public static Object getFieldValueByName(String fieldName, Object o)
			throws Exception {

		Object value = null;
		//根据字段名得到字段对象
		Field field = getFieldByName(fieldName, o.getClass());

		//如果该字段存在，则取出该字段的值
		if (field != null) {
			//类中的成员变量为private,在类外边使用属性值，故必须进行此操作
			field.setAccessible(true);
			//获取当前对象中当前Field的value
			value = field.get(o);
		} else {
			throw new Exception(o.getClass().getSimpleName() + "类不存在字段名 "
					+ fieldName);
		}

		return value;
	}
	/**
	 * 根据字段名获取字段对象
	 *
	 * @param fieldName
	 *            字段名
	 * @param clazz
	 *            包含该字段的类
	 * @return 字段
	 */
	public static Field getFieldByName(String fieldName, Class<?> clazz) {
		// 拿到本类的所有字段
		Field[] selfFields = clazz.getDeclaredFields();

		// 如果本类中存在该字段，则返回
		for (Field field : selfFields) {
			//如果本类中存在该字段，则返回
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}

		// 否则，查看父类中是否存在此字段，如果有则返回
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			//递归
			return getFieldByName(fieldName, superClazz);
		}

		// 如果本类和父类都没有，则返回空
		return null;
	}
}
