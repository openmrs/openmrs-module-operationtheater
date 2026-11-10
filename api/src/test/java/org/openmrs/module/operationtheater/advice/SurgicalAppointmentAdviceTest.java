package org.openmrs.module.operationtheater.advice;

import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SurgicalAppointmentAdviceTest {
	
	private static final String URL_PATTERN = "atomfeed.event.urlPatternForSurgicalAppointment";
	
	private static final String EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE = "atomfeed.publish.eventsForSurgicalAppointmentChange";
	
	private static final String UUID = "5631b434-78aa-102b-91a0-001e378eb17e";
	
	private static final String DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN = "/openmrs/ws/rest/v1/surgicalAppointment/{uuid}?v=full";
	
	@Mock
	private SurgicalAppointment surgicalAppointment;
	
	@Mock
	private PlatformTransactionManager transactionManager;
	
	@Mock
	private AdministrationService administrationService;
	
	private MockedStatic<Context> mockedContext;
	
	private MockedConstruction<AtomFeedSpringTransactionManager> mockedTxManager;
	
	private MockedConstruction<AllEventRecordsQueueJdbcImpl> mockedAllEventRecordsQueue;
	
	private MockedConstruction<EventServiceImpl> mockedEventService;
	
	private MockedConstruction<Event> mockedEvent;
	
	private SurgicalAppointmentAdvice surgicalAppointmentAdvice;
	
	private AtomFeedSpringTransactionManager atomFeedSpringTransactionManager;
	
	private EventServiceImpl eventService;
	
	private List<List<?>> capturedEventConstructorArgs;
	
	@Before
	public void setUp() throws Exception {
		capturedEventConstructorArgs = new ArrayList<>();
		
		mockedContext = Mockito.mockStatic(Context.class);
		mockedContext.when(() -> Context.getRegisteredComponents(any()))
		        .thenReturn(Collections.singletonList(transactionManager));
		mockedContext.when(Context::getAdministrationService).thenReturn(administrationService);
		
		mockedTxManager = Mockito.mockConstruction(AtomFeedSpringTransactionManager.class, (mock, ctx) -> {
			Mockito.doAnswer(invocation -> {
				AFTransactionWorkWithoutResult work = (AFTransactionWorkWithoutResult) invocation.getArgument(0);
				work.execute();
				return null;
			}).when(mock).executeWithTransaction(any());
		});
		
		mockedAllEventRecordsQueue = Mockito.mockConstruction(AllEventRecordsQueueJdbcImpl.class);
		
		mockedEventService = Mockito.mockConstruction(EventServiceImpl.class, (mock, ctx) -> {
			doNothing().when(mock).notify(any());
		});
		
		mockedEvent = Mockito.mockConstruction(Event.class, (mock, ctx) -> {
			capturedEventConstructorArgs.add(ctx.arguments());
		});
		
		when(surgicalAppointment.getUuid()).thenReturn(UUID);
		when(administrationService.getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE)).thenReturn("true");
		when(administrationService.getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN))
		        .thenReturn(DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
		
		surgicalAppointmentAdvice = new SurgicalAppointmentAdvice();
		
		atomFeedSpringTransactionManager = mockedTxManager.constructed().get(0);
		eventService = mockedEventService.constructed().get(0);
	}
	
	@After
	public void tearDown() {
		mockedContext.close();
		mockedTxManager.close();
		mockedAllEventRecordsQueue.close();
		mockedEventService.close();
		mockedEvent.close();
	}
	
	@Test
	public void shouldRaiseSurgicalAppointmentChangeEventToEventRecordsTable() throws Throwable {
		surgicalAppointmentAdvice.afterReturning(surgicalAppointment, this.getClass().getMethod("save"), null, null);
		
		verify(atomFeedSpringTransactionManager, times(1)).executeWithTransaction(any(AFTransactionWorkWithoutResult.class));
		verify(administrationService, times(1)).getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE);
		verify(administrationService, times(1)).getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
		verify(eventService, times(1)).notify(any());
		
		assertEquals(1, capturedEventConstructorArgs.size());
		List<?> args = capturedEventConstructorArgs.get(0);
		assertNotNull(args.get(0));
		assertEquals("Surgical Appointment", args.get(1));
		assertNotNull(args.get(2));
		assertNull(args.get(3));
		assertEquals(String.format("/openmrs/ws/rest/v1/surgicalAppointment/%s?v=full", UUID), args.get(4));
		assertEquals("surgicalappointment", args.get(5));
	}
	
	@Test
	public void shouldRaiseSurgicalAppointmentChangeEventToEventRecordsTableWithCustomUrlPattern() throws Throwable {
		when(administrationService.getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN))
		        .thenReturn("/openmrs/ws/{uuid}");
		
		surgicalAppointmentAdvice.afterReturning(surgicalAppointment, this.getClass().getMethod("save"), null, null);
		
		verify(atomFeedSpringTransactionManager, times(1)).executeWithTransaction(any(AFTransactionWorkWithoutResult.class));
		verify(administrationService, times(1)).getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE);
		verify(administrationService, times(1)).getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
		verify(eventService, times(1)).notify(any());
		
		assertEquals(1, capturedEventConstructorArgs.size());
		List<?> args = capturedEventConstructorArgs.get(0);
		assertNotNull(args.get(0));
		assertEquals("Surgical Appointment", args.get(1));
		assertNotNull(args.get(2));
		assertNull(args.get(3));
		assertEquals(String.format("/openmrs/ws/%s", UUID), args.get(4));
		assertEquals("surgicalappointment", args.get(5));
	}
	
	@Test
	public void shouldNotRaiseEventToEventRecordTableIfGlobalPropertyIsDisabled() throws Throwable {
		when(administrationService.getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE)).thenReturn("false");
		
		surgicalAppointmentAdvice.afterReturning(surgicalAppointment, this.getClass().getMethod("save"), null, null);
		
		verify(atomFeedSpringTransactionManager, never()).executeWithTransaction(any());
		verify(administrationService, times(1)).getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE);
		verify(administrationService, never()).getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
	}
	
	@Test
	public void shouldNotRaiseEventToEventRecordTableIfMethodNameIsNotSave() throws Throwable {
		surgicalAppointmentAdvice.afterReturning(surgicalAppointment, this.getClass().getMethod("temp"), null, null);
		
		verify(atomFeedSpringTransactionManager, never()).executeWithTransaction(any());
		verify(administrationService, times(1)).getGlobalProperty(EVENTS_FOR_SURGICAL_APPOINTMENT_CHANGE);
		verify(administrationService, never()).getGlobalProperty(URL_PATTERN, DEFAULT_SURGICAL_APPOINTMENT_URL_PATTERN);
	}
	
	// As Mockito can't mock reflection methods, we need these 2 empty method
	public void save() {
	}
	
	public void temp() {
	}
}
