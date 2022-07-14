#!/bin/bash

wmctrl -l | grep Coursera | sed 's/ .*//' > windows.txt

# move the windows

input="./windows.txt"

echo "Moving windows"

count=1

while IFS= read -r windowid
do

  echo $windowid

  wmctrl -ir $windowid -t $count

  count=$((count+1))

done < "$input"
