var urls = document.getElementsByTagName('a');
var rtn = {};
for (url in urls) {
    if(urls[url].href && 
urls[url].href.startsWith("https://www.coursera.org/teach/uol-graphics-programming/PjESn54jEey9ig43bzvlxw/grading/assignment-grading/Cg7EC/submission/")) 
{
        rtn[urls[url].innerText] = urls[url].href;
    }
}

console.log("Found " + Object.keys(rtn).length + " students.");
console.log("Scanning for assignments... please wait. Each student will 
take 20 seconds unless you can find a way to detect page loaded state ?");

let list = Object.entries(rtn);

for(var index = 0; index < list.length; index++) {

    let item = list[index];
    if(index > 1) continue;

    let id = item[0]; // student id
    let url = item[1]; // url to student zip download and grading page

    var w = window.open(url, "_blank"); // open in a new tab otherwise 
this process will terminate !
    await new Promise(resolve => { setTimeout(resolve, 20000) }); // hack 
sleep 10 seconds

    var urls = w.document.getElementsByTagName('a'); // get the potential 
urls
    var zipLinks = [];
    for (url in urls) { // extract the urls we need (zip files)
        if(urls[url].href && 
urls[url].href.includes("https://coursera-assessments.s3.amazonaws.com/assessments/")) 
{
            zipLinks.push(urls[url].href);
        }
    }

    rtn[id] = zipLinks; // replace the student data with the url links

    console.log("Found " + zipLinks.length + " zip files for student " + 
id + ".");
}

console.log(rtn);

