#!/bin/bash

# run first and paste the contents of the clipboard into data.txt
# usage: type "bash w1.sh 3 10"

# this will open the urls of student IDs 3, 4, 5, ... 9 (change the bash arguments for different ranges)
# it will also open the associated student folders AND move them to consecutive workspaces

# next run "bash w2.sh" to move the student web pages to the corresponding workspace.

input="./data.txt"

echo "Opening windows"

startindex=$1
endindex=$2
count=0

while IFS= read -r studentid
do

  read -r url

  if  [[ $count -ge $startindex && $count -lt $endindex ]]; then
    google-chrome $url --args --new-window &
    echo "/home/paul/Teaching/Goldsmiths/CM2030 - Copmuter Graphics/Mid-term marking/Student Assignments/"$studentid
    echo ""
    xdg-open "/home/paul/Teaching/Goldsmiths/CM2030 - Copmuter Graphics/Mid-term marking/Student Assignments/"$studentid &
    sleep 5
    wmctrl -r $studentid -t $(($count+1))
  fi


  count=$((count+1))

done < "$input"

wmctrl -l | grep Coursera | sed 's/ .*//' > windows.txt
