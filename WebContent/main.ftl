<!DOCTYPE html>
<html>
<head>
	<title>Story Reader</title>
	<style>
		body,html {
			width: 80%;
			height: 99%;
			margin: 0 auto;
			background-color:#FFFFFF;
		}
		
		.container {
			margin: 0px auto;
			display: -webkit-flex;
			display: flex;
			-webkit-justify-content: center
			justify-content: center;
			-webkit-flex-wrap: wrap;
			flex-wrap: wrap;
			width: 100%;
			height: 100%;
			background-color:white;
			
		}
		
		.header {
			width: 100%;
			height: 10%;
			border: 1px solid #DDDDDD;
			
			display: -webkit-flex;
			display: flex;
		
			
			
			font-family: "Lucida Console", Monaco, monospace;
		}
		
		#tlt {
			width:50%;
			height:60%;
			margin-left:0px;
			margin-bottom:auto;
			margin-top:auto;
			text-align:left;
			
		}
		
		.but {
			width:50%;
			height:60%;
			margin-right:0px;
			margin-bottom:auto;
			margin-top:auto;
			text-align:right;
			display: -webkit-flex;
			display: flex;
			-webkit-flex-wrap: wrap;
			flex-wrap: wrap;
			
		}
		
		#footer {
			width: 100%;
			height: 5%;
			border: 1px solid #DDDDDD;
			text-align: center;
			vertical-align: middle;
			margin-bottom: 0px;
			margin-top:0px;
		}
		
		.c {
			height: 85%;
			display: -webkit-flex;
			display: flex;
			-webkit-justify-content: center
			justify-content: center;
			
			width: 100%;
			
			border: 1px solid #FFFFFF;
			text-align: center;
			vertical-align: middle;
		}
		
		#list {
			width: 80%;
			height: 100%;
			overflow:auto;
			text-align: left;
			vertical-align: middle;
			border-right: 1px solid #DDDDDD;
		}
		
		#login {
			
			display: -webkit-flex;
			display: flex;
			-webkit-justify-content: center
			justify-content: center;
			-webkit-flex-wrap: wrap;
			flex-wrap: wrap;
			height: 300px;
			width: 400px;
			margin:auto;
			border: 1px solid #00FF00;
		}
		
		
		#choice {
			width: 20%;
			height: 100%;
			border-right: 1px solid #DDDDDD;
			border-left: 1px solid #DDDDDD;
			font-family: "Lucida Console", Monaco, monospace;
		}
		
		
	</style>
</head>
<body>
	<div class = "container">
		<div class = "header">
			<div id='tlt'><h1>Story Reader</h1></div>
			<div class='but'>
				${button}
			</div>
		</div>
		
		<div class = "c">
		  <div id = "choice">
		  	${menu}
		  </div>
		  <div id = "list">
		    ${list}
		  </div>
		</div>
		
		<div id = "footer">Created By: Mingchao Liao</div>
	</div>
	

	
</body>
</html>
