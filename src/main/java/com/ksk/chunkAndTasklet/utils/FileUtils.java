package com.ksk.chunkAndTasklet.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksk.chunkAndTasklet.model.Line;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class FileUtils {
	Logger logger = LogManager.getLogger(FileUtils.class);
	
	private String fileName;

	private FileReader fileReader;
	private CSVReader csvReader;

	private FileWriter fileWriter;
	private CSVWriter csvWriter;

	private File file;

	public FileUtils(String fileName) {
		this.fileName = fileName;
	}

	public Line readLine(){
		try {
			if (fileReader == null) initReader();
			String[] line = csvReader.readNext();
			if (line == null)
				return null;
			Line tempLine = new Line();
			tempLine.setName(line[0]);
			tempLine.setDob(LocalDate.parse(line[1], DateTimeFormatter.ofPattern("MM/dd/yyyy")));
			return tempLine;
//				Line line = new Line(csvReader., null, null)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void writeLine(Line line) throws IOException {
		if(fileWriter == null) initWriter();
		
		csvWriter.writeNext(new String[] { line.getName(), line.getAge().toString()});
	}

	private void initReader() throws FileNotFoundException {
		
		ClassLoader classLoader = this.getClass().getClassLoader();
		if (file == null) file = new File(classLoader.getResource(fileName).getFile());
		logger.info("Does the file exists: {}", file.exists());
		
//			file = new File(classLoader.getResource(fileName).getFile());
		if (fileReader == null) fileReader = new FileReader(file);

		if (csvReader == null) csvReader = new CSVReader(fileReader);
	}

	private void initWriter() throws IOException {
		if (file == null) {
			file = new File(fileName);
			file.createNewFile();
		}

		if (fileWriter == null)
			fileWriter = new FileWriter(file);
		if (csvWriter == null)
			csvWriter = new CSVWriter(fileWriter);
	}

	public void closeReader() throws IOException {
		fileReader.close();
		csvReader.close();
	}

	public void closeWriter() throws IOException {
		csvWriter.close();
		fileWriter.close();
	}
}
