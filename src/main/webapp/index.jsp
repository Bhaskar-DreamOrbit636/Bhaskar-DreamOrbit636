<!-- <html>
<body>
<h1>Reminder365 is running ...</h1>
</body>
file upload section
<form action="savefile" method="post" enctype="multipart/form-data">  
Select File: <input type="file" name="file"/>  
<input type="submit" value="Upload File"/>  
</form>

<h3> Multiple File Upload </h3>
	<form method="post" enctype="multipart/form-data" action="multipleSave">
		Upload File 1: <input type="file" name="file"> <br/><br/>
		Upload File 2: <input type="file" name="file"> <br/><br/>
		Upload File 3: <input type="file" name="file"> <br/><br/>
		Upload File 4: <input type="file" name="file"> <br/>
		<br /><br /><input type="submit" value="Upload"> 
	</form>
</html> -->

<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>App</title>
<base href="./dist">
<meta name="viewport" content="width=device-width,initial-scale=1">
<link href="${pageContext.request.contextPath}/dist/style.bundle.css" rel="stylesheet" />
<link rel="icon" type="image/x-icon" href="favicon.ico">

</head>
<body>


	<app-root></app-root>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/dist/inline.bundle.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/dist/polyfills.bundle.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/dist/scripts.bundle.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/dist/main.bundle.js" ></script>
</body>
</html>  
