#!/usr/bin/env python

import sys

input = sys[1]
output = sys[2]

with(output, 'w') as file:
  file.write(input)

import time
time.sleep(1000)


