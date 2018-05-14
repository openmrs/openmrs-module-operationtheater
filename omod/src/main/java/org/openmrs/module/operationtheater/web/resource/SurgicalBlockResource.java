package org.openmrs.module.operationtheater.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.api.model.SurgicalBlock;
import org.openmrs.module.operationtheater.api.service.SurgicalBlockService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.validation.ValidateUtil;

import java.util.Date;
import java.util.Set;

@Resource(name = RestConstants.VERSION_1
        + "/surgicalBlock", supportedClass = SurgicalBlock.class, supportedOpenmrsVersions = { "2.0.*", "2.1.*" })
public class SurgicalBlockResource extends DataDelegatingCrudResource<SurgicalBlock> {
	
	@Override
	public SurgicalBlock getByUniqueId(String surgicalBlockUuid) {
		SurgicalBlockService surgicalBlockService = (SurgicalBlockService) Context.getService(SurgicalBlockService.class);
		return (SurgicalBlock) surgicalBlockService.getSurgicalBlockWithAppointments(surgicalBlockUuid);
	}
	
	@Override
	protected void delete(SurgicalBlock surgicalBlock, String s, RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Delete not supported on SurgicalBlock resource");
	}
	
	@Override
	public SurgicalBlock newDelegate() {
		return new SurgicalBlock();
	}
	
	@Override
	public SurgicalBlock save(SurgicalBlock surgicalBlock) {
		return Context.getService(SurgicalBlockService.class).save(surgicalBlock);
	}
	
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		SurgicalBlock savedSurgicalBlock = this.getByUniqueId(uuid);
		SurgicalBlock cloneOfSurgicalBlock = new SurgicalBlock(savedSurgicalBlock);
		this.setConvertedProperties(cloneOfSurgicalBlock, propertiesToUpdate, this.getUpdatableProperties(), false);
		Context.getService(SurgicalBlockService.class).validateSurgicalBlock(cloneOfSurgicalBlock);
		this.setConvertedProperties(savedSurgicalBlock, propertiesToUpdate, this.getUpdatableProperties(), false);
		ValidateUtil.validate(savedSurgicalBlock);
		savedSurgicalBlock = this.save(savedSurgicalBlock);
		return ConversionUtil.convertToRepresentation(savedSurgicalBlock, Representation.DEFAULT);
	}
	
	@Override
	public void purge(SurgicalBlock surgicalBlock, RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Purge not supported on SurgicalBlock resource");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		if ((representation instanceof DefaultRepresentation) || (representation instanceof RefRepresentation)) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("id");
			description.addProperty("uuid");
			description.addProperty("provider", Representation.DEFAULT);
			description.addProperty("location", Representation.DEFAULT);
			description.addProperty("startDatetime");
			description.addProperty("endDatetime");
			description.addProperty("surgicalAppointments");
			description.addProperty("voided");
			description.addProperty("voidReason");
			return description;
		}
		if ((representation instanceof FullRepresentation)) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("id");
			description.addProperty("uuid");
			description.addProperty("provider", Representation.FULL);
			description.addProperty("location", Representation.FULL);
			description.addProperty("startDatetime");
			description.addProperty("endDatetime");
			description.addProperty("surgicalAppointments");
			description.addProperty("voided");
			description.addProperty("voidReason");
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		delegatingResourceDescription.addProperty("id");
		delegatingResourceDescription.addProperty("uuid");
		delegatingResourceDescription.addRequiredProperty("provider");
		delegatingResourceDescription.addRequiredProperty("location");
		delegatingResourceDescription.addRequiredProperty("startDatetime");
		delegatingResourceDescription.addRequiredProperty("endDatetime");
		delegatingResourceDescription.addProperty("surgicalAppointments");
		delegatingResourceDescription.addProperty("voided");
		delegatingResourceDescription.addProperty("voidReason");
		return delegatingResourceDescription;
	}
	
	@PropertyGetter("surgicalAppointments")
	public static Set<SurgicalAppointment> getSurgicalAppointments(SurgicalBlock surgicalBlock) {
		return surgicalBlock.getSurgicalAppointments();
	}
	
	@PropertySetter("surgicalAppointments")
	public static void setSurgicalAppointments(SurgicalBlock instance, Set<SurgicalAppointment> surgicalAppointments) {
		for (SurgicalAppointment surgicalAppointment : surgicalAppointments) {
			surgicalAppointment.setSurgicalBlock(instance);
			for (SurgicalAppointmentAttribute surgicalAppointmentAttribute : surgicalAppointment
			        .getSurgicalAppointmentAttributes()) {
				surgicalAppointmentAttribute.setSurgicalAppointment(surgicalAppointment);
			}
		}
		instance.setSurgicalAppointments(surgicalAppointments);
	}
	
	@PropertySetter("voided")
	public static void setVoided(SurgicalBlock instance, Boolean voided) {
		instance.setVoided(voided);
		if (voided) {
			instance.setVoidedBy(Context.getAuthenticatedUser());
			instance.setDateVoided(new Date());
		}
	}
}
