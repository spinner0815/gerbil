<%@page import="org.aksw.gerbil.web.ExperimentTaskStateHelper"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<head>
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap/3.2.0/css/bootstrap.min.css">
<title>Overview</title>
<script type="text/javascript"
	src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>
<script type="text/javascript"
	src="/gerbil/webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="http://d3js.org/d3.v3.min.js"></script>
<script type="text/javascript"
	src="/gerbil/webResources/js/gerbil.color.js"></script>
<script type="text/javascript"
	src="/gerbil/webResources/js/RadarChart.js"></script>
<script type="text/javascript"
	src="/gerbil/webResources/js/script_radar_overview.js"></script>
</head>
<style>
table {
	table-layout: fixed;
}

.table>thead>tr>th {
	vertical-align: middle !important;
	height: 280px;
	width: 20px !important;
}

.rotated_cell div {
	display: block;
	transform: rotate(270deg);
	-moz-transform: rotate(270deg);
	-ms-transform: rotate(270deg);
	-o-transform: rotate(270deg);
	-webkit-transform: rotate(270deg);
	width: 250px;
	position: relative;
	top: -10;
	left: -100;
}

.col-md-12 {
	padding: 5px 0px;
}

#chart { /*position: absolute;*/
	top: 50px;
	left: 100px;
	vertical-align: center;
}

#body {
	overflow: hidden;
	margin: 0;
	font-size: 14px;
	font-family: "Helvetica Neue", Helvetica;
}
</style>

<body>
	<div class="container">
		<!-- mappings to URLs in back-end controller -->
		<c:url var="experimentoverview" value="/experimentoverview" />
		<c:url var="matchings" value="/matchings" />
		<c:url var="exptypes" value="/exptypes" />

		<%@include file="navbar.jsp"%>
		<h1>GERBIL Experiment Overview</h1>
		<div class="form-horizontal">
			<div class="col-md-12">
				<div class="control-group">
					<label class="col-md-4 control-label">Experiment Type</label>
					<div id="expTypes" class="col-md-8"></div>
				</div>
			</div>

			<div class="col-md-12">
				<div class="control-group">
					<label class="col-md-4 control-label">Matching</label>
					<div id="matching" class="col-md-8"></div>
				</div>
			</div>
			<div class="col-md-12">
				<div class="control-group">
					<label class="col-md-4 control-label"></label>
					<div class="col-md-8">
						<button id="show" type="button" class="btn btn-default">Show
							table!</button>
					</div>
				</div>
			</div>
			<div class="col-md-12">
				<p>The table as well as the diagram contain the micro
					F1-measure.</p>
			</div>
			<div class="container-fluid">
				<div id="body">
					<div id="chart"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="container-fluid">
		<table id="outputTable" class="table table-hover table-condensed">
			<thead></thead>
			<tbody></tbody>
		</table>
	</div>

	<script type="text/javascript">
		function loadMatchings() {
	        $
	                .getJSON(
	                        '${matchings}',
	                        {
	                            experimentType : $('#expTypes input').length != 0 ? $('#expTypes input:checked').val()
	                                    : "D2W",
	                            ajax : 'false'
	                        },
	                        function(data) {
		                        var htmlMatchings = "";
		                        for ( var i = 0; i < data.length; i++) {
			                        htmlMatchings += "<label class=\"btn btn-primary\" >";
			                        htmlMatchings += " <input class=\"toggle\" type=\"radio\" name=\"matchingType\" id=\"" + data[i] + "\" value=\"" + data[i] + "\" >"
			                                + data[i];
			                        htmlMatchings += "</label>";
		                        }
		                        $('#matching').html(htmlMatchings);
		                        $('#matching input')[0].checked = true;
	                        });
        };

        function loadExperimentTypes() {
	        $
	                .getJSON(
	                        '${exptypes}',
	                        {
		                        ajax : 'false'
	                        },
	                        function(data) {
		                        var htmlExperimentTypes = "";
		                        for ( var i = 0; i < data.length; i++) {
			                        htmlExperimentTypes += "<label class=\"btn btn-primary\" >";
			                        htmlExperimentTypes += " <input class=\"toggle\" type=\"radio\" name=\"experimentType\" id=\"" + data[i] + "\" value=\"" + data[i] + "\" >"
			                                + data[i];
			                        htmlExperimentTypes += "</label>";
		                        }
		                        $('#expTypes').html(htmlExperimentTypes);
		                        $('#expTypes input')[0].checked = true;
		                        // Add the listener for loading the matchings
		                        $("#expTypes input").change(loadMatchings);
	                        });
        };

        function loadTable() {
	        $.getJSON('${experimentoverview}', {
	            experimentType : $('#expTypes input:checked').val(),
	            matching : $('#matching input:checked').val(),
	            ajax : 'false'
	        }, function(data) {
		        //http://stackoverflow.com/questions/1051061/convert-json-array-to-an-html-table-in-jquery
		        var tbl_body = "";
		        var tbl_hd = "";

		        $.each(data, function(i) {
			        var tbl_row = "";
			        if (i > 0) {
				        $.each(this, function(k, v) {
					        tbl_row += "<td>" + v + "</td>";
				        });
				        tbl_body += "<tr>" + tbl_row + "</tr>";
			        } else {
				        $.each(this, function(k, v) {
					        tbl_row += "<th class=\"rotated_cell\"><div >" + v + "</div></th>";
				        });
				        tbl_hd += "<tr>" + tbl_row + "</tr>";
			        }
		        });
		        $("#outputTable thead").html(tbl_hd);
		        $("#outputTable tbody").html(tbl_body);
		        //draw spider chart
		        var chartData = [];
		        //Legend titles  ['Smartphone','Tablet'];
		        var LegendOptions = [];
		        $.each(data, function(i) {
			        //iterate over rows
			        if (i > 0) {
				        var annotatorResults = [];
				        $.each(this, function(k, v) {
					        if (k == 0) {
						        //annotator
						        LegendOptions.push(v);
					        } else {
						        //results like {axis:"Email",value:0.71},
						        var tmp = {};
						        tmp.axis = data[0][k];
						        if (v == "n.a." || v.indexOf("error") > -1) {
							        tmp.value = 0;
						        } else {
							        tmp.value = v;
						        }
						        annotatorResults.push(tmp);
					        }
				        });
				        chartData.push(annotatorResults);
			        }
		        });
		        //[[{axis:"Email",value:0.71},{axis:"aa",value:0}],[{axis:"Email",value:0.71},{axis:"aa",value:0.1},]];
		        console.log(chartData);
		        drawChart(chartData, LegendOptions);
	        }).fail(function() {
		        console.log("error loading data for table");
	        });
        };

        $(document).ready(function() {
	        //++++++++++++
	        //creating the radioboxes
	        //++++++++++++
	        loadExperimentTypes();
	        loadMatchings();

	        $("#show").click(function(e) {
		        loadTable();
	        });
        });
	</script>
</body>
