#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ taglib tagdir="/WEB-INF/tags" prefix="glassmaker"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>


<glassmaker:header title="Welcome" />

<div class="page-header">
	<h1>Subscribing to timeline</h1>
</div>

<div class="alert alert-success">
	<form action="${symbol_dollar}{request.contextPath}/gm/subscribe" method="post">
		<select name="collection" id="collection">
			<option value="timeline">timeline</option>
			<option value="locations">locations</option>
			<option>
		</select>
		<button type="submit" name="Subscribe!" value="Subscribe!">Subscribe!</button>
	</form>
</div>

<div class="well">
	<h2 id="subscribing_to_timeline_notifications">Subscribing to
		timeline notifications</h2>
	<p>The Google Mirror API can send notifications of user events to
		your Glassware, which include:</p>
	<ul class="list-group">
		<li class="list-group-item">When a user selects menu items</li>
		<li class="list-group-item">When a user shares content with a
			contact</li>
		<li class="list-group-item">The user's location, every ten
			minutes or so</li>
	</ul>
	<p>To receive notifications, you create a subscription to them,
		specifying a callback URL that receives the notification when it
		occurs. When you receive the notification, you can carry out the
		desired action based on the notification.</p>
</div>

<glassmaker:footer />
</body>
</html>