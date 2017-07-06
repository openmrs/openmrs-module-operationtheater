package org.openmrs.module.operationtheater.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({Context.class, SurgicalAppointmentResource.class})
@RunWith(PowerMockRunner.class)
public class SurgicalAppointmentAttributeResourceTest {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Mock
    SurgicalAppointmentService surgicalAppointmentService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockStatic(Context.class);
        PowerMockito.when(Context.getService(SurgicalAppointmentService.class)).thenReturn(surgicalAppointmentService);
    }

    @Test
    public void shouldGetTheSurgicalAppointmentAttribute() throws Exception {
        String attributeUuid = "attributeUuid";
        SurgicalAppointmentAttribute attribute = new SurgicalAppointmentAttribute();
        attribute.setUuid(attributeUuid);
        when(surgicalAppointmentService.getSurgicalAppointmentAttributeByUuid(attributeUuid)).thenReturn(attribute);
        SurgicalAppointmentAttributeResource surgicalAppointmentAttributeResource = new SurgicalAppointmentAttributeResource();
        surgicalAppointmentAttributeResource.getByUniqueId(attributeUuid);
        verify(surgicalAppointmentService, times(1)).getSurgicalAppointmentAttributeByUuid(attributeUuid);
    }
}
