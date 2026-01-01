package com.autohub.agency.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.autohub.agency.service.CarService;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.CarRequest;
import com.autohub.dto.agency.CarResponse;
import com.autohub.dto.common.AvailableCarInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest(classes = CarController.class)
@AutoConfigureMockMvc
@EnableWebMvc
//@Import(TestSecurityConfig.class)
class CarControllerTest {

    private static final String PATH = "/cars";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllCarsTest_success() throws Exception {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findAllCars()).thenReturn(List.of(carResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findAllCarsTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findCarByIdTest_success() throws Exception {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarById(anyLong())).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findCarsByFilterTest_success() throws Exception {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarsByFilter(anyString())).thenReturn(List.of(carResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/filter/{filter}", "filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findCarsByFilterTest_unauthorized() throws Exception {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarsByFilter(anyString())).thenReturn(List.of(carResponse));

        mockMvc.perform(get(PATH + "/filter/{filter}", "filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void findCarByIdTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findCarsByMakeTest_success() throws Exception {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarsByMake(anyString())).thenReturn(List.of(carResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/make/{make}", "Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findCarsByMakeTest_unauthorized() throws Exception {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findCarsByMake(anyString())).thenReturn(List.of(carResponse));

        mockMvc.perform(get(PATH + "/make/{make}", "Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAvailableCarTest_success() throws Exception {
        AvailableCarInfo carResponse =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        when(carService.findAvailableCar(anyLong())).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{id}/availability", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllAvailableCarsTest_success() throws Exception {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findAllAvailableCars()).thenReturn(List.of(carResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/available", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findAllAvailableCarsTest_unauthorized() throws Exception {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carService.findAllAvailableCars()).thenReturn(List.of(carResponse));

        mockMvc.perform(get(PATH + "/available", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
    }

    @Test
    @WithAnonymousUser
    void findAvailableCarTest_unauthorized() throws Exception {
        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        when(carService.findAvailableCar(anyLong())).thenReturn(availableCarInfo);

        mockMvc.perform(get(PATH + "/{id}/availability", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findCarImageTest_success() throws Exception {
        when(carService.getCarImage(anyLong())).thenReturn(new byte[]{});

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findCarImageTest_unauthorized() throws Exception {
        when(carService.getCarImage(anyLong())).thenReturn(new byte[]{});

        mockMvc.perform(get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countCarsTest_success() throws Exception {
        when(carService.countCars()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void countCarsTest_unauthorized() throws Exception {
        when(carService.countCars()).thenReturn(1L);

        mockMvc.perform(get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void addCarTest_success() throws Exception {
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        File carImage = new File("src/test/resources/image/car.jpg");
        InputStream stream = new FileInputStream(carImage);

        MockMultipartFile image =
                new MockMultipartFile("image", carImage.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        MockMultipartFile carRequestMockPart =
                new MockMultipartFile("carRequest", "carRequest", MediaType.APPLICATION_JSON_VALUE, TestUtil.writeValueAsString(carRequest).getBytes(StandardCharsets.UTF_8));

        when(carService.saveCar(any(CarRequest.class), any(MultipartFile.class))).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(multipart(HttpMethod.POST, PATH)
                        .file(image)
                        .file(carRequestMockPart)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void addCarTest_unauthorized() throws Exception {
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        File carImage = new File("src/test/resources/image/car.jpg");
        InputStream stream = new FileInputStream(carImage);

        MockMultipartFile image =
                new MockMultipartFile("image", carImage.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        MockMultipartFile carRequestMockPart =
                new MockMultipartFile("carRequest", "carRequest", MediaType.APPLICATION_JSON_VALUE, TestUtil.writeValueAsString(carRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart(HttpMethod.POST, PATH)
                        .file(image)
                        .file(carRequestMockPart)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void addCarTest_forbidden() throws Exception {
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        File carImage = new File("src/test/resources/image/car.jpg");
        InputStream stream = new FileInputStream(carImage);

        MockMultipartFile image =
                new MockMultipartFile("image", carImage.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        MockMultipartFile carRequestMockPart =
                new MockMultipartFile("carRequest", "carRequest", MediaType.APPLICATION_JSON_VALUE, TestUtil.writeValueAsString(carRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart(HttpMethod.POST, PATH)
                        .file(image)
                        .file(carRequestMockPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void uploadCarsTest_success() throws Exception {
        File excelFile = new File("src/test/resources/file/Cars.xlsx");

        InputStream stream = new FileInputStream(excelFile);

        MockMultipartFile file =
                new MockMultipartFile("file", excelFile.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carResponses = List.of(carResponse);

        when(carService.uploadCars(any(MultipartFile.class))).thenReturn(carResponses);

        MockHttpServletResponse response = mockMvc.perform(multipart(HttpMethod.POST, PATH + "/upload")
                        .file(file)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void uploadCarsTest_unauthorized() throws Exception {
        File excelFile = new File("src/test/resources/file/Cars.xlsx");

        InputStream stream = new FileInputStream(excelFile);

        MockMultipartFile file =
                new MockMultipartFile("file", excelFile.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);
        mockMvc.perform(multipart(HttpMethod.POST, PATH + "/upload")
                        .file(file)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateCarTest_success() throws Exception {
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        File carImage = new File("src/test/resources/image/car.jpg");
        InputStream stream = new FileInputStream(carImage);

        MockMultipartFile image =
                new MockMultipartFile("image", carImage.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        MockMultipartFile carRequestMockPart =
                new MockMultipartFile("carRequest", "carRequest", MediaType.APPLICATION_JSON_VALUE, TestUtil.writeValueAsString(carRequest).getBytes(StandardCharsets.UTF_8));

        when(carService.updateCar(anyLong(), any(CarRequest.class), any(MultipartFile.class))).thenReturn(carResponse);

        MockHttpServletResponse response = mockMvc.perform(multipart(HttpMethod.PUT, PATH + "/{id}", 1L)
                        .file(image)
                        .file(carRequestMockPart)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void updateCarTest_unauthorized() throws Exception {
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        File carImage = new File("src/test/resources/image/car.jpg");
        InputStream stream = new FileInputStream(carImage);

        MockMultipartFile image =
                new MockMultipartFile("image", carImage.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        MockMultipartFile carRequestMockPart =
                new MockMultipartFile("carRequest", "carRequest", MediaType.APPLICATION_JSON_VALUE, TestUtil.writeValueAsString(carRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart(HttpMethod.PUT, PATH + "/{id}", 1L)
                        .file(image)
                        .file(carRequestMockPart)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void updateCarTest_forbidden() throws Exception {
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        File carImage = new File("src/test/resources/image/car.jpg");
        InputStream stream = new FileInputStream(carImage);

        MockMultipartFile image =
                new MockMultipartFile("image", carImage.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        MockMultipartFile carRequestMockPart =
                new MockMultipartFile("carRequest", "carRequest", MediaType.APPLICATION_JSON_VALUE, TestUtil.writeValueAsString(carRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart(HttpMethod.PUT, PATH + "/{id}", 1L)
                        .file(image)
                        .file(carRequestMockPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void deleteCarByIdTest_success() throws Exception {
        doNothing().when(carService).deleteCarById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(delete(PATH + "/{id}", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void deleteCarByIdTest_forbidden() throws Exception {
        mockMvc.perform(delete(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

}
