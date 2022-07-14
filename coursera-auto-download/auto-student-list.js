// this code forms part of one of the other larger auto files
//
// Go Coursera / Edit courses / Grading / Submission of graded programming assignments [001]
//
// Paste this code into the develoepr console and run.
//
// A list of student IDs and the student assignment page URLs will be copied to the clipboard

var START_INDEX = 0;
var END_INDEX = 50;

console.log("Scanning for students");
var urls = document.getElementsByTagName('a');
var rtn = {};
for (url in urls) {
    if(urls[url].href && urls[url].href.startsWith("https://www.coursera.org/teach/uol-graphics-programming/PjESn54jEey9ig43bzvlxw/grading/assignment-grading/Cg7EC/submission/")) {
        rtn[urls[url].innerText] = urls[url].href;
    }
}
let list2 = Object.entries(rtn);
var output = "";
for(var c1 = START_INDEX; c1 < Math.min(END_INDEX, list2.length); c1++) {
    output += [list2[c1][0], list2[c1][1]].join("\n") + "\n";
}
copy(output);
console.log("The data was copied to the clipboard.");
