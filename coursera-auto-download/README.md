These files enable you to automatically scan Coursera to download student IDs and associated ZIP files.

**Step 1**

1. Go to `Coursera / Edit courses / Grading / Submission of graded programming assignments [001]`, or whichever list of student IDs you're interested in.
2. Open the Developer Console and paste in the code from `auto-scan-1.js` file.
3. This will scan the current page for relevant URLs and automatically open / close the student pages and scan them for ZIP file URLs.
4. Once complete, an object containing all the relevant data will be outputted to the console.

*CAVEAT* - Chrome seems not to like too many setTimeouts and during tests I found it slowed down after scanning around 33 students. If you leave it, it will still work, but you may want to limit the number of students downloaded at once via the for-loop indices, e.g. change it to 0-24, then run it again for 25-49, etc.

I will update the code at a later point so that it downloads a maximum of 25, say, and once rerun picks up where it left off.

**Step 2**

1. In the same Developer Console paste in the code from `auto-convert-2.js` file and run.
2. This will transform the object into a list.
3. Copy this list and paste it into a text file.

**Step 3**

1. Open up a bash terminal and run e.g. `sh auto-download-3.sh` to process the text file.
2. The bash script will automatically create student ID folders and download all the zip files.

*WARNING* - there is an `unzip` command in there too, but I commented it out as I haven't had time to test it yet. You may want to uncomment it and give it a whirl.

**Summary**

I successfully downloaded all 50 of my students IDs and zip files using the above scripts, so I know it works, but if you find you're having problems, let me know and I'll see what I can do.
I will update it at a later date with the above comments in mind and combine Steps 1 and 2. Hope it helps save you all some time !
