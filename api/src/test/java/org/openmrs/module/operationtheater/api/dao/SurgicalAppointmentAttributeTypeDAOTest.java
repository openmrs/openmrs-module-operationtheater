package org.openmrs.module.operationtheater.api.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttributeType;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class SurgicalAppointmentAttributeTypeDAOTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    SurgicalAppointmentAttributeTypeDAO surgicalAppointmentAttributeTypeDAO;

    @Before
    public void setUp() throws Exception {
        executeDataSet("SurgicalAppointmentAttributeTypes.xml");
    }

    @Test
    public void shouldGetAllSurgicalAppointmentAttributeTypes() throws Exception {
        List<SurgicalAppointmentAttributeType> savedSurgicalAppointmentAttributeTypes = surgicalAppointmentAttributeTypeDAO.getAllAttributeTypes();

        assertNotNull(savedSurgicalAppointmentAttributeTypes);
        assertEquals(3, savedSurgicalAppointmentAttributeTypes.size());
        assertEquals("Procedure", savedSurgicalAppointmentAttributeTypes.get(0).getName());
        assertEquals("Nurse", savedSurgicalAppointmentAttributeTypes.get(1).getName());
        assertEquals("Anaesthetist", savedSurgicalAppointmentAttributeTypes.get(2).getName());
    }

    @Test
    public void shouldGetTheSurgicalAppointmentAttributeTypeWithTheGivenUuid() throws Exception {
        String uuid = "0f1f7d08-076b-4fc6-acac-4bb915151sda";
        SurgicalAppointmentAttributeType savedAttributeType = surgicalAppointmentAttributeTypeDAO.getSurgicalAppointmentAttributeTypeByUuid(uuid);

        assertNotNull(savedAttributeType);
        assertEquals("Anaesthetist", savedAttributeType.getName());
    }
}
