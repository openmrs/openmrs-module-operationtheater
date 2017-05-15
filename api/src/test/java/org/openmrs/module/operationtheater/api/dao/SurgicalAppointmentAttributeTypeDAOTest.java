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
        List<SurgicalAppointmentAttributeType> savedSurgicalAppointmentAttributeType = surgicalAppointmentAttributeTypeDAO.getAllAttributeTypes();

        assertNotNull(savedSurgicalAppointmentAttributeType);
        assertEquals(3, savedSurgicalAppointmentAttributeType.size());
        assertEquals("Procedure", savedSurgicalAppointmentAttributeType.get(0).getName());
        assertEquals("Nurse", savedSurgicalAppointmentAttributeType.get(1).getName());
        assertEquals("Anaesthetist", savedSurgicalAppointmentAttributeType.get(2).getName());
    }
}
