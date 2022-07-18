#!/bin/bash

# run first and paste the contents of the clipboard into data.txt
# usage: type "bash w1.sh 3 10"

# this will open the urls of student IDs 3, 4, 5, ... 9 (change the bash arguments for different ranges)
# it will also open the associated student folders AND move them to consecutive workspaces

# next run "bash w2.sh" to move the student web pages to the corresponding workspace.

#!/bin/bash

assignmentFolder="/home/paul/Teaching/Goldsmiths/CM2030 - Copmuter Graphics/Mid-term marking/Student Assignments/"

input="./data.txt"

echo "Opening windows"

startindex=$1
endindex=$2
count=0
count2=0

while IFS= read -r studentid
do

  read -r url

  if  [[ $count -ge $startindex && $count -lt $endindex ]]; then
  echo $studentid $url

    google-chrome $url --args --new-window &
    xdg-open "$assignmentFolder"$studentid &
    sleep 5
    wmctrl -r $studentid -t $(($count2+1))
    count2=$((count2+1))
  fi


  count=$((count+1))

done < "$input"

rm windows.txt
wmctrl -l | grep Coursera | sed 's/ .*//' > windows.txt
