#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@page import="org.glassmaker.spring.oauth.OAuth2Util"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="glassmaker"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>


<glassmaker:header title="Welcome" />

<div class="page-header">
	<h1>Template Web Application for Google Mirror API</h1>
</div>

<div class="row">
	<div class="col-sm-12">
		<div class="panel panel-default">
			<div class="panel-heading"></div>
			<h3 class="panel-title" data-bind="text: title"></h3>
			<div class="panel-body">
				<p>The template Java Web application gets you started with
					building Mirror API for the Glass. To authorize the project to send
					and receive data from your account, you will be asked to Authorize
					with you google account. There should be a glass associated with
					your account for the project to work.</p>

			</div>
		</div>
	</div>
</div>
<glassmaker:footer />
</body>
</html>