package org.openmrs.module.operationtheater.api.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class SurgicalBlockServiceTest extends BaseModuleWebContextSensitiveTest {
    private String superUser;
    private String superUserPassword;
    private String normalUser;
    private String normalUserPassword;
    private String userWithoutPrivilege;
    private String userWithoutPrivilegePassword;


    @Autowired
    SurgicalBlockService surgicalBlockService;

    SimpleDateFormat simpleDateFormat;

    @Before
    public void setUp() throws Exception {
        superUser = "test-user";
        superUserPassword = "test";
        normalUser = "normal-user";
        normalUserPassword = "normal-password";
        userWithoutPrivilege = "user-without-privilege";
        userWithoutPrivilegePassword = "test";
        executeDataSet("SurgicalBlock.xml");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Test
    public void shouldPassGetSurgicalAppointmentsIfTheUserHasManageOTSchedulesPrivileges() {
        Context.authenticate(superUser, superUserPassword);
        assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
    }

    @Test
    public void shouldPassGetSurgicalAppointmentsIfTheUserHasViewOTSchedulesPrivileges() {
        Context.authenticate(normalUser, normalUserPassword);
        assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveSufficientPrivileges() {
        Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
        assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveManageOTSchedulesPrivileges() {
        Context.authenticate(normalUser, normalUserPassword);
        surgicalBlockService.save(new SurgicalBlock());
    }

    @Test
    public void shouldPassGetSurgicalBlocksWithinDateRangeIfTheUserHasManageOTSchedulesPrivileges() {
        Context.authenticate(superUser, superUserPassword);
        assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
    }

    @Test
    public void shouldPassGetSurgicalBlocksWithinDateRangeIfTheUserHasViewOTSchedulesPrivileges() {
        Context.authenticate(normalUser, normalUserPassword);
        assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetSurgicalBlocksPrivileges() {
        Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
        assertNotNull(surgicalBlockService.getSurgicalBlockWithAppointments("5580cddd-c290-66c8-8d3a-96dc33d109f1"));
    }

    @Test
    public void shouldPassGetSurgicalBlocksBetweenStartDatetimeAndEndDatetimeIfTheUserHasManageOTSchedulesPrivileges() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-25 09:00:00.0");
        Date endDatetime = simpleDateFormat.parse("2017-04-25 14:45:00");
        Context.authenticate(superUser, superUserPassword);
        assertNotNull(surgicalBlockService.getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(startDatetime, endDatetime, false));
    }

    @Test
    public void shouldPassGetSurgicalBlocksBetweenStartDatetimeAndEndDatetimeIfTheUserHasViewOTSchedulesPrivileges() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-25 09:00:00.0");
        Date endDatetime = simpleDateFormat.parse("2017-04-25 14:45:00");
        Context.authenticate(normalUser, normalUserPassword);
        assertNotNull(surgicalBlockService.getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(startDatetime, endDatetime, false));    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetSurgicalBlocksBetweenStartDatetimeAndEndDatetimePrivileges() throws ParseException {
        Date startDatetime = simpleDateFormat.parse("2017-04-25 09:00:00.0");
        Date endDatetime = simpleDateFormat.parse("2017-04-25 14:45:00");
        Context.authenticate(userWithoutPrivilege, userWithoutPrivilegePassword);
        assertNotNull(surgicalBlockService.getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(startDatetime, endDatetime, false));    }

}
