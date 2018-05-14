package org.openmrs.module.operationtheater.api.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.dao.SurgicalAppointmentAttributeTypeDAO;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttributeType;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class SurgicalAppointmentAttributeTypeServiceImplTest {
	
	@Mock
	SurgicalAppointmentAttributeTypeDAO surgicalAppointmentAttributeTypeDAO;
	
	@InjectMocks
	SurgicalAppointmentAttributeTypeServiceImpl surgicalAppointmentAttributeTypeService;
	
	@Before
	public void run() {
		initMocks(this);
	}
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void shouldGetAllSurgicalAppointmentAttributeTypes() throws ParseException {
		List<SurgicalAppointmentAttributeType> surgicalAppointmentAttributeTypes = new ArrayList<>();
		SurgicalAppointmentAttributeType procedureAttributeType = new SurgicalAppointmentAttributeType();
		procedureAttributeType.setId(1);
		procedureAttributeType.setName("Procedure");
		surgicalAppointmentAttributeTypes.add(procedureAttributeType);
		when(surgicalAppointmentAttributeTypeDAO.getAllAttributeTypes()).thenReturn(surgicalAppointmentAttributeTypes);
		
		surgicalAppointmentAttributeTypeService.getAllAttributeTypes();
		
		verify(surgicalAppointmentAttributeTypeDAO, times(1)).getAllAttributeTypes();
	}
	
	@Test
	public void shouldGetTheSurgicalAppointmentAttributeTypeByUsingGivenUuid() throws ParseException {
		String uuid = "0f1f7d08-076b-4fc6-acac-4bb915151sdd";
		SurgicalAppointmentAttributeType procedureAttributeType = new SurgicalAppointmentAttributeType();
		procedureAttributeType.setId(1);
		procedureAttributeType.setName("Procedure");
		when(surgicalAppointmentAttributeTypeDAO.getSurgicalAppointmentAttributeTypeByUuid(uuid))
		        .thenReturn(procedureAttributeType);
		
		surgicalAppointmentAttributeTypeService.getSurgicalAppointmentAttributeTypeByUuid(uuid);
		
		verify(surgicalAppointmentAttributeTypeDAO, times(1)).getSurgicalAppointmentAttributeTypeByUuid(uuid);
	}
}
