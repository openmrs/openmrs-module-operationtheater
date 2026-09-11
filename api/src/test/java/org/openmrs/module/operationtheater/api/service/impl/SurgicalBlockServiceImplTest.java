package org.openmrs.module.operationtheater.api.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
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

import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class })
public class SurgicalBlockServiceImplTest {
	
	@Mock
	SurgicalBlockDAO surgicalBlockDAO;
	
	@Mock
	AdministrationService adminService;
	
	@Mock
	EncounterService encounterService;
	
	@Mock
	OrderService orderService;
	
	@Mock
	ConceptService conceptService;
	
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
	
	@Test
	public void shouldCreateEncounterAndOrderForNewAppointment() throws ParseException {
		SurgicalBlock block = buildValidBlock();
		SurgicalAppointment newAppointment = buildNewAppointment(block);
		block.setSurgicalAppointments(Collections.singleton(newAppointment));
		setupOrderCreationMocks();
		Encounter savedEncounter = new Encounter();
		Order savedOrder = new Order();
		doReturn(savedEncounter).when(encounterService).saveEncounter(any(Encounter.class));
		doReturn(savedOrder).when(orderService).saveOrder(any(Order.class), eq(null));
		doReturn(block).when(surgicalBlockDAO).save(block);
		
		surgicalBlockService.save(block);
		
		ArgumentCaptor<Encounter> encounterCaptor = ArgumentCaptor.forClass(Encounter.class);
		verify(encounterService).saveEncounter(encounterCaptor.capture());
		Encounter capturedEncounter = encounterCaptor.getValue();
		assertEquals(newAppointment.getPatient(), capturedEncounter.getPatient());
		assertNotNull(capturedEncounter.getEncounterType());
		assertNotNull(capturedEncounter.getEncounterDatetime());
		assertNull(capturedEncounter.getVisit());
		
		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		verify(orderService).saveOrder(orderCaptor.capture(), eq(null));
		Order capturedOrder = orderCaptor.getValue();
		assertEquals(newAppointment.getPatient(), capturedOrder.getPatient());
		assertEquals(savedEncounter, capturedOrder.getEncounter());
		assertNotNull(capturedOrder.getOrderType());
		assertNotNull(capturedOrder.getConcept());
		assertNotNull(capturedOrder.getCareSetting());
		assertEquals(block.getProvider(), capturedOrder.getOrderer());
		assertNotNull(capturedOrder.getDateActivated());
		assertEquals(savedOrder, newAppointment.getOrder());
	}
	
	@Test
	public void shouldSkipOrderCreationForExistingAppointment() throws ParseException {
		SurgicalBlock block = buildValidBlock();
		SurgicalAppointment existingAppointment = buildNewAppointment(block);
		existingAppointment.setId(99); // already persisted
		block.setSurgicalAppointments(Collections.singleton(existingAppointment));
		when(surgicalBlockDAO.save(block)).thenReturn(block);
		
		surgicalBlockService.save(block);
		
		verify(encounterService, never()).saveEncounter(any(Encounter.class));
		verify(orderService, never()).saveOrder(any(Order.class), any());
		assertNull(existingAppointment.getOrder());
	}
	
	@Test
	public void shouldSkipOrderCreationForVoidedNewAppointment() throws ParseException {
		SurgicalBlock block = buildValidBlock();
		SurgicalAppointment voidedAppointment = buildNewAppointment(block);
		voidedAppointment.setVoided(true);
		block.setSurgicalAppointments(Collections.singleton(voidedAppointment));
		when(surgicalBlockDAO.save(block)).thenReturn(block);
		
		surgicalBlockService.save(block);
		
		verify(encounterService, never()).saveEncounter(any(Encounter.class));
		verify(orderService, never()).saveOrder(any(Order.class), any());
	}
	
