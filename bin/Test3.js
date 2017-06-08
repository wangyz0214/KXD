for (var i = 0; i < arr.length; i++){
	if(arr[i] === undefined) continue)
	//to add code
}

var table = new Array(10)
for(var i = 0; i < table.lenfth; i++){
	table[i] = new Array(10);
}
for (var row = 0; row < table.length; row++){
	for(var col = 0; col < table[row].length; col++){
		table[row][col] = row * col;
	}
}