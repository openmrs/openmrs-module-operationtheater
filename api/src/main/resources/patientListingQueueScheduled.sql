DELETE FROM global_property WHERE property = 'emrapi.sqlSearch.otScheduledQueue';
SELECT uuid() INTO @uuid;

INSERT INTO global_property (`property`, `property_value`, `description`, `uuid`)
VALUES ('emrapi.sqlSearch.otScheduledQueue',
"SELECT
  pi.identifier                              AS PATIENT_LISTING_QUEUES_HEADER_IDENTIFIER,
  CONCAT(pn.given_name, ' ', pn.family_name) AS PATIENT_LISTING_QUEUES_HEADER_NAME,
  DATE_FORMAT(sb.start_datetime, '%d/%m/%Y') AS `Date of Surgery`,
  l.name                                     AS Location,
  providerNames.provider_name                AS `Primary provider`,
  p.uuid                                     AS uuid
FROM surgical_block sb
  INNER JOIN surgical_appointment sa ON sb.surgical_block_id = sa.surgical_block_id
                                        AND sb.voided IS FALSE
                                        AND sa.voided IS FALSE
                                        AND sa.status = 'SCHEDULED'
  INNER JOIN person p ON p.person_id = sa.patient_id AND p.voided IS FALSE
  INNER JOIN person_name pn ON pn.person_id = sa.patient_id
                               AND pn.voided IS FALSE
  INNER JOIN patient_identifier pi ON pi.patient_id = pn.person_id AND pi.voided IS FALSE
  INNER JOIN patient_identifier_type pit ON pi.identifier_type = pit.patient_identifier_type_id AND
                                            pit.name = 'Patient Identifier'
  INNER JOIN location l ON sb.location_id = l.location_id AND l.retired IS FALSE
  INNER JOIN (
               SELECT
                 p2.provider_id,
                 pn2.person_id,
                 CONCAT(pn2.given_name, ' ', pn2.family_name) AS provider_name
               FROM provider p2
                 INNER JOIN person_name pn2 ON pn2.person_id = p2.person_id AND
                                               p2.retired IS FALSE AND pn2.voided IS FALSE
             ) providerNames ON providerNames.provider_id = sb.primary_provider_id
ORDER BY sb.start_datetime DESC;"
   ,'SQL for scheduled patient listing queues for OT module',@uuid);