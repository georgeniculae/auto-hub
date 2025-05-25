package com.autohub.agency.service;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.Car;
import com.autohub.agency.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcelParserServiceTest {

    @InjectMocks
    private ExcelParserService excelParserService;

    @Mock
    private BranchService branchService;

    @Test
    void extractDataFromExcelTest() throws IOException {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        File excelFile = new File("src/test/resources/file/Cars.xlsx");
        InputStream stream = new FileInputStream(excelFile);

        MockMultipartFile file =
                new MockMultipartFile("file", excelFile.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);

        List<Car> cars = excelParserService.extractDataFromExcel(file);
        assertFalse(cars.isEmpty());
    }

}
