package org.jlab.jaws.presentation.controller.setup;

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
import org.jlab.jaws.persistence.entity.*;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "SyncRuleController",
    urlPatterns = {"/setup/syncs"})
public class SyncRuleController extends HttpServlet {

  @EJB SyncRuleFacade syncFacade;
  @EJB ActionFacade actionFacade;
  @EJB SyncServerFacade serverFacade;

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

    BigInteger syncRuleId = ParamConverter.convertBigInteger(request, "syncRuleId");
    String actionName = request.getParameter("actionName");
    String systemName = request.getParameter("systemName");
    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    List<Action> actionList = actionFacade.findAll(new AbstractFacade.OrderDirective("name"));
    List<SyncServer> serverList = serverFacade.findAll(new AbstractFacade.OrderDirective("name"));
    List<SyncRule> syncList =
        syncFacade.filterList(syncRuleId, actionName, systemName, offset, maxPerPage);

    long totalRecords = syncFacade.countList(syncRuleId, actionName, systemName);

    Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

    String selectionMessage = createSelectionMessage(paginator, syncRuleId, actionName, systemName);

    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("actionList", actionList);
    request.setAttribute("serverList", serverList);
    request.setAttribute("syncList", syncList);
    request.setAttribute("paginator", paginator);

    request.getRequestDispatcher("/WEB-INF/views/setup/syncs.jsp").forward(request, response);
  }

  private String createSelectionMessage(
      Paginator paginator, BigInteger syncRuleId, String actionName, String systemName) {
    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Sync Rules ";

    List<String> filters = new ArrayList<>();

    if (syncRuleId != null) {
      filters.add("Sync Rule ID \"" + syncRuleId + "\"");
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
