

function showTree(node, indent) {

	getProperties(node);
	children = getChildren(node);
	keys = getKeys();
	
	for (i = 0; i < keys.length; i++)
		IJ.log(indent + keys[i] + " = " + List.get(keys[i]));
		
	for (i = 0; i < children.length; i++)
		showTree(children[i], indent + "    ");
		
}

showTree(root, "");
IJ.log(List.get("name"));

