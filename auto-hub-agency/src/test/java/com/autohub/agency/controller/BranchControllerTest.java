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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.autohub.agency.service.BranchService;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.BranchRequest;
import com.autohub.dto.agency.BranchResponse;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest(classes = BranchController.class)
@AutoConfigureMockMvc
@EnableWebMvc
//@Import(TestSecurityConfig.class)
class BranchControllerTest {

    private static final String PATH = "/branches";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BranchService branchService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllBranchesTest_success() throws Exception {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        when(branchService.findAllBranches()).thenReturn(List.of(branchResponse));

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
    void findAllBranchesTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findBranchByIdTest_success() throws Exception {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        when(branchService.findBranchById(anyLong())).thenReturn(branchResponse);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser()
    void findBranchByIdTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findBranchesByFilterTest_success() throws Exception {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        when(branchService.findBranchesByFilter(anyString())).thenReturn(List.of(branchResponse));

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
    void findBranchesByFilterTest_unauthorized() throws Exception {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        when(branchService.findBranchesByFilter(anyString())).thenReturn(List.of(branchResponse));

        mockMvc.perform(get(PATH + "/filter/{filter}", "filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countBranchesTest_success() throws Exception {
        when(branchService.countBranches()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void countBranchesTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void addBranchTest_success() throws Exception {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        String content = TestUtil.writeValueAsString(branchResponse);

        when(branchService.saveBranch(any(BranchRequest.class))).thenReturn(branchResponse);

        MockHttpServletResponse response = mockMvc.perform(post(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void addBranchTest_unauthorized() throws Exception {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        String content = TestUtil.writeValueAsString(branchResponse);

        mockMvc.perform(post(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void addBranchTest_forbidden() throws Exception {
        BranchRequest branchRequest = TestUtil.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);
        String content = TestUtil.writeValueAsString(branchRequest);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateBranchTest_success() throws Exception {
        BranchResponse branchRequest =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        String content = TestUtil.writeValueAsString(branchRequest);

        when(branchService.updateBranch(anyLong(), any(BranchRequest.class))).thenReturn(branchRequest);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void updateBranchTest_forbidden() throws Exception {
        BranchRequest branchRequest = TestUtil.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);
        String content = TestUtil.writeValueAsString(branchRequest);

        mockMvc.perform(put(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void deleteBranchByIdTest_success() throws Exception {
        doNothing().when(branchService).deleteBranchById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(delete(PATH + "/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void deleteBranchByIdTest_forbidden() throws Exception {
        mockMvc.perform(delete(PATH + "/{id}", 1L))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

}
