<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<link href="css/bootstrap.min.css" rel="stylesheet">
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>FindYOURCar</title>
</head>
<body>
	<div class="container">
		<h1>Error Message</h1>
		<div class="card text-white bg-danger mb-3" style="max-width: 18rem;">
			<div class="card-header">Error!</div>
			<div class="card-body">
				<h5 class="card-title">Something went wrong.</h5>
				<p class="card-text">${ error }</p>
			</div>
		</div>
		<a href="index.jsp"> Back to the starting page.</a>
	</div>
</body>
</html>