// template for each macro that runs inside ISBatch
//
// when a macro runs from the ISBatch program it is given an argument string.
// This argument string contains the type of node the macro is operating on
// and also specifies the output folder where files can be stored. The argument
// string also contains a list of all files that lie below the selected node.
//
// argument string : type, output folder, file 1, file 2, ..., file n
//
// the string '%user_macro% is replaced by the given macro file
//
//

var _isb_argument_list = split(getArgument(), ",");
var _isb_output_arguments = "";

function getNumberOfFiles() {
    return _isb_argument_list.length - 2;
}

function getFile(i) {
    return _isb_argument_list[i + 2];
}

function getType() {
    return _isb_argument_list[0];
}

function getOutputFolder() {
    return _isb_argument_list[1];
}

function addOutputFile(filename) {

    if (lengthOf(_isb_output_arguments) == 0) 
        _isb_output_arguments = filename;
    else
        _isb_output_arguments += "," + filename;
        
}

%user_macro%

return _isb_output_arguments;
