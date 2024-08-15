package com.smart.Helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Entities.Contact;
import com.smart.Entities.User;

public class FileUploadHelper {
	
	public static boolean isFileValid(MultipartFile file) {
		String fileType = file.getContentType();
		return fileType.equals("application/vnd.ms-excel")||
				fileType.equals("application/vnd.ms-excel.sheet.macroEnabled.12")||
				fileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}
	
	public static List<Contact> extractContactListFromFile(InputStream fis, User user){
		XSSFWorkbook workbook;
		List<Contact> contacts = new ArrayList<Contact>();
		try {
			workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheet("contacts");
			Iterator<Row> rows = sheet.iterator();
			int rowNum = 0;
			while(rows.hasNext()) {
				Contact contact = new Contact();
				Row row = rows.next();
				if(rowNum>0) {
					Iterator<Cell> cells = row.iterator();
					while(cells.hasNext()) {
						Cell cell = cells.next();
						switch(cell.getColumnIndex())
						{
						case 0:contact.setDescription(cell.getStringCellValue());
						break;
						case 1:contact.setEmail(cell.getStringCellValue());
						break;
						case 2:contact.setName(cell.getStringCellValue());
						break;
						case 3:contact.setNickName(cell.getStringCellValue());
						break;
						case 4:contact.setPhone(String.format("%.0f", cell.getNumericCellValue()).substring(0,10));
						break;
						case 5:contact.setWork(cell.getStringCellValue());
						break;
						default: break;
						}
					}
					
					contact.setImage("default.webp");
					contact.setUser(user);
					contacts.add(contact);
				}
				rowNum++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contacts;
	}
}
