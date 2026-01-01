package com.autohub.agency.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.autohub.agency.service.EmployeeService;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.EmployeeRequest;
import com.autohub.dto.agency.EmployeeResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest(classes = EmployeeController.class)
@AutoConfigureMockMvc
@EnableWebMvc
//@Import(TestSecurityConfig.class)
class EmployeeControllerTest {

    private static final String PATH = "/employees";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllEmployeesTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        when(employeeService.findAllEmployees()).thenReturn(List.of(employeeResponse));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findAllEmployeesTest_forbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findEmployeeByIdTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        when(employeeService.findEmployeeById(anyLong())).thenReturn(employeeResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findEmployeeByIdTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findEmployeesByBranchIdTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        when(employeeService.findEmployeesByBranchId(anyLong())).thenReturn(List.of(employeeResponse));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/branch/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findEmployeesByBranchIdTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/branch/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findEmployeesByFilterTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        when(employeeService.findEmployeesByFilter(anyString())).thenReturn(List.of(employeeResponse));

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
    void findEmployeesByFilterTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/filter/{filter}", "filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countEmployeesTest_success() throws Exception {
        when(employeeService.countEmployees()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void countEmployeesTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void addEmployeeTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        String valueAsString = TestUtil.writeValueAsString(employeeResponse);

        when(employeeService.saveEmployee(any(EmployeeRequest.class))).thenReturn(employeeResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void addEmployeeTest_unauthorized() throws Exception {
        EmployeeRequest employeeRequest = TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);
        String valueAsString = TestUtil.writeValueAsString(employeeRequest);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(valueAsString));
    }

    @Test
    @WithAnonymousUser
    void addEmployeeTest_forbidden() throws Exception {
        EmployeeRequest employeeRequest = TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);
        String valueAsString = TestUtil.writeValueAsString(employeeRequest);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateEmployeeTest_success() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        String valueAsString = TestUtil.writeValueAsString(employeeResponse);

        when(employeeService.updateEmployee(anyLong(), any(EmployeeRequest.class))).thenReturn(employeeResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void updateEmployeeTest_unauthorized() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        String valueAsString = TestUtil.writeValueAsString(employeeResponse);

        mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(valueAsString));
    }

    @Test
    @WithAnonymousUser
    void updateEmployeeTest_forbidden() throws Exception {
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        String valueAsString = TestUtil.writeValueAsString(employeeResponse);

        mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void deleteEmployeeByIdTest_success() throws Exception {
        doNothing().when(employeeService).deleteEmployeeById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void deleteEmployeeByIdTest_forbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

}
