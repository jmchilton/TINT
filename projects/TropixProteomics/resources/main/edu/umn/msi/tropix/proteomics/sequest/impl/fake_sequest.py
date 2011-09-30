#!/usr/bin/env python

import sys
import os

#http://stackoverflow.com/questions/1158076/implement-touch-using-python
def touch(fname, times = None):
    with file(fname, 'a'):
        os.utime(fname, times)

def main():
    argv = sys.argv
    files_path_arg = argv[len(argv) - 1]
    files_path = files_path_arg[2:] # strip -R
    contents = ""
    with open(files_path, "r") as file:
        contents = file.read()
    import re
    files = re.split("[\n\r]", contents)
    for file in files:
      out_file = file[0:len(file)-4] + ".out"
      touch(out_file)

if __name__ == "__main__":
    main()
