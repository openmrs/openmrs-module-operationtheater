package org.openmrs.module.operationtheater.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointment;
import org.openmrs.module.operationtheater.api.model.SurgicalAppointmentAttribute;
import org.openmrs.module.operationtheater.api.service.SurgicalAppointmentService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@SubResource(parent = SurgicalAppointmentResource.class, path = "attribute", supportedClass = SurgicalAppointmentAttribute.class, supportedOpenmrsVersions = {
        "2.0.*", "2.1.*" })
public class SurgicalAppointmentAttributeResource extends DelegatingSubResource<SurgicalAppointmentAttribute, SurgicalAppointment, SurgicalAppointmentResource> {
	
	@Override
	public SurgicalAppointmentAttribute getByUniqueId(String uuid) {
		return Context.getService(SurgicalAppointmentService.class).getSurgicalAppointmentAttributeByUuid(uuid);
	}
	
	@Override
	protected void delete(SurgicalAppointmentAttribute surgicalAppointmentAttribute, String s, RequestContext requestContext)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Delete not supported on SurgicalAppointmentAttribute resource");
	}
	
	@Override
	public SurgicalAppointmentAttribute newDelegate() {
		return new SurgicalAppointmentAttribute();
	}
	
	@Override
	public SurgicalAppointmentAttribute save(SurgicalAppointmentAttribute surgicalAppointmentAttribute) {
		throw new ResourceDoesNotSupportOperationException("Purge not supported on SurgicalAppointmentAttribute resource");
	}
	
	@Override
	public void purge(SurgicalAppointmentAttribute surgicalAppointmentAttribute, RequestContext requestContext)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Purge not supported on SurgicalAppointmentAttribute resource");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		if ((representation instanceof DefaultRepresentation) || (representation instanceof RefRepresentation)) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("id");
			description.addProperty("uuid");
			description.addProperty("surgicalAppointmentAttributeType", Representation.DEFAULT);
			description.addProperty("value");
			return description;
		}
		if ((representation instanceof FullRepresentation)) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("id");
			description.addProperty("uuid");
			description.addProperty("surgicalAppointmentAttributeType", Representation.FULL);
			description.addProperty("value");
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		delegatingResourceDescription.addProperty("id");
		delegatingResourceDescription.addProperty("uuid");
		delegatingResourceDescription.addProperty("surgicalAppointmentAttributeType");
		delegatingResourceDescription.addProperty("value");
		return delegatingResourceDescription;
	}
	
	@Override
	public SurgicalAppointment getParent(SurgicalAppointmentAttribute instance) {
		return instance.getSurgicalAppointment();
	}
	
	@Override
	public void setParent(SurgicalAppointmentAttribute instance, SurgicalAppointment surgicalAppointment) {
		instance.setSurgicalAppointment(surgicalAppointment);
	}
	
	@Override
	public PageableResult doGetAll(SurgicalAppointment surgicalAppointment, RequestContext requestContext)
	        throws ResponseException {
		return new NeedsPaging<SurgicalAppointmentAttribute>(surgicalAppointment.getActiveAttributes(), requestContext);
	}
}
