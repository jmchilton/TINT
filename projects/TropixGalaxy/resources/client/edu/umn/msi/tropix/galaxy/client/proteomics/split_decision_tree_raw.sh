#!/bin/bash

input=$1
output_cid=$2
output_etd=$3

mkdir output
msconvert -o output --mzXML --filter "activation CID" $input 2>&1
mv output/* $output_cid
msconvert -o output --mzXML --filter "activation ETD" $input 2>&1
mv output/* $output_etd

