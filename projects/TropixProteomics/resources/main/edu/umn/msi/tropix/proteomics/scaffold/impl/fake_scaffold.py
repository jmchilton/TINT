#!/usr/bin/env python

import sys
import re
import os

def get_input_files(contents):
    matches = re.findall("<InputFile>(.*?)</InputFile>", contents)
    return [match for match in matches]

def get_input_summary(contents):
    input_files = get_input_files(contents)
    input_sizes = [os.path.getsize(input_file) for input_file in input_files]
    return '\n'.join([("%s=%s" % (file_size_tuple[0].replace(' ', '\ '), file_size_tuple[1])) for file_size_tuple in zip(input_files, input_sizes)])

def main():
    argv = sys.argv
    path = argv[len(argv) - 1]
    contents = ""
    with open(path, "r") as file:
        contents = file.read()
    input_summary = get_input_summary(contents)
    location = 0
    next_location = -1        
    while True:
        next_location = contents.find("<Export", location)
        if next_location == -1:
            break
        start = contents.find("path=\"", next_location)
        start = start + len("path=\"")
        end = contents.find("\"", start + 1)
        epath = contents[start:end]
        with open(epath, "w") as file:
            file.write(input_summary)
        location = end

if __name__ == "__main__":
    main()
