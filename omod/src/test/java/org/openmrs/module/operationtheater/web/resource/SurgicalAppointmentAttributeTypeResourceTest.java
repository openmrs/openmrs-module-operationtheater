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
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentAttributeTypeService;
import org.openmrs.module.webservices.rest.web.RequestContext;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SurgicalAppointmentAttributeTypeResourceTest {
	
	@Mock
	SurgicalAppointmentAttributeTypeService surgicalAppointmentAttributeTypeService;
	
	private MockedStatic<Context> mockedContext;
	
	@Before
	public void setUp() throws Exception {
		mockedContext = Mockito.mockStatic(Context.class);
		mockedContext.when(() -> Context.getService(SurgicalAppointmentAttributeTypeService.class))
		        .thenReturn(surgicalAppointmentAttributeTypeService);
	}
	
	@After
	public void tearDown() {
		mockedContext.close();
	}
	
	@Test
	public void shouldSaveTheValidSurgicalAppointment() throws Exception {
		SurgicalAppointmentAttributeTypeResource surgicalAppointmentAttributeTypeResource = new SurgicalAppointmentAttributeTypeResource();
		surgicalAppointmentAttributeTypeResource.doGetAll(any(RequestContext.class));
		verify(surgicalAppointmentAttributeTypeService, times(1)).getAllAttributeTypes();
	}
}
