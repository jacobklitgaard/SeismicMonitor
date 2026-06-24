package org.example.seismicmonitor.controller;

import org.example.seismicmonitor.service.EarthquakeAlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EarthquakeAlertController.class)
class EarthquakeAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EarthquakeAlertService alertService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getActiveAlertsReturns200() throws Exception {
        when(alertService.getActiveAlerts()).thenReturn(List.of());

        mockMvc.perform(get("/api/alerts/active"))
                .andExpect(status().isOk());
    }
}