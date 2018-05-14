package org.openmrs.module.operationtheater.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentAttributeTypeService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({ Context.class, SurgicalAppointmentResource.class })
@RunWith(PowerMockRunner.class)
public class SurgicalAppointmentAttributeTypeResourceTest {
	
	@Mock
	SurgicalAppointmentAttributeTypeService surgicalAppointmentAttributeTypeService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockStatic(Context.class);
		PowerMockito.when(Context.getService(SurgicalAppointmentAttributeTypeService.class))
		        .thenReturn(surgicalAppointmentAttributeTypeService);
	}
	
	@Test
	public void shouldSaveTheValidSurgicalAppointment() throws Exception {
		SurgicalAppointmentAttributeTypeResource surgicalAppointmentAttributeTypeResource = new SurgicalAppointmentAttributeTypeResource();
		surgicalAppointmentAttributeTypeResource.doGetAll(any(RequestContext.class));
		verify(surgicalAppointmentAttributeTypeService, times(1)).getAllAttributeTypes();
	}
}
