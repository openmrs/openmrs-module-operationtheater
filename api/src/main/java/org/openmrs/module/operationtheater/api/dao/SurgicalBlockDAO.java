package org.openmrs.module.operationtheater.api.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.operationtheater.SurgicalBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class SurgicalBlockDAO {

    @Autowired
    SessionFactory sessionFactory;


    public ArrayList<SurgicalBlock> getOverlappingSurgicalBlocks(SurgicalBlock surgicalBlock) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(SurgicalBlock.class, "surgicalBlock");
        criteria.add(Restrictions.le("startDatetime", surgicalBlock.getEndDatetime()));
        criteria.add(Restrictions.ge("endDatetime", surgicalBlock.getStartDatetime()));
        criteria.add(Restrictions.eq("location", surgicalBlock.getLocation()));
        criteria.add(Restrictions.eq("voided", false));
        return (ArrayList<SurgicalBlock>) criteria.list();

    }

    public SurgicalBlock save(SurgicalBlock surgicalBlock) {
        Session session = sessionFactory.getCurrentSession();
        session.save(surgicalBlock);
        session.flush();
        return surgicalBlock;
    }
}
