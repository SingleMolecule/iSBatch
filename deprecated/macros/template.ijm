var _output_paths = "";

function addToTree(format, name, extension) {
	path = "%output_path%" + name + "%tags%" + "." + extension;
	saveAs(format, path);
	_output_paths += path + "\n";
}

if (File.exists("%input_path%")) {
	open("%input_path%");
}

%macro%

if (File.exists("%input_path%")) {
	close();
}

return _output_paths;