var START_STUDENT_INDEX = 0;
var END_STUDENT_INDEX = 5;

var urls = document.getElementsByTagName('a');
var rtn = {};
for (url in urls) {
    if(urls[url].href && urls[url].href.startsWith("https://www.coursera.org/teach/uol-graphics-programming/PjESn54jEey9ig43bzvlxw/grading/assignment-grading/Cg7EC/submission/")) {
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
        if((w.document.documentElement.innerText && w.document.documentElement.innerText.indexOf("Submission & Grading") > -1) || timeout > 40) resolve();
        else waitUntilLoaded(resolve);
    }, 500);
}

for(var index = START_STUDENT_INDEX; index < Math.min(END_STUDENT_INDEX, list.length); index++) { // loop through all students

    let id = list[index][0]; // student id
    let url = list[index][1]; // url to student zip download and grading page

    w = window.open(url, "_blank"); // open in a new tab otherwise this process will terminate !

    timeout = 0;
    await new Promise(resolve => waitUntilLoaded(resolve)); // a bit of a hack to wait until the page loads properly, or we timeout

    var urls = w.document.getElementsByTagName('a'); // get the potential urls
    var zipLinks = [];
    for (url in urls) { // extract the urls we need (zip files)
        if(urls[url].href && urls[url].href.includes("https://coursera-assessments.s3.amazonaws.com/assessments/")) {
            zipLinks.push(urls[url].href);
        }
    }

    w.close(); // close the tab so we don't make a mess
    rtn[id] = zipLinks; // replace the student data with the url links
    console.log(index + ": Found " + zipLinks.length + " zip files for student " + id + ".");
}

console.log(rtn); // output the object
