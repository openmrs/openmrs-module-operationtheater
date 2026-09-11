package org.openmrs.module.operationtheater.api.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.module.operationtheater.api.dao.SurgicalBlockDAO;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.service.SurgicalBlockService;
import org.openmrs.module.operationtheater.exception.ValidationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SurgicalBlockServiceImpl extends BaseOpenmrsService implements SurgicalBlockService {
	
	private static final Log log = LogFactory.getLog(SurgicalBlockServiceImpl.class);
	
	static final String SURGERY_SCHEDULING_ENCOUNTER_TYPE_GP = "operationtheater.surgerySchedulingEncounterTypeUuid";
	
	static final String SURGERY_ORDER_TYPE_UUID_GP = "operationtheater.surgeryOrderTypeUuid";
	
	static final String SURGICAL_ORDER_CONCEPT_UUID_GP = "operationtheater.surgicalOrderConceptUuid";
	
	SurgicalBlockDAO surgicalBlockDAO;
	
	private OrderService orderService;
	
	private ConceptService conceptService;
	
	private EncounterService encounterService;
	
	private AdministrationService adminService;
	
	public void setSurgicalBlockDAO(SurgicalBlockDAO surgicalBlockDAO) {
		this.surgicalBlockDAO = surgicalBlockDAO;
	}
	
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
	
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}
	
	public void setAdminService(AdministrationService adminService) {
		this.adminService = adminService;
	}
	
	@Override
	@Transactional
	public SurgicalBlock save(SurgicalBlock surgicalBlock) {
		// Snapshot new appointments BEFORE validation triggers Hibernate flush (which
		// assigns IDs)
		List<SurgicalAppointment> newAppointments = new ArrayList<>();
		for (SurgicalAppointment appointment : surgicalBlock.getSurgicalAppointments()) {
			if (!appointment.getVoided() && appointment.getId() == null) {
				newAppointments.add(appointment);
			}
		}
		validateSurgicalBlock(surgicalBlock);
		
		for (SurgicalAppointment appointment : newAppointments) {
			createAndLinkSurgeryOrder(appointment, surgicalBlock);
		}
		
		return surgicalBlockDAO.save(surgicalBlock);
	}
	
	@Override
	public void validateSurgicalBlock(SurgicalBlock surgicalBlock) {
		checkForOverlappingSurgicalBlocks(surgicalBlock);
		checkForOverlappingSurgicalAppointmentsForThePatient(surgicalBlock);
	}
	
	@Override
	@Transactional
	public SurgicalBlock getSurgicalBlockWithAppointments(String surgicalBlockUuid) {
		return surgicalBlockDAO.getSurgicalBlockWithAppointments(surgicalBlockUuid);
	}
	
	@Override
	public List<SurgicalBlock> getSurgicalBlocksBetweenStartDatetimeAndEndDatetime(Date startDatetime, Date endDatetime,
	        Boolean includeVoided, Boolean activeBlocks) {
		return surgicalBlockDAO.getSurgicalBlocksFor(startDatetime, endDatetime, null, null, includeVoided, activeBlocks);
	}
	
	private void checkForOverlappingSurgicalAppointmentsForThePatient(SurgicalBlock surgicalBlock) {
		for (SurgicalAppointment surgicalAppointment : surgicalBlock.getSurgicalAppointments()) {
			List<SurgicalAppointment> overlappingSurgicalAppointmentsForPatient = surgicalBlockDAO
			        .getOverlappingSurgicalAppointmentsForPatient(surgicalBlock.getStartDatetime(),
			            surgicalBlock.getEndDatetime(), surgicalAppointment.getPatient(), surgicalBlock.getId());
			if (overlappingSurgicalAppointmentsForPatient.size() > 0) {
				SurgicalAppointment conflictingSurgicalAppointment = overlappingSurgicalAppointmentsForPatient.get(0);
				SurgicalBlock conflictingSurgicalBlock = conflictingSurgicalAppointment.getSurgicalBlock();
				throw new ValidationException(conflictingSurgicalAppointment.getPatient().getGivenName() + " "
				        + conflictingSurgicalAppointment.getPatient().getFamilyName() + " has conflicting appointment at "
				        + conflictingSurgicalBlock.getLocation().getDisplayString() + " with "
				        + conflictingSurgicalBlock.getProvider().getName());
			}
		}
	}
	
	private void checkForOverlappingSurgicalBlocks(SurgicalBlock surgicalBlock) {
		if (surgicalBlock.getEndDatetime().before(surgicalBlock.getStartDatetime())) {
			throw new ValidationException("Surgical Block start date after end date");
		} else if (!getOverlappingSurgicalBlocksForProvider(surgicalBlock).isEmpty()) {
			throw new ValidationException("Surgical Block has conflicting time with existing block(s) for this surgeon");
		} else if (!getOverlappingSurgicalBlocksForLocation(surgicalBlock).isEmpty()) {
			throw new ValidationException("Surgical Block has conflicting time with existing block(s) for this OT");
		}
	}
	
	private List<SurgicalBlock> getOverlappingSurgicalBlocksForProvider(SurgicalBlock surgicalBlock) {
		return surgicalBlockDAO.getOverlappingSurgicalBlocksFor(surgicalBlock.getStartDatetime(),
		    surgicalBlock.getEndDatetime(), surgicalBlock.getProvider(), null, surgicalBlock.getId());
	}
	
	private List<SurgicalBlock> getOverlappingSurgicalBlocksForLocation(SurgicalBlock surgicalBlock) {
		return surgicalBlockDAO.getOverlappingSurgicalBlocksFor(surgicalBlock.getStartDatetime(),
		    surgicalBlock.getEndDatetime(), null, surgicalBlock.getLocation(), surgicalBlock.getId());
	}
	
	private void createAndLinkSurgeryOrder(SurgicalAppointment appointment, SurgicalBlock block) {
		List<String> privileges = Arrays.asList(PrivilegeConstants.GET_ENCOUNTER_TYPES, PrivilegeConstants.ADD_ENCOUNTERS,
		    PrivilegeConstants.GET_VISITS, PrivilegeConstants.GET_VISIT_TYPES, PrivilegeConstants.ADD_VISITS,
		    PrivilegeConstants.GET_ORDER_TYPES, PrivilegeConstants.ADD_ORDERS, PrivilegeConstants.GET_CONCEPTS,
		    PrivilegeConstants.GET_CARE_SETTINGS, PrivilegeConstants.GET_VISIT_ATTRIBUTE_TYPES);
		privileges.forEach(Context::addProxyPrivilege);
		try {
			createAndLinkSurgeryOrderWithPrivileges(appointment, block);
		}
		finally {
			privileges.forEach(Context::removeProxyPrivilege);
		}
	}
	
	private void createAndLinkSurgeryOrderWithPrivileges(SurgicalAppointment appointment, SurgicalBlock block) {
		// Resolve ALL prerequisites before any DB write to prevent orphaned encounters.
		String encounterTypeUuid = adminService.getGlobalProperty(SURGERY_SCHEDULING_ENCOUNTER_TYPE_GP, "");
		if (StringUtils.isBlank(encounterTypeUuid)) {
			log.warn(SURGERY_SCHEDULING_ENCOUNTER_TYPE_GP + " GP not configured; skipping order creation");
			return;
		}
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(encounterTypeUuid);
		if (encounterType == null) {
			log.warn("SURGERY_SCHEDULING encounter type not found for uuid: " + encounterTypeUuid);
			return;
		}
		
		String orderTypeUuid = adminService.getGlobalProperty(SURGERY_ORDER_TYPE_UUID_GP, "");
		if (StringUtils.isBlank(orderTypeUuid)) {
			log.warn(SURGERY_ORDER_TYPE_UUID_GP + " GP not configured; skipping order creation");
			return;
		}
		OrderType orderType = orderService.getOrderTypeByUuid(orderTypeUuid);
		if (orderType == null) {
			log.warn("Surgery Order type not found for uuid: " + orderTypeUuid);
			return;
		}
		
		String conceptUuid = adminService.getGlobalProperty(SURGICAL_ORDER_CONCEPT_UUID_GP, "");
		if (StringUtils.isBlank(conceptUuid)) {
			log.warn(SURGICAL_ORDER_CONCEPT_UUID_GP + " GP not configured; skipping order creation");
			return;
		}
		Concept concept = conceptService.getConceptByUuid(conceptUuid);
		if (concept == null) {
			log.warn("Surgical Order concept not found for uuid: " + conceptUuid);
			return;
		}
		
		CareSetting careSetting = orderService.getCareSettingByName(CareSetting.CareSettingType.OUTPATIENT.toString());
		if (careSetting == null) {
			log.warn("OUTPATIENT care setting not found; skipping order creation");
			return;
		}
		
		if (block.getProvider() == null) {
			log.warn("Surgical block provider is null; skipping order creation");
			return;
		}
		
		// All prerequisites validated — now safe to create encounter (first DB write).
		// Visit assignment is left to core's visits.assignmentHandler: it attaches the
		// encounter to an open patient visit if one exists, or leaves it unset.
		Encounter encounter = new Encounter();
		encounter.setPatient(appointment.getPatient());
		encounter.setEncounterType(encounterType);
		encounter.setEncounterDatetime(new Date());
		if (block.getLocation() != null) {
			encounter.setLocation(block.getLocation());
		}
		encounter = encounterService.saveEncounter(encounter);
		
		Order order = new Order();
		order.setPatient(appointment.getPatient());
		order.setEncounter(encounter);
		order.setOrderType(orderType);
		// The procedure attribute on SurgicalAppointment is a plain java.lang.String,
		// so there is no concept to derive from the selection. This default concept is
		// used for every order until procedure concept-set support is introduced.
		order.setConcept(concept);
		order.setCareSetting(careSetting);
		order.setOrderer(block.getProvider());
		order.setDateActivated(new Date());
		// NOTE: when a surgical appointment is cancelled or voided, the Surgery Order
		// is
		// not automatically voided. Order lifecycle management on cancellation is a
		// follow-up task.
		order = orderService.saveOrder(order, null);
		
		appointment.setOrder(order);
	}
}
