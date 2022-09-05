// Changes made on 5-9-22
// Paul Haynes
//
// 1. added COURSE_CODE variable as this changes per cohort
// 2. removed copy [to clipboard] as no longer seems to work, and replaced with a prompt hack instead; 
just CMD+C when the prompt appears
//

var START_INDEX = 0;
var END_INDEX = 5;
var COURSE_CODE = "K7HJb";

var urls = document.getElementsByTagName('a');
var rtn = {};
for (url in urls) {
    if(urls[url].href && 
urls[url].href.startsWith("https://www.coursera.org/teach/uol-graphics-programming/PjESn54jEey9ig43bzvlxw/grading/assignment-grading/" 
+ COURSE_CODE + "/submission/")) {
        rtn[urls[url].innerText] = urls[url].href;
    }
}

console.log("Found " + Object.keys(rtn).length + " students.");
console.log("Scanning for assignments, please wait...");

let list = Object.entries(rtn);

var w;
var timeout;

function waitUntilLoaded(resolve) {
    console.log("waiting...");

    // we use this approach to avoid problems with browsers / tabs / setInterval !
    setTimeout(() => {
        timeout++;
        if((w.document.documentElement.innerText && 
w.document.documentElement.innerText.indexOf("Submission & Grading") > -1) || timeout > 40) resolve();
        else waitUntilLoaded(resolve);
    }, 500);
}

for(var index = START_INDEX; index < Math.min(END_INDEX, list.length); index++) { // loop through all 
students

    let id = list[index][0]; // student id
    let url = list[index][1]; // url to student zip download and grading page

    w = window.open(url, "_blank"); // open in a new tab otherwise this process will terminate !

    timeout = 0;
    await new Promise(resolve => waitUntilLoaded(resolve)); // a bit of a hack to wait until the page 
loads properly, or we timeout

    var urls = w.document.getElementsByTagName('a'); // get the potential urls
    var zipLinks = [];
    for (url in urls) { // extract the urls we need (zip files)
        if(urls[url].href && 
urls[url].href.includes("https://coursera-assessments.s3.amazonaws.com/assessments/")) {
            zipLinks.push(urls[url].href);
        }
    }

    w.close(); // close the tab so we don't make a mess
    rtn[id] = zipLinks; // replace the student data with the url links
    console.log(index + ": Found " + zipLinks.length + " zip files for student " + id + ".");
}

// console.log(rtn); // output the object

var output = "";
let list2 = Object.entries(rtn);
for(var c1 = START_INDEX; c1 < Math.min(END_INDEX, list2.length); c1++) {
    output += [list2[c1][0], list2[c1][1].length, list2[c1][1]].join("\n") + "\n";
}
window.prompt("Copy to clipboard: Ctrl+C, Enter", output);
console.log("The data was copied to the clipboard !");
