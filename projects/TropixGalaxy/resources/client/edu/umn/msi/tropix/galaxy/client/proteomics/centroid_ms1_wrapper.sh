#!/bin/bash

input=$1
output=$2

mkdir output
msconvert -o output --mzXML --filter "peakPicking true 1" $input 2>&1
mv output/* $output