	@Test
	public void shouldNotSetOrderWhenGPNotConfigured() throws ParseException {
		SurgicalBlock block = buildValidBlock();
		SurgicalAppointment newAppointment = buildNewAppointment(block);
		block.setSurgicalAppointments(Collections.singleton(newAppointment));
		org.powermock.api.mockito.PowerMockito.mockStatic(Context.class);
		doReturn("").when(adminService).getGlobalProperty(SurgicalBlockServiceImpl.SURGERY_SCHEDULING_ENCOUNTER_TYPE_GP, "");
		doReturn(block).when(surgicalBlockDAO).save(block);
		
		surgicalBlockService.save(block);
		
		verify(encounterService, never()).saveEncounter(any(Encounter.class));
		assertNull(newAppointment.getOrder());
	}
	
	@Test
	public void shouldPropagateExceptionWhenOrderSaveFails() throws ParseException {
		SurgicalBlock block = buildValidBlock();
		SurgicalAppointment newAppointment = buildNewAppointment(block);
		block.setSurgicalAppointments(Collections.singleton(newAppointment));
		setupOrderCreationMocks();
		Encounter savedEncounter = new Encounter();
		when(encounterService.saveEncounter(any(Encounter.class))).thenReturn(savedEncounter);
		when(orderService.saveOrder(any(Order.class), eq(null))).thenThrow(new RuntimeException("Order save failed"));
		
		exception.expect(RuntimeException.class);
		exception.expectMessage("Order save failed");
		surgicalBlockService.save(block);
	}
	
	private SurgicalBlock buildValidBlock() throws ParseException {
		SurgicalBlock block = new SurgicalBlock();
		block.setStartDatetime(simpleDateFormat.parse("2017-04-25 13:45:00"));
		block.setEndDatetime(simpleDateFormat.parse("2017-04-25 14:45:00"));
		block.setLocation(new Location(1));
		Provider provider = new Provider(1);
		block.setProvider(provider);
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(any(), any(), any(), eq(null), any()))
		        .thenReturn(new ArrayList<>());
		when(surgicalBlockDAO.getOverlappingSurgicalBlocksFor(any(), any(), eq(null), any(), any()))
		        .thenReturn(new ArrayList<>());
		when(surgicalBlockDAO.getOverlappingSurgicalAppointmentsForPatient(any(), any(), any(), any()))
		        .thenReturn(new ArrayList<>());
		return block;
	}
	
	private SurgicalAppointment buildNewAppointment(SurgicalBlock block) {
		SurgicalAppointment appointment = new SurgicalAppointment();
		appointment.setPatient(new Patient(1));
		appointment.setSurgicalBlock(block);
		return appointment; // getId() == null → new appointment
	}
	
	private void setupOrderCreationMocks() {
		org.powermock.api.mockito.PowerMockito.mockStatic(Context.class);
		org.powermock.api.mockito.PowerMockito.when(Context.getEncounterService()).thenReturn(encounterService);
		doReturn("encounter-type-uuid").when(adminService)
		        .getGlobalProperty(SurgicalBlockServiceImpl.SURGERY_SCHEDULING_ENCOUNTER_TYPE_GP, "");
		doReturn("order-type-uuid").when(adminService).getGlobalProperty(SurgicalBlockServiceImpl.SURGERY_ORDER_TYPE_UUID_GP,
		    "");
		doReturn("concept-uuid").when(adminService)
		        .getGlobalProperty(SurgicalBlockServiceImpl.SURGICAL_ORDER_CONCEPT_UUID_GP, "");
		doReturn(new EncounterType()).when(encounterService).getEncounterTypeByUuid("encounter-type-uuid");
		doReturn(new OrderType()).when(orderService).getOrderTypeByUuid("order-type-uuid");
		doReturn(new Concept()).when(conceptService).getConceptByUuid("concept-uuid");
		doReturn(new CareSetting()).when(orderService)
		        .getCareSettingByName(CareSetting.CareSettingType.OUTPATIENT.toString());
	}
}
