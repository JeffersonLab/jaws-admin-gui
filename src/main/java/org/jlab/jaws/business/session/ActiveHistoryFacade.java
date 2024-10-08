package org.jlab.jaws.business.session;

import java.math.BigInteger;
import java.util.*;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.jlab.jaws.entity.OverriddenAlarmType;
import org.jlab.jaws.persistence.entity.*;

/**
 * @author ryans
 */
@Stateless
public class ActiveHistoryFacade extends AbstractFacade<ActiveHistory> {
  private static final Logger logger = Logger.getLogger(ActiveHistoryFacade.class.getName());

  @EJB LocationFacade locationFacade;

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ActiveHistoryFacade() {
    super(ActiveHistory.class);
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb,
      CriteriaQuery<? extends Object> cq,
      Root<ActiveHistory> root,
      Date start,
      Date end,
      OverriddenAlarmType override,
      String activationType,
      BigInteger[] locationIdArray,
      BigInteger priorityId,
      BigInteger teamId,
      Boolean registered,
      String alarmName,
      String actionName,
      String systemName,
      Map<String, Join> joins) {
    List<Predicate> filters = new ArrayList<>();

    Join<ActiveHistory, AlarmEntity> alarmJoin = root.join("alarm", JoinType.LEFT);
    Join<AlarmEntity, Action> actionJoin = alarmJoin.join("action", JoinType.LEFT);
    Join<Action, SystemEntity> systemJoin = actionJoin.join("system", JoinType.LEFT);

    joins.put("alarm", alarmJoin);
    joins.put("action", actionJoin);
    joins.put("system", systemJoin);

    if (start != null) {
      filters.add(cb.greaterThanOrEqualTo(root.get("activeHistoryPK").get("activeStart"), start));
    }

    if (end != null) {
      filters.add(cb.lessThan(root.get("activeHistoryPK").get("activeStart"), end));
    }

    if (override != null) {
      filters.add(cb.equal(root.get("incitedWith"), override.name()));
    }

    if (activationType != null && !activationType.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("activationType")), activationType.toLowerCase()));
    }

    if (locationIdArray != null && locationIdArray.length > 0) {
      // Parent locations imply children locations
      Set<Location> materializedLocations = new HashSet<>();
      for (BigInteger locationId : locationIdArray) {
        if (locationId != null) {
          Set<Location> subset = locationFacade.findBranchAsSet(locationId);
          materializedLocations.addAll(subset);
        }
      }

      List<BigInteger> locationIdList = new ArrayList<>();

      for (Location l : materializedLocations) {
        locationIdList.add(l.getLocationId());
      }

      if (!locationIdList.isEmpty()) {
        Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
        Root<Location> subqueryRoot = subquery.from(Location.class);
        Join<Location, AlarmEntity> alarmLocationJoin = subqueryRoot.join("alarmList");
        subquery.select(alarmLocationJoin.get("alarmId"));
        subquery.where(subqueryRoot.get("locationId").in(locationIdList));
        filters.add(cb.in(alarmJoin.get("alarmId")).value(subquery));
      }
    }

    if (alarmName != null && !alarmName.isEmpty()) {
      filters.add(cb.like(cb.lower(alarmJoin.get("name")), alarmName.toLowerCase()));
    }

    if (priorityId != null) {
      filters.add(cb.equal(actionJoin.get("priority"), priorityId));
    }

    if (actionName != null && !actionName.isEmpty()) {
      filters.add(cb.like(cb.lower(actionJoin.get("name")), actionName.toLowerCase()));
    }

    if (systemName != null && !systemName.isEmpty()) {
      filters.add(cb.like(cb.lower(systemJoin.get("name")), systemName.toLowerCase()));
    }

    if (teamId != null) {
      filters.add(cb.equal(systemJoin.get("team"), teamId));
    }

    if (registered != null) {
      if (registered) {
        filters.add(cb.isNotNull(actionJoin.get("priority")));
      } else {
        filters.add(cb.isNull(actionJoin.get("priority")));
      }
    }

    return filters;
  }

  @PermitAll
  public List<ActiveHistory> filterList(
      Date start,
      Date end,
      OverriddenAlarmType override,
      String activationType,
      BigInteger[] locationIdArray,
      BigInteger priorityId,
      BigInteger teamId,
      Boolean registered,
      String alarmName,
      String actionName,
      String systemName,
      int offset,
      int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<ActiveHistory> cq = cb.createQuery(ActiveHistory.class);
    Root<ActiveHistory> root = cq.from(ActiveHistory.class);
    cq.select(root);

    Map<String, Join> joins = new HashMap<>();

    List<Predicate> filters =
        getFilters(
            cb,
            cq,
            root,
            start,
            end,
            override,
            activationType,
            locationIdArray,
            priorityId,
            teamId,
            registered,
            alarmName,
            actionName,
            systemName,
            joins);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("activeHistoryPK").get("activeStart");
    Order o0 = cb.desc(p0);
    orders.add(o0);
    Path p1 = joins.get("action").get("priority");
    Order o1 = cb.asc(p1);
    orders.add(o1);
    Path p2 = root.get("activeHistoryPK").get("name");
    Order o2 = cb.asc(p2);
    orders.add(o2);
    cq.orderBy(orders);
    return getEntityManager()
        .createQuery(cq)
        .setFirstResult(offset)
        .setMaxResults(max)
        .getResultList();
  }

  @PermitAll
  public long countList(
      Date start,
      Date end,
      OverriddenAlarmType override,
      String activationType,
      BigInteger[] locationIdArray,
      BigInteger priorityId,
      BigInteger teamId,
      Boolean registered,
      String alarmName,
      String actionName,
      String systemName) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<ActiveHistory> root = cq.from(ActiveHistory.class);

    Map<String, Join> joins = new HashMap<>();

    List<Predicate> filters =
        getFilters(
            cb,
            cq,
            root,
            start,
            end,
            override,
            activationType,
            locationIdArray,
            priorityId,
            teamId,
            registered,
            alarmName,
            actionName,
            systemName,
            joins);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }
}
