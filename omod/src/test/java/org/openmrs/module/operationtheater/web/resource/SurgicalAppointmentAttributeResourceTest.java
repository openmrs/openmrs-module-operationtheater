package org.openmrs.module.operationtheater.web.resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;

import java.text.SimpleDateFormat;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SurgicalAppointmentAttributeResourceTest {
	
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Mock
	SurgicalAppointmentService surgicalAppointmentService;
	
	private MockedStatic<Context> mockedContext;
	
	@Before
	public void setUp() throws Exception {
		mockedContext = Mockito.mockStatic(Context.class);
		mockedContext.when(() -> Context.getService(SurgicalAppointmentService.class))
		        .thenReturn(surgicalAppointmentService);
	}
	
	@After
	public void tearDown() {
		mockedContext.close();
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
