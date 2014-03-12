#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="glassmaker"%>


<glassmaker:gwheader title="All Cards" />



<div class="row">
	<c:forEach items="${symbol_dollar}{itemResponse.items}" var="item">
		<div class="col-sm-6">
			<div class="panel panel-info">
				<div class="panel-heading">
					<h3 class="panel-title" data-bind="text: title"></h3>
					<form action="${symbol_dollar}{request.contextPath}/gm/deleteTimelineItem"
						method="post">
						<button type="submit" class="btn btn-danger btn-sm roundbutton">
							<span class="glyphicon glyphicon-remove-sign"></span>
						</button>
						<input type="hidden" name="id" value="${symbol_dollar}{item.id}">
					</form>
				</div>
				<div class="panel-body" style="height: 240px;">
					<div class="smallcard">
						<div class="card">
							<c:choose>
								<c:when test="${symbol_dollar}{not fn:containsIgnoreCase(item.html, '</article>')}">
									<article>
										<section>
											<p class="text-x-large" data-text-autosize="true"
												contenteditable="true" style="">
											<div>${symbol_dollar}{item.text}</div>
											</p>
										</section>
										<footer id="map-time-footer" class="has-brand-icon">
											<time>${symbol_dollar}{item.created}<img class="footer-brand-icon"
													src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAIAAAD8GO2jAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDo1Q0E0REVDMzg0NDMxMUUyOUZGNjlDREQ3QjM4QTcyRiIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDo1Q0E0REVDNDg0NDMxMUUyOUZGNjlDREQ3QjM4QTcyRiI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjVDQTRERUMxODQ0MzExRTI5RkY2OUNERDdCMzhBNzJGIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjVDQTRERUMyODQ0MzExRTI5RkY2OUNERDdCMzhBNzJGIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+Z0w5lgAAAlVJREFUeNrsVjFs2kAUBftsE4GEgmiQwkQqoXpIB4Z0SBcGlmRJFxZcFSqVDiViqMUEA1mgUioGAoHAku4MKTAws7kzTM2KWgk1EgPY2Jh+HKlSCcWmPW89MXwk7r3Pu//unnV317u1ZaMoZLVaLfjWfD6XZWUyEdHOziPX9rbFnPXj7g6J4uTbd8kkAlVVCbzKLC0AJywmr/8ELper2Wze3HyezWYwmg9/gP4F3Wazlcvlvb3HUEvSlGFokiSx/QPAOj//uL//9P6roiiqOscpUTqdCQaDoij+Mq/2wUQQj78Nh8Pgo0TiHf5DPjl5kUwmochk0p1OBzPB4eHzbDYLRalUur7+RFEUTgKWZQuFAkKo0Wjk8zlAp2kaG4HX661Uqna7vdvt8vx7kkQ0TREEgYfA6XRWq1dut7vf78fjb8BT0DtMqu5dSRg01MVFyefzDQYDjuPG4zGgI0QauYkJI4bK5z8EAoHRaBSJRIbDoYZu9AXUJ0ilUqFQSJblWCx6e/sVdDeOrk8Qi73muJeg+OlpQhC+QO8wOboHa5Tg6OiY53koYPDb7bbW+2bo6wgODp7lcjkoarVavV6ntEWSGxtz9Qa/318sFkHrVqt1dpaFUAOW2rT3PxJ4PB4wlMPhEAQBpNcMBSP/l/FgxYNzeVkBjl6vF42+MmIouFPXBQuWfbK0GTZMp7KiyJA5jAyloswkaZGsGIYB9y3lO7QyzFAL2EXXoLuuMqAeQN8XhiQCxIdP6/p0tdT4b4e8MgpgjMAEZGDzCAAcQcKGyrz4/lOAAQAsM83mQ6mK6AAAAABJRU5ErkJggg==">
											</time>
										</footer>
									</article>
								</c:when>
								<c:otherwise>
										${symbol_dollar}{item.html}
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
			</div>
		</div>
	</c:forEach>
</div>
<glassmaker:footer />

</body>
</html>