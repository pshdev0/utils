var output = "";
let list = Object.entries(rtn);
console.log(list);
for(var c1 = 0; c1 < list.length; c1++) {
    output += [list[c1][0], list[c1][1].length, ...list[c1][1]].join("\n") 
+ "\n";
}
console.log(output);

