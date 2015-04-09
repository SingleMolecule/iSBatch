var gaussian = function(x, y, h, s) {

	return h * Math.exp(-(x * x + y * y) / (2 * s * s));

};



var table = Analyzer.getResultsTable();

var headings = table.getColumnHeadings().split("\t|,");



var xColumn = headings[0];

var yColumn = headings[0];

var sliceColumn = headings[0];

var sliceFrom = 0;

var sliceTo = 1000;

var xMin = -10;

var yMin = -10;

var xMax = 100;

var yMax = 10;

var pixelSize = 0.1;

var height = 1.0;

var sigma = 0.8;



var dl = new DialogListener() {

	dialogItemChanged : function(gd, e) {

		

		var fields = gd.getNumericFields();

		var xc = gd.getNextChoice();

		var yc = gd.getNextChoice();

		

		sliceColumn = gd.getNextChoice();

		sliceFrom = gd.getNextNumber();

		sliceTo = gd.getNextNumber();

		xMin = gd.getNextNumber();

		yMin = gd.getNextNumber();

		xMax = gd.getNextNumber();

		yMax = gd.getNextNumber();

		pixelSize = gd.getNextNumber();

		height = gd.getNextNumber();

		sigma = gd.getNextNumber();

		

		if (xc != xColumn && table.getColumnIndex(xc) != ResultsTable.COLUMN_NOT_FOUND) {

			

			xColumn = xc;

			xMin = xMax = table.getValue(xColumn, 0);

			

			for (var row = 0; row < table.getCounter(); row++) {

				var x = table.getValue(xColumn, row);

				

				if (x < xMin) xMin = Math.floor(x);

				if (x > xMax) xMax = Math.ceil(x);

			}

			

			//fields.get(2).setText(Double.toString(xMin));

			//fields.get(4).setText(Double.toString(xMax));

		}

		

		if (yc != yColumn && table.getColumnIndex(yc) != ResultsTable.COLUMN_NOT_FOUND) {

			

			yColumn = yc;

			yMin = yMax = table.getValue(yColumn, 0);

			

			for (var row = 0; row < table.getCounter(); row++) {

				var y = table.getValue(yColumn, row);

				

				if (y < yMin) yMin = Math.floor(y);

				if (y > yMax) yMax = Math.ceil(y);

			}

			

			//fields.get(3).setText(Double.toString(yMin));

			//fields.get(5).setText(Double.toString(yMax));

		}

		



		return table.getColumnIndex(xColumn) != ResultsTable.COLUMN_NOT_FOUND

			&& table.getColumnIndex(yColumn) != ResultsTable.COLUMN_NOT_FOUND

			&& table.getColumnIndex(sliceColumn) != ResultsTable.COLUMN_NOT_FOUND

			&& sliceFrom < sliceTo

			&& xMin < xMax

			&& yMin < yMax

			&& pixelSize > 0;

	}

};



var dialog = new GenericDialog("Draw Data");

dialog.addChoice("x_column", headings, xColumn);

dialog.addChoice("y_column", headings, yColumn);

dialog.addChoice("slice_column", headings, sliceColumn);

dialog.addNumericField("slice_from", sliceFrom, 0);

dialog.addNumericField("slice_to", sliceTo, 0);

dialog.addNumericField("x_min", xMin, 2);

dialog.addNumericField("y_min", yMin, 2);

dialog.addNumericField("x_max", xMax, 2);

dialog.addNumericField("y_max", yMax, 2);

dialog.addNumericField("pixel_size", pixelSize, 2);

dialog.addMessage("gaussian function");

dialog.addNumericField("height", height, 2);

dialog.addNumericField("standard deviation", sigma, 2);

dialog.addDialogListener(dl);

dialog.showDialog();



if (dialog.wasCanceled())

	exit();



var width = Math.round(xMax - xMin) / pixelSize;

var height = Math.round(yMax - yMin) / pixelSize;



var image = IJ.createImage("Image", "32-bit", width, height, 1);



// draw all the points

var ip = image.getProcessor();



for (var row = 0; row < table.getCounter(); row++) {

	var x = table.getValue(xColumn, row);

	var y = table.getValue(yColumn, row);

	var s = table.getValue(sliceColumn, row);

	

	if (s >= sliceFrom && s <= sliceTo) {

		x -= xMin;

		y -= yMin;

		x /= pixelSize;

		y /= pixelSize;



		for (var y1 = -5; y1 <= 5; y1++) {

			for (var x1 = -5; x1 <= 5; x1++) {

				var value = gaussian(x1, y1, height, sigma);

				ip.putPixelValue(x + x1, y + y1, ip.getPixelValue(x + x1, y + y1) + value);

			}

		}

	}

	

	IJ.showProgress(row, table.getCounter() - 1);

}



var calibration = image.getCalibration();

calibration.pixelWidth = calibration.pixelHeight = pixelSize;

calibration.xOrigin = -xMin / pixelSize;

calibration.yOrigin = -yMin / pixelSize;



image.show();