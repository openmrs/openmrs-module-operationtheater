package org.openmrs.module.operationtheater.api.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.dao.SurgicalAppointmentDao;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class SurgicalAppointmentServiceImplTest {
	
	@Mock
	SurgicalAppointmentDao surgicalAppointmentDao;
	
	@Mock
	SurgicalBlockDAO surgicalBlockDAO;
	
	@InjectMocks
	SurgicalAppointmentServiceImpl surgicalAppointmentService;
	
	private SimpleDateFormat simpleDateFormat;
	
	private SurgicalAppointment surgicalAppointment;
	
	@Before
	public void run() {
		initMocks(this);
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		surgicalAppointment = new SurgicalAppointment();
	}
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void shouldSaveTheSurgicalAppointmet() throws ParseException {
		surgicalAppointment.setActualStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
		surgicalAppointment.setActualEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));
		Patient patient = new Patient();
		surgicalAppointment.setPatient(patient);
		surgicalAppointment.setId(1);
		SurgicalBlock surgicalBlock = new SurgicalBlock();
		surgicalBlock.setUuid("blockUuid1");
		Date startDatetime = simpleDateFormat.parse("2017-08-30 9:00:00");
		Date endDatetime = simpleDateFormat.parse("2017-08-30 11:00:00");
		surgicalBlock.setStartDatetime(startDatetime);
		surgicalBlock.setEndDatetime(endDatetime);
		surgicalBlock.setId(1);
		surgicalAppointment.setSurgicalBlock(surgicalBlock);
		
		when(surgicalAppointmentDao.save(surgicalAppointment)).thenReturn(surgicalAppointment);
		when(surgicalBlockDAO.getOverlappingSurgicalAppointmentsForPatient(any(Date.class), any(Date.class), eq(patient),
		    eq(1))).thenReturn(new ArrayList<SurgicalAppointment>());
		
		surgicalAppointmentService.save(surgicalAppointment);
		verify(surgicalAppointmentDao, times(1)).save(surgicalAppointment);
	}
	
	@Test
	public void shouldNotSaveWhenActualTimeAreOverlapping() throws ParseException {
		surgicalAppointment.setActualStartDatetime(simpleDateFormat.parse("2017-06-06 09:00:00"));
		surgicalAppointment.setActualEndDatetime(simpleDateFormat.parse("2017-06-06 10:00:00"));
		surgicalAppointment.setPatient(new Patient());
		surgicalAppointment.setId(1);
		
		when(surgicalAppointmentDao.getOverlappingActualTimeEntriesForAppointment(surgicalAppointment))
		        .thenReturn(Arrays.asList(surgicalAppointment));
		exception.expect(ValidationException.class);
		exception.expectMessage("Surgical Appointment has conflicting actual time with existing appointments in this OT");
		surgicalAppointmentService.save(surgicalAppointment);
		
	}
	
	@Test
	public void shouldGetSurgicalAppointmentByUuid() throws ParseException {
		String surgicalAppoitmentUuid = "surgicalAppointmentUuid";
		SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
		surgicalAppointment.setUuid(surgicalAppoitmentUuid);
		
		when(surgicalAppointmentDao.getSurgicalAppointmentByUuid(surgicalAppoitmentUuid)).thenReturn(surgicalAppointment);
		
		surgicalAppointmentService.getSurgicalAppointmentByUuid(surgicalAppoitmentUuid);
		verify(surgicalAppointmentDao, times(1)).getSurgicalAppointmentByUuid(surgicalAppoitmentUuid);
	}
	
	@Test
	public void shouldGetSurgicalAppointmentAttributeByUuid() throws ParseException {
		String attributeUuid = "surgicalAppointmentAttributeUuid";
		SurgicalAppointmentAttribute surgicalAppointmentAttribute = new SurgicalAppointmentAttribute();
		surgicalAppointmentAttribute.setUuid(attributeUuid);
		
		when(surgicalAppointmentDao.getSurgicalAppointmentAttributeByUuid(attributeUuid))
		        .thenReturn(surgicalAppointmentAttribute);
		
		surgicalAppointmentService.getSurgicalAppointmentAttributeByUuid(attributeUuid);
		verify(surgicalAppointmentDao, times(1)).getSurgicalAppointmentAttributeByUuid(attributeUuid);
	}
	
	@Test
	public void shouldValidateForOverlappingSurgicalAppointmnetsforthePatientBeforeUpdatingSurgicalAppointment()
	        throws Exception {
		surgicalAppointment.setUuid("appointmentUuid");
		Patient patient = new Patient();
		patient.setUuid("patientUuid");
		patient.setId(1);
		surgicalAppointment.setPatient(patient);
		SurgicalBlock surgicalBlock = new SurgicalBlock();
		surgicalBlock.setUuid("blockUuid1");
		Date startDatetime = simpleDateFormat.parse("2017-08-30 9:00:00");
		Date endDatetime = simpleDateFormat.parse("2017-08-30 11:00:00");
		surgicalBlock.setStartDatetime(startDatetime);
		surgicalBlock.setEndDatetime(endDatetime);
		surgicalBlock.setId(1);
		surgicalAppointment.setSurgicalBlock(surgicalBlock);
		
		when(surgicalBlockDAO.getOverlappingSurgicalAppointmentsForPatient(startDatetime, endDatetime, patient, 1))
		        .thenReturn(new ArrayList<SurgicalAppointment>());
		
		surgicalAppointmentService.save(surgicalAppointment);
		verify(surgicalBlockDAO, times(1)).getOverlappingSurgicalAppointmentsForPatient(surgicalBlock.getStartDatetime(),
		    surgicalBlock.getEndDatetime(), surgicalAppointment.getPatient(), surgicalBlock.getId());
	}
	
	@Test
	public void shouldThrowExceptionIfPatientishavingOverlappingSurgicalAppointmentsBeforeSaving() throws Exception {
		surgicalAppointment.setUuid("appointmentUuid");
		Set<PersonName> personNames = new LinkedHashSet<>();
		personNames.add(new PersonName("Iron", "Returns", "Man"));
		Patient patient = new Patient(1);
		patient.setNames(personNames);
		surgicalAppointment.setPatient(patient);
		SurgicalBlock surgicalBlock = new SurgicalBlock();
		surgicalBlock.setUuid("blockUuid1");
		Date startDatetime = simpleDateFormat.parse("2017-08-30 9:00:00");
		Date endDatetime = simpleDateFormat.parse("2017-08-30 11:00:00");
		surgicalBlock.setStartDatetime(startDatetime);
		surgicalBlock.setEndDatetime(endDatetime);
		surgicalBlock.setId(1);
		
		surgicalAppointment.setSurgicalBlock(surgicalBlock);
		SurgicalAppointment overlappingSurgicalAppointment = new SurgicalAppointment();
		overlappingSurgicalAppointment.setUuid("OverlapUuid");
		SurgicalBlock overlappingSurgicalBlock = new SurgicalBlock();
		overlappingSurgicalAppointment.setSurgicalBlock(overlappingSurgicalBlock);
		overlappingSurgicalAppointment.setPatient(patient);
		Provider provider = new Provider(1);
		Set<PersonName> personNamesForProvider = new LinkedHashSet<>();
		personNamesForProvider.add(new PersonName("Dr.", "Tony", "Stark"));
		Person person = new Person(1);
		person.setNames(personNamesForProvider);
		provider.setPerson(person);
		provider.setName("Tony Stark");
		overlappingSurgicalBlock.setProvider(provider);
		Location location = new Location(1);
		location.setName("Stark Labs");
		overlappingSurgicalBlock.setLocation(location);
		List<SurgicalAppointment> overlappingSurgicalAppointments = new ArrayList<>();
		overlappingSurgicalAppointments.add(overlappingSurgicalAppointment);
		
		when(surgicalBlockDAO.getOverlappingSurgicalAppointmentsForPatient(startDatetime, endDatetime, patient, 1))
		        .thenReturn(overlappingSurgicalAppointments);
		
		exception.expect(ValidationException.class);
		exception.expectMessage("Iron Man has conflicting appointment at Stark Labs with Dr. Tony Stark");
		surgicalAppointmentService.save(surgicalAppointment);
		verify(surgicalBlockDAO, times(1)).getOverlappingSurgicalAppointmentsForPatient(surgicalBlock.getStartDatetime(),
		    surgicalBlock.getEndDatetime(), surgicalAppointment.getPatient(), surgicalBlock.getId());
	}
}
