Automatically scan Coursera to download student IDs and associated ZIP files. Seems a bit buggy since 
the last cohort - probably due to Chrome updates, so may need a bit of tweaking !

**Steps**

*This was written on Mac, but should work on Linux*

1. Go to `Coursera / Edit courses / Grading / Submission of graded programming assignments [001]`, or whichever list of student IDs you're interested in.
2. Open the Developer Console and paste in the code from `auto-scan.js` file. Press Enter.
3. This will scan the current page for relevant URLs and automatically open / close the student pages and scan them for ZIP file URLs.
4. Once completed a list of student IDs, # zips, and zip file urls will be copied to the clipboard, which you can paste into a text file.

5. Once you've got all the data into a text file (call it `data.txt`), copy it into a folder of your choice.
6. Copy the bash script into the same folder and run it with `sh auto-download.sh` to process the text file, which will automatically create student ID folders and download all the zip files.

Hopefully it will save you some time ! I successfully downloaded all my student zip files automatically (around 50)

*NOTES*

1. Chrome seems not to like too many `setTimeout` commands and during tests I found it slowed down after scanning around 33 students. If you leave it, it will still work, but you may want to limit the number of students downloaded at once via the `START_INDEX` and `END_INDEX`. Maybe split your scans into two parts (0-24, 25-49) ?
2. Since hacky code like this is a bit of an art, once I had to reboot my computer to reset Chrome because it got into a bit of a mess and wouldn't open the new tabs when the program was run.
3. The `unzip` command is not present in the bash script for some reason, I must have not saved it out properly, so you'll have to unzip manually or try adding it yourself. I will add this to the repo at a later date.
4. You may also need to make sure the http links in the JS code match those for your Coursera student pages. You can find out by clicking a student ID and comparing the URL it shows against that in the JS code.

**FURTHER AUTOMATION**

*This automation was written on Linux Mint, but should work on Mac too*

Another useful script opens up all my student windows (I open around 25 at a time) on separate Workspaces. This is useful to remove some of the laborious tasks involved in mass student marking. My original plan was to mark in parallel, but in the end I found it easier just to mark each assignment in full before moving on to the next. Repeating this for each assignment for each student completes the grading !

1. Make sure you have enough Workspaces open for each student + a couple extra for yourself.
2. Run the `auto-student-list.js` in the Chrome developer console (this is just a small part of the original automation scripts).
3. The result will be stored in the clipboard, so paste it into another file, called `data.txt` (note that `data.txt` has different contents to the original `data.txt` above so yo may want to use a different name - I should change this in future)
4. Open up your Chrome browser and change its position/size to that you'd like all consecutively Chrome windows open to.
5. Run `bash w1.sh 0 5` to process the first 5 students in the list. Change the arguments, e.g. "25 49", for different student ranges.
6. This example will open 5 Chrome windows, each with the URL to the student assignment marking page.
7. It will also open 5 desktop folders showing the student projects AND it will move them to different workspaces automatically for you.
8. Next run `bash w2.sh` which will automatically move the Chrome windows to the corresponding workspaces.
