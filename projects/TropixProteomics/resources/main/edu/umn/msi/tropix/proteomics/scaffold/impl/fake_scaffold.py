#!/usr/bin/env python

import sys

def main():
    argv = sys.argv
    path = argv[len(argv) - 1]
    contents = ""
    with open(path, "r") as file:
        contents = file.read()
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
            file.write("Hello World!")
        location = end

if __name__ == "__main__":
    main()
