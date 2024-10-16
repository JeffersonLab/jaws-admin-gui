<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="jaws" uri="http://jlab.org/jaws/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Sync Summary"/>
<t:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/sync-summary.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/sync-summary.js"></script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <table class="data-table">
                <thead>
                    <tr>
                        <th></th>
                        <th>Matched</th>
                        <th>Add</th>
                        <th>Remove</th>
                        <th>Update</th>
                        <th>Link</th>
                        <th></th>
                    </tr>
                    <tr id="total-row">
                        <th id="total-status-cell"><div class="button-indicator"></div> Working...</th>
                        <th>0</th>
                        <th>0</th>
                        <th>0</th>
                        <th>0</th>
                        <th>0</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
            <c:forEach items="${ruleSetList}" var="rs">
                    <c:if test="${fn:length(rs.ruleList) > 0}">
                        <tr>
                            <td colspan="7"><h3><c:out value="${rs.name}"/></h3></td>
                        </tr>
                            <c:forEach items="${rs.ruleList}" var="rule">
                                <tr class="rule-row" data-id="${rule.syncRuleId}">
                                    <td>
                                        <div><c:out value="${rule.syncServer.name} #${rule.syncRuleId}"/></div>
                                        <div><c:out value="${rule.action.name}"/></div>
                                        <div><c:out value="${rule.description}"/></div>
                                    </td>
                                    <td colspan="5"><div class="status">Pending</div></td>
                                    <td>
                                        <!-- Use onclick to avoid https://bugs.webkit.org/show_bug.cgi?id=30103 -->
                                        <c:url value="/setup/syncs/${jaws:urlEncodePath(rule.syncRuleId)}" var="url">
                                        </c:url>
                                        <form method="get"
                                              action="${pageContext.request.contextPath}/setup/syncs/${fn:escapeXml(rule.syncRuleId)}">
                                        <button class="single-char-button" type="button" onclick="window.open('${url}', '_blank');  return false;">🗗</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                    </c:if>
            </c:forEach>
                </tbody>
            </table>
        </section>
    </jsp:body>         
</t:setup-page>