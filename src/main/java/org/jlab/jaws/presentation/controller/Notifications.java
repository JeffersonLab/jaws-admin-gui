package org.jlab.jaws.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.*;
import org.jlab.jaws.entity.OverriddenAlarmType;
import org.jlab.jaws.persistence.entity.*;
import org.jlab.jaws.persistence.model.BinaryState;
import org.jlab.jaws.persistence.model.OverriddenState;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "Notifications",
    urlPatterns = {"/notifications"})
public class Notifications extends HttpServlet {

  @EJB NotificationFacade notificationFacade;
  @EJB TeamFacade teamFacade;
  @EJB PriorityFacade priorityFacade;
  @EJB ActionFacade actionFacade;
  @EJB LocationFacade locationFacade;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    BinaryState state = convertState(request, "state");
    Boolean overridden = ParamConverter.convertYNBoolean(request, "overridden");
    OverriddenAlarmType override = convertOverrideKey(request, "override");
    String activationType = request.getParameter("type");
    String alarmName = request.getParameter("alarmName");
    BigInteger[] locationIdArray = ParamConverter.convertBigIntegerArray(request, "locationId");
    String actionName = request.getParameter("actionName");
    BigInteger priorityId = ParamConverter.convertBigInteger(request, "priorityId");
    String systemName = request.getParameter("systemName");
    BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
    Boolean registered = ParamConverter.convertYNBoolean(request, "registered");
    Boolean filterable = ParamConverter.convertYNBoolean(request, "filterable");
    boolean alwaysIncludeUnregistered =
        ParamUtil.convertAndValidateYNBoolean(request, "alwaysIncludeUnregistered", false);
    boolean alwaysIncludeUnfilterable =
        ParamUtil.convertAndValidateYNBoolean(request, "alwaysIncludeUnfilterable", false);
    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    List<Notification> notificationList =
        notificationFacade.filterList(
            state,
            overridden,
            override,
            activationType,
            locationIdArray,
            priorityId,
            teamId,
            registered,
            filterable,
            alarmName,
            actionName,
            systemName,
            alwaysIncludeUnregistered,
            alwaysIncludeUnfilterable,
            offset,
            maxPerPage);
    List<Team> teamList = teamFacade.findAll(new AbstractFacade.OrderDirective("name"));
    List<OverriddenState> overrideList = Arrays.asList(OverriddenState.values());
    List<BinaryState> stateList = Arrays.asList(BinaryState.values());
    List<Priority> priorityList =
        priorityFacade.findAll(new AbstractFacade.OrderDirective("priorityId"));
    List<Action> actionList = actionFacade.findAll(new AbstractFacade.OrderDirective("name"));
    Location locationRoot = locationFacade.findBranch(Location.TREE_ROOT);
    List<String> typeList = new ArrayList<>();

    typeList.add("NotActive");
    typeList.add("Simple");
    typeList.add("ChannelError");
    typeList.add("EPICS");
    typeList.add("Note");

    List<Location> selectedLocationList = new ArrayList<>();

    if (locationIdArray != null && locationIdArray.length > 0) {
      for (BigInteger id : locationIdArray) {
        if (id == null) { // TODO: the convertBigIntegerArray method should be excluding empty/null
          continue;
        }

        Location l = locationFacade.find(id);
        selectedLocationList.add(l);
      }
    }

    Priority selectedPriority = null;

    if (priorityId != null) {
      selectedPriority = priorityFacade.find(priorityId);
    }

    Team selectedTeam = null;

    if (teamId != null) {
      selectedTeam = teamFacade.find(teamId);
    }

    long totalRecords =
        notificationFacade.countList(
            state,
            overridden,
            override,
            activationType,
            locationIdArray,
            priorityId,
            teamId,
            registered,
            filterable,
            alarmName,
            actionName,
            systemName,
            alwaysIncludeUnregistered,
            alwaysIncludeUnfilterable);

    Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

    String selectionMessage =
        createSelectionMessage(
            "Notifications",
            paginator,
            null,
            null,
            state,
            overridden,
            override,
            activationType,
            selectedLocationList,
            selectedPriority,
            selectedTeam,
            registered,
            filterable,
            alarmName,
            actionName,
            systemName,
            alwaysIncludeUnregistered,
            alwaysIncludeUnfilterable);

