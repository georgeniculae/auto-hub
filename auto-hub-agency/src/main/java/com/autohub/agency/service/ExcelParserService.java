package com.autohub.agency.service;

import com.autohub.agency.entity.BodyType;
import com.autohub.agency.entity.Car;
import com.autohub.agency.entity.CarFields;
import com.autohub.agency.entity.CarStatus;
import com.autohub.exception.AutoHubException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelParserService {

    private final RentalOfficeService rentalOfficeService;

    public List<Car> extractDataFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            return getValuesFromSheet(sheet);
        } catch (Exception e) {
            throw new AutoHubException(e.getMessage());
        }
    }

    private List<Car> getValuesFromSheet(Sheet sheet) {
        DataFormatter dataFormatter = new DataFormatter();
        List<Picture> sheetPictures = getSheetPictures(sheet);
        List<Car> excelCarRequests = new ArrayList<>();

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row currentRow = sheet.getRow(rowIndex);
            List<Object> values = getCellValues(currentRow, sheetPictures, dataFormatter);

            excelCarRequests.add(generateCar(values));
        }

        return List.copyOf(excelCarRequests);
    }

    private List<Picture> getSheetPictures(Sheet sheet) {
        return ((XSSFSheet) sheet).createDrawingPatriarch()
                .getShapes()
                .stream()
                .filter(xssfShape -> xssfShape instanceof Picture)
                .map(xssfShape -> ((Picture) xssfShape))
                .toList();
    }

    private List<Object> getCellValues(Row currentRow, List<Picture> sheetPictures, DataFormatter dataFormatter) {
        Iterator<Cell> cellIterator = currentRow.cellIterator();
        List<Object> values = new ArrayList<>();

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            switch (cell.getCellType()) {
                case STRING -> values.add(cell.getStringCellValue());
                case NUMERIC -> values.add(dataFormatter.formatCellValue(cell));
                default -> throw new AutoHubException("Unknown Excel cell type");
            }
        }

        values.add(getCarPictureData(sheetPictures, currentRow));

        return List.copyOf(values);
    }

    private PictureData getCarPictureData(List<Picture> sheetPictures, Row currentRow) {
        return sheetPictures.stream()
                .filter(picture -> hasCorrespondingRowAndColumn(currentRow, picture))
                .findFirst()
                .map(Picture::getPictureData)
                .orElse(null);
    }

    private boolean hasCorrespondingRowAndColumn(Row currentRow, Picture picture) {
        return currentRow.getRowNum() == picture.getClientAnchor().getRow1() &&
                currentRow.getLastCellNum() == picture.getClientAnchor().getCol1();
    }

    private Car generateCar(List<Object> values) {
        return Car.builder()
                .make((String) values.get(CarFields.MAKE.ordinal()))
                .model((String) values.get(CarFields.MODEL.ordinal()))
                .bodyType(BodyType.valueOf(((String) values.get(CarFields.BODY_TYPE.ordinal())).toUpperCase()))
                .yearOfProduction(Integer.parseInt((String) values.get(CarFields.YEAR_OF_PRODUCTION.ordinal())))
                .color((String) values.get(CarFields.COLOR.ordinal()))
                .mileage(Integer.parseInt((String) values.get(CarFields.MILEAGE.ordinal())))
                .carStatus(CarStatus.valueOf(((String) values.get(CarFields.CAR_STATUS.ordinal())).toUpperCase()))
                .amount(new BigDecimal((String) values.get(CarFields.AMOUNT.ordinal())))
                .initialRentalOffice(rentalOfficeService.findEntityById(Long.valueOf((String) values.get(CarFields.INITIAL_RENTAL_OFFICE_ID.ordinal()))))
                .actualRentalOffice(rentalOfficeService.findEntityById(Long.valueOf((String) values.get(CarFields.ACTUAL_RENTAL_OFFICE_ID.ordinal()))))
                .image(getImageData((PictureData) values.get(CarFields.IMAGE.ordinal())))
                .build();
    }

    private byte[] getImageData(PictureData pictureData) {
        return ObjectUtils.isEmpty(pictureData) ? null : pictureData.getData();
    }

}
