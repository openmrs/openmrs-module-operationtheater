package org.openmrs.module.operationtheater.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class SurgicalAppointmentAttributeTypeResourceIntegrationTest extends MainResourceControllerTest{

    @Override
    public String getURI() {
        return "surgicalAppointmentAttributeType";
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public long getAllCount() {
        return 0;
    }

    @Before
    public void init() throws Exception {
        executeDataSet("SurgicalAppointmentAttributeTypes.xml");
    }

    @Test
    public void shouldSaveTheValidSurgicalAppointment() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        SimpleObject results = deserialize(handle(request));
        List<SimpleObject> attributeTypes = results.get("results");

        assertNotNull(attributeTypes);
        LinkedHashMap<String, Object> procedureAttributeType = (LinkedHashMap<String, Object>) attributeTypes.get(0);
        assertNotNull(procedureAttributeType.get("id"));
        assertEquals("Procedure", procedureAttributeType.get("name"));
        LinkedHashMap<String, Object> nurseAttributeType = (LinkedHashMap<String, Object>) attributeTypes.get(1);
        assertNotNull(nurseAttributeType.get("id"));
        assertEquals("Nurse", nurseAttributeType.get("name"));
        LinkedHashMap<String, Object> anaesthetistAttributeType = (LinkedHashMap<String, Object>) attributeTypes.get(2);
        assertNotNull(anaesthetistAttributeType.get("id"));
        assertEquals("Anaesthetist", anaesthetistAttributeType.get("name"));
    }
}
