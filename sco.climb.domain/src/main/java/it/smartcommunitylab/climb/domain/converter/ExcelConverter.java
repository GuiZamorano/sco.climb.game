package it.smartcommunitylab.climb.domain.converter;

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.InvalidParametersException;
import it.smartcommunitylab.climb.domain.model.WsnEvent;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class ExcelConverter {
	private static final transient Logger logger = LoggerFactory.getLogger(ExcelConverter.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static void writeAttendance(Date dateFrom, Date dateTo, 
		List<WsnEvent> events, List<Child> childList, 
		List<Volunteer> volunteerList, OutputStream outputStream) throws Exception {
		Map<String, Child> childMap = new HashMap<String, Child>();
		Map<String, Volunteer> volunteerMap = new HashMap<String, Volunteer>();
		Map<String, List<WsnEvent>> eventMap = new TreeMap<String, List<WsnEvent>>();
		
		for(Child child : childList) {
			childMap.put(child.getObjectId(), child);
		}
		
		for(Volunteer volunteer : volunteerList) {
			volunteerMap.put(volunteer.getObjectId(), volunteer);
		}
		
		for(WsnEvent event : events) {
			String day = sdf.format(event.getTimestamp());
			List<WsnEvent> eventList = eventMap.get(day);
			if(eventList == null) {
				eventList = new ArrayList<WsnEvent>();
				eventMap.put(day, eventList);
			}
			eventList.add(event);
		}
		
		Workbook wb = new HSSFWorkbook();
		
		Sheet sheetVolunteers = wb.createSheet("VOLONTARI");
		int rowCounter = 0;
		for(String day : eventMap.keySet()) {
			if(logger.isInfoEnabled()) {
				logger.info("writeAttendance:" + day);
			}
			Row rowDate = sheetVolunteers.createRow(rowCounter);
			Cell cellDateLabel = rowDate.createCell(0);
			Cell cellDateValue = rowDate.createCell(1);
			cellDateLabel.setCellValue("Giorno");
			cellDateValue.setCellValue(day);
			rowCounter++;
			List<WsnEvent> eventList = eventMap.get(day);
			for(WsnEvent event : eventList) {
				if(event.getEventType() == Const.SET_DRIVER) {
					Row rowData = sheetVolunteers.createRow(rowCounter);
					cellDateLabel = rowData.createCell(0);
					cellDateLabel.setCellValue("Responsabile");
					cellDateValue = rowData.createCell(1);
					String volunteerId = (String)event.getPayload().get("volunteerId");
					String value = null;
					Volunteer volunteer = volunteerMap.get(volunteerId);
					if(volunteer != null) {
						value = volunteer.getName();
					} else {
						value = volunteerId;
					}
					cellDateValue.setCellValue(value);
					rowCounter++;
				} else if(event.getEventType() == Const.SET_HELPER) {
					Row rowData = sheetVolunteers.createRow(rowCounter);
					cellDateLabel = rowData.createCell(0);
					cellDateLabel.setCellValue("Aiutante");
					cellDateValue = rowData.createCell(1);
					String volunteerId = (String)event.getPayload().get("volunteerId");
					String value = null;
					Volunteer volunteer = volunteerMap.get(volunteerId);
					if(volunteer != null) {
						value = volunteer.getName();
					} else {
						value = volunteerId;
					}
					cellDateValue.setCellValue(value);
					rowCounter++;
				}
			}
		}
    
		Sheet sheetChildren = wb.createSheet("BAMBINI");
		rowCounter = 0;
		for(String day : eventMap.keySet()) {
			Row rowDate = sheetChildren.createRow(rowCounter);
			Cell cellDateLabel = rowDate.createCell(0);
			Cell cellDateValue = rowDate.createCell(1);
			cellDateLabel.setCellValue("Giorno");
			cellDateValue.setCellValue(day);
			rowCounter++;
			List<WsnEvent> eventList = eventMap.get(day);
			List<String> valueList = Lists.newArrayList();
			for(WsnEvent event : eventList) {
				if(event.getEventType() == Const.NODE_AT_DESTINATION) {
					String passengerId = (String)event.getPayload().get("passengerId");
					String value = null;
					Child child = childMap.get(passengerId);
					if(child != null) {
						value = child.getSurname() + " " + child.getName();
					} else {
						value = passengerId;
					}
					valueList.add(value);
				}
			}
			Collections.sort(valueList,	new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			for(String value : valueList) {
				Row rowData = sheetChildren.createRow(rowCounter);
				cellDateLabel = rowData.createCell(0);
				cellDateLabel.setCellValue("Presente");
				cellDateValue = rowData.createCell(1);
				cellDateValue.setCellValue(value);
				rowCounter++;
			}
		}
		
    wb.write(outputStream);
    wb.close();
	}
	
	public static Map<String, Route> readRoutes(InputStream excel, 
			String ownerId, String instituteId, String schoolId, List<ExcelError> errors) throws Exception {
		Map<String, Route> result = new HashMap<String, Route>();
		XSSFWorkbook wb = new XSSFWorkbook(excel);
		try {
			XSSFSheet sheet = wb.getSheet("Pedibus");
			if(sheet == null) {
				throw new InvalidParametersException("Pedibus sheet not found");
			}
			for(int i=1; i <= sheet.getLastRowNum(); i++) {
				try {
					Row row = sheet.getRow(i);
					String name = row.getCell(0).getStringCellValue();
					Date dataInizio = row.getCell(1).getDateCellValue();
					Date dataFine = row.getCell(2).getDateCellValue();
					
					Route route = new Route();
					route.setOwnerId(ownerId);
					route.setInstituteId(instituteId);
					route.setSchoolId(schoolId);
					route.setObjectId(Utils.getUUID());
					route.setName(name);
					route.setFrom(dataInizio);
					route.setTo(dataFine);
					
					result.put(name, route);
				} catch (Exception e) {
					ExcelError error = new ExcelError(sheet.getSheetName(), i, e.toString());
					errors.add(error);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			wb.close();
		}
		return result;
	}
	
	public static Map<String, Stop> readStops(InputStream excel,
			String ownerId, String instituteId, String schoolId,
			Map<String, Route> routesMap, List<ExcelError> errors) throws Exception {
		Map<String, Stop> result = new HashMap<String, Stop>();
		XSSFWorkbook wb = new XSSFWorkbook(excel);
		try {
			XSSFSheet sheet = wb.getSheet("Fermate");
			if(sheet == null) {
				throw new InvalidParametersException("Fermate sheet not found");
			}
			for(int i=1; i <= sheet.getLastRowNum(); i++) {
				try {
					Row row = sheet.getRow(i);
					String pedibus = row.getCell(0).getStringCellValue();
					String name = row.getCell(1).getStringCellValue();
					String oraPartenza = row.getCell(2).getStringCellValue();
					String posizione = row.getCell(3).getStringCellValue();
					double[] geocoding = new double[2];
					String[] split = posizione.split(",");
					geocoding[0] = Double.valueOf(split[0]);
					geocoding[1] = Double.valueOf(split[1]);
					double distanza = row.getCell(4).getNumericCellValue();
					String partenza = row.getCell(5).getStringCellValue();
					String arrivo = row.getCell(6).getStringCellValue();
					Double ordine = row.getCell(7).getNumericCellValue();
					
					Route route = routesMap.get(pedibus);
					if(route == null) {
						throw new InvalidParametersException(String.format("Route '%s' not found", pedibus));
					}
					
					Stop stop = new Stop();
					stop.setOwnerId(ownerId);
					stop.setRouteId(route.getObjectId());
					stop.setObjectId(Utils.getUUID());
					stop.setName(name);
					stop.setDepartureTime(oraPartenza);
					stop.setStart(Boolean.valueOf(partenza.trim().toLowerCase()));
					stop.setDestination(Boolean.valueOf(arrivo.trim().toLowerCase()));
					if(stop.isDestination()) {
						stop.setSchool(true);
					}
					stop.setGeocoding(geocoding);
					stop.setDistance(distanza);
					stop.setPosition(ordine.intValue());
					
					result.put(route.getName() + ", " + name, stop);
				} catch (Exception e) {
					ExcelError error = new ExcelError(sheet.getSheetName(), i, e.toString());
					errors.add(error);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			wb.close();
		}
		return result;
	}
	
	public static Map<String, Child> readChildren(InputStream excel,
			String ownerId, String instituteId, String schoolId,
			Map<String, Stop> stopsMap, List<ExcelError> errors) throws Exception {
		Map<String, Child> result = new HashMap<String, Child>();
		XSSFWorkbook wb = new XSSFWorkbook(excel);
		try {
			XSSFSheet sheet = wb.getSheet("Bambini");
			if(sheet == null) {
				throw new InvalidParametersException("Bambini sheet not found");
			}
			DataFormatter fmt = new DataFormatter();
			for(int i=1; i <= sheet.getLastRowNum(); i++) {
				try {
					Row row = sheet.getRow(i);
					String cognome = row.getCell(0).getStringCellValue();
					String nome = row.getCell(1).getStringCellValue();
					String genitore = fmt.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
					String telefono = fmt.formatCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
					String classe = fmt.formatCellValue(row.getCell(4));
					String fermata = fmt.formatCellValue(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
					String cf = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue()
							.trim().toUpperCase();
					String nodo = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					Child child = new Child();
					child.setOwnerId(ownerId);
					child.setInstituteId(instituteId);
					child.setSchoolId(schoolId);
					child.setObjectId(Utils.getUUID());
					child.setCf(cf);
					child.setName(nome);
					child.setSurname(cognome);
					child.setParentName(genitore);
					child.setPhone(telefono);
					child.setClassRoom(classe);
					child.setWsnId(nodo);
					
					if(Utils.isNotEmpty(fermata)) {
						Stop stop = stopsMap.get(fermata);
						if(stop != null) {
							stop.getPassengerList().add(child.getObjectId());
						} else {
							logger.warn(String.format("Stop '%s' not found", fermata));
						}
					}
					
					result.put(child.getObjectId(), child);
				} catch (Exception e) {
					ExcelError error = new ExcelError(sheet.getSheetName(), i, e.toString());
					errors.add(error);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			wb.close();
		}
		return result;
	}
	
	public static Map<String, Volunteer> readVolunteers(InputStream excel,
			String ownerId, String instituteId, String schoolId, List<ExcelError> errors) throws Exception {
		Map<String, Volunteer> result = new HashMap<String, Volunteer>();
		XSSFWorkbook wb = new XSSFWorkbook(excel);
		try {
			XSSFSheet sheet = wb.getSheet("Volontari");
			if(sheet == null) {
				throw new InvalidParametersException("Volontari sheet not found");
			}
			for(int i=1; i <= sheet.getLastRowNum(); i++) {
				try {
					Row row = sheet.getRow(i);
					String cognome = row.getCell(0).getStringCellValue();
					String nome = row.getCell(1).getStringCellValue();
					String telefono = row.getCell(2).getStringCellValue();
					//String linea = row.getCell(3).getStringCellValue();
					String email = row.getCell(4).getStringCellValue();
					
					Volunteer volunteer = new Volunteer();
					volunteer.setOwnerId(ownerId);
					volunteer.setInstituteId(instituteId);
					volunteer.setSchoolId(schoolId);
					volunteer.setObjectId(Utils.getUUID());
					volunteer.setName(cognome + " " + nome);
					volunteer.setPhone(telefono);
					volunteer.setPassword(email);
					
					result.put(volunteer.getObjectId(), volunteer);					
				} catch (Exception e) {
					ExcelError error = new ExcelError(sheet.getSheetName(), i, e.toString());
					errors.add(error);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			wb.close();
		}
		return result;
	}
	
}
