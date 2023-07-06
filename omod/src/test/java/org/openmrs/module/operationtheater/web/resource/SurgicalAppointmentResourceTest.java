package org.openmrs.module.operationtheater.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Person;
import org.openmrs.Obs;
import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({ Context.class, SurgicalAppointmentResource.class })
@RunWith(PowerMockRunner.class)
public class SurgicalAppointmentResourceTest {
	
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Mock
	SurgicalAppointmentService surgicalAppointmentService;
	
	@Mock
	BedManagementService bedManagementService;

	@Mock
	private AdministrationService administrationService;

	@Mock
	private ConceptService conceptService;

	@Mock
	private ObsService obsService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockStatic(Context.class);
		PowerMockito.when(Context.getService(SurgicalAppointmentService.class)).thenReturn(surgicalAppointmentService);
		PowerMockito.when(Context.getService(BedManagementService.class)).thenReturn(bedManagementService);
		PowerMockito.when(Context.getAdministrationService()).thenReturn(administrationService);
		PowerMockito.when(Context.getConceptService()).thenReturn(conceptService);
		PowerMockito.when(Context.getObsService()).thenReturn(obsService);
	}
	
	@Test
	public void shouldSaveTheValidSurgicalAppointment() throws Exception {
		SurgicalBlock surgicalBlock = new SurgicalBlock();
		Date startDatetime = simpleDateFormat.parse("2017-04-24 10:00:00");
		Date endDatetime = simpleDateFormat.parse("2017-04-24 18:00:00");
		Location location = new Location(1);
		Provider provider = new Provider(1);
		surgicalBlock.setStartDatetime(startDatetime);
		surgicalBlock.setEndDatetime(endDatetime);
		surgicalBlock.setLocation(location);
		surgicalBlock.setProvider(provider);
		
		SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
		surgicalAppointment.setActualStartDatetime(simpleDateFormat.parse("2017-04-24 10:00:00"));
		surgicalAppointment.setActualEndDatetime(simpleDateFormat.parse("2017-04-24 12:00:00"));
		surgicalAppointment.setPatient(new Patient());
		surgicalAppointment.setId(1);
		
		SurgicalAppointmentResource surgicalAppointmentResource = new SurgicalAppointmentResource();
		surgicalAppointmentService.save(surgicalAppointment);
		verify(surgicalAppointmentService, times(1)).save(surgicalAppointment);
	}
	
	@Test
	public void shouldGetTheSurgicalAppointment() throws Exception {
		String surgicalAppointmentUuid = "surgicalAppointmentUuid";
		SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
		surgicalAppointment.setUuid(surgicalAppointmentUuid);
		when(surgicalAppointmentService.getSurgicalAppointmentByUuid(surgicalAppointmentUuid))
		        .thenReturn(surgicalAppointment);
		SurgicalAppointmentResource surgicalAppointmentResource = new SurgicalAppointmentResource();
		surgicalAppointmentResource.getByUniqueId(surgicalAppointmentUuid);
		verify(surgicalAppointmentService, times(1)).getSurgicalAppointmentByUuid(surgicalAppointmentUuid);
	}
	
	@Test
	public void shouldGetBedNumberForPatient() {
		SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
		Patient patient = new Patient();
		surgicalAppointment.setPatient(patient);
		patient.setUuid("uuid");
		BedDetails bedDetails = new BedDetails();
		bedDetails.setBedNumber("123");
		when(bedManagementService.getBedAssignmentDetailsByPatient(patient)).thenReturn(bedDetails);
		
		String bedNumber = SurgicalAppointmentResource.getBedNumber(surgicalAppointment);
		
		assertEquals("123", bedNumber);
		verify(bedManagementService, times(1)).getBedAssignmentDetailsByPatient(patient);
	}
	
	@Test
	public void shouldGetBedLocationForPatient() {
		SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
		Patient patient = new Patient();
		surgicalAppointment.setPatient(patient);
		patient.setUuid("uuid");
		BedDetails bedDetails = new BedDetails();
		Location location = new Location();
		location.setName("Ward");
		bedDetails.setPhysicalLocation(location);
		when(bedManagementService.getBedAssignmentDetailsByPatient(patient)).thenReturn(bedDetails);
		
		String bedLocation = SurgicalAppointmentResource.getBedLocation(surgicalAppointment);
		
		assertEquals("Ward", bedLocation);
		verify(bedManagementService, times(1)).getBedAssignmentDetailsByPatient(patient);
	}

	@Test
	public void shouldGetObservationsForPatient() {
		SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
		Person person = new Person();
		person.setPersonId(1);
		Patient patient = new Patient();
		patient.setUuid("uuid");
		patient.setPersonId(1);
		surgicalAppointment.setPatient(patient);

		Concept concept = new Concept();
		concept.setUuid("conceptuuid");
		Obs obs = new Obs();
		obs.setConcept(concept);
		obs.setPerson(person);
		obs.setObsDatetime(new Date());
		List<Obs> expectedObsList = new ArrayList<>();
		expectedObsList.add(obs);

		when(administrationService.getGlobalProperty("obs.conceptMappingsForOT")).thenReturn("emrsource:observation");
		when(conceptService.getConceptByMapping("observation", "emrsource", false)).thenReturn(concept);
		when(obsService.getObservationsByPersonAndConcept(person, concept)).thenReturn(expectedObsList);

		List<Obs> actualObsList = SurgicalAppointmentResource.getPatientObservations(surgicalAppointment);

		assertEquals(expectedObsList, actualObsList);
		verify(Context.getObsService(), times(1)).getObservationsByPersonAndConcept(person, concept);
	}
}
