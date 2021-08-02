<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<link href="css/bootstrap.min.css" rel="stylesheet">
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Find YOUR Car</title>
</head>
<body>
	<div class="container">
		<h1>Daten eingeben um passendes Fahrzeug zu finden</h1>

		<form action="CarServlet" method="get" class="form-horizontal">
			<table>
				<tr>
					<td>Marke</td>
					<td><input name="marke" placeholder="Marke" value=${ inputTitle }></td>
				</tr>
				<tr>
					<td>Modell</td>
					<td><input name="modell" placeholder="Modell" value=${ inputModell }></td>
				</tr>
				<tr>
					<td>Preisklasse</td>
					<td><select name="preis" size="3">
							<option>5000</option>
							<option>10000</option>
							<option>15000</option>
					</select></td>
				</tr>
				<tr>
					<td>Hubraum (ccm)</td>
					<td><input name="hubraum" placeholder="Hubraum" value=${ inputHubraum }></td>
				</tr>
				<tr>
					<td>PS</td>
					<td><input name="ps" placeholder="PS" value=${ inputPs }></td>
				</tr>
				<tr>
					<td>Kraftstoffart</td>
					<td><select name="kraftstoff" size="3">
							<option>Benzin</option>
							<option>Diesel</option>
							<option>Elektro</option>
					</select></td>
				</tr>
				<tr>
					<td></td>
					<td><input type="submit" value="Search!"></td>
				</tr>
			</table>
		</form>
		
		<hr>

		<c:if test="${ resultingBooks != null }">
			<c:forEach var="resultingBooks" items="${ resultingBooks }" varStatus="loop">

				<table class="table table-bordered table-hover">
					<tr>
						<td><b>Rank ${ loop.count }</b></td>
						<td>Similarity: ${ resultingBooks.getSimilarity() }</td>
					</tr>
					<tr>
						<td>Title:</td>
						<td>${ resultingBooks.getTitle() }</td>
					</tr>
					<tr>
						<td>Year:</td>
						<td>${ resultingBooks.getYear() }</td>
					</tr>
					<tr>
						<td>Category:</td>
						<td>${ resultingBooks.getCategory() }</td>
					</tr>
					<tr>
						<td>Author:</td>
						<td>${ resultingBooks.getAuthor() }</td>
					</tr>
					<tr>
						<td>Award:</td>
						<td>${ resultingBooks.isAwarded() }</td>
					</tr>
				</table>
			</c:forEach>
		</c:if>
	</div>
</body>
</html>