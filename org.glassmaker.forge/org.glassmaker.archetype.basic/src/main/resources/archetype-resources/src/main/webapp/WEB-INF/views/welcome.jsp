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
	<h1>Send a greeting!</h1>
</div>
<div class="alert alert-success">
	<form action="${symbol_dollar}{request.contextPath}/gm/greet" method="post">
		<input placeholder="Greeting..." name="greeting" size="100"/>
		<button type="submit" name="Send Greeting!" value="Send Greeting!">Send!</button>
	</form>
</div>


<glassmaker:footer />
</body>
</html>