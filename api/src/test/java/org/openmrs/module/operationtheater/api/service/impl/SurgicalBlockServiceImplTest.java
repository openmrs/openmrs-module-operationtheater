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

import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class SurgicalBlockServiceImplTest {
	
	@Mock
	SurgicalBlockDAO surgicalBlockDAO;
	
	@InjectMocks
	SurgicalBlockServiceImpl surgicalBlockService;
	
	@Mock
	SurgicalAppointmentServiceImpl surgicalAppointmentService;
	
	private SimpleDateFormat simpleDateFormat;
	
	private SurgicalBlock surgicalBlock;
	
	@Before
	public void run() {
		initMocks(this);
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		surgicalBlock = new SurgicalBlock();
	}
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void shouldThrowExceptionWhenSurgicalBlockStartDatetimeIsAfterEndDatetime() throws ParseException {
		surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
		surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 11:45:00"));
		
		exception.expect(ValidationException.class);
		exception.expectMessage("Surgical Block start date after end date");
		surgicalBlockService.save(surgicalBlock);
	}
	
	@Test
	public void shouldThrowExceptionWhenTheNewSurgicalBlockOverlapsWithExistingOnesAtALocation() throws ParseException {
		Location location = new Location(1);
		surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-24 13:45:00"));
		surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-24 14:45:00"));
		surgicalBlock.setLocation(location);
		
		ArrayList<SurgicalBlock> surgicalBlocks = new ArrayList<SurgicalBlock>();
		surgicalBlocks.add(surgicalBlock);
		
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()),
		    eq(surgicalBlock.getEndDatetime()), eq(surgicalBlock.getProvider()), eq(null), eq(null)))
		            .thenReturn(new ArrayList<>());
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()),
		    eq(surgicalBlock.getEndDatetime()), eq(null), eq(surgicalBlock.getLocation()), eq(null)))
		            .thenReturn(surgicalBlocks);
		
		exception.expect(ValidationException.class);
		exception.expectMessage("Surgical Block has conflicting time with existing block(s) for this OT");
		surgicalBlockService.save(surgicalBlock);
	}
	
	@Test
	public void shouldThrowExceptionWhenTheNewSurgicalBlockOverlapsWithExistingOnesForAProvider() throws ParseException {
		surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
		surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));
		
		ArrayList<SurgicalBlock> surgicalBlocks = new ArrayList<SurgicalBlock>();
		surgicalBlocks.add(surgicalBlock);
		
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()),
		    eq(surgicalBlock.getEndDatetime()), any(), eq(null), eq(null))).thenReturn(surgicalBlocks);
		
		exception.expect(ValidationException.class);
		exception.expectMessage("Surgical Block has conflicting time with existing block(s) for this surgeon");
		surgicalBlockService.save(surgicalBlock);
	}
	
	@Test
	public void shouldReturnSurgicalBlockIfItsAlreadySaved() throws ParseException {
		surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
		surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));
		surgicalBlock.setLocation(new Location(1));
		surgicalBlock.setId(1);
		surgicalBlock.setSurgicalAppointments(new HashSet<>());
		ArrayList<SurgicalBlock> surgicalBlocks = new ArrayList<>();
		
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()),
		    eq(surgicalBlock.getEndDatetime()), eq(null), any(Location.class), eq(surgicalBlock.getId())))
		            .thenReturn(surgicalBlocks);
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()),
		    eq(surgicalBlock.getEndDatetime()), any(Provider.class), eq(null), eq(surgicalBlock.getId())))
		            .thenReturn(surgicalBlocks);
		when(surgicalBlockDAO.save(surgicalBlock)).thenReturn(surgicalBlock);
		
		surgicalBlockService.save(surgicalBlock);
		
		verify(surgicalBlockDAO, times(1)).save(surgicalBlock);
	}
	
	@Test
	public void shouldSaveAValidNewSurgicalBlock() throws ParseException {
		surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
		surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));
		surgicalBlock.setLocation(new Location(1));
		surgicalBlock.setSurgicalAppointments(new HashSet<>());
		ArrayList<SurgicalBlock> surgicalBlocks = new ArrayList<>();
		
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()),
		    eq(surgicalBlock.getEndDatetime()), eq(null), any(Location.class), eq(null))).thenReturn(surgicalBlocks);
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()),
		    eq(surgicalBlock.getEndDatetime()), any(Provider.class), eq(null), eq(null))).thenReturn(surgicalBlocks);
		when(surgicalBlockDAO.save(surgicalBlock)).thenReturn(surgicalBlock);
		
		surgicalBlockService.save(surgicalBlock);
		
		verify(surgicalBlockDAO, times(1)).save(surgicalBlock);
	}
	
	@Test
	public void shouldCheckForOverlappingSurgicalAppointmentsForThePatient() throws ParseException {
		surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
		surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));
		
		Provider provider = new Provider(1);
		Set<PersonName> personNamesForProvider = new LinkedHashSet<>();
		personNamesForProvider.add(new PersonName("Dr.", "Tony", "Stark"));
		Person person = new Person(1);
		person.setNames(personNamesForProvider);
		provider.setPerson(person);
		provider.setName("Tony Stark");
		surgicalBlock.setProvider(provider);
		
		Location location = new Location(1);
		location.setName("Stark Labs");
		surgicalBlock.setLocation(location);
		
		Set<SurgicalAppointment> surgicalAppointments = new HashSet<>();
		SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
		surgicalAppointment.setSurgicalBlock(surgicalBlock);
		
		Set<PersonName> personNames = new LinkedHashSet<>();
		personNames.add(new PersonName("Iron", "Returns", "Man"));
		Patient patient = new Patient(1);
		patient.setNames(personNames);
		surgicalAppointment.setPatient(patient);
		
		surgicalAppointments.add(surgicalAppointment);
		surgicalBlock.setSurgicalAppointments(surgicalAppointments);
		
		ArrayList<SurgicalBlock> overlappingSurgicalBlocks = new ArrayList<>();
		ArrayList<SurgicalAppointment> overlappingSurgicalAppointments = new ArrayList<>();
		overlappingSurgicalAppointments.add(surgicalAppointment);
		
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()),
		    eq(surgicalBlock.getEndDatetime()), eq(null), any(Location.class), eq(null)))
		            .thenReturn(overlappingSurgicalBlocks);
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(eq(surgicalBlock.getStartDatetime()),
		    eq(surgicalBlock.getEndDatetime()), any(Provider.class), eq(null), eq(null)))
		            .thenReturn(overlappingSurgicalBlocks);
		when(surgicalBlockDAO.getOverlappingSurgicalAppointmentsForPatient(eq(surgicalBlock.getStartDatetime()),
		    eq(surgicalBlock.getEndDatetime()), eq(surgicalAppointment.getPatient()), eq(surgicalBlock.getId())))
		            .thenReturn(overlappingSurgicalAppointments);
		when(surgicalBlockDAO.save(surgicalBlock)).thenReturn(surgicalBlock);
		
		exception.expect(ValidationException.class);
		exception.expectMessage("Iron Man has conflicting appointment at Stark Labs with Dr. Tony Stark");
		surgicalBlockService.save(surgicalBlock);
	}
	
	@Test
	public void shouldGetTheSurgicalBlockWithAppointmentWithGivenSurgicalBlockUuid() throws Exception {
		surgicalBlock.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
		surgicalBlock.setEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));
		
		Provider provider = new Provider(1);
		surgicalBlock.setProvider(provider);
		
		Location location = new Location(1);
		surgicalBlock.setLocation(location);
		
		Set<SurgicalAppointment> surgicalAppointments = new HashSet<>();
		SurgicalAppointment surgicalAppointment = new SurgicalAppointment();
		surgicalAppointment.setSurgicalBlock(surgicalBlock);
		Patient patient = new Patient(1);
		surgicalAppointment.setPatient(patient);
		
		surgicalAppointments.add(surgicalAppointment);
		surgicalBlock.setSurgicalAppointments(surgicalAppointments);
		
		String surgicalBlockUuid = "surgicalBlockUuid";
		when(surgicalBlockDAO.getSurgicalBlockWithAppointments(surgicalBlockUuid)).thenReturn(surgicalBlock);
		
		SurgicalBlock surgicalBlockWithAppointments = surgicalBlockService
		        .getSurgicalBlockWithAppointments(surgicalBlockUuid);
		
		verify(surgicalBlockDAO, times(1)).getSurgicalBlockWithAppointments(surgicalBlockUuid);
	}
	
	@Test
	public void shouldGetSurgicalBlocksWhichFallInTheDateRange() throws ParseException {
		Date startDatetime = simpleDateFormat.parse("2017-04-25 13:45:00");
		Date endDatetime = simpleDateFormat.parse("2017-04-25 14:45:00");
		
		when(surgicalBlockDAO.getSurgicalBlocksFor(eq(startDatetime), eq(endDatetime), eq(null), eq(null), eq(false),
		    eq(false))).thenReturn(Arrays.asList(surgicalBlock));
		
		List<SurgicalBlock> surgicalBlocks = surgicalBlockService
		        .getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(startDatetime, endDatetime, false, false);
		
		verify(surgicalBlockDAO, times(1)).getSurgicalBlocksFor(startDatetime, endDatetime, null, null, false, false);
		assertEquals(surgicalBlock, surgicalBlocks.get(0));
	}
	
	@Test
	public void shouldGetSurgicalBlocksAllSurgicalBlocksIncludingVoided() throws ParseException {
		Date startDatetime = simpleDateFormat.parse("2017-04-25 13:45:00");
		Date endDatetime = simpleDateFormat.parse("2017-04-25 14:45:00");
		
		when(surgicalBlockDAO.getSurgicalBlocksFor(eq(startDatetime), eq(endDatetime), eq(null), eq(null), eq(true),
		    eq(false))).thenReturn(Arrays.asList(surgicalBlock));
		
		List<SurgicalBlock> surgicalBlocks = surgicalBlockService
		        .getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(startDatetime, endDatetime, true, false);
		
		verify(surgicalBlockDAO, times(1)).getSurgicalBlocksFor(startDatetime, endDatetime, null, null, true, false);
		assertEquals(surgicalBlock, surgicalBlocks.get(0));
	}
	
	@Test
	public void shouldGetSurgicalBlocksAllSurgicalBlocksWhichAreAcrossMultipleDaysIfWePassActiveAsTrue()
	        throws ParseException {
		Date startDatetime = simpleDateFormat.parse("2016-04-24 10:00:00");
		Date endDatetime = simpleDateFormat.parse("2016-04-25 16:00:00");
		
		when(surgicalBlockDAO.getSurgicalBlocksFor(eq(startDatetime), eq(endDatetime), eq(null), eq(null), eq(false),
		    eq(true))).thenReturn(Arrays.asList(surgicalBlock));
		
		List<SurgicalBlock> surgicalBlocks = surgicalBlockService
		        .getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(startDatetime, endDatetime, false, true);
		
		verify(surgicalBlockDAO, times(1)).getSurgicalBlocksFor(startDatetime, endDatetime, null, null, false, true);
		assertEquals(surgicalBlock, surgicalBlocks.get(0));
	}
}