    request.setAttribute("notificationList", notificationList);
    request.setAttribute("actionList", actionList);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("teamList", teamList);
    request.setAttribute("stateList", stateList);
    request.setAttribute("overrideList", overrideList);
    request.setAttribute("typeList", typeList);
    request.setAttribute("priorityList", priorityList);
    request.setAttribute("locationRoot", locationRoot);
    request.setAttribute("paginator", paginator);

    request.getRequestDispatcher("/WEB-INF/views/notifications.jsp").forward(request, response);
  }

  public static OverriddenAlarmType convertOverrideKey(HttpServletRequest request, String name) {
    String value = request.getParameter(name);

    OverriddenAlarmType type = null;

    if (value != null && !value.isBlank()) {
      OverriddenState intermediate = OverriddenState.valueOf(value);
      type = intermediate.getOverrideType();
    }

    return type;
  }

  public static BinaryState convertState(HttpServletRequest request, String name) {
    String value = request.getParameter(name);

    BinaryState state = null;

    if (value != null && !value.isBlank()) {
      state = BinaryState.valueOf(value);
    }

    return state;
  }

  public static String createSelectionMessage(
      String entityName,
      Paginator paginator,
      Date start,
      Date end,
      BinaryState state,
      Boolean overridden,
      OverriddenAlarmType override,
      String activationType,
      List<Location> locationList,
      Priority priority,
      Team team,
      Boolean registered,
      Boolean filterable,
      String alarmName,
      String actionName,
      String systemName,
      boolean alwaysIncludeUnregistered,
      boolean alwaysIncludeUnfilterable) {
    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All " + entityName + " ";

    List<String> filters = new ArrayList<>();

    if (start != null && end != null) {
      filters.add(TimeUtil.formatSmartRangeSeparateTime(start, end));
    } else if (start != null) {
      filters.add("Active After " + TimeUtil.formatSmartSingleTime(start));
    } else if (end != null) {
      filters.add("Active Before " + TimeUtil.formatSmartSingleTime(end));
    }

    if (state != null) {
      String stateFilter = "State \"" + state + "\"";

      if ("Active".equals(state.name())) {
        if (alwaysIncludeUnregistered) {
          stateFilter = stateFilter + " (+Unregistered)";
        }
        if (alwaysIncludeUnfilterable) {
          stateFilter = stateFilter + " (+Unfilterable)";
        }

        filters.add(stateFilter);
      }
    }

    if (overridden != null) {
      filters.add("Overridden \"" + (overridden ? "Yes" : "No") + "\"");
    }

    if (override != null) {
      filters.add("Override \"" + override + "\"");
    }

    if (activationType != null && !activationType.isBlank()) {
      filters.add("Activation Type \"" + activationType + "\"");
    }

    if (locationList != null && !locationList.isEmpty()) {
      String sublist = "\"" + locationList.get(0).getName() + "\"";

      for (int i = 1; i < locationList.size(); i++) {
        Location l = locationList.get(i);
        sublist = sublist + ", \"" + l.getName() + "\"";
      }

      filters.add("Location " + sublist);
    }

    if (priority != null) {
      filters.add("Priority \"" + priority.getName() + "\"");
    }

    if (team != null) {
      filters.add("Team \"" + team.getName() + "\"");
    }

    if (registered != null) {
      filters.add("Registered \"" + (registered ? "Yes" : "No") + "\"");
    }

    if (filterable != null) {
      filters.add("Filterable \"" + (filterable ? "Yes" : "No") + "\"");
    }

    if (alarmName != null && !alarmName.isBlank()) {
      filters.add("Alarm Name \"" + alarmName + "\"");
    }

    if (actionName != null && !actionName.isBlank()) {
      filters.add("Action Name \"" + actionName + "\"");
    }

    if (systemName != null && !systemName.isBlank()) {
      filters.add("System Name \"" + systemName + "\"");
    }

    if (!filters.isEmpty()) {
      selectionMessage = filters.get(0);

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        selectionMessage += " and " + filter;
      }
    }

    if (paginator.getTotalRecords() < paginator.getMaxPerPage() && paginator.getOffset() == 0) {
      selectionMessage =
          selectionMessage + " {" + formatter.format(paginator.getTotalRecords()) + "}";
    } else {
      selectionMessage =
          selectionMessage
              + " {"
              + formatter.format(paginator.getStartNumber())
              + " - "
              + formatter.format(paginator.getEndNumber())
              + " of "
              + formatter.format(paginator.getTotalRecords())
              + "}";
    }

    return selectionMessage;
  }
}
