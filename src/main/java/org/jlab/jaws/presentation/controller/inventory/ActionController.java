package org.jlab.jaws.presentation.controller.inventory;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jaws.business.session.*;
import org.jlab.jaws.persistence.entity.Action;
import org.jlab.jaws.persistence.entity.Priority;
import org.jlab.jaws.persistence.entity.SystemEntity;
import org.jlab.jaws.persistence.entity.Team;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "ActionController",
    urlPatterns = {"/inventory/actions"})
public class ActionController extends HttpServlet {

  @EJB ActionFacade actionFacade;

  @EJB TeamFacade teamFacade;

  @EJB PriorityFacade priorityFacade;

  @EJB SystemFacade systemFacade;

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

    String actionName = request.getParameter("actionName");
    BigInteger priorityId = ParamConverter.convertBigInteger(request, "priorityId");
    String systemName = request.getParameter("systemName");
    BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");
    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    List<Action> actionList =
        actionFacade.filterList(priorityId, teamId, actionName, systemName, offset, maxPerPage);
    List<Team> teamList = teamFacade.findAll(new AbstractFacade.OrderDirective("name"));
    List<Priority> priorityList =
        priorityFacade.findAll(new AbstractFacade.OrderDirective("priorityId"));
    List<SystemEntity> systemList = systemFacade.findAll(new AbstractFacade.OrderDirective("name"));

    Priority selectedPriority = null;

    if (priorityId != null) {
      selectedPriority = priorityFacade.find(priorityId);
    }

    Team selectedTeam = null;

    if (teamId != null) {
      selectedTeam = teamFacade.find(teamId);
    }

    long totalRecords = actionFacade.countList(priorityId, teamId, actionName, systemName);

    Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

    String selectionMessage =
        createSelectionMessage(paginator, selectedPriority, selectedTeam, actionName, systemName);

    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("actionList", actionList);
    request.setAttribute("teamList", teamList);
    request.setAttribute("priorityList", priorityList);
    request.setAttribute("systemList", systemList);
    request.setAttribute("paginator", paginator);

    request.getRequestDispatcher("/WEB-INF/views/inventory/actions.jsp").forward(request, response);
  }

  private String createSelectionMessage(
      Paginator paginator, Priority priority, Team team, String actionName, String systemName) {
    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Actions ";

    List<String> filters = new ArrayList<>();

    if (priority != null) {
      filters.add("Priority \"" + priority.getName() + "\"");
    }

    if (team != null) {
      filters.add("Team \"" + team.getName() + "\"");
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
