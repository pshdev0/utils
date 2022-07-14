Automatically scan Coursera to download student IDs and associated ZIP files.

**Steps**

1. Go to `Coursera / Edit courses / Grading / Submission of graded programming assignments [001]`, or whichever list of student IDs you're interested in.
2. Open the Developer Console and paste in the code from `auto-scan.js` file. Press Enter.
3. This will scan the current page for relevant URLs and automatically open / close the student pages and scan them for ZIP file URLs.
4. Once completed you'll a list of student IDs, # zips, and zip file url links will be copied to the clipboard, which you can paste into a text file.

5. Once you've got all the data into a text file (call it `data.txt`), copy it into a folder of your choice.
6. Copy the bash script into the same folder and run it with `sh auto-download.sh` to process the text file, which will automatically create student ID folders and download all the zip files.

Hopefully it will save you some time ! I successfully downloaded all my student zip files automatically (around 50)

**Further Automation**

I'm trialling opening up all my student windows (or at least 20 at a time) on separate Workspaces. However, this is a bit laborious, so I've written some more scripts to automate setting up my desktop.

1. Run: ``

*NOTES*

1. Chrome seems not to like too many `setTimeout` commands and during tests I found it slowed down after scanning around 33 students. If you leave it, it will still work, but you may want to limit the number of students downloaded at once via the `START_INDEX` and `END_INDEX`. Maybe split your scans into two parts (0-24, 25-49) ?
2. Since hacky code like this is a bit of an art, once I had to reboot my computer to reset Chrome because it got into a bit of a mess and wouldn't open the new tabs when the program was run.
3. The `unzip` command is not present in the bash script for some reason, I must have not saved it out properly, so you'll have to unzip manually or try adding it yourself. I will add this to the repo at a later date.
4. You may also need to make sure the http links in the JS code match those for your Coursera student pages. You can find out by clicking a student ID and comparing the URL it shows against that in the JS code.
