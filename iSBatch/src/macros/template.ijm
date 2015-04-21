// template for each macro that runs inside ISBatch
//
// when a macro runs from the ISBatch program it is given an argument string.
// This argument string contains all the tree node properties underneath the selected node.
// The format of the argument string is the following :
//
// (A=1 B=2 (A=3 B=4) (A=5 B=6 (A=7)(A=8)))
//
// this represents the following tree structure:
//
//       (A=1 B=2)
//          / \
// (A=3 B=4)   (A=5 B=6)
//                / \
//           (A=7)   (A=8)  
//
// This template macro contains all the functions to parse the argument string
// and functions to get the property values. Properties are stored in a list.
// The variable 'root' refers to the root node.
//
// example of usage :
//
// children = getChildren(root);
// numberOfChildren = children.length;	// the root node has 2 children
// IJ.log("the root has " + numberOfChildren + " child nodes");
// child1 = children[0];
// child2 = children[1];
// getProperties(child1);	// load the properties in the list
// a = List.get("A");
// b = List.get("B");
// IJ.log(a);	// this will show 5
// IJ.log(b);	// this will show 6
//
//

var outputFiles = "";

function getChildren(str) {

    children = newArray(1000);
    n = 0;
    content = "";
    level = 0;
    ch = "";
    
    for (i = 0; i < lengthOf(str); i++) {
        
        lch = ch;
        ch = substring(str, i, i + 1);

        if (lch != "\\" && ch == ")") {
            level--;
            
            if (level == 0) {
                children[n++] = content;
                content = "";
            }
        }
        
        if (level >= 1)
            content += ch;
            
        if (lch != "\\" && ch == "(")
            level++;
      
    }

    return Array.trim(children, n);
}

function getProperties(str) {

    prop = "";  // properties
    level = 0;  // level in the tree
    ch = "";
    
    for (i = 0; i < lengthOf(str); i++) {
        lch = ch;
        ch = substring(str, i, i + 1);
        
        if (lch != "\\" && ch == "(")
            level++;
        else if (lch != "\\" && ch == ")") {
            level--;
            if (level == 0) prop += " ";
        }
        else if (level == 0)
            prop += ch;
    }

    prop = escape(prop);
    List.clear;
    List.setList(prop);
}

function getKeys() {
    return split(replace(List.getList, "=[^\\n]*", ""));
}

function addOutputFile(filename) {
	outputFiles += filename + "\n";
}


function escape(str) {

	result = "";
	
	for (i = 0; i < lengthOf(str); i++) {
	
		ch = substring(str, i, i + 1);
		
		if (ch == "\\" && i + 1 < lengthOf(str)) {
			i++;
			ch = substring(str, i, i + 1);
			
			if (ch == "\\")
				result += "\\\\";
			else
				result += ch;
				
		}
		else if (ch == " ") {
		
			while (ch == " " && i + 1 < lengthOf(str)) {
				i++;
				ch = substring(str, i, i + 1);
			}
			
			result += "\n" + ch;		
		}
		else
			result += ch;
	}

	return result;
}


/*
function escape(str) {
	str = replace(str, " +", "\n");
	str = replace(str, "\\\(", "(");
	str = replace(str, "\\\)", ")");
	str = replace(str, "\\\\=", "=");
	str = replace(str, "\\\\", "\\\\\\\\");
	
	
	return str;
}
*/

children = getChildren(getArgument());
root = children[0];
children = getChildren(root);
getProperties(root);


%user_macro%

return outputFiles;
